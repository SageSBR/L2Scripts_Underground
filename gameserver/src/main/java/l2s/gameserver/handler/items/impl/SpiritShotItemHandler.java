package l2s.gameserver.handler.items.impl;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import l2s.commons.util.Rnd;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExAutoSoulShot;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.templates.item.WeaponTemplate;

public class SpiritShotItemHandler extends DefaultItemHandler
{
	private static final TIntObjectMap<Skill> SHOT_SKILLS = new TIntObjectHashMap<Skill>();
	static
	{
		SHOT_SKILLS.put(0, SkillHolder.getInstance().getSkill(2047, 1)); // None Grade
		SHOT_SKILLS.put(1, SkillHolder.getInstance().getSkill(2155, 1)); // D Grade
		SHOT_SKILLS.put(2, SkillHolder.getInstance().getSkill(2156, 1)); // C Grade
		SHOT_SKILLS.put(3, SkillHolder.getInstance().getSkill(2157, 1)); // B Grade
		SHOT_SKILLS.put(4, SkillHolder.getInstance().getSkill(2158, 1)); // A Grade
		SHOT_SKILLS.put(5, SkillHolder.getInstance().getSkill(2159, 1)); // S Grade
		SHOT_SKILLS.put(6, SkillHolder.getInstance().getSkill(9194, 1)); // R Grade
	};

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;

		Player player = (Player) playable;

		// spiritshot is already active
		if(player.getChargedSpiritshotPower() > 0)
			return false;

		int shotId = item.getItemId();
		boolean isAutoSoulShot = false;

		if(player.isFake() || player.getAutoSoulShot().contains(shotId))
			isAutoSoulShot = true;

		if(player.getActiveWeaponInstance() == null)
		{
			if(!isAutoSoulShot)
				player.sendPacket(SystemMsg.YOU_MAY_NOT_USE_SPIRITSHOTS);
			return false;
		}

		WeaponTemplate weaponItem = player.getActiveWeaponTemplate();

		int spsConsumption = weaponItem.getSpiritShotCount();
		if(spsConsumption <= 0)
		{
			// Can't use Spiritshots
			if(isAutoSoulShot)
			{
				player.removeAutoSoulShot(shotId);
				player.sendPacket(new ExAutoSoulShot(shotId, false));
				player.sendPacket(new SystemMessage(SystemMessage.THE_AUTOMATIC_USE_OF_S1_WILL_NOW_BE_CANCELLED).addItemName(shotId));
				return false;
			}
			player.sendPacket(SystemMsg.YOU_MAY_NOT_USE_SPIRITSHOTS);
			return false;
		}

		int[] reducedSpiritshot = weaponItem.getReducedSpiritshot();
		if(reducedSpiritshot[0] > 0 && Rnd.chance(reducedSpiritshot[0]))
			spsConsumption = reducedSpiritshot[1];

		if(spsConsumption <= 0)
			return false;

		int grade = weaponItem.getGrade().extOrdinal();
		if(grade != item.getGrade().extOrdinal())
		{
			// wrong grade for weapon
			if(isAutoSoulShot)
				return false;

			player.sendPacket(SystemMsg.YOUR_SPIRITSHOT_DOES_NOT_MATCH_THE_WEAPONS_GRADE);
			return false;
		}

		if(!player.isFake() && !player.getInventory().destroyItem(item, spsConsumption))
		{
			if(isAutoSoulShot)
			{
				player.removeAutoSoulShot(shotId);
				player.sendPacket(new ExAutoSoulShot(shotId, false));
				player.sendPacket(new SystemMessage(SystemMessage.THE_AUTOMATIC_USE_OF_S1_WILL_NOW_BE_CANCELLED).addItemName(shotId));
				return false;
			}
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SPIRITSHOT_FOR_THAT);
			return false;
		}

		Skill skill = player.getAdditionalSSEffect(true, false);
		if(skill == null)
			skill = item.getTemplate().getFirstSkill();
		if(skill == null)
			skill = SHOT_SKILLS.get(grade);

		player.forceUseSkill(skill, player);
		return true;
	}
}
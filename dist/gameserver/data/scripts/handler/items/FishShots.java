package handler.items;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExAutoSoulShot;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.templates.item.WeaponTemplate;
import l2s.gameserver.templates.item.WeaponTemplate.WeaponType;

public class FishShots extends ScriptItemHandler
{
	private static final TIntObjectMap<Skill> SHOT_SKILLS = new TIntObjectHashMap<Skill>();
	static
	{
		SHOT_SKILLS.put(0, SkillHolder.getInstance().getSkill(2181, 1)); // None Grade
		//SHOT_SKILLS.put(6, SkillHolder.getInstance().getSkill(, 1)); // R Grade
	};

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;

		Player player = (Player) playable;

		// spiritshot is already active
		if(player.getChargedFishshotPower() > 0)
			return false;

		int shotId = item.getItemId();
		boolean isAutoSoulShot = false;

		if(player.getAutoSoulShot().contains(shotId))
			isAutoSoulShot = true;

		WeaponTemplate weaponItem = player.getActiveWeaponTemplate();

		if(player.getActiveWeaponInstance() == null || weaponItem.getItemType() != WeaponType.ROD)
		{
			if(!isAutoSoulShot)
				player.sendPacket(SystemMsg.CANNOT_USE_SOULSHOTS);
			return false;
		}

		if(item.getCount() < 1)
		{
			if(isAutoSoulShot)
			{
				player.removeAutoSoulShot(shotId);
				player.sendPacket(new ExAutoSoulShot(shotId, false), new SystemMessage(SystemMessage.THE_AUTOMATIC_USE_OF_S1_WILL_NOW_BE_CANCELLED).addString(item.getName()));
				return false;
			}
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SPIRITSHOT_FOR_THAT);
			return false;
		}

		int grade = weaponItem.getGrade().extOrdinal();

		if(grade == 0 && shotId != 45497)
		{
			if(isAutoSoulShot)
				return false;
			player.sendPacket(SystemMsg.THAT_IS_THE_WRONG_GRADE_OF_SOULSHOT_FOR_THAT_FISHING_POLE);
			return false;
		}

		if(player.getInventory().destroyItem(item, 1L))
		{
			Skill skill = item.getTemplate().getFirstSkill();
			if(skill == null)
				skill = SHOT_SKILLS.get(grade);

			player.forceUseSkill(skill, player);
		}
		return true;
	}
}

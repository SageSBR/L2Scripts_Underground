package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.data.xml.holder.PetDataHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.World;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.pet.PetData;

public class PetSummon extends Skill
{
	public PetSummon(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		Player player = activeChar.getPlayer();
		if(player == null)
			return false;

		if(player.getPetControlItem() == null)
			return false;

		PetData petTemplate = PetDataHolder.getInstance().getTemplateByItemId(player.getPetControlItem().getItemId());
		if(petTemplate == null)
			return false;

		if(player.isInStoreMode())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_A_PRIVATE_STORE);
			return false;
		}

		if(player.isInCombat())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_SUMMON_DURING_COMBAT);
			return false;
		}

		if(player.isProcessingRequest())
		{
			player.sendPacket(SystemMsg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
			return false;
		}

		if(player.isMounted() || player.getPet() != null)
		{
			player.sendPacket(SystemMsg.YOU_ALREADY_HAVE_A_PET);
			return false;
		}

		if(player.isInBoat())
		{
			player.sendPacket(SystemMsg.YOU_MAY_NOT_CALL_FORTH_A_PET_OR_SUMMONED_CREATURE_FROM_THIS_LOCATION);
			return false;
		}

		if(player.isInFlyingTransform())
			return false;

		if(player.isInOlympiadMode())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_USE_THAT_ITEM_IN_A_GRAND_OLYMPIAD_MATCH);
			return false;
		}

		if(player.isCursedWeaponEquipped())
		{
			player.sendPacket(SystemMsg.YOU_MAY_NOT_USE_MULTIPLE_PETS_OR_SERVITORS_AT_THE_SAME_TIME);
			return false;
		}

		if(player.isInFightClub())
		{
			player.sendMessage("You cannot use that item in Fight Club!");
			return false;
		}

		if(target.isPlayable() && target.getPlayer().isInFightClub())
		{
			player.sendMessage("You cannot use that item while target is on Fight Club!");
			return false;
		}
		
		for(GameObject o : World.getAroundObjects(player, 120, 200))
			if(o.isDoor())
			{
				player.sendPacket(SystemMsg.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION);
				return false;
			}

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void onEndCast(Creature activeChar, List<Creature> targets)
	{
		super.onEndCast(activeChar, targets);

		if(!activeChar.isPlayer())
			return;

		activeChar.getPlayer().summonPet();
	}
}
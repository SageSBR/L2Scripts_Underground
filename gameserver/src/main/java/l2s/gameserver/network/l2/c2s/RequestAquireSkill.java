package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.pledge.SubUnit;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.utils.SkillUtils;

public class RequestAquireSkill extends L2GameClientPacket
{
	private AcquireType _type;
	private int _id, _level, _subUnit;

	@Override
	protected void readImpl()
	{
		_id = readD();
		_level = readD();
		_type = AcquireType.getById(readD());
		if(_type == AcquireType.SUB_UNIT)
			_subUnit = readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null || player.isTransformed() || _type == null)
			return;

		NpcInstance trainer = player.getLastNpc();
		if((trainer != null && player.getDistance(trainer.getX(), trainer.getY()) > Creature.INTERACTION_DISTANCE) && !player.isGM())
			trainer = null;

		Skill skill = SkillHolder.getInstance().getSkill(_id, _level);
		if(skill == null)
			return;

		if(!SkillAcquireHolder.getInstance().isSkillPossible(player, skill, _type))
			return;

		SkillLearn skillLearn = SkillAcquireHolder.getInstance().getSkillLearn(player, _id, _level, _type);
		if(skillLearn == null)
			return;

		if(skillLearn.getMinLevel() > player.getLevel())
			return;

		if(skillLearn.getDualClassMinLvl() > player.getDualClassLevel())
			return;

		if(!checkSpellbook(player, skillLearn))
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_NECESSARY_MATERIALS_OR_PREREQUISITES_TO_LEARN_THIS_SKILL);
			return;
		}

		switch(_type)
		{
			case NORMAL:
				learnSimpleNextLevel(player, skillLearn, skill, true);
				break;
			case TRANSFORMATION:
				if(trainer != null)
				{
					learnSimpleNextLevel(player, skillLearn, skill, false);
					trainer.showTransformationSkillList(player, AcquireType.TRANSFORMATION);
				}
				break;
			case COLLECTION:
				if(trainer != null)
				{
					learnSimpleNextLevel(player, skillLearn, skill, false);
					NpcInstance.showCollectionSkillList(player);
				}
				break;
			case TRANSFER_CARDINAL:
			case TRANSFER_EVA_SAINTS:
			case TRANSFER_SHILLIEN_SAINTS:
				if(trainer != null)
				{
					learnSimple(player, skillLearn, skill, false);
					trainer.showTransferSkillList(player);
				}
				break;
			case FISHING:
				if(trainer != null)
				{
					learnSimpleNextLevel(player, skillLearn, skill, false);
					NpcInstance.showFishingSkillList(player);
				}
				break;
			case CLAN:
				if(trainer != null)
					learnClanSkill(player, skillLearn, trainer, skill);
				break;
			case SUB_UNIT:
				if(trainer != null)
					learnSubUnitSkill(player, skillLearn, trainer, skill, _subUnit);
				break;
			case CERTIFICATION:
			case DUAL_CERTIFICATION:
				if(trainer != null)
				{
					if(!player.getActiveSubClass().isBase())
					{
						player.sendPacket(SystemMsg.THIS_SKILL_CANNOT_BE_LEARNED_WHILE_IN_THE_SUBCLASS_STATE);
						return;
					}
					learnSimpleNextLevel(player, skillLearn, skill, false);
					trainer.showTransformationSkillList(player, _type);
				}
				break;
			case CHAOS:
			case DUAL_CHAOS:
				if(trainer != null)
				{
					learnSimpleNextLevel(player, skillLearn, skill, false);
					NpcInstance.showChaosSkillList(player);
				}
				break;
			case ALCHEMY:
				if(trainer != null)
				{
					learnAlchemyNextLevel(player, skillLearn, skill);
					NpcInstance.showAlchemySkillList(player);
				}
				break;
		}
	}

	/**
	 * Изучение следующего возможного уровня скилла
	 */
	private static void learnSimpleNextLevel(Player player, SkillLearn skillLearn, Skill skill, boolean normal)
	{
		final int skillLevel = player.getSkillLevel(skillLearn.getId(), 0);
		if(SkillUtils.getSkillLevelFromMask(skillLevel) != skillLearn.getLevel() - 1)
			return;

		learnSimple(player, skillLearn, skill, normal);
	}

	private static void learnSimple(Player player, SkillLearn skillLearn, Skill skill, boolean normal)
	{
		if(player.getSp() < skillLearn.getCost())
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_LEARN_THIS_SKILL);
			return;
		}

		if(skillLearn.getItemId() > 0)
			if(!player.consumeItem(skillLearn.getItemId(), skillLearn.getItemCount(), true))
				return;

		player.sendPacket(new SystemMessagePacket(SystemMsg.YOU_HAVE_EARNED_S1_SKILL).addSkillName(skill.getId(), skill.getLevel()));

		player.setSp(player.getSp() - skillLearn.getCost());
		player.addSkill(skill, true);

		if(normal)
			player.rewardSkills(false);

		player.sendUserInfo();
		player.updateStats();

		player.sendSkillList(skill.getId());

		player.updateSkillShortcuts(skill.getId(), skill.getLevel());
	}

	private static void learnClanSkill(Player player, SkillLearn skillLearn, NpcInstance trainer, Skill skill)
	{
		if(!player.isClanLeader())
		{
			player.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
			return;
		}

		Clan clan = player.getClan();
		final int skillLevel = clan.getSkillLevel(skillLearn.getId(), 0);
		if(skillLevel != skillLearn.getLevel() - 1) // можно выучить только следующий уровень
			return;
		if(clan.getReputationScore() < skillLearn.getCost())
		{
			player.sendPacket(SystemMsg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
			return;
		}

		if(skillLearn.getItemId() > 0)
			if(!player.consumeItem(skillLearn.getItemId(), skillLearn.getItemCount(), true))
				return;

		clan.incReputation(-skillLearn.getCost(), false, "AquireSkill: " + skillLearn.getId() + ", lvl " + skillLearn.getLevel());
		clan.addSkill(skill, true);
		clan.broadcastToOnlineMembers(new SystemMessagePacket(SystemMsg.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED).addSkillName(skill));

		NpcInstance.showClanSkillList(player);
	}

	private static void learnSubUnitSkill(Player player, SkillLearn skillLearn, NpcInstance trainer, Skill skill, int id)
	{
		Clan clan = player.getClan();
		if(clan == null)
			return;
		SubUnit sub = clan.getSubUnit(id);
		if(sub == null)
			return;

		if((player.getClanPrivileges() & Clan.CP_CL_TROOPS_FAME) != Clan.CP_CL_TROOPS_FAME)
		{
			player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}

		int lvl = sub.getSkillLevel(skillLearn.getId(), 0);
		if(lvl >= skillLearn.getLevel())
		{
			player.sendPacket(SystemMsg.THIS_SQUAD_SKILL_HAS_ALREADY_BEEN_ACQUIRED);
			return;
		}

		if(lvl != (skillLearn.getLevel() - 1))
		{
			player.sendPacket(SystemMsg.THE_PREVIOUS_LEVEL_SKILL_HAS_NOT_BEEN_LEARNED);
			return;
		}

		if(clan.getReputationScore() < skillLearn.getCost())
		{
			player.sendPacket(SystemMsg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
			return;
		}

		if(skillLearn.getItemId() > 0)
			if(!player.consumeItem(skillLearn.getItemId(), skillLearn.getItemCount(), true))
				return;

		clan.incReputation(-skillLearn.getCost(), false, "AquireSkill2: " + skillLearn.getId() + ", lvl " + skillLearn.getLevel());
		sub.addSkill(skill, true);
		player.sendPacket(new SystemMessagePacket(SystemMsg.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED).addSkillName(skill));

		if(trainer != null)
			NpcInstance.showSubUnitSkillList(player);
	}

	private static void learnAlchemyNextLevel(Player player, SkillLearn skillLearn, Skill skill)
	{
		final int skillLevel = player.getAlchemySkillLevel(skillLearn.getId(), 0);
		if(skillLevel != skillLearn.getLevel() - 1)
			return;

		if(player.getSp() < skillLearn.getCost())
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_LEARN_THIS_SKILL);
			return;
		}

		if(skillLearn.getItemId() > 0)
		{
			if(!player.consumeItem(skillLearn.getItemId(), skillLearn.getItemCount(), true))
				return;
		}

		player.sendPacket(new SystemMessagePacket(SystemMsg.YOU_HAVE_EARNED_S1_SKILL).addSkillName(skill.getId(), skill.getLevel()));

		player.setSp(player.getSp() - skillLearn.getCost());
		player.addAlchemySkill(skill, true);

		player.sendAlchemySkillList();
	}

	private static boolean checkSpellbook(Player player, SkillLearn skillLearn)
	{
		if(Config.ALT_DISABLE_SPELLBOOKS)
			return true;

		if(skillLearn.getItemId() == 0)
			return true;

		return player.getInventory().getCountOf(skillLearn.getItemId()) >= skillLearn.getItemCount();
	}
}
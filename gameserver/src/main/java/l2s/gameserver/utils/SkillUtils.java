package l2s.gameserver.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Skill.EnchantType;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.base.AcquireType;

/**
 * @author Bonux
**/
public final class SkillUtils
{
	public static int generateSkillHashCode(int id, int level)
	{
		return id * 1000 + level;
	}

	public static int getSubSkillLevel(int enchantType, int enchantLevel)
	{
		return enchantType * 1000 + enchantLevel;
	}

	public static int getSkillLevelMask(int skillLevel, int subSkillLevel)
	{
		return skillLevel | (subSkillLevel << 16);
	}

	public static boolean isEnchantedSkill(int level)
	{
		return getSkillEnchantLevel(level) > 0;
	}

	public static int getSkillEnchantType(int level)
	{
		final int subSkillLevel = getSubSkillLevelFromMask(level);
		return subSkillLevel / 1000;
	}

	public static int getSkillEnchantLevel(int level)
	{
		final int subSkillLevel = getSubSkillLevelFromMask(level);
		if(subSkillLevel > 1000)
			return subSkillLevel % 1000;
		return 0;
	}

	public static int getSkillLevelFromMask(int skillLevelMask)
	{	
		final int mask = 0b1111111111111111;
		return mask & skillLevelMask;
	}

	public static int getSubSkillLevelFromMask(int skillLevelMask)
	{	
		final int mask = 0b1111111111111111;
		return mask & skillLevelMask >>> 16;
	}

	public static boolean checkSkill(Player player, Skill skill)
	{
		if(!Config.ALT_REMOVE_SKILLS_ON_DELEVEL)
			return false;

		SkillLearn learn = SkillAcquireHolder.getInstance().getSkillLearn(player, skill.getId(), skill.getLevelWithoutEnchant(), AcquireType.NORMAL);
		if(learn == null)
			return false;

		final int subSkillLevel = getSubSkillLevelFromMask(skill.getLevel());

		boolean update = false;

		int lvlDiff = learn.isFreeAutoGet() ? 1 : 4;
		if(learn.getMinLevel() >= (player.getLevel() + lvlDiff) || learn.getDualClassMinLvl() >= (player.getDualClassLevel() + lvlDiff))
		{
			player.removeSkill(skill, true);

			// если у нас низкий лвл для скила, то заточка обнуляется 100%
			// и ищем от большего к меньшему подходящий лвл для скила
			for(int i = skill.getLevelWithoutEnchant() - 1; i != 0; i--)
			{
				SkillLearn learn2 = SkillAcquireHolder.getInstance().getSkillLearn(player, skill.getId(), i, AcquireType.NORMAL);
				if(learn2 == null)
					continue;

				int lvlDiff2 = learn2.isFreeAutoGet() ? 1 : 4;
				if(learn2.getMinLevel() >= (player.getLevel() + lvlDiff2) || learn2.getDualClassMinLvl() >= (player.getDualClassLevel() + lvlDiff2))
					continue;

				Skill newSkill = SkillHolder.getInstance().getSkill(skill.getId(), getSkillLevelMask(i, subSkillLevel));
				if(newSkill == null)
					newSkill = SkillHolder.getInstance().getSkill(skill.getId(), i);

				if(newSkill != null)
				{
					player.addSkill(newSkill, true);
					break;
				}
			}
			update = true;
		}

		if(player.isTransformed())
		{
			learn = player.getTransform().getAdditionalSkill(skill.getId(), skill.getLevel());
			if(learn == null)
				return false;

			if(learn.getMinLevel() >= player.getLevel() + 1)
			{
				player.removeTransformSkill(skill);
				player.removeSkill(skill, false);

				for(int i = skill.getLevelWithoutEnchant() - 1; i != 0; i--)
				{
					SkillLearn learn2 = player.getTransform().getAdditionalSkill(skill.getId(), i);
					if(learn2 == null)
						continue;

					if(learn2.getMinLevel() >= player.getLevel() + 1)
						continue;

					Skill newSkill = SkillHolder.getInstance().getSkill(skill.getId(), getSkillLevelMask(i, subSkillLevel));
					if(newSkill == null)
						newSkill = SkillHolder.getInstance().getSkill(skill.getId(), i);

					if(newSkill != null)
					{
						player.addTransformSkill(newSkill);
						player.addSkill(newSkill, false);
						break;
					}
				}
				update = true;
			}
		}
		return update;
	}

	public static List<Skill> getSkillsForChangeEnchant(int id, int level)
	{
		final int enchantLevel = getSkillEnchantLevel(level);
		if(enchantLevel <= 0)
			return Collections.emptyList();

		final int skillLevel = getSkillLevelFromMask(level);
		final int enchantType = getSkillEnchantType(level);
		final List<Skill> skills = new ArrayList<Skill>();
		for(Skill skill : SkillHolder.getInstance().getSkills(id))
		{
			if(skill.isEnchantable() && enchantType != getSkillEnchantType(skill.getLevel()) && skillLevel == skill.getLevelWithoutEnchant() && getSkillEnchantLevel(skill.getLevel()) == enchantLevel)
				skills.add(skill);
		}
		return skills;
	}

	public static List<Skill> getSkillsForFirstEnchant(int id, int level)
	{
		final int skillLevel = getSkillLevelFromMask(level);

		List<Skill> skills = new ArrayList<Skill>();
		for(Skill skill : SkillHolder.getInstance().getSkills(id))
		{
			if(skill.isEnchantable() && skillLevel == skill.getLevelWithoutEnchant() && getSkillEnchantLevel(skill.getLevel()) == 1)
				skills.add(skill);
		}
		return skills;
	}

	public static List<Skill> getAvaiableEnchantSkills(Player player)
	{
		List<Skill> enchants = new ArrayList<Skill>();
		for(Skill skill : player.getAllSkills())
		{
			if(skill.isEnchantable())
			{
				if(isEnchantedSkill(skill.getLevel()))
				{
					int skillLevel = getSkillLevelFromMask(skill.getLevel());
					int subSkillLevel = getSubSkillLevelFromMask(skill.getLevel()) + 1;
					int skillLevelMask = getSkillLevelMask(skillLevel, subSkillLevel);
					Skill enchant = SkillHolder.getInstance().getSkill(skill.getId(), skillLevelMask);
					if(enchant != null)
						enchants.add(enchant);
				}
				else
				{
					for(Skill enchant : SkillHolder.getInstance().getSkills(skill.getId()))
					{
						if(getSkillEnchantLevel(enchant.getLevel()) == 1)
							enchants.add(enchant);
					}
				}
			}
		}
		return enchants;
	}

	public static boolean isSkillEnchantAvailable(Player player, Skill skill)
	{
		if(player.isTransformed())
			return false;

		return skill.isEnchantable();
	}

	public static boolean isAvailableSkillEnchant(Player player, Skill skill, EnchantType type)
	{
		Skill baseSkill = player.getKnownSkill(skill.getId());
		if(baseSkill == null)
			return false;

		if(!baseSkill.isEnchantable())
			return false;

		if(baseSkill.getLevelWithoutEnchant() != baseSkill.getLevelWithoutEnchant())
			return false;

		if(type == EnchantType.NORMAL || type == EnchantType.BLESSED || type == EnchantType.IMMORTAL)
		{
			if(getSkillEnchantLevel(skill.getLevel()) == (getSkillEnchantLevel(baseSkill.getLevel()) + 1))
				return true;
		}
		else if(type == EnchantType.UNTRAIN)
		{
			if(isEnchantedSkill(baseSkill.getLevel()) && (getSkillEnchantLevel(baseSkill.getLevel()) - 1) == getSkillEnchantLevel(skill.getLevel()))
				return true;
		}
		else if(type == EnchantType.CHANGE)
		{
			if(isEnchantedSkill(baseSkill.getLevel()) && isEnchantedSkill(skill.getLevel()))
			{
				if(getSkillEnchantLevel(baseSkill.getLevel()) == getSkillEnchantLevel(skill.getLevel()))
					return true;
			}
		}

		return false;
	}
}

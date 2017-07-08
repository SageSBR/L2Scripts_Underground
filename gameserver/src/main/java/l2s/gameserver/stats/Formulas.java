package l2s.gameserver.stats;

import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.GameTimeController;
import l2s.gameserver.data.xml.holder.HitCondBonusHolder;
import l2s.gameserver.data.xml.holder.KarmaIncreaseDataHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Skill.SkillType;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.model.base.BaseStats;
import l2s.gameserver.model.base.Element;
import l2s.gameserver.model.base.HitCondBonusType;
import l2s.gameserver.model.base.SkillTrait;
import l2s.gameserver.model.instances.ReflectionBossInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.skills.EffectUseType;
import l2s.gameserver.templates.item.WeaponTemplate;
import l2s.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2s.gameserver.templates.skill.EffectTemplate;
import l2s.gameserver.utils.PositionUtils;

public class Formulas
{
	private static final double CRAFTING_MASTERY_CHANCE = 1.5; // TODO: Check.

	public static class AttackInfo
	{
		public double damage = 0;
		public double defence = 0;
		public double crit_static = 0;
		public boolean crit = false;
		public boolean shld = false;
		public boolean miss = false;
		public boolean blow = false;
	}

	/**
	 * Для простых ударов
	 * patk = patk
	 * При крите простым ударом:
	 * patk = patk * (1 + crit_damage_rcpt) * crit_damage_mod + crit_damage_static
	 * Для blow скиллов
	 * TODO
	 * Для скилловых критов, повреждения просто удваиваются, бафы не влияют (кроме blow, для них выше)
	 * patk = (1 + crit_damage_rcpt) * (patk + skill_power)
	 * Для обычных атак
	 * damage = patk * ss_bonus * 70 / pdef
	 */
	public static AttackInfo calcPhysDam(Creature attacker, Creature target, Skill skill, boolean dual, boolean blow, boolean useShot, boolean onCrit)
	{
		return calcPhysDam(attacker, target, skill, skill == null ? 0. : skill.getPower(target), dual, blow, useShot, onCrit);
	}

	public static AttackInfo calcPhysDam(Creature attacker, Creature target, Skill skill, double power, boolean dual, boolean blow, boolean useShot, boolean onCrit)
	{
		AttackInfo info = new AttackInfo();

		info.damage = attacker.getPAtk(target);
		info.defence = target.getPDef(attacker);
		info.blow = blow;
		info.crit_static = attacker.calcStat(Stats.P_CRITICAL_DAMAGE_STATIC, target, skill);
		info.crit = calcPCrit(attacker, target, skill, info.blow);
		info.shld = (skill == null || !skill.getShieldIgnore()) && calcShldUse(attacker, target);
		info.miss = false;
		boolean isPvP = attacker.isPlayable() && target.isPlayable();
		boolean isPvE = attacker.isPlayable() && target.isNpc();

		if(info.shld)
		{
			double shldDef = target.getShldDef();
			if(skill != null && skill.getShieldIgnorePercent() > 0)
				shldDef -= shldDef * skill.getShieldIgnorePercent() / 100.;
			info.defence += shldDef;
		}

		if(skill != null && skill.getDefenceIgnorePercent() > 0)
			info.defence *= (1. - (skill.getDefenceIgnorePercent() / 100.));

		info.defence = Math.max(info.defence, 1);

		if(skill != null)
		{
			// @Rivelia. Pinpoint Shot ignores 30% of target's P. Def. (hardcoded for now).
			//if (skill.getId() == 10763)
			//	info.defence *= 0.7;

			// если скилл не имеет своей силы дальше идти бесполезно, можно сразу вернуть дамаг от летала
			if(power == 0)
				return new AttackInfo();	// @Rivelia. Send empty AttackInfo so it does not show up in system messages.

			if(info.damage > 0 && skill.canBeEvaded() && Rnd.chance(target.calcStat(Stats.P_SKILL_EVASION, 0, attacker, skill)))
			{
				// @Rivelia. info.miss makes the Damage Text "Evaded" appear.
				info.miss = true;
				info.damage = 0;
				attacker.sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_EVADED_C2S_ATTACK).addName(target).addName(attacker));
				target.sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_EVADED_C2S_ATTACK).addName(target).addName(attacker));
				return info;
			}

			if(info.blow && !skill.isBehind() && useShot) // Для обычных blow не влияет на power
				info.damage *= ((100 + attacker.getChargedSoulshotPower()) / 100.);

			// @Rivelia. Chain Blow adds 20% to P. Skill Power if enemy is on Bleed effect (hardcoded for now).
			double skillPowerMod = 1.;
			if (skill.getId() == 10510)
			{
				for(Effect effect : target.getEffectList().getEffects())
				{
					if(effect.getAbnormalType() == AbnormalType.bleeding)
					{
						skillPowerMod = 1.2;
						break;
					}
				}
			}

			double powerWithBonuses = Math.max(0., attacker.calcStat(Stats.P_SKILL_POWER, power * skillPowerMod));
			if(skill.getNumCharges() > 0)
				powerWithBonuses = Math.max(0., attacker.calcStat(Stats.CHARGED_P_SKILL_POWER, powerWithBonuses));

			info.damage += powerWithBonuses;

			if(info.blow && skill.isBehind() && useShot) // Для backstab влияет на power, но меньше множитель
				info.damage *= ((100 + (attacker.getChargedSoulshotPower() / 2.)) / 100.);

			//Заряжаемые скилы имеют постоянный урон
			if(!skill.isChargeBoost())
				info.damage *= 1 + (Rnd.get() * attacker.getRandomDamage() * 2 - attacker.getRandomDamage()) / 100;

			if(info.blow)
			{
				// @Rivelia. Since Lindvior, Critical Damage Receptive only applies if the the critical damage receptive of the target is > 1.
				double critDmg = info.damage;
				// @Rivelia. Add 1 to the modifier so it will not become x1 (= no crit change) when P. Critical Damage = 100.
				critDmg *= Math.pow(attacker.getPCriticalDmg(target, skill), 0.66);
				critDmg += 6.1 * info.crit_static;
				critDmg -= info.damage;
				double tempDamage = target.calcStat(Stats.P_CRIT_DAMAGE_RECEPTIVE, critDmg, attacker, skill);
				critDmg = Math.min(tempDamage, critDmg);
				critDmg = Math.max(0, critDmg);
				info.damage += critDmg;
			}

			if(skill.isChargeBoost())
			{
				int force = attacker.getIncreasedForce();
				// @Rivelia. Jump Attack hardcoded to 5 momentum max. Others are set to 3.
				if (force > 5 && skill.getId() == 10269)
					force = 5;
				else if (force > 3)
					force = 3;

				// @Rivelia. Momentum increases damage up to 30% if 3 forces used, so 10% per momentum.
				info.damage *= 1 + 0.1 * force;
			}
			else if(skill.isSoulBoost())
				info.damage *= 1.0 + 0.06 * Math.min(attacker.getConsumedSouls(), 5);

			// Gracia Physical Skill Damage Bonus
			info.damage *= 1.10113;

			if(info.crit)
			{
				// @Rivelia. Damage is always 2x during a skill crit, no matter what.
				double skillCritDam = attacker.calcStat(Stats.SKILL_CRIT_DAM_MOD, 1.);
				info.damage *= 2. * skillCritDam; //  * target.calcStat(Stats.P_CRIT_DAMAGE_RECEPTIVE, skillCritDam, attacker, skill); ??

			}
		}
		else
		{
			info.damage *= 1 + (Rnd.get() * attacker.getRandomDamage() * 2 - attacker.getRandomDamage()) / 100;

			if(dual)
				info.damage /= 2.;

			if(info.crit)
			{
				// @Rivelia. Auto attack crits are always reduced by Critical Damage Receptive.
				info.crit_static = attacker.calcStat(Stats.P_CRITICAL_DAMAGE_STATIC, target, null);
				double critDmg = info.damage;
				// @Rivelia. Add 1 to the modifier so it will not become x1 (= no crit change) when P. Critical Damage = 100.
				critDmg *= attacker.getPCriticalDmg(target, null);
				critDmg += info.crit_static;
				critDmg -= info.damage;
				critDmg = target.calcStat(Stats.P_CRIT_DAMAGE_RECEPTIVE, critDmg, attacker, null);
				critDmg = Math.max(0, critDmg);
				info.damage += critDmg;
			}
		}

		if(info.crit)
		{
			// шанс абсорбации души (без анимации) при крите, если Soul Mastery 4го уровня или более
			int chance = attacker.getSkillLevel(Skill.SKILL_SOUL_MASTERY);
			if(chance > 0)
			{
				if(chance >= 21)
					chance = 30;
				else if(chance >= 15)
					chance = 25;
				else if(chance >= 9)
					chance = 20;
				else if(chance >= 4)
					chance = 15;
				if(Rnd.chance(chance))
					attacker.setConsumedSouls(attacker.getConsumedSouls() + 1, null);
			}
		}

		if(attacker.isDistortedSpace())
			info.damage *= 1.2;
		else
		{
			switch(PositionUtils.getDirectionTo(target, attacker))
			{
				case BEHIND:
					info.damage *= 1.2;
					break;
				case SIDE:
					info.damage *= 1.1;
					break;
			}
		}

		if(useShot && !info.blow)
			info.damage *= ((100 + attacker.getChargedSoulshotPower()) / 100.);

		info.damage *= 70. / info.defence;
		info.damage = attacker.calcStat(Stats.INFLICTS_P_DAMAGE_POWER, info.damage, target, skill);
		info.damage = target.calcStat(Stats.RECEIVE_P_DAMAGE_POWER, info.damage, attacker, skill);

		if(info.shld)
		{
			if(Rnd.chance(Config.EXCELLENT_SHIELD_BLOCK_CHANCE))
			{
				info.damage = Config.EXCELLENT_SHIELD_BLOCK_RECEIVED_DAMAGE;
				target.sendPacket(SystemMsg.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
				return info;
			}
			else
				target.sendPacket(SystemMsg.YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED);
		}

		if(isPvP)
		{
			if(skill == null)
			{
				info.damage *= attacker.calcStat(Stats.PVP_PHYS_DMG_BONUS, 1);
				info.damage /= target.calcStat(Stats.PVP_PHYS_DEFENCE_BONUS, 1);
			}
			else
			{
				info.damage *= attacker.calcStat(Stats.PVP_PHYS_SKILL_DMG_BONUS, 1);
				info.damage /= target.calcStat(Stats.PVP_PHYS_SKILL_DEFENCE_BONUS, 1);
			}
		}
		else if(isPvE)
		{
			if(skill == null)
			{
				info.damage *= attacker.calcStat(Stats.PVE_PHYS_DMG_BONUS, 1);
				info.damage /= target.calcStat(Stats.PVE_PHYS_DEFENCE_BONUS, 1);
				if(target.isRaid()) {
					info.damage *= attacker.calcStat(Stats.P_PVE_PHYSICAL_ATTACK_DMG_BONUS_BOSS, 1);
				}
			}
			else
			{
				info.damage *= attacker.calcStat(Stats.PVE_PHYS_SKILL_DMG_BONUS, 1);
				info.damage /= target.calcStat(Stats.PVE_PHYS_SKILL_DEFENCE_BONUS, 1);
				if(target.isRaid()) {
					info.damage *= attacker.calcStat(Stats.P_PVE_PHYSICAL_SKILL_DMG_BONUS_BOSS, 1);
				}
			}
		}
		if(info.crit)
			info.damage = info.damage * getPCritDamageMode(attacker, skill == null);
		if(info.blow)
			info.damage = info.damage * Config.ALT_BLOW_DAMAGE_MOD;
		if(!info.crit && !info.blow)
			info.damage = info.damage * getPDamModifier(attacker);

		// Тут проверяем только если skill != null, т.к. L2Character.onHitTimer не обсчитывает дамаг.
		if(skill != null)
		{
			if(info.damage > 0 && skill.isDeathlink())
				info.damage *= 1.8 * (1.0 - attacker.getCurrentHpRatio());

			if(onCrit && !calcBlow(attacker, target, skill))
			{
				info.miss = true;
				info.damage = 0;
				attacker.sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_EVADED_C2S_ATTACK).addName(target).addName(attacker));
				target.sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_EVADED_C2S_ATTACK).addName(target).addName(attacker));
				return info;
			}

			if(info.damage > 0)
			{
				WeaponTemplate weaponItem = attacker.getActiveWeaponTemplate();
				if(skill.getIncreaseOnPole() > 0. && weaponItem != null && weaponItem.getItemType() == WeaponType.POLE)
					info.damage *= skill.getIncreaseOnPole();
				if(skill.getDecreaseOnNoPole() > 0. && weaponItem != null && weaponItem.getItemType() != WeaponType.POLE)
					info.damage *= skill.getDecreaseOnNoPole();
			}
			if(calcCastBreak(target, info.crit))
				target.abortCast(false, true);
		}

		if(target.isStunned() && calcStunBreak(info.crit, (skill != null), false) && info.damage > 0)
			target.getEffectList().stopEffects(EffectType.Stun);

		return info;
	}

	public static double calcLethalDamage(Creature attacker, Creature target, Skill skill)
	{
		if(skill == null)
			return 0.;

		if(target.isLethalImmune())
			return 0.;

		final double deathRcpt = 0.01 * target.calcStat(Stats.DEATH_VULNERABILITY, attacker, skill);
		final double lethal1Chance = skill.getLethal1(attacker) * deathRcpt;
		final double lethal2Chance = skill.getLethal2(attacker) * deathRcpt;

		double damage = 0.;

		if(Rnd.chance(lethal2Chance))
		{
			if(target.isPlayer())
			{
				damage = target.getCurrentHp() + target.getCurrentCp() - 1.1; // Oly\Duel хак установки не точно 1 ХП, а чуть больше для предотвращения псевдосмерти
				target.sendPacket(SystemMsg.LETHAL_STRIKE);
			}
			else
				damage = target.getCurrentHp() - 1;
			attacker.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
		}
		else if(Rnd.chance(lethal1Chance))
		{
			if(target.isPlayer())
			{
				damage = target.getCurrentCp();
				target.sendPacket(SystemMsg.YOUR_CP_WAS_DRAINED_BECAUSE_YOU_WERE_HIT_WITH_A_CP_SIPHON_SKILL);
			}
			else
				damage = target.getCurrentHp() / 2;
			attacker.sendPacket(SystemMsg.CP_SIPHON);
		}
		return damage;
	}

	private static double getMSimpleDamageMode(Creature attacker) //ONLY for 4-th proffs
	{
		if(!attacker.isPlayer())
			return Config.ALT_M_SIMPLE_DAMAGE_MOD;
		switch(attacker.getPlayer().getActiveClassId())
		{
			case 139:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_SIGEL;
			case 140:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_TIR_WARRIOR;
			case 141:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_OTHEL_ROGUE;
			case 142:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_YR_ARCHER;
			case 143:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_FEO_WIZZARD;
			case 144:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_ISS_ENCHANTER;
			case 145:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_WYN_SUMMONER;
			case 146:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_EOL_HEALER;
				//new
			case 148:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_SIGEL_PHOENIX_KNIGHT;
			case 149:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_SIGEL_HELL_KNIGHT;
			case 150:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_SIGEL_EVAS_TEMPLAR;
			case 151:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_SIGEL_SHILLIEN_TEMPLAR;
			case 152:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_TYR_DUELIST;
			case 153:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_TYR_DREADNOUGHT;
			case 154:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_TYR_TITAN;
			case 155:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_TYR_GRAND_KHAVATARI;
			case 156:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_TYR_MAESTRO;
			case 157:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_TYR_DOOMBRINGER;
			case 158:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_OTHELL_ADVENTURER;
			case 159:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_OTHELL_WIND_RIDER;
			case 160:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_OTHELL_GHOST_HUNTER;
			case 161:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_OTHELL_FORTUNE_SEEKER;
			case 162:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_YR_SAGITTARIUS;
			case 163:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_YR_MOONLIGHT_SENTINEL;	
			case 164:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_YR_GHOST_SENTINEL;	
			case 165:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_YR_TRICKSTER;	
			case 166:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_FEOH_ARCHMAGE;	
			case 167:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_FEOH_SOULTAKER;	
			case 168:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_FEOH_MYSTIC_MUSE;	
			case 169:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_FEOH_STORM_SCREAMER;	
			case 170:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_FEOH_SOUL_HOUND;	
			case 171:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_ISS_HIEROPHANT;	
			case 172:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_ISS_SWORD_MUSE;	
			case 173:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_ISS_SPECTRAL_DANCER;	
			case 174:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_ISS_DOMINATOR;	
			case 175:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_ISS_DOOMCRYER;	
			case 176:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_WYNN_ARCANA_LORD;	
			case 177:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_WYNN_ELEMENTAL_MASTER;	
			case 178:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_WYNN_SPECTRAL_MASTER;	
			case 179:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_AEORE_CARDINAL;	
			case 180:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_AEORE_EVAS_SAINT;
			case 181:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_AEORE_SHILLIEN_SAINT;
			case 188:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_EVISCERATOR;
			case 189:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD_SAHYAS_SEER;
			default:
				return Config.ALT_M_SIMPLE_DAMAGE_MOD;
		}
	}

	public static double getMCritDamageMode(Creature attacker) //ONLY for 4-th proffs
	{
		if(!attacker.isPlayer())
			return Config.ALT_M_CRIT_DAMAGE_MOD;
		switch(attacker.getPlayer().getActiveClassId())
		{
			case 139:
				return Config.ALT_M_CRIT_DAMAGE_MOD_SIGEL;
			case 140:
				return Config.ALT_M_CRIT_DAMAGE_MOD_TIR_WARRIOR;
			case 141:
				return Config.ALT_M_CRIT_DAMAGE_MOD_OTHEL_ROGUE;
			case 142:
				return Config.ALT_M_CRIT_DAMAGE_MOD_YR_ARCHER;
			case 143:
				return Config.ALT_M_CRIT_DAMAGE_MOD_FEO_WIZZARD;
			case 144:
				return Config.ALT_M_CRIT_DAMAGE_MOD_ISS_ENCHANTER;
			case 145:
				return Config.ALT_M_CRIT_DAMAGE_MOD_WYN_SUMMONER;
			case 146:
				return Config.ALT_M_CRIT_DAMAGE_MOD_EOL_HEALER;
				//new
			case 148:
				return Config.ALT_M_CRIT_DAMAGE_MOD_SIGEL_PHOENIX_KNIGHT;
			case 149:
				return Config.ALT_M_CRIT_DAMAGE_MOD_SIGEL_HELL_KNIGHT;
			case 150:
				return Config.ALT_M_CRIT_DAMAGE_MOD_SIGEL_EVAS_TEMPLAR;
			case 151:
				return Config.ALT_M_CRIT_DAMAGE_MOD_SIGEL_SHILLIEN_TEMPLAR;
			case 152:
				return Config.ALT_M_CRIT_DAMAGE_MOD_TYR_DUELIST;
			case 153:
				return Config.ALT_M_CRIT_DAMAGE_MOD_TYR_DREADNOUGHT;
			case 154:
				return Config.ALT_M_CRIT_DAMAGE_MOD_TYR_TITAN;
			case 155:
				return Config.ALT_M_CRIT_DAMAGE_MOD_TYR_GRAND_KHAVATARI;
			case 156:
				return Config.ALT_M_CRIT_DAMAGE_MOD_TYR_MAESTRO;
			case 157:
				return Config.ALT_M_CRIT_DAMAGE_MOD_TYR_DOOMBRINGER;
			case 158:
				return Config.ALT_M_CRIT_DAMAGE_MOD_OTHELL_ADVENTURER;
			case 159:
				return Config.ALT_M_CRIT_DAMAGE_MOD_OTHELL_WIND_RIDER;
			case 160:
				return Config.ALT_M_CRIT_DAMAGE_MOD_OTHELL_GHOST_HUNTER;
			case 161:
				return Config.ALT_M_CRIT_DAMAGE_MOD_OTHELL_FORTUNE_SEEKER;
			case 162:
				return Config.ALT_M_CRIT_DAMAGE_MOD_YR_SAGITTARIUS;
			case 163:
				return Config.ALT_M_CRIT_DAMAGE_MOD_YR_MOONLIGHT_SENTINEL;	
			case 164:
				return Config.ALT_M_CRIT_DAMAGE_MOD_YR_GHOST_SENTINEL;	
			case 165:
				return Config.ALT_M_CRIT_DAMAGE_MOD_YR_TRICKSTER;	
			case 166:
				return Config.ALT_M_CRIT_DAMAGE_MOD_FEOH_ARCHMAGE;	
			case 167:
				return Config.ALT_M_CRIT_DAMAGE_MOD_FEOH_SOULTAKER;	
			case 168:
				return Config.ALT_M_CRIT_DAMAGE_MOD_FEOH_MYSTIC_MUSE;	
			case 169:
				return Config.ALT_M_CRIT_DAMAGE_MOD_FEOH_STORM_SCREAMER;	
			case 170:
				return Config.ALT_M_CRIT_DAMAGE_MOD_FEOH_SOUL_HOUND;	
			case 171:
				return Config.ALT_M_CRIT_DAMAGE_MOD_ISS_HIEROPHANT;	
			case 172:
				return Config.ALT_M_CRIT_DAMAGE_MOD_ISS_SWORD_MUSE;	
			case 173:
				return Config.ALT_M_CRIT_DAMAGE_MOD_ISS_SPECTRAL_DANCER;	
			case 174:
				return Config.ALT_M_CRIT_DAMAGE_MOD_ISS_DOMINATOR;	
			case 175:
				return Config.ALT_M_CRIT_DAMAGE_MOD_ISS_DOOMCRYER;	
			case 176:
				return Config.ALT_M_CRIT_DAMAGE_MOD_WYNN_ARCANA_LORD;	
			case 177:
				return Config.ALT_M_CRIT_DAMAGE_MOD_WYNN_ELEMENTAL_MASTER;	
			case 178:
				return Config.ALT_M_CRIT_DAMAGE_MOD_WYNN_SPECTRAL_MASTER;	
			case 179:
				return Config.ALT_M_CRIT_DAMAGE_MOD_AEORE_CARDINAL;	
			case 180:
				return Config.ALT_M_CRIT_DAMAGE_MOD_AEORE_EVAS_SAINT;	
			case 181:
				return Config.ALT_M_CRIT_DAMAGE_MOD_AEORE_SHILLIEN_SAINT;
			case 188:
				return Config.ALT_M_CRIT_DAMAGE_MOD_EVISCERATOR;
			case 189:
				return Config.ALT_M_CRIT_DAMAGE_MOD_SAHYAS_SEER;
			default:
				return Config.ALT_M_CRIT_DAMAGE_MOD;
		}
	}

	private static double getPDamModifier(Creature attacker) //ONLY for 4-th proffs
	{
		if(!attacker.isPlayer())
			return Config.ALT_P_DAMAGE_MOD;
		switch(attacker.getPlayer().getActiveClassId())
		{
			case 139:
				return Config.ALT_P_DAMAGE_MOD_SIGEL;
			case 140:
				return Config.ALT_P_DAMAGE_MOD_TIR_WARRIOR;
			case 141:
				return Config.ALT_P_DAMAGE_MOD_OTHEL_ROGUE;
			case 142:
				return Config.ALT_P_DAMAGE_MOD_YR_ARCHER;
			case 143:
				return Config.ALT_P_DAMAGE_MOD_FEO_WIZZARD;
			case 144:
				return Config.ALT_P_DAMAGE_MOD_ISS_ENCHANTER;
			case 145:
				return Config.ALT_P_DAMAGE_MOD_WYN_SUMMONER;
			case 146:
				return Config.ALT_P_DAMAGE_MOD_EOL_HEALER;
				//new
			case 148:
				return Config.ALT_P_DAMAGE_MOD_SIGEL_PHOENIX_KNIGHT;
			case 149:
				return Config.ALT_P_DAMAGE_MOD_SIGEL_HELL_KNIGHT;
			case 150:
				return Config.ALT_P_DAMAGE_MOD_SIGEL_EVAS_TEMPLAR;
			case 151:
				return Config.ALT_P_DAMAGE_MOD_SIGEL_SHILLIEN_TEMPLAR;
			case 152:
				return Config.ALT_P_DAMAGE_MOD_TYR_DUELIST;
			case 153:
				return Config.ALT_P_DAMAGE_MOD_TYR_DREADNOUGHT;
			case 154:
				return Config.ALT_P_DAMAGE_MOD_TYR_TITAN;
			case 155:
				return Config.ALT_P_DAMAGE_MOD_TYR_GRAND_KHAVATARI;
			case 156:
				return Config.ALT_P_DAMAGE_MOD_TYR_MAESTRO;
			case 157:
				return Config.ALT_P_DAMAGE_MOD_TYR_DOOMBRINGER;
			case 158:
				return Config.ALT_P_DAMAGE_MOD_OTHELL_ADVENTURER;
			case 159:
				return Config.ALT_P_DAMAGE_MOD_OTHELL_WIND_RIDER;
			case 160:
				return Config.ALT_P_DAMAGE_MOD_OTHELL_GHOST_HUNTER;
			case 161:
				return Config.ALT_P_DAMAGE_MOD_OTHELL_FORTUNE_SEEKER;
			case 162:
				return Config.ALT_P_DAMAGE_MOD_YR_SAGITTARIUS;
			case 163:
				return Config.ALT_P_DAMAGE_MOD_YR_MOONLIGHT_SENTINEL;	
			case 164:
				return Config.ALT_P_DAMAGE_MOD_YR_GHOST_SENTINEL;	
			case 165:
				return Config.ALT_P_DAMAGE_MOD_YR_TRICKSTER;	
			case 166:
				return Config.ALT_P_DAMAGE_MOD_FEOH_ARCHMAGE;	
			case 167:
				return Config.ALT_P_DAMAGE_MOD_FEOH_SOULTAKER;	
			case 168:
				return Config.ALT_P_DAMAGE_MOD_FEOH_MYSTIC_MUSE;	
			case 169:
				return Config.ALT_P_DAMAGE_MOD_FEOH_STORM_SCREAMER;	
			case 170:
				return Config.ALT_P_DAMAGE_MOD_FEOH_SOUL_HOUND;	
			case 171:
				return Config.ALT_P_DAMAGE_MOD_ISS_HIEROPHANT;	
			case 172:
				return Config.ALT_P_DAMAGE_MOD_ISS_SWORD_MUSE;	
			case 173:
				return Config.ALT_P_DAMAGE_MOD_ISS_SPECTRAL_DANCER;	
			case 174:
				return Config.ALT_P_DAMAGE_MOD_ISS_DOMINATOR;	
			case 175:
				return Config.ALT_P_DAMAGE_MOD_ISS_DOOMCRYER;	
			case 176:
				return Config.ALT_P_DAMAGE_MOD_WYNN_ARCANA_LORD;	
			case 177:
				return Config.ALT_P_DAMAGE_MOD_WYNN_ELEMENTAL_MASTER;	
			case 178:
				return Config.ALT_P_DAMAGE_MOD_WYNN_SPECTRAL_MASTER;	
			case 179:
				return Config.ALT_P_DAMAGE_MOD_AEORE_CARDINAL;	
			case 180:
				return Config.ALT_P_DAMAGE_MOD_AEORE_EVAS_SAINT;
			case 181:
				return Config.ALT_P_DAMAGE_MOD_AEORE_SHILLIEN_SAINT;
			case 188:
				return Config.ALT_P_DAMAGE_MOD_EVISCERATOR;
			case 189:
				return Config.ALT_P_DAMAGE_MOD_SAHYAS_SEER;
			default:
				return Config.ALT_P_DAMAGE_MOD;
		}
	}

	private static double getPCritDamageMode(Creature attacker, boolean notSkill) //ONLY for 4-th proffs
	{
		if(!attacker.isPlayer())
			return Config.ALT_P_CRIT_DAMAGE_MOD;
		switch(attacker.getPlayer().getActiveClassId())
		{
			case 139:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_SIGEL_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_SIGEL;
			case 140:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_TIR_WARRIOR_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_TIR_WARRIOR;
			case 141:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_OTHEL_ROGUE_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_OTHEL_ROGUE;
			case 142:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_YR_ARCHER_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_YR_ARCHER;
			case 143:
				return Config.ALT_P_CRIT_DAMAGE_MOD_FEO_WIZZARD;
			case 144:
				return Config.ALT_P_CRIT_DAMAGE_MOD_ISS_ENCHANTER;
			case 145:
				return Config.ALT_P_CRIT_DAMAGE_MOD_WYN_SUMMONER;
			case 146:
				return Config.ALT_P_CRIT_DAMAGE_MOD_EOL_HEALER;
				//new
			case 148:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_SIGEL_PHOENIX_KNIGHT_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_SIGEL_PHOENIX_KNIGHT;
			case 149:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_SIGEL_HELL_KNIGHT_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_SIGEL_HELL_KNIGHT;
			case 150:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_SIGEL_EVAS_TEMPLAR_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_SIGEL_EVAS_TEMPLAR;
			case 151:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_SIGEL_SHILLIEN_TEMPLAR_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_SIGEL_SHILLIEN_TEMPLAR;
			case 152:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_TYR_DUELIST_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_TYR_DUELIST;
			case 153:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_TYR_DREADNOUGHT_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_TYR_DREADNOUGHT;
			case 154:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_TYR_TITAN_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_TYR_TITAN;
			case 155:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_TYR_GRAND_KHAVATARI_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_TYR_GRAND_KHAVATARI;
			case 156:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_TYR_MAESTRO_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_TYR_MAESTRO;
			case 157:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_TYR_DOOMBRINGER_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_TYR_DOOMBRINGER;
			case 158:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_OTHELL_ADVENTURER_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_OTHELL_ADVENTURER;
			case 159:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_OTHELL_WIND_RIDER_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_OTHELL_WIND_RIDER;
			case 160:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_OTHELL_GHOST_HUNTER_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_OTHELL_GHOST_HUNTER;
			case 161:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_OTHELL_FORTUNE_SEEKER_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_OTHELL_FORTUNE_SEEKER;
			case 162:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_YR_SAGITTARIUS_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_YR_SAGITTARIUS;
			case 163:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_YR_MOONLIGHT_SENTINEL_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_YR_MOONLIGHT_SENTINEL;	
			case 164:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_YR_GHOST_SENTINEL_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_YR_GHOST_SENTINEL;	
			case 165:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_YR_TRICKSTER_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_YR_TRICKSTER;	
			case 166:
				return Config.ALT_P_CRIT_DAMAGE_MOD_FEOH_ARCHMAGE;	
			case 167:
				return Config.ALT_P_CRIT_DAMAGE_MOD_FEOH_SOULTAKER;	
			case 168:
				return Config.ALT_P_CRIT_DAMAGE_MOD_FEOH_MYSTIC_MUSE;	
			case 169:
				return Config.ALT_P_CRIT_DAMAGE_MOD_FEOH_STORM_SCREAMER;	
			case 170:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_FEOH_SOUL_HOUND_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_FEOH_SOUL_HOUND;	
			case 171:
				return Config.ALT_P_CRIT_DAMAGE_MOD_ISS_HIEROPHANT;	
			case 172:
				return Config.ALT_P_CRIT_DAMAGE_MOD_ISS_SWORD_MUSE;	
			case 173:
				return Config.ALT_P_CRIT_DAMAGE_MOD_ISS_SPECTRAL_DANCER;	
			case 174:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_ISS_DOMINATOR_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_ISS_DOMINATOR;	
			case 175:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_ISS_DOOMCRYER_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_ISS_DOOMCRYER;	
			case 176:
				return Config.ALT_P_CRIT_DAMAGE_MOD_WYNN_ARCANA_LORD;	
			case 177:
				return Config.ALT_P_CRIT_DAMAGE_MOD_WYNN_ELEMENTAL_MASTER;	
			case 178:
				return Config.ALT_P_CRIT_DAMAGE_MOD_WYNN_SPECTRAL_MASTER;	
			case 179:
				return Config.ALT_P_CRIT_DAMAGE_MOD_AEORE_CARDINAL;	
			case 180:
				return Config.ALT_P_CRIT_DAMAGE_MOD_AEORE_EVAS_SAINT;	
			case 181:
				return Config.ALT_P_CRIT_DAMAGE_MOD_AEORE_SHILLIEN_SAINT;
			case 188:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_EVISCERATOR_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_EVISCERATOR;
			case 189:
				return notSkill ? Config.ALT_P_CRIT_DAMAGE_MOD_SAHYAS_SEER_FIZ : Config.ALT_P_CRIT_DAMAGE_MOD_SAHYAS_SEER;
			default:
				return Config.ALT_P_CRIT_DAMAGE_MOD;
		}
	}

	private static double getPCritChanceMode(Creature attacker) //ONLY for 4-th proffs
	{
		if(!attacker.isPlayer())
			return Config.ALT_P_CRIT_CHANCE_MOD;
		switch(attacker.getPlayer().getActiveClassId())
		{
			case 139:
				return Config.ALT_P_CRIT_CHANCE_MOD_SIGEL;
			case 140:
				return Config.ALT_P_CRIT_CHANCE_MOD_TIR_WARRIOR;
			case 141:
				return Config.ALT_P_CRIT_CHANCE_MOD_OTHEL_ROGUE;
			case 142:
				return Config.ALT_P_CRIT_CHANCE_MOD_YR_ARCHER;
			case 143:
				return Config.ALT_P_CRIT_CHANCE_MOD_FEO_WIZZARD;
			case 144:
				return Config.ALT_P_CRIT_CHANCE_MOD_ISS_ENCHANTER;
			case 145:
				return Config.ALT_P_CRIT_CHANCE_MOD_WYN_SUMMONER;
			case 146:
				return Config.ALT_P_CRIT_CHANCE_MOD_EOL_HEALER;
				//new
			case 148:
				return Config.ALT_P_CRIT_CHANCE_MOD_SIGEL_PHOENIX_KNIGHT;
			case 149:
				return Config.ALT_P_CRIT_CHANCE_MOD_SIGEL_HELL_KNIGHT;
			case 150:
				return Config.ALT_P_CRIT_CHANCE_MOD_SIGEL_EVAS_TEMPLAR;
			case 151:
				return Config.ALT_P_CRIT_CHANCE_MOD_SIGEL_SHILLIEN_TEMPLAR;
			case 152:
				return Config.ALT_P_CRIT_CHANCE_MOD_TYR_DUELIST;
			case 153:
				return Config.ALT_P_CRIT_CHANCE_MOD_TYR_DREADNOUGHT;
			case 154:
				return Config.ALT_P_CRIT_CHANCE_MOD_TYR_TITAN;
			case 155:
				return Config.ALT_P_CRIT_CHANCE_MOD_TYR_GRAND_KHAVATARI;
			case 156:
				return Config.ALT_P_CRIT_CHANCE_MOD_TYR_MAESTRO;
			case 157:
				return Config.ALT_P_CRIT_CHANCE_MOD_TYR_DOOMBRINGER;
			case 158:
				return Config.ALT_P_CRIT_CHANCE_MOD_OTHELL_ADVENTURER;
			case 159:
				return Config.ALT_P_CRIT_CHANCE_MOD_OTHELL_WIND_RIDER;
			case 160:
				return Config.ALT_P_CRIT_CHANCE_MOD_OTHELL_GHOST_HUNTER;
			case 161:
				return Config.ALT_P_CRIT_CHANCE_MOD_OTHELL_FORTUNE_SEEKER;
			case 162:
				return Config.ALT_P_CRIT_CHANCE_MOD_YR_SAGITTARIUS;
			case 163:
				return Config.ALT_P_CRIT_CHANCE_MOD_YR_MOONLIGHT_SENTINEL;	
			case 164:
				return Config.ALT_P_CRIT_CHANCE_MOD_YR_GHOST_SENTINEL;	
			case 165:
				return Config.ALT_P_CRIT_CHANCE_MOD_YR_TRICKSTER;	
			case 166:
				return Config.ALT_P_CRIT_CHANCE_MOD_FEOH_ARCHMAGE;	
			case 167:
				return Config.ALT_P_CRIT_CHANCE_MOD_FEOH_SOULTAKER;	
			case 168:
				return Config.ALT_P_CRIT_CHANCE_MOD_FEOH_MYSTIC_MUSE;	
			case 169:
				return Config.ALT_P_CRIT_CHANCE_MOD_FEOH_STORM_SCREAMER;	
			case 170:
				return Config.ALT_P_CRIT_CHANCE_MOD_FEOH_SOUL_HOUND;	
			case 171:
				return Config.ALT_P_CRIT_CHANCE_MOD_ISS_HIEROPHANT;	
			case 172:
				return Config.ALT_P_CRIT_CHANCE_MOD_ISS_SWORD_MUSE;	
			case 173:
				return Config.ALT_P_CRIT_CHANCE_MOD_ISS_SPECTRAL_DANCER;	
			case 174:
				return Config.ALT_P_CRIT_CHANCE_MOD_ISS_DOMINATOR;	
			case 175:
				return Config.ALT_P_CRIT_CHANCE_MOD_ISS_DOOMCRYER;	
			case 176:
				return Config.ALT_P_CRIT_CHANCE_MOD_WYNN_ARCANA_LORD;	
			case 177:
				return Config.ALT_P_CRIT_CHANCE_MOD_WYNN_ELEMENTAL_MASTER;	
			case 178:
				return Config.ALT_P_CRIT_CHANCE_MOD_WYNN_SPECTRAL_MASTER;	
			case 179:
				return Config.ALT_P_CRIT_CHANCE_MOD_AEORE_CARDINAL;	
			case 180:
				return Config.ALT_P_CRIT_CHANCE_MOD_AEORE_EVAS_SAINT;	
			case 181:
				return Config.ALT_P_CRIT_CHANCE_MOD_AEORE_SHILLIEN_SAINT;
			case 188:
				return Config.ALT_P_CRIT_CHANCE_MOD_EVISCERATOR;
			case 189:
				return Config.ALT_P_CRIT_CHANCE_MOD_SAHYAS_SEER;
			default:
				return Config.ALT_P_CRIT_CHANCE_MOD;
		}
	}
	
	private static double getMCritChanceMode(Creature attacker) //ONLY for 4-th proffs
	{
		if(!attacker.isPlayer())
			return Config.ALT_M_CRIT_CHANCE_MOD;
		switch(attacker.getPlayer().getActiveClassId())
		{
			case 139:
				return Config.ALT_M_CRIT_CHANCE_MOD_SIGEL;
			case 140:
				return Config.ALT_M_CRIT_CHANCE_MOD_TIR_WARRIOR;
			case 141:
				return Config.ALT_M_CRIT_CHANCE_MOD_OTHEL_ROGUE;
			case 142:
				return Config.ALT_M_CRIT_CHANCE_MOD_YR_ARCHER;
			case 143:
				return Config.ALT_M_CRIT_CHANCE_MOD_FEO_WIZZARD;
			case 144:
				return Config.ALT_M_CRIT_CHANCE_MOD_ISS_ENCHANTER;
			case 145:
				return Config.ALT_M_CRIT_CHANCE_MOD_WYN_SUMMONER;
			case 146:
				return Config.ALT_M_CRIT_CHANCE_MOD_EOL_HEALER;
				//new
			case 148:
				return Config.ALT_M_CRIT_CHANCE_MOD_SIGEL_PHOENIX_KNIGHT;
			case 149:
				return Config.ALT_M_CRIT_CHANCE_MOD_SIGEL_HELL_KNIGHT;
			case 150:
				return Config.ALT_M_CRIT_CHANCE_MOD_SIGEL_EVAS_TEMPLAR;
			case 151:
				return Config.ALT_M_CRIT_CHANCE_MOD_SIGEL_SHILLIEN_TEMPLAR;
			case 152:
				return Config.ALT_M_CRIT_CHANCE_MOD_TYR_DUELIST;
			case 153:
				return Config.ALT_M_CRIT_CHANCE_MOD_TYR_DREADNOUGHT;
			case 154:
				return Config.ALT_M_CRIT_CHANCE_MOD_TYR_TITAN;
			case 155:
				return Config.ALT_M_CRIT_CHANCE_MOD_TYR_GRAND_KHAVATARI;
			case 156:
				return Config.ALT_M_CRIT_CHANCE_MOD_TYR_MAESTRO;
			case 157:
				return Config.ALT_M_CRIT_CHANCE_MOD_TYR_DOOMBRINGER;
			case 158:
				return Config.ALT_M_CRIT_CHANCE_MOD_OTHELL_ADVENTURER;
			case 159:
				return Config.ALT_M_CRIT_CHANCE_MOD_OTHELL_WIND_RIDER;
			case 160:
				return Config.ALT_M_CRIT_CHANCE_MOD_OTHELL_GHOST_HUNTER;
			case 161:
				return Config.ALT_M_CRIT_CHANCE_MOD_OTHELL_FORTUNE_SEEKER;
			case 162:
				return Config.ALT_M_CRIT_CHANCE_MOD_YR_SAGITTARIUS;
			case 163:
				return Config.ALT_M_CRIT_CHANCE_MOD_YR_MOONLIGHT_SENTINEL;	
			case 164:
				return Config.ALT_M_CRIT_CHANCE_MOD_YR_GHOST_SENTINEL;	
			case 165:
				return Config.ALT_M_CRIT_CHANCE_MOD_YR_TRICKSTER;	
			case 166:
				return Config.ALT_M_CRIT_CHANCE_MOD_FEOH_ARCHMAGE;	
			case 167:
				return Config.ALT_M_CRIT_CHANCE_MOD_FEOH_SOULTAKER;	
			case 168:
				return Config.ALT_M_CRIT_CHANCE_MOD_FEOH_MYSTIC_MUSE;	
			case 169:
				return Config.ALT_M_CRIT_CHANCE_MOD_FEOH_STORM_SCREAMER;	
			case 170:
				return Config.ALT_M_CRIT_CHANCE_MOD_FEOH_SOUL_HOUND;	
			case 171:
				return Config.ALT_M_CRIT_CHANCE_MOD_ISS_HIEROPHANT;	
			case 172:
				return Config.ALT_M_CRIT_CHANCE_MOD_ISS_SWORD_MUSE;	
			case 173:
				return Config.ALT_M_CRIT_CHANCE_MOD_ISS_SPECTRAL_DANCER;	
			case 174:
				return Config.ALT_M_CRIT_CHANCE_MOD_ISS_DOMINATOR;	
			case 175:
				return Config.ALT_M_CRIT_CHANCE_MOD_ISS_DOOMCRYER;	
			case 176:
				return Config.ALT_M_CRIT_CHANCE_MOD_WYNN_ARCANA_LORD;	
			case 177:
				return Config.ALT_M_CRIT_CHANCE_MOD_WYNN_ELEMENTAL_MASTER;	
			case 178:
				return Config.ALT_M_CRIT_CHANCE_MOD_WYNN_SPECTRAL_MASTER;	
			case 179:
				return Config.ALT_M_CRIT_CHANCE_MOD_AEORE_CARDINAL;	
			case 180:
				return Config.ALT_M_CRIT_CHANCE_MOD_AEORE_EVAS_SAINT;	
			case 181:
				return Config.ALT_M_CRIT_CHANCE_MOD_AEORE_SHILLIEN_SAINT;
			case 188:
				return Config.ALT_M_CRIT_CHANCE_MOD_EVISCERATOR;
			case 189:
				return Config.ALT_M_CRIT_CHANCE_MOD_SAHYAS_SEER;
			default:
				return Config.ALT_M_CRIT_CHANCE_MOD;
		}
	}	

	public static AttackInfo calcMagicDam(Creature attacker, Creature target, Skill skill, boolean useShot)
	{
		return calcMagicDam(attacker, target, skill, skill.getPower(target), useShot);
	}

	public static AttackInfo calcMagicDam(Creature attacker, Creature target, Skill skill, double power, boolean useShot)
	{
		boolean isPvP = attacker.isPlayable() && target.isPlayable();
		boolean isPvE = attacker.isPlayable() && target.isNpc();
		// @Rivelia. If skill doesn't ignore shield and CalcShieldUse returns true, shield = true.
		boolean shield = !skill.getShieldIgnore() && calcShldUse(attacker, target);
		double crit_static = attacker.calcStat(Stats.M_CRITICAL_DAMAGE_STATIC, target, skill);

		double mAtk = attacker.getMAtk(target, skill);

		if(useShot)
			mAtk *= ((100 + attacker.getChargedSpiritshotPower()) / 100.);

		double mdef = target.getMDef(null, skill);

		if(shield)
		{
			double shldDef = target.getShldDef();
			if(skill.getShieldIgnorePercent() > 0)
				shldDef -= shldDef * skill.getShieldIgnorePercent() / 100.;
			mdef += shldDef;
		}

		if(skill.getDefenceIgnorePercent() > 0)
			mdef *= (1. - (skill.getDefenceIgnorePercent() / 100.));

		mdef = Math.max(mdef, 1);

		AttackInfo info = new AttackInfo();
		if(power == 0)
			return info;

		if(skill.isSoulBoost())
			power *= 1.0 + 0.06 * Math.min(attacker.getConsumedSouls(), 5);

		info.damage = (91 * power * Math.sqrt(mAtk)) / mdef;

		//TODO: [Bonux] Переделать по оффу.
		if(info.damage > 0 && !skill.hasEffects(EffectUseType.NORMAL) && calcMagicHitMiss(attacker, target))
		{
			info.miss = true;
			info.damage = 0;
			attacker.sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_EVADED_C2S_ATTACK).addName(target).addName(attacker));
			target.sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_EVADED_C2S_ATTACK).addName(target).addName(attacker));
			return info;
		}

		info.damage *= 1 + (((Rnd.get() * attacker.getRandomDamage() * 2) - attacker.getRandomDamage()) / 100);

		info.crit = calcMCrit(attacker, target, skill);

		if(info.crit)
		{
			//info.damage *= attacker.calcStat(Stats.M_CRITICAL_DAMAGE, attacker.isPlayable() && target.isPlayable() ? 2.5 : 3., target, skill);
			// @Rivelia. Based on config, Magic skills can be reduced by Critical Damage Reduction if Critical Damage Receptive < 1.
			if (Config.ENABLE_CRIT_DMG_REDUCTION_ON_MAGIC)
			{
				double critDmg = info.damage;
				critDmg *= attacker.getMagicCriticalDmg(target, skill);
				critDmg += crit_static;
				critDmg *= getMCritDamageMode(attacker);
				critDmg -= info.damage;
				double tempDamage = target.calcStat(Stats.M_CRIT_DAMAGE_RECEPTIVE, critDmg, attacker, skill);
				critDmg = Math.min(tempDamage, critDmg);
				critDmg = Math.max(0, critDmg);
				info.damage += critDmg;
			}
			else
			{
				info.damage *= attacker.getMagicCriticalDmg(target, skill);
				info.damage += crit_static;			
				info.damage *= getMCritDamageMode(attacker);
			}
		}
		else
			info.damage = info.damage * getMSimpleDamageMode(attacker);

		info.damage = attacker.calcStat(Stats.INFLICTS_M_DAMAGE_POWER, info.damage, target, skill);
		info.damage = target.calcStat(Stats.RECEIVE_M_DAMAGE_POWER, info.damage, attacker, skill);

		if(shield)
		{
			if(Rnd.chance(Config.EXCELLENT_SHIELD_BLOCK_CHANCE))
			{
				info.damage = Config.EXCELLENT_SHIELD_BLOCK_RECEIVED_DAMAGE;
				target.sendPacket(SystemMsg.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
				attacker.sendPacket(new SystemMessagePacket(SystemMsg.C1_RESISTED_C2S_MAGIC).addName(target).addName(attacker));
				return info;
			}
			else
			{
				target.sendPacket(SystemMsg.YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED);
				attacker.sendPacket(new SystemMessagePacket(SystemMsg.YOUR_OPPONENT_HAS_RESISTANCE_TO_MAGIC_THE_DAMAGE_WAS_DECREASED));
			}
		}

		int levelDiff = target.getLevel() - attacker.getLevel(); // C Gracia Epilogue уровень маг. атак считается только по уроню атакующего

		if(info.damage > 0 && skill.isDeathlink())
			info.damage *= 1.8 * (1.0 - attacker.getCurrentHpRatio());

		if(info.damage > 0 && skill.isBasedOnTargetDebuff())
			info.damage *= 1 + 0.05 * target.getEffectList().getEffectsCount();

		if(skill.getSkillType() == SkillType.MANADAM)
			info.damage = Math.max(1, info.damage / 4.);
		else if(info.damage > 0)
		{
			if(isPvP)
			{
				info.damage *= attacker.calcStat(Stats.PVP_MAGIC_SKILL_DMG_BONUS, 1);
				info.damage /= target.calcStat(Stats.PVP_MAGIC_SKILL_DEFENCE_BONUS, 1);
			}
			else if(isPvE)
			{
				info.damage *= attacker.calcStat(Stats.PVE_MAGIC_SKILL_DMG_BONUS, 1);
				info.damage /= target.calcStat(Stats.PVE_MAGIC_SKILL_DEFENCE_BONUS, 1);
			}
		}
		if(target.isRaid())
		{
			info.damage *= attacker.calcStat(Stats.P_PVE_MAGICAL_SKILL_DMG_BONUS_BOSS, 1);
		}
		double magic_rcpt = target.calcStat(Stats.MAGIC_RESIST, attacker, skill) - (attacker.calcStat(Stats.MAGIC_POWER, target, skill) * attacker.calcStat(Stats.M_SKILL_POWER, 1, target, skill));
		double failChance = 4. * Math.max(1., levelDiff) * (1. + magic_rcpt / 100.);
		if(Rnd.chance(failChance))
		{
			if(levelDiff > 9)
			{
				info.damage = 0;
				SystemMessagePacket msg = new SystemMessagePacket(SystemMsg.C1_RESISTED_C2S_MAGIC).addName(target).addName(attacker);
				attacker.sendPacket(msg);
				target.sendPacket(msg);
			}
			else
			{
				info.damage /= 2;
				SystemMessagePacket msg = new SystemMessagePacket(SystemMsg.DAMAGE_IS_DECREASED_BECAUSE_C1_RESISTED_C2S_MAGIC).addName(target).addName(attacker);
				attacker.sendPacket(msg);
				target.sendPacket(msg);
			}
		}

		if(calcCastBreak(target, info.crit))
			target.abortCast(false, true);

		if(target.isStunned() && calcStunBreak(info.crit, true, true) && info.damage > 0)
			target.getEffectList().stopEffects(EffectType.Stun);

		return info;
	}

	/* @Rivelia. Default chances:
	* On magical skill non-crit: 33.33%
	* On magical skill crit: 66.67%
	* On physical skill non-crit: 33.33%
	* On physical skill crit: 66.67%
	* On regular hit non-crit: 16.67%
	* On regular hit crit: 33.33%
	*/
	public static boolean calcStunBreak(boolean crit, boolean isSkill, boolean isMagic)
	{
		if (!Config.ENABLE_STUN_BREAK_ON_ATTACK)
			return false;

		if (isSkill)
		{
			if (isMagic)
				return Rnd.chance(crit ? Config.CRIT_STUN_BREAK_CHANCE_ON_MAGICAL_SKILL : Config.NORMAL_STUN_BREAK_CHANCE_ON_MAGICAL_SKILL);
			return Rnd.chance(crit ? Config.CRIT_STUN_BREAK_CHANCE_ON_PHYSICAL_SKILL : Config.NORMAL_STUN_BREAK_CHANCE_ON_PHYSICAL_SKILL);
		}
		return Rnd.chance(crit ? Config.CRIT_STUN_BREAK_CHANCE_ON_REGULAR_HIT : Config.NORMAL_STUN_BREAK_CHANCE_ON_REGULAR_HIT);
	}

	/** Returns true in case of fatal blow success */
	public static boolean calcBlow(Creature activeChar, Creature target, Skill skill)
	{
		WeaponTemplate weapon = activeChar.getActiveWeaponTemplate();

		double base_weapon_crit = weapon == null ? 4. : weapon.getCritical();
		double crit_height_bonus = 1;
		if (Config.ENABLE_CRIT_HEIGHT_BONUS)
			crit_height_bonus = 0.008 * Math.min(25, Math.max(-25, target.getZ() - activeChar.getZ())) + 1.1;
		double buffs_mult = (activeChar.calcStat(Stats.FATALBLOW_RATE, target, skill) / target.calcStat(Stats.P_FATAL_LETHAL_RESIST, 1, target, skill));
		// @Rivelia. Default values: BLOW_SKILL_CHANCE_MOD_ON_BEHIND = 5, BLOW_SKILL_CHANCE_MOD_ON_FRONT = 4
		double skill_mod = skill.isBehind() ? Config.BLOW_SKILL_CHANCE_MOD_ON_BEHIND : Config.BLOW_SKILL_CHANCE_MOD_ON_FRONT;

		double chance = base_weapon_crit * buffs_mult * crit_height_bonus * skill_mod;

		if(!target.isInCombat())
			chance *= 1.1;

		if(activeChar.isDistortedSpace())
			chance *= 1.3;
		else
		{
			switch(PositionUtils.getDirectionTo(target, activeChar))
			{
				case BEHIND:
					chance *= 1.3;
					break;
				case SIDE:
					chance *= 1.1;
					break;
				case FRONT:
					if(skill.isBehind())
						chance = 3.0;
					break;
			}
		}
		// @Rivelia. Default values: MAX_BLOW_RATE_ON_BEHIND = 100, MAX_BLOW_RATE_ON_FRONT_AND_SIDE = 80.
		chance = Math.min(skill.isBehind() ? Config.MAX_BLOW_RATE_ON_BEHIND : Config.MAX_BLOW_RATE_ON_FRONT_AND_SIDE, chance);
		return Rnd.chance(chance);
	}

	/** Возвращает шанс крита в процентах */
	public static boolean calcPCrit(Creature attacker, Creature target, Skill skill, boolean blow)
	{
		if(attacker.isPlayer() && attacker.getActiveWeaponTemplate() == null)
			return false;
		if(skill != null)
		{
			// @Rivelia.
			boolean dexDep = attacker.calcStat(Stats.P_SKILL_CRIT_RATE_DEX_DEPENDENCE) > 0;
			double statModifier = dexDep ? BaseStats.DEX.calcBonus(attacker) : 1.;
			// @Rivelia. Default values: BLOW_SKILL_DEX_CHANCE_MOD = 1, NORMAL_SKILL_DEX_CHANCE_MOD = 1.
			if (dexDep)
			{
				if (blow)
					statModifier *= Config.BLOW_SKILL_DEX_CHANCE_MOD;
				else
					statModifier *= Config.NORMAL_SKILL_DEX_CHANCE_MOD;
			}
			double skillRate = skill.getCriticalRate() * statModifier * 0.01 * attacker.calcStat(Stats.SKILL_CRIT_CHANCE_MOD, target, skill);
			if(blow)
				skillRate *= Config.ALT_BLOW_CRIT_RATE_MODIFIER;
			return Rnd.chance(skillRate * getPCritChanceMode(attacker));
		}
		double rate = attacker.getPCriticalHit(target) * 0.01 * target.calcStat(Stats.P_CRIT_CHANCE_RECEPTIVE, attacker, skill);

		if(attacker.isDistortedSpace())
			rate *= 1.4;
		else
		{
			switch(PositionUtils.getDirectionTo(target, attacker))
			{
				case BEHIND:
					rate *= 1.4;
					break;
				case SIDE:
					rate *= 1.2;
					break;
			}
		}

		return Rnd.chance(rate / 10 * getPCritChanceMode(attacker));
	}

	public static boolean calcMCrit(Creature attacker, Creature target, Skill skill)
	{
		double rate = attacker.getMCriticalHit(target, skill) * 0.01 * target.calcStat(Stats.M_CRIT_CHANCE_RECEPTIVE, attacker, skill);
		// @Rivelia. At 500 M. Crit Rate, chance of having a magical crit should be 33.33%.
		return Rnd.chance(rate / 15 * getMCritChanceMode(attacker));
	}

	public static boolean calcCastBreak(Creature target, boolean crit)
	{
		if(target == null || target.isInvul() || target.isRaid() || !target.isCastingNow() && !target.isDualCastingNow())
			return false;

		Skill skill = target.getCastingSkill();
		if(skill != null && (skill.isPhysic() || skill.getSkillType() == SkillType.TAKECASTLE || skill.getSkillType() == SkillType.TAKEFORTRESS))
			return false;

		skill = target.getDualCastingSkill();
		if(skill != null && (skill.isPhysic() || skill.getSkillType() == SkillType.TAKECASTLE || skill.getSkillType() == SkillType.TAKEFORTRESS))
			return false;

		return Rnd.chance(target.calcStat(Stats.CAST_INTERRUPT, crit ? 75 : 10, null, skill));
	}

	/** Calculate delay (in milliseconds) before next ATTACK */
	public static int calcPAtkSpd(double rate)
	{
		return (int) (500000 / rate); // в миллисекундах поэтому 500*1000
	}

	/** Calculate delay (in milliseconds) for skills cast */
	public static int calcSkillCastSpd(Creature attacker, Skill skill, double skillTime)
	{
		if(skill.isMagic())
			return (int) (skillTime * 333 / Math.max(attacker.getMAtkSpd(), 1));
		if(skill.isPhysic())
			return (int) (skillTime * 333 / Math.max(attacker.getPAtkSpd(), 1));
		return (int) skillTime;
	}

	/** Calculate reuse delay (in milliseconds) for skills */
	public static long calcSkillReuseDelay(Creature actor, Skill skill)
	{
		long reuseDelay = skill.getReuseDelay();
		if(actor.isMonster())
			reuseDelay = skill.getReuseForMonsters();
		if(skill.isHandler() || skill.isItemSkill())
			return reuseDelay;
		// @Rivelia. If getMasteryLevel is set in XML, ignore isReuseDelayPermanent factor, otherwise count it.
		if(actor.getSkillMastery(skill.getId()) == 1 && (!skill.isReuseDelayPermanent() || skill.getMasteryLevel() != -1))
		{
			actor.removeSkillMastery(skill.getId());
			return 0;
		}
		if(skill.isReuseDelayPermanent())
			return reuseDelay;
		if(skill.isMusic())
			return (long) actor.calcStat(Stats.MUSIC_REUSE_RATE, reuseDelay, null, skill);
		if(skill.isMagic())
			return (long) actor.calcStat(Stats.MAGIC_REUSE_RATE, reuseDelay, null, skill);
		return (long) actor.calcStat(Stats.PHYSIC_REUSE_RATE, reuseDelay, null, skill);
	}

	private static double getConditionBonus(Creature attacker, Creature target)
	{
		double mod = 100;
		// Get high or low bonus
		if((attacker.getZ() - target.getZ()) > 50)
			mod += HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.HIGH);
		else if ((attacker.getZ() - target.getZ()) < -50)
			mod += HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.LOW);
		
		// Get weather bonus
		if(GameTimeController.getInstance().isNowNight())
			mod += HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.DARK);

		/*if(isRain)
			mod += HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.RAIN);*/

		if(attacker.isDistortedSpace())
			mod += HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.BACK);
		else
		{
			PositionUtils.TargetDirection direction = PositionUtils.getDirectionTo(attacker, target);
			switch(direction)
			{
				case BEHIND:
					mod += HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.BACK);
					break;
				case SIDE:
					mod += HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.SIDE);
					break;
				default:
					mod += HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.AHEAD);
					break;
			}
		}

		// If (mod / 100) is less than 0, return 0, because we can't lower more than 100%.
		return Math.max(mod / 100, 0);
	}

	/** Returns true if hit missed (target evaded) */
	public static boolean calcHitMiss(Creature attacker, Creature target)
	{
		double chanceToHit = 80 + 2 * (attacker.getPAccuracy() - target.getPEvasionRate(attacker));

		chanceToHit *= getConditionBonus(attacker, target);

		// @Rivelia. CalcHitMiss default max: 27.5. Default min: 98.0
		chanceToHit = Math.max(chanceToHit, Config.PHYSICAL_MIN_CHANCE_TO_HIT);
		chanceToHit = Math.min(chanceToHit, Config.PHYSICAL_MAX_CHANCE_TO_HIT);

		return !Rnd.chance(chanceToHit);
	}

	//TODO: [Bonux] Переделать по оффу.
	private static boolean calcMagicHitMiss(Creature attacker, Creature target)
	{
		double chanceToHit = 100 + 2 * (attacker.getMAccuracy() - target.getMEvasionRate(attacker));

		// @Rivelia. CalcMagicHitMiss default max: 72.5. Default min: 98.0
		chanceToHit = Math.max(chanceToHit, Config.MAGIC_MIN_CHANCE_TO_HIT);
		chanceToHit = Math.min(chanceToHit, Config.MAGIC_MAX_CHANCE_TO_HIT);

		return !Rnd.chance(chanceToHit);
	}

	/** Returns true if shield defence successfull */
	public static boolean calcShldUse(Creature attacker, Creature target)
	{
		WeaponTemplate template = target.getSecondaryWeaponTemplate();
		if(template == null || template.getItemType() != WeaponTemplate.WeaponType.NONE)
			return false;
		int angle = (int) target.calcStat(Stats.SHIELD_ANGLE, attacker, null);
		if(angle < 360 && !PositionUtils.isFacing(target, attacker, angle))
			return false;
		return Rnd.chance((int) target.calcStat(Stats.SHIELD_RATE, attacker, null));
	}

	public static boolean calcSkillSuccess(Env env, EffectTemplate et, boolean useShot)
	{
		if(env.value == -1)
			return true;

		env.value = Math.max(Math.min(env.value, 100), 1); // На всякий случай
		final double base = env.value; // Запоминаем базовый шанс (нужен позже)

		final Skill skill = env.skill;
		if(!skill.isOffensive())
			return Rnd.chance(env.value);
		
		final Creature caster = env.character;
		final Creature target = env.target;

		boolean debugCaster = false;
		boolean debugTarget = false;
		boolean debugGlobal = false;
		if(Config.ALT_DEBUG_ENABLED)
		{
			// Включена ли отладка на кастере
			debugCaster = caster.getPlayer() != null && caster.getPlayer().isDebug();
			// Включена ли отладка на таргете
			debugTarget = target.getPlayer() != null && target.getPlayer().isDebug();
			// Разрешена ли отладка в PvP
			final boolean debugPvP = Config.ALT_DEBUG_PVP_ENABLED && (debugCaster || debugTarget) && (!Config.ALT_DEBUG_PVP_DUEL_ONLY || (caster.getPlayer().isInDuel() && target.getPlayer().isInDuel()));
			// Включаем отладку в PvP и для PvE если разрешено
			debugGlobal = debugPvP || (Config.ALT_DEBUG_PVE_ENABLED && ((debugCaster && target.isMonster()) || (debugTarget && caster.isMonster())));
		}

		double statMod = 1.;
		if(skill.getSaveVs() != null)
		{
			statMod = skill.getSaveVs().calcChanceMod(target);
			statMod = Math.min(statMod, Config.MIN_SAVEVS_REDUCTION);	// @Rivelia. Default: 2.0
			statMod = Math.max(statMod, Config.MAX_SAVEVS_REDUCTION);	// @Rivelia. Default: 0.0
			env.value *= statMod; // Бонус от MEN/CON/etc
		}

		env.value = Math.max(env.value, 1);

		double mAtkMod = 1.;
		// @Rivelia. Default value: ENABLE_MATK_SKILL_LANDING_MOD = true.
		if(skill.isMagic() && Config.ENABLE_MATK_SKILL_LANDING_MOD) // Этот блок только для магических скиллов
		{
			int mdef = Math.max(1, target.getMDef(target, skill)); // Вычисляем mDef цели
			double matk = caster.getMAtk(target, skill);

			if(skill.isSSPossible() && useShot)
				matk *= ((100 + caster.getChargedSpiritshotPower()) / 100.);

			mAtkMod = Math.sqrt(Config.SKILLS_CHANCE_MOD * Math.pow(matk, Config.SKILLS_CHANCE_POW) / mdef);


			if(mAtkMod < 0.7)
				mAtkMod = 0.7;
			else if(mAtkMod > 1.4)
				mAtkMod = 1.4;


			env.value *= mAtkMod;
			env.value = Math.max(env.value, 1);
		}
		// @Rivelia. TODO: Check values for WIT mod, as they are very very approximative.
		double witMod = 1.;
		if (skill.isMagic() && Config.ENABLE_WIT_SKILL_LANDING_MOD)
		{
			witMod = BaseStats.WIT.calcBonus(caster) / 4.;

			if(skill.isSSPossible() && useShot)
				witMod *= ((100 + caster.getChargedSpiritshotPower()) / 100.);

			witMod = 1 + witMod * 0.01;
			env.value *= witMod;
			env.value = Math.max(env.value, 1);
		}

		double lvlDependMod = skill.getLevelModifier();
		if(lvlDependMod != 0)
		{
			final int attackLevel = skill.getMagicLevel() > 0 ? skill.getMagicLevel() : caster.getLevel();
			/*final int delta = attackLevel - target.getLevel();
			lvlDependMod = delta / 5;
			lvlDependMod = lvlDependMod * 5;
			if(lvlDependMod != delta)
				lvlDependMod = delta < 0 ? lvlDependMod - 5 : lvlDependMod + 5;

			env.value += lvlDependMod;*/
			lvlDependMod = 1. + (attackLevel - target.getLevel()) * 0.03 * lvlDependMod;
			if(lvlDependMod < 0)
				lvlDependMod = 0;
			else if(lvlDependMod > 2)
				lvlDependMod = 2;

			env.value *= lvlDependMod;
		}

		double vulnMod = 0;
		double profMod = 0;
		double resMod = 1.;
		SkillTrait trait = skill.getTraitType();
		if(!skill.isIgnoreResists() && trait != null)
		{
			vulnMod = trait.calcVuln(env);
			profMod = trait.calcProf(env);

			final double maxResist = 90 + profMod * 0.85;
			resMod = (maxResist - vulnMod) / 60.;

			if(resMod != 1) // Внимание, знак был изменен на противоположный !
			{
				if(resMod == Double.NEGATIVE_INFINITY)
				{
					if(debugGlobal)
					{
						if(debugCaster)
							caster.getPlayer().sendMessage("Full immunity");
						if(debugTarget)
							target.getPlayer().sendMessage("Full immunity");
					}
					return false;
				}
				if(resMod == Double.POSITIVE_INFINITY)
				{
					if(debugGlobal)
					{
						if(debugCaster)
							caster.getPlayer().sendMessage("Full vulnerability");
						if(debugTarget)
							target.getPlayer().sendMessage("Full vulnerability");
					}
					return true;
				}

				resMod = Math.max(resMod, 0);
				env.value *= resMod;
			}
		}

		double elementModSum = 0.;
		final Element attackElement = getAttackElement(caster, target);
		final Element[] elements = skill.getElements();
		for(Element element : elements)
		{
			if(element != Element.NONE)
			{
				double elementMod = skill.getElementsPower();
				if(attackElement == element)
					elementMod += caster.calcStat(element.getAttack(), 0);

				elementMod -= target.calcStat(element.getDefence(), 0);
				/*if(elementMod < 0)
					elementMod = 0;
				else
					elementMod = Math.round(elementMod / 10);*/
				elementMod = Math.round(elementMod / 10);

				elementModSum += elementMod;
			}
		}
		env.value += elementModSum;

		// Debuff Resist is on priority from all other resist types.
		double debuffMod = 1.;
		if(!skill.isIgnoreResists())
		{
			debuffMod = 1. - target.calcStat(Stats.DEBUFF_RESIST, caster, skill) * 0.01;
			//debuffMod = 1. - target.calcStat(Stats.DEBUFF_RESIST, caster, skill) * 0.008333333333333;
			if(debuffMod != 1)
			{
				if(debuffMod == Double.NEGATIVE_INFINITY)
				{
					if(debugGlobal)
					{
						if(debugCaster)
							caster.getPlayer().sendMessage("Full debuff immunity");
						if(debugTarget)
							target.getPlayer().sendMessage("Full debuff immunity");
					}
					return false;
				}
				if(debuffMod == Double.POSITIVE_INFINITY)
				{
					if(debugGlobal)
					{
						if(debugCaster)
							caster.getPlayer().sendMessage("Full debuff vulnerability");
						if(debugTarget)
							target.getPlayer().sendMessage("Full debuff vulnerability");
					}
					return true;
				}

				debuffMod = Math.max(debuffMod, 0);
				env.value *= debuffMod;
			}
		}

		//if(skill.isSoulBoost()) // Бонус от душ камаелей
		//	env.value *= 0.85 + 0.06 * Math.min(character.getConsumedSouls(), 5);

		if(target.isRaid())
			env.value /= 2;

		if(caster.isRaid())
			env.value *= 2;

		env.value = Math.max(env.value, Math.min(base, Config.SKILLS_CHANCE_MIN)); // Если базовый шанс более Config.SKILLS_CHANCE_MIN, то при небольшой разнице в уровнях, делаем кап снизу.
		env.value = Math.max(Math.min(env.value, Config.SKILLS_CHANCE_CAP), 1); // Применяем кап
		final boolean result = Rnd.chance((int) env.value);

		if(debugGlobal)
		{
			StringBuilder stat = new StringBuilder(100);
			if(et == null)
				stat.append(skill.getName());
			else
				stat.append(et._effectType.name());
			stat.append(" AR:");
			stat.append((int) base);
			stat.append(" ");
			if(skill.getSaveVs() != null)
			{
				stat.append(skill.getSaveVs().name());
				stat.append(":");
				stat.append(String.format("%1.1f", statMod));
			}
			if(skill.isMagic())
			{
				stat.append(" ");
				stat.append(" mAtk:");
				stat.append(String.format("%1.1f", mAtkMod));
				stat.append(" ");
				stat.append(" witMod:");
				stat.append(String.format("%1.1f", witMod));
			}
			if(skill.getTraitType() != null)
			{
				stat.append(" ");
				stat.append(skill.getTraitType().name());
			}
			stat.append(" ");
			stat.append(String.format("%1.1f", resMod));
			stat.append("(");
			stat.append(String.format("%1.1f", profMod));
			stat.append("/");
			stat.append(String.format("%1.1f", vulnMod));
			if(debuffMod != 0)
			{
				stat.append("+");
				stat.append(String.format("%1.1f", debuffMod));
			}
			stat.append(") lvl:");
			stat.append(String.format("%1.1f", lvlDependMod));
			stat.append(" elem:");
			stat.append((int) elementModSum);
			stat.append(" Chance:");
			stat.append(String.format("%1.1f", env.value));
			if(!result)
				stat.append(" failed");

			// отсылаем отладочные сообщения
			if(debugCaster)
				caster.getPlayer().sendMessage(stat.toString());
			if(debugTarget)
				target.getPlayer().sendMessage(stat.toString());
		}
		return result;
	}

	public static boolean calcSkillSuccess(Creature player, Creature target, Skill skill, int activateRate)
	{
		Env env = new Env();
		env.character = player;
		env.target = target;
		env.skill = skill;
		env.value = activateRate;
		return calcSkillSuccess(env, null, true);
	}

	public static void calcSkillMastery(Skill skill, Creature activeChar)
	{
		if(skill.isHandler())
			return;

		boolean calcSkillMastery = false;

		boolean activateINT = activeChar.calcStat(Stats.ACTIVATE_SKILL_MASTERY_INT, 0) > 0;
		if(activateINT)
		{
			// @Rivelia. Default divider of Config.INT_SKILLMASTERY_MODIFIER: 2.
			double chanceINT = activeChar.calcStat(Stats.SKILL_MASTERY, activeChar.getINT() / Config.INT_SKILLMASTERY_MODIFIER) / 10; // TODO: [Bonux] Уточнить формулу.
			calcSkillMastery = Rnd.chance(chanceINT);
		}

		if(!calcSkillMastery)
		{
			boolean activateSTR = activeChar.calcStat(Stats.ACTIVATE_SKILL_MASTERY_STR, 0) > 0;
			if(activateSTR)
			{
				// @Rivelia. Default divider of Config.STR_SKILLMASTERY_MODIFIER: 2.
				double chanceSTR = activeChar.calcStat(Stats.SKILL_MASTERY, activeChar.getSTR() / Config.STR_SKILLMASTERY_MODIFIER) / 10; // TODO: [Bonux] Уточнить формулу.
				calcSkillMastery = Rnd.chance(chanceSTR);
			}
		}

		if(calcSkillMastery && !skill.isToggle())
		{
			//byte mastery level, 0 = no skill mastery, 1 = no reuseTime, 2 = effect duration*2, 3 = power*3
			int masteryLevel = 0;
			// @Rivelia. If skill has effects -> effect duration * 2, else -> no reuse time.
			if(Config.OFFLIKE_MASTERY_SYSTEM)
			{
				int skillMastery = skill.getMasteryLevel();	// -1 = calculate. 0 = no mastery. 1 = no reuse. 2 = effect duration * 2.
				if(skillMastery == 0)
					return;

				if(skill.isToggle())
					return;

				switch(skill.getSkillType())
				{
					case CHANGE_CLASS:
					case CLAN_GATE:
					case CRAFT:
					case DEATH_PENALTY:
					case ENCHANT_ARMOR:
					case ENCHANT_WEAPON:
					case EXTRACT_STONE:
					case FISHING:
					case HARDCODED:
					case KAMAEL_WEAPON_EXCHANGE:
					case LUCK:
					case ADD_PC_BANG:
					case NOTDONE:
					case NOTUSED:
					case PASSIVE:
					case BEAST_FEED:
					case PET_FEED:
					case PUMPING:
					case REELING:
					case REFILL:
					case CHAIN_CALL:
					case SOWING:
					case EXPHEAL:
					case SPHEAL:
					case EXPGIVE:
					case SUMMON_FLAG:
					case RESTORATION:
					case TAKECASTLE:
					case TAKEFORTRESS:
					case TAMECONTROL:
					case WATCHER_GAZE:
					case VITALITY_HEAL:
						return;
				}

				if(skillMastery == -1)
				{
					// @Rivelia.
					// Skill has valid effect. So duration must double.
					if(skill.hasEffects(EffectUseType.NORMAL))
						masteryLevel = 2;
					else
						masteryLevel = 1;	// No valid effects found or skill effects count <= 0. So skill must reset its reuse time.
				}
				else
					masteryLevel = skillMastery;	// XML-defined mastery level.

				activeChar.setSkillMastery(skill.getId(), masteryLevel);
			}
			else
			{
				if(skill.hasEffects(EffectUseType.NORMAL))
					masteryLevel = 2;
				else
					masteryLevel = 1;

				if(masteryLevel > 0)
					activeChar.setSkillMastery(skill.getId(), masteryLevel);
			}
		}
	}

	public static double calcDamageResists(Skill skill, Creature attacker, Creature defender, double value)
	{
		if(attacker == defender) // это дамаг от местности вроде ожога в лаве, наносится от своего имени
			return value; // TODO: по хорошему надо учитывать защиту, но поскольку эти скиллы немагические то надо делать отдельный механизм
//DEPRECATED
		if(attacker.isBoss())
			value *= Config.RATE_EPIC_ATTACK;
		else if(attacker.isRaid() || attacker instanceof ReflectionBossInstance)
			value *= Config.RATE_RAID_ATTACK;

		if(defender.isBoss())
			value /= Config.RATE_EPIC_DEFENSE;
		else if(defender.isRaid() || defender instanceof ReflectionBossInstance)
			value /= Config.RATE_RAID_DEFENSE;

		Player pAttacker = attacker.getPlayer();

		// если уровень игрока ниже чем на 2 и более уровней моба 78+, то его урон по мобу снижается
		int diff = defender.getLevel() - (pAttacker != null ? pAttacker.getLevel() : attacker.getLevel());
		if(attacker.isPlayable() && defender.isMonster() && defender.getLevel() >= 78 && diff > 2)
			value *= .7 / Math.pow(diff - 2, .25);

		double elementModSum = 1.;

		// использует элемент умения
		if(skill != null)
		{
			// DEBUG
			boolean haveDebug = false;
			final StringBuilder elementNameDebug = new StringBuilder("Element:");
			final StringBuilder elementAttackDebug = new StringBuilder("Attack:");
			final StringBuilder elementDefenceDebug = new StringBuilder("Defence:");
			final StringBuilder elementModifierDebug = new StringBuilder("Modifier:");

			final Element[] elements = skill.getElements();
			final double power = skill.getElementsPower();
			for(Element element : elements)
			{
				if(element == Element.NONE)
					continue;

				double attack = attacker.calcStat(element.getAttack(), power);
				double defence = defender.calcStat(element.getDefence(), 0.);
				double elementMod = getElementMod(defence, attack);
				if(pAttacker != null && pAttacker.isGM() && Config.ALT_DEBUG_ENABLED)
				{
					haveDebug = true;
					elementNameDebug.append(" ");
					elementNameDebug.append(element.name());
					elementAttackDebug.append(" ");
					elementAttackDebug.append(attack);
					elementDefenceDebug.append(" ");
					elementDefenceDebug.append(defence);
					elementModifierDebug.append(" ");
					elementModifierDebug.append(elementMod);
				}
				elementModSum += (elementMod - 1.0) / ((pAttacker != null && defender.getPlayer() != null) ? 2 : 1);
			}

			if(haveDebug)
			{
				pAttacker.sendMessage(elementNameDebug.toString());
				pAttacker.sendMessage(elementAttackDebug.toString());
				pAttacker.sendMessage(elementDefenceDebug.toString());
				pAttacker.sendMessage(elementModifierDebug.toString());
			}

		}
		else // используем максимально эффективный элемент
		{
			final Element element = getAttackElement(attacker, defender);
			if(element == Element.NONE)
				return value;

			double attack = attacker.calcStat(element.getAttack(), 0.);
			double defence = defender.calcStat(element.getDefence(), 0.);
			elementModSum = getElementMod(defence, attack);
			if(pAttacker != null && pAttacker.isGM() && Config.DEBUG)
			{
				pAttacker.sendMessage("Element: " + element.name());
				pAttacker.sendMessage("Attack: " + attack);
				pAttacker.sendMessage("Defence: " + defence);
				pAttacker.sendMessage("Modifier: " + elementModSum);
			}
		}

		return value * elementModSum;
	}

	/**
	 * Возвращает множитель для атаки из значений атакующего и защитного элемента.
	 * <br /><br />
	 * Диапазон от 1.0 до 1.7 (Freya)
	 * <br /><br />
	 * @param defense значение защиты
	 * @param attack значение атаки
	 * @return множитель
	 */
	private static double getElementMod(double defense, double attack)
	{
		double diff = attack - defense;
		if(diff <= 0)
			return 1.0;
		else if(diff < 50)
			return 1.0 + diff * 0.003948;
		else if(diff < 150)
			return 1.2;
		else if(diff < 300)
			return 1.4;
		else
			return 1.7;
	}

	/**
	 * Возвращает максимально эффективный атрибут, при атаке цели
	 * @param attacker
	 * @param target
	 * @return
	 */
	public static Element getAttackElement(Creature attacker, Creature target)
	{
		double val, max = Double.MIN_VALUE;
		Element result = Element.NONE;
		for(Element e : Element.VALUES)
		{
			val = attacker.calcStat(e.getAttack(), 0.);
			if(val <= 0.)
				continue;

			if(target != null)
				val -= target.calcStat(e.getDefence(), 0.);

			if(val > max)
			{
				result = e;
				max = val;
			}
		}

		return result;
	}

	public static int calculateKarmaLost(Player player, long exp)
	{
		if(Config.RATE_KARMA_LOST_STATIC != -1)
			return Config.RATE_KARMA_LOST_STATIC;

		double karmaLooseMul = KarmaIncreaseDataHolder.getInstance().getData(player.getLevel());
		if(exp > 0) // Received exp
			exp /= Config.KARMA_RATE_KARMA_LOST == -1 ? Config.RATE_XP_BY_LVL[player.getLevel()] : Config.KARMA_RATE_KARMA_LOST;
		return (int) ((Math.abs(exp) / karmaLooseMul) / 15);
	}

	public static boolean calcCraftingMastery(Player player)
	{
		if(player.getSkillLevel(Skill.SKILL_CRAFTING_MASTERY) > 0)
		{
			if(Rnd.chance(CRAFTING_MASTERY_CHANCE))
				return true;
		}
		double vCalcMastery = player.calcStat(Stats.CRAFT_MASTERY, 1, null, null);
		if(vCalcMastery > 0)
		{
			if(Rnd.chance(vCalcMastery))
				return true;
		}
		return false;
	}

	public static boolean tryLuck(Player player)
	{
		//TODO: [Bonux] Сделать правильную формулу.
		double luc = player.getLUC() * Config.LUC_BONUS_MODIFIER;
		if(luc > 0)
			return Rnd.chance(luc);
		return false;
	}

	public static double upplyCHABonus(Player player, double value)
	{
		//TODO: [Bonux] Сделать правильную формулу.
		double modifier = 1. + (player.getCHA() * 0.001);
		double valueWithBonus = modifier * value;
		return valueWithBonus;
	}
}
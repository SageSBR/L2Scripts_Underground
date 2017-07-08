package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.stats.funcs.Func;
import l2s.gameserver.stats.funcs.FuncTemplate;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author G1ta0
 */
public class EffectServitorShare extends Effect
{
	public class FuncShare extends Func
	{
		public FuncShare(Stats stat, int order, Object owner, double value)
		{
			super(stat, order, owner, value);
		}

		@Override
		public void calc(Env env)
		{
			if(!env.character.isSummon())
				return;

			if(stat == Stats.POWER_ATTACK)
				env.value += env.character.getPlayer().getPAtk(null) * value;
			else if(stat == Stats.POWER_DEFENCE)
				env.value += env.character.getPlayer().getPDef(null) * value;
			else if(stat == Stats.MAGIC_ATTACK)
				env.value += env.character.getPlayer().getMAtk(null, null) * value;
			else if(stat == Stats.MAGIC_DEFENCE)
				env.value += env.character.getPlayer().getMDef(null, null) * value;
			else if(stat == Stats.MAX_HP)
				env.value += env.character.getPlayer().getMaxHp() * value;
			else if(stat == Stats.MAX_MP)
				env.value += env.character.getPlayer().getMaxMp() * value;
			else if(stat == Stats.POWER_ATTACK_SPEED)
				env.value += env.character.getPlayer().getPAtkSpd() * value;
			else if(stat == Stats.MAGIC_ATTACK_SPEED)
				env.value += env.character.getPlayer().getMAtkSpd() * value;
			else if(stat == Stats.P_CRITICAL_RATE)
				env.value += env.character.getPlayer().getPCriticalHit(null) * value;
			else if(stat == Stats.M_CRITICAL_RATE)
				env.value += env.character.getPlayer().getMCriticalHit(null, null) * value;
			else if(stat == Stats.PVP_PHYS_DMG_BONUS)
				env.value += env.character.getPlayer().calcStat(Stats.PVE_PHYS_SKILL_DMG_BONUS, 0) * value;
			else if(stat == Stats.PVP_PHYS_SKILL_DMG_BONUS)
				env.value += env.character.getPlayer().calcStat(Stats.PVP_PHYS_SKILL_DMG_BONUS, 0) * value;
			else if(stat == Stats.PVP_MAGIC_SKILL_DMG_BONUS)
				env.value += env.character.getPlayer().calcStat(Stats.PVP_MAGIC_SKILL_DMG_BONUS, 0) * value;
			else if(stat == Stats.PVP_PHYS_DEFENCE_BONUS)
				env.value += env.character.getPlayer().calcStat(Stats.PVP_PHYS_DEFENCE_BONUS, 0) * value;
			else if(stat == Stats.PVP_PHYS_SKILL_DEFENCE_BONUS)
				env.value += env.character.getPlayer().calcStat(Stats.PVP_PHYS_SKILL_DEFENCE_BONUS, 0) * value;
			else if(stat == Stats.PVP_MAGIC_SKILL_DEFENCE_BONUS)
				env.value += env.character.getPlayer().calcStat(Stats.PVP_MAGIC_SKILL_DEFENCE_BONUS, 0) * value;
			else
				env.value += env.character.getPlayer().calcStat(stat, stat.getInit()) * value;
		}
	}

	public EffectServitorShare(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public Func[] getStatFuncs()
	{
		Func[] funcs = new Func[0];
		if(_effected.isSummon())
		{
			FuncTemplate[] funcTemplates = getTemplate().getAttachedFuncs();
			funcs = new Func[funcTemplates.length];
			for(int i = 0; i < funcs.length; i++)
			{
				funcs[i] = new FuncShare(funcTemplates[i]._stat, funcTemplates[i]._order, this, funcTemplates[i]._value);
			}
		}
		return funcs;
	}

	@Override
	public int getDisplayId()
	{
		if(_effected.isSummon())
			return -1;

		return super.getDisplayId();
	}
}
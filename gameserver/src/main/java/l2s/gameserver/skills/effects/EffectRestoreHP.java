package l2s.gameserver.skills.effects;

import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExRegenMaxPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public class EffectRestoreHP extends EffectRestore
{
	private final boolean _cpIncluding;

	public EffectRestoreHP(Env env, EffectTemplate template)
	{
		super(env, template);
		_cpIncluding = template.getParam().getBool("cp_including", false);
	}

	private int[] calcAddToHpCp()
	{
		if(getEffected().isHealBlocked())
			return new int[2];

		double power = calc();
		if(power <= 0)
			return new int[2];

		if(!_staticPower)
		{
			if(!_percent)
			{
				//TODO: Check formulas.
				power += 0.1 * power * Math.sqrt(getEffector().getMAtk(null, getSkill()) / 333);

				if(getSkill().isSSPossible() && getSkill().getHpConsume() == 0)
					power *= 1 + ((200 + getEffector().getChargedSpiritshotPower()) * 0.001);

				if(getSkill().isMagic())
				{
					if(Formulas.calcMCrit(getEffector(), getEffected(), getSkill()))
						power *= 1.3;
				}
			}
		}

		if(_percent)
			power = getEffected().getMaxHp() / 100. * power;

		if(!_staticPower)
		{
			if(!_ignoreBonuses)
			{
				power *= getEffected().calcStat(Stats.HEAL_EFFECTIVNESS, 100., getEffector(), getSkill()) / 100.;
				power = getEffector().calcStat(Stats.HEAL_POWER, power, getEffected(), getSkill());
			}
		}

		double newHp = getEffected().getCurrentHp() + power;
		newHp = Math.max(0, Math.min(newHp, getEffected().getMaxHp() / 100. * getEffected().calcStat(Stats.HP_LIMIT, null, null)));

		int addToHp = (int) Math.max(0, newHp - getEffected().getCurrentHp());
		int addToCp = 0;

		if(_cpIncluding && getEffected().isPlayer())
		{
			if(_percent) // Проверить эту часть.
			{
				if(addToHp > 0)
					power = 0;
				else
				{
					power = getEffected().getMaxCp() / 100. * calc();

					if(!_ignoreBonuses)
						power *= getEffected().calcStat(Stats.CPHEAL_EFFECTIVNESS, 100., getEffector(), getSkill()) / 100.;
				}
			}
			else
				power = power - addToHp;

			if(power > 0)
			{
				double newCp = getEffected().getCurrentCp() + power;
				newCp = Math.max(0, Math.min(newCp, getEffected().getMaxCp() / 100. * getEffected().calcStat(Stats.CP_LIMIT, null, null)));

				addToCp = (int) Math.max(0, newCp - getEffected().getCurrentCp());
			}
		}

		return new int[]{ addToHp, addToCp };
	}

	@Override
	public void onStart()
	{
		super.onStart();

		int[] addToHpCp = calcAddToHpCp();

		int addToHp = addToHpCp[0];
		if(!getTemplate().isInstant())
		{
			if(getEffected().isPlayer())
				getEffected().sendPacket(new ExRegenMaxPacket(addToHp, getDuration(), getInterval()));
			return;
		}

		if(addToHp > 0)
		{
			if(getSkill().getId() == 4051)
				getEffected().sendPacket(SystemMsg.REJUVENATING_HP);
			else if(getEffector() != getEffected())
				getEffected().sendPacket(new SystemMessagePacket(SystemMsg.S2_HP_HAS_BEEN_RESTORED_BY_C1).addName(getEffector()).addInteger(addToHp));
			else
				getEffected().sendPacket(new SystemMessagePacket(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addInteger(addToHp));

			getEffected().setCurrentHp(getEffected().getCurrentHp() + addToHp, false);
		}

		int addToCp = addToHpCp[1];
		if(addToCp > 0)
		{
			if(getEffector() != getEffected())
				getEffected().sendPacket(new SystemMessagePacket(SystemMsg.S2_CP_HAS_BEEN_RESTORED_BY_C1).addName(getEffector()).addInteger(addToCp));
			else
				getEffected().sendPacket(new SystemMessagePacket(SystemMsg.S1_CP_HAS_BEEN_RESTORED).addInteger(addToCp));

			getEffected().setCurrentCp(getEffected().getCurrentCp() + addToCp);
		}
	}

	@Override
	public boolean onActionTime()
	{
		if(getTemplate().isInstant())
			return false;

		int[] addToHpCp = calcAddToHpCp();

		int addToHp = addToHpCp[0];
		if(addToHp > 0)
			getEffected().setCurrentHp(getEffected().getCurrentHp() + addToHp, false);

		int addToCp = addToHpCp[1];
		if(addToCp > 0)
			getEffected().setCurrentCp(getEffected().getCurrentCp() + addToCp);

		return true;
	}
}
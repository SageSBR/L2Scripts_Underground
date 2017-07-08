package l2s.gameserver.skills.effects;

import l2s.gameserver.Config;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public class EffectRestoreMP extends EffectRestore
{
	public EffectRestoreMP(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	private int calcAddToMp()
	{
		if(getEffected().isHealBlocked())
			return 0;

		double power = calc();
		if(power <= 0)
			return 0;

		if(!_staticPower)
		{
			if(!_percent)
			{
				//TODO: Check formulas.
				if(getSkill().isSSPossible() && Config.MANAHEAL_SPS_BONUS)
					power *= 1 + ((200 + getEffector().getChargedSpiritshotPower()) * 0.001);
			}
		}

		if(_percent)
			power = getEffected().getMaxMp() / 100. * power;

		if(!_staticPower)
		{
			if(!_ignoreBonuses)
			{
				if(_percent || getEffector() != getEffected()) // TODO: Check this:
					power *= getEffected().calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100., getEffector(), getSkill()) / 100.;
			}
			else if(!_percent)
				// TODO: Check this:
				power *= 1.7;
		}

		double newMp = getEffected().getCurrentMp() + power;
		newMp = Math.max(0, Math.min(newMp, getEffected().getMaxMp() / 100. * getEffected().calcStat(Stats.MP_LIMIT, null, null)));

		if(!_staticPower)
		{
			if(!_percent)
			{
				// TODO: Check this:
				// Обработка разницы в левелах при речардже. Учитывыется разница уровня скилла и уровня цели.
				// 1013 = id скилла recharge. Для сервиторов не проверено убавление маны, пока оставлено так как есть.
				if(getSkill().getMagicLevel() > 0 && getEffector() != getEffected())
				{
					int diff = getEffected().getLevel() - getSkill().getMagicLevel();
					if(diff > 5)
					{
						if(diff < 20)
							newMp = newMp / 100 * (100 - diff * 5);
						else
							newMp = 0;
					}
				}
			}
		}

		return (int) Math.max(0, newMp - getEffected().getCurrentMp());
	}

	@Override
	public void onStart()
	{
		super.onStart();

		if(!getTemplate().isInstant())
			return;

		int addToMp = calcAddToMp();
		if(addToMp > 0)
		{
			if(getEffector() != getEffected())
				getEffected().sendPacket(new SystemMessagePacket(SystemMsg.S2_MP_HAS_BEEN_RESTORED_BY_C1).addName(getEffector()).addInteger(addToMp));
			else
				getEffected().sendPacket(new SystemMessagePacket(SystemMsg.S1_MP_HAS_BEEN_RESTORED).addInteger(addToMp));

			getEffected().setCurrentMp(getEffected().getCurrentMp() + addToMp);
		}
	}

	@Override
	public boolean onActionTime()
	{
		if(getTemplate().isInstant())
			return false;

		int addToMp = calcAddToMp();
		if(addToMp > 0)
			getEffected().setCurrentMp(getEffected().getCurrentMp() + addToMp);

		return true;
	}
}
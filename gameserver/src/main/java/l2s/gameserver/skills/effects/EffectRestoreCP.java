package l2s.gameserver.skills.effects;

import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public class EffectRestoreCP extends EffectRestore
{
	public EffectRestoreCP(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	private int calcAddToCp()
	{
		if(getEffected().isHealBlocked())
			return 0;

		if(!getEffected().isPlayer())
			return 0;

		double power = calc();
		if(power <= 0)
			return 0;

		if(_percent)
			power = getEffected().getMaxCp() / 100. * power;

		if(!_staticPower)
		{
			if(!_ignoreBonuses)
				power *= getEffected().calcStat(Stats.CPHEAL_EFFECTIVNESS, 100., getEffector(), getSkill()) / 100.;
		}

		double newCp = getEffected().getCurrentCp() + power;
		newCp = Math.max(0, Math.min(newCp, getEffected().getMaxCp() / 100. * getEffected().calcStat(Stats.CP_LIMIT, null, null)));

		return (int) Math.max(0, newCp - getEffected().getCurrentCp());
	}

	@Override
	public void onStart()
	{
		super.onStart();

		if(!getTemplate().isInstant())
			return;

		int addToCp = calcAddToCp();
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

		int addToCp = calcAddToCp();
		if(addToCp > 0)
			getEffected().setCurrentCp(getEffected().getCurrentCp() + addToCp);

		return true;
	}
}
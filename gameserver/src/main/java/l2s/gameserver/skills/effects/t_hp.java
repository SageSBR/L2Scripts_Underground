package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public class t_hp extends Effect
{
	private final boolean _percent;

	public t_hp(Env env, EffectTemplate template)
	{
		super(env, template);
		_percent = getTemplate().getParam().getBool("percent", false);
	}

	@Override
	public boolean onActionTime()
	{
		if(getEffected().isDead())
			return false;

		double hp = calc();
		if(_percent)
			hp = getEffected().getMaxHp() / 100 * hp;

		if(hp > 0)
		{
			//TODO: Реализовать Хилку.
		}
		else if(hp < 0)
		{
			boolean awake = !getEffected().isNpc() && getEffected() != getEffector(); // TODO: Check this.
			boolean standUp = getEffected() != getEffector(); // TODO: Check this.
			boolean directHp = getEffector().isNpc() || getEffected() == getEffector(); // TODO: Check this.
			getEffected().reduceCurrentHp(Math.abs(hp), getEffector(), getSkill(), awake, standUp, directHp, false, false, true, false);
		}
		return true;
	}
}
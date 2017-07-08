package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
 */
public final class i_soul_shot extends Effect
{
	private final double _power;

	public i_soul_shot(Env env, EffectTemplate template)
	{
		super(env, template);
		_power = template.getParam().getDouble("power", 100.);
	}

	@Override
	public void onStart()
	{
		getEffected().sendPacket(SystemMsg.YOUR_SOULSHOTS_ARE_ENABLED);
		getEffected().setChargedSoulshotPower(_power);
	}
}
package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
 */
public final class i_fishing_shot extends Effect
{
	private final double _power;

	public i_fishing_shot(Env env, EffectTemplate template)
	{
		super(env, template);
		_power = template.getParam().getDouble("power", 100.);
	}

	@Override
	public boolean checkCondition()
	{
		if(!getEffected().isPlayer())
			return false;

		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		if(!getEffected().isPlayer())
			return;

		getEffected().sendPacket(SystemMsg.YOUR_SPIRITSHOT_HAS_BEEN_ENABLED); // TODO: Check message.
		getEffected().getPlayer().setChargedFishshotPower(_power);
	}
}
package l2s.gameserver.skills.effects;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public final class EffectCharge extends Effect
{
	// Максимальное количество зарядов находится в поле val="xx"

	public EffectCharge(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();

		if(getEffected().isPlayer())
		{
			final Player player = (Player) getEffected();

			if(player.getIncreasedForce() >= calc())
				player.sendPacket(SystemMsg.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY_);
			else
				player.setIncreasedForce(player.getIncreasedForce() + 1);
		}
	}
}

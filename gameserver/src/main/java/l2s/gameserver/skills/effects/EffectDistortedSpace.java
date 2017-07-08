package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public final class EffectDistortedSpace extends Effect
{
	public EffectDistortedSpace(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		getEffected().startDistortedSpace();
	}

	@Override
	public void onExit()
	{
		super.onExit();
		getEffected().stopDistortedSpace();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
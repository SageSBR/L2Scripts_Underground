package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public final class EffectDeathPenalty extends Effect
{
	public EffectDeathPenalty(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public boolean isHidden()
	{
		return true;
	}
}
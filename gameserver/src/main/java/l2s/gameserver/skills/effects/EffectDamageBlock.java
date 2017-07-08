package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public final class EffectDamageBlock extends Effect
{
	private final boolean _withException;

	public EffectDamageBlock(Env env, EffectTemplate template)
	{
		super(env, template);
		_withException = template.getParam().getBool("with_exception", false);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		getEffected().startDamageBlocked();
		if(_withException)
		{
			if(getEffected() == getEffector())
			{
				if(getSkill() == getEffector().getCastingSkill())
					getEffected().setDamageBlockedException(getEffector().getCastingTarget());
				else if(getSkill() == getEffector().getDualCastingSkill())
					getEffected().setDamageBlockedException(getEffector().getDualCastingTarget());
			}
			else
				getEffected().setDamageBlockedException(getEffector());
		}
	}

	@Override
	public void onExit()
	{
		super.onExit();
		getEffected().stopDamageBlocked();
		getEffected().setDamageBlockedException(null);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
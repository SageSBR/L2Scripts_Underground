package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public final class EffectEffectImmunity extends Effect
{
	private final boolean _withException;

	public EffectEffectImmunity(Env env, EffectTemplate template)
	{
		super(env, template);
		_withException = template.getParam().getBool("with_exception", false);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		getEffected().startEffectImmunity();
		if(_withException)
		{
			if(getEffected() == getEffector())
			{
				if(getSkill() == getEffector().getCastingSkill())
					getEffected().setEffectImmunityException(getEffector().getCastingTarget());
				else if(getSkill() == getEffector().getDualCastingSkill())
					getEffected().setEffectImmunityException(getEffector().getDualCastingTarget());
			}
			else
				getEffected().setEffectImmunityException(getEffector());
		}
	}

	@Override
	public void onExit()
	{
		super.onExit();
		getEffected().stopEffectImmunity();
		getEffected().setEffectImmunityException(null);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
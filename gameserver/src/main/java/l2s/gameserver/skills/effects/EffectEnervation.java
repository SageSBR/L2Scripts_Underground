package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public class EffectEnervation extends Effect
{
	public EffectEnervation(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(_effected.isNpc())
			((NpcInstance) _effected).setParameter("DebuffIntention", 0.5);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		if(_effected.isNpc())
			((NpcInstance) _effected).setParameter("DebuffIntention", 1.);
	}
}
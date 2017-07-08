package l2s.gameserver.skills.effects;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.List;

import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public final class p_dual_cast extends Effect
{
	private final TIntSet _elementalSkills;

	public p_dual_cast(Env env, EffectTemplate template)
	{
		super(env, template);
		_elementalSkills = new TIntHashSet(template.getParam().getIntegerArray("elemental_skills", new int[0]));
	}

	@Override
	public void onStart()
	{
		getEffected().setDualCastEnable(true);
		for(int skillId : _elementalSkills.toArray())
		{
			Skill skill = getEffected().getKnownSkill(skillId);
			if(skill == null)
				continue;

			if(getEffected().getEffectList().containsEffects(skill))
				continue;

			skill.getEffects(getEffector(), getEffected());
		}
		super.onStart();
	}

	@Override
	public void onExit()
	{
		getEffected().setDualCastEnable(false);

		Effect previousEffect = null;
		for(Effect e : getEffected().getEffectList().getEffects())
		{
			if(!_elementalSkills.contains(e.getSkill().getId()))
				continue;

			if(previousEffect == null || previousEffect.getStartTime() > e.getStartTime())
				previousEffect = e;
		}

		int previousSkillId = previousEffect == null ? 0 : previousEffect.getSkill().getId();
		for(int skillId : _elementalSkills.toArray())
		{
			if(skillId != previousSkillId)
				getEffected().getEffectList().stopEffects(skillId);
		}

		super.onExit();
	}
}
package l2s.gameserver.skills.effects;

import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Skill.SkillType;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * 
 * @author nonam3
 * @date 08/01/2011 17:37
 *
 */
public final class EffectRemoveTarget extends Effect
{
	private final boolean _stopTarget;

	public EffectRemoveTarget(Env env, EffectTemplate template)
	{
		super(env, template);
		_stopTarget = template.getParam().getBool("stop_target", false);
	}

	@Override
	public void onStart()
	{
		super.onStart();

		if(getEffected().getAI() instanceof DefaultAI)
			((DefaultAI) getEffected().getAI()).setGlobalAggro(System.currentTimeMillis() + 3000L);

		getEffected().setTarget(null);

		if(_stopTarget)
			getEffected().stopMove();

		getEffected().abortAttack(true, true);

		Skill castingSkill = getEffected().getCastingSkill();
		if(castingSkill == null || !(castingSkill.getSkillType() == SkillType.TAKECASTLE || castingSkill.getSkillType() == SkillType.TAKEFORTRESS))
			getEffected().abortCast(true, true, true, false);

		castingSkill = getEffected().getDualCastingSkill();
		if(castingSkill == null || !(castingSkill.getSkillType() == SkillType.TAKECASTLE || castingSkill.getSkillType() == SkillType.TAKEFORTRESS))
			getEffected().abortCast(true, true, false, true);

		getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, getEffector());
	}
}
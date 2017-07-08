package l2s.gameserver.skills.effects;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.FinishRotatingPacket;
import l2s.gameserver.network.l2.s2c.StartRotatingPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.stats.Formulas.AttackInfo;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public class i_p_attack extends Effect
{
	private final boolean _onCrit;
	private final boolean _directHp;
	private final boolean _turner;
	private final boolean _blow;

	public i_p_attack(Env env, EffectTemplate template)
	{
		super(env, template);

		_onCrit = template.getParam().getBool("onCrit", false);
		_directHp = template.getParam().getBool("directHp", false);
		_turner = template.getParam().getBool("turner", false);
		_blow = template.getParam().getBool("blow", false);
	}

	@Override
	public void onStart()
	{
		super.onStart();

		if(getEffected().isDead())
			return;

		if(_turner && !getEffected().isInvul())
		{
			getEffected().broadcastPacket(new StartRotatingPacket(getEffected(), getEffected().getHeading(), 1, 65535));
			getEffected().broadcastPacket(new FinishRotatingPacket(getEffected(), getEffector().getHeading(), 65535));
			getEffected().setHeading(getEffector().getHeading());
			getEffected().sendPacket(new SystemMessagePacket(SystemMsg.S1S_EFFECT_CAN_BE_FELT).addSkillName(getSkill()));
		}

		final Creature realTarget = isReflected() ? getEffector() : getEffected();
		final AttackInfo info = Formulas.calcPhysDam(getEffector(), realTarget, getSkill(), calc(), false, _blow, getSkill().isSSPossible(), _onCrit);

		realTarget.reduceCurrentHp(info.damage, getEffector(), getSkill(), true, true, _directHp, true, false, false, calc() != 0, true, info.crit || info.blow, false, false, false);

		if(!info.miss || info.damage >= 1)
		{
			double lethalDmg = Formulas.calcLethalDamage(getEffector(), realTarget, getSkill());
			if(lethalDmg > 0)
				realTarget.reduceCurrentHp(lethalDmg, getEffector(), getSkill(), true, true, false, false, false, false, false);
			else if(!isReflected())
				realTarget.doCounterAttack(getSkill(), getEffector(), _blow);
		}
	}
}
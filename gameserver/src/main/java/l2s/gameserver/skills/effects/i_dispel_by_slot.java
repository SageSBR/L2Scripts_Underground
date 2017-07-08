package l2s.gameserver.skills.effects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;
import l2s.gameserver.utils.EffectsComparator;

/**
 * @author Bonux
**/
public class i_dispel_by_slot extends Effect
{
	private final AbnormalType _abnormalType;
	private final int _maxAbnormalLvl;
	private final boolean _self;

	public i_dispel_by_slot(Env env, EffectTemplate template)
	{
		super(env, template);

		_abnormalType = template.getParam().getEnum("abnormal_type", AbnormalType.class);
		if(_abnormalType == AbnormalType.none)
			_maxAbnormalLvl = 0;
		else
			_maxAbnormalLvl = template.getParam().getInteger("max_abnormal_level", 0);

		if(template.getEffectType() == EffectType.i_dispel_by_slot_myself)
			_self = true;
		else
			_self = false;
	}

	@Override
	public void onStart()
	{
		super.onStart();

		if(_maxAbnormalLvl == 0)
			return;

		final Creature target = _self ? getEffector() : getEffected();

		final List<Skill> dispelledSkills = new ArrayList<Skill>();

		final List<Effect> effects = new ArrayList<Effect>(target.getEffectList().getEffects());
		Collections.sort(effects, EffectsComparator.getInstance()); // ToFix: Comparator to HF
		Collections.reverse(effects);

		for(Effect effect : effects)
		{
			/*if(!effect.isCancelable())
				continue;*/

			Skill effectSkill = effect.getSkill();
			if(effectSkill == null)
				continue;

			if(effectSkill.isToggle())
				continue;

			if(effectSkill.isPassive())
				continue;

			/*if(target.isSpecialEffect(effectSkill))
				continue;*/

			if(effect.getAbnormalType() != _abnormalType)
				continue;

			if(_maxAbnormalLvl != -1 && effect.getAbnormalLvl() > _maxAbnormalLvl)
				continue;

			effect.exit();

			if(!effect.isHidden() && !dispelledSkills.contains(effectSkill))
			{
				dispelledSkills.add(effectSkill);
				target.sendPacket(new SystemMessagePacket(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(effectSkill));
			}
		}
	}
}
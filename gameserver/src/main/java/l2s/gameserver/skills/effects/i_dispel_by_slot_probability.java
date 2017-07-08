package l2s.gameserver.skills.effects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;
import l2s.gameserver.utils.EffectsComparator;

/**
 * @author Bonux
**/
public class i_dispel_by_slot_probability extends Effect
{
	private final AbnormalType _abnormalType;
	private final int _dispelChance;

	public i_dispel_by_slot_probability(Env env, EffectTemplate template)
	{
		super(env, template);

		_abnormalType = template.getParam().getEnum("abnormal_type", AbnormalType.class);
		if(_abnormalType == AbnormalType.none)
			_dispelChance = 0;
		else
			_dispelChance = template.getParam().getInteger("dispel_chance", 100);
	}

	@Override
	public void onStart()
	{
		//TODO: [Bonux] Проверить и добавить резисты кансила.
		super.onStart();

		if(_dispelChance == 0)
			return;

		final List<Skill> dispelledSkills = new ArrayList<Skill>();
		final List<Skill> notDispelledSkills = new ArrayList<Skill>();

		final List<Effect> effects = new ArrayList<Effect>(getEffected().getEffectList().getEffects());
		Collections.sort(effects, EffectsComparator.getInstance()); // ToFix: Comparator to HF
		Collections.reverse(effects);

		for(Effect effect : effects)
		{
			if(!effect.isCancelable())
				continue;

			Skill effectSkill = effect.getSkill();
			if(effectSkill == null)
				continue;

			if(notDispelledSkills.contains(effectSkill))
				continue;

			if(effectSkill.isToggle())
				continue;

			if(effectSkill.isPassive())
				continue;

			/*if(getEffected().isSpecialEffect(effectSkill))
				continue;*/

			if(effect.getAbnormalType() != _abnormalType)
				continue;

			boolean dispelled = dispelledSkills.contains(effectSkill);
			if(dispelled || Rnd.chance(_dispelChance))
			{
				effect.exit();
				dispelledSkills.add(effectSkill);

				if(!effect.isHidden() && !dispelled)
					getEffected().sendPacket(new SystemMessagePacket(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(effectSkill));
			}
			else
				notDispelledSkills.add(effectSkill);
		}
	}
}
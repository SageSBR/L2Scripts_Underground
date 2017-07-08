package l2s.gameserver.skills.effects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;
import l2s.gameserver.utils.EffectsComparator;

/**
 * @author Bonux
**/
public class i_dispel_all extends Effect
{
	public i_dispel_all(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();

		final List<Skill> dispelledSkills = new ArrayList<Skill>();

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

			if(effectSkill.isToggle())
				continue;

			if(effectSkill.isPassive())
				continue;

			if(getEffected().isSpecialEffect(effectSkill))
				continue;

			effect.exit();

			if(!effect.isHidden() && !dispelledSkills.contains(effectSkill))
			{
				dispelledSkills.add(effectSkill);
				getEffected().sendPacket(new SystemMessagePacket(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(effectSkill));
			}
		}
	}
}
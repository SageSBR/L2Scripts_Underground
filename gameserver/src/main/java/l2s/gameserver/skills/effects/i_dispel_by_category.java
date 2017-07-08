package l2s.gameserver.skills.effects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.skill.EffectTemplate;
import l2s.gameserver.utils.EffectsComparator;

/**
 * @author Bonux
**/
public class i_dispel_by_category extends Effect
{
	private static enum AbnormalCategory
	{
		slot_buff,
		slot_debuff
	}

	private final AbnormalCategory _abnormalCategory;
	private final int _dispelChance;
	private final int _maxCount;

	public i_dispel_by_category(Env env, EffectTemplate template)
	{
		super(env, template);

		_abnormalCategory = template.getParam().getEnum("abnormal_category", AbnormalCategory.class);
		_dispelChance = template.getParam().getInteger("dispel_chance", 100);
		_maxCount = template.getParam().getInteger("max_count", 0);
	}

	@Override
	public void onStart()
	{
		//TODO: [Bonux] Проверить и добавить резисты кансила.
		super.onStart();

		if(_dispelChance == 0 || _maxCount == 0)
			return;

		final List<Skill> dispelledSkills = new ArrayList<Skill>();
		final List<Skill> notDispelledSkills = new ArrayList<Skill>();

		final List<Effect> effects = new ArrayList<Effect>(getEffected().getEffectList().getEffects());
		Collections.sort(effects, EffectsComparator.getInstance()); // ToFix: Comparator to HF
		Collections.reverse(effects);

		if(_abnormalCategory == AbnormalCategory.slot_debuff)
		{
			for(Effect effect : effects)
			{
				if(!effect.isCancelable())
					continue;

				Skill effectSkill = effect.getSkill();
				if(effectSkill == null)
					continue;

				if(notDispelledSkills.contains(effectSkill))
					continue;

				boolean dispelled = dispelledSkills.contains(effectSkill);
				if(_maxCount > 0 && !dispelled && dispelledSkills.size() >= _maxCount)
					continue;

				if(!effect.isOffensive())
					continue;

				if(effectSkill.isToggle())
					continue;

				if(effectSkill.isPassive())
					continue;

				if(getEffected().isSpecialEffect(effectSkill))
					continue;

				if(effectSkill.getMagicLevel() <= 0)
					continue;

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
		else if(_abnormalCategory == AbnormalCategory.slot_buff)
		{
			for(Effect effect : effects)
			{
				if(!effect.isCancelable())
					continue;

				Skill effectSkill = effect.getSkill();
				if(effectSkill == null)
					continue;

				if(notDispelledSkills.contains(effectSkill))
					continue;

				boolean dispelled = dispelledSkills.contains(effectSkill);
				if(_maxCount > 0 && !dispelled && dispelledSkills.size() >= _maxCount)
					continue;

				if(effect.isOffensive())
					continue;

				if(effectSkill.isToggle())
					continue;

				if(effectSkill.isPassive())
					continue;

				if(getEffected().isSpecialEffect(effectSkill))
					continue;

				if(effectSkill.getMagicLevel() <= 0)
					continue;

				if(dispelled || calcCancelChance(effect))
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

	private boolean calcCancelChance(Effect effect) // TODO: [Bonux] Пересмотреть эту формулу.
	{

		double prelim_chance, cancel_res_multiplier = getEffected().calcStat(Stats.CANCEL_RESIST, 0, null, null); // constant resistance is applied for whole cycle of cancellation

		int cancellMagicLvl = getSkill().getMagicLevel() > 0 ? getSkill().getMagicLevel() : getEffector().getLevel(); // FIXME: no effect can have have mLevel == 0. Tofix in skilldata
		int effectMagicLvl = effect.getSkill().getMagicLevel() > 0 ? effect.getSkill().getMagicLevel() : getEffected().getLevel(); // FIXME: no effect can have have mLevel == 0. Tofix in skilldata
		int dml = cancellMagicLvl - effectMagicLvl;
		int buff_duration = effect.getTimeLeft();

		if(cancellMagicLvl < 85 && effectMagicLvl >= 85)
			return false;

		cancel_res_multiplier = 1 - (cancel_res_multiplier * .01);
		prelim_chance = (2. * dml + _dispelChance + buff_duration / 120) * cancel_res_multiplier; // retail formula
		prelim_chance = Math.max(Math.min(prelim_chance, 75), 25);

		return Rnd.chance(prelim_chance);
	}
}
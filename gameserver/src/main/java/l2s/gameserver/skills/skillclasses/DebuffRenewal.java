package l2s.gameserver.skills.skillclasses;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.templates.StatsSet;

public class DebuffRenewal extends Skill
{
	public DebuffRenewal(StatsSet set)
	{
		super(set);
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		renewEffects(target);
		target.updateEffectIcons();
	}

	private void renewEffects(Creature target)
	{
		TIntSet skillsToRefresh = new TIntHashSet();

		for(Effect effect : target.getEffectList().getEffects())
		{
			if(effect.isOffensive())
				skillsToRefresh.add(effect.getSkill().getId());
		}

		for(Effect effect : target.getEffectList().getEffects())
		{
			if(skillsToRefresh.contains(effect.getSkill().getId()))
				effect.restart();
		}
	}
}
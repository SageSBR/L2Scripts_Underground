package l2s.gameserver.stats.conditions;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.stats.Env;

public final class ConditionTargetHasBuffId extends Condition
{
	private final int _id;
	private final int _level;

	public ConditionTargetHasBuffId(int id, int level)
	{
		_id = id;
		_level = level;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		Creature target = env.target;
		if(target == null)
			return false;

		for(Effect effect : target.getEffectList().getEffects())
		{
			if(effect.getSkill().getId() != _id)
				continue;

			if(_level == -1)
				return true;

			if(effect.getSkill().getLevel() >= _level)
				return true;
		}

		return false;
	}
}

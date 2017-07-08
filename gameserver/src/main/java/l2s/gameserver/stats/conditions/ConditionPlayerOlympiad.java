package l2s.gameserver.stats.conditions;

import l2s.gameserver.stats.Env;

public class ConditionPlayerOlympiad extends Condition
{
	private final boolean _value;

	public ConditionPlayerOlympiad(boolean v)
	{
		_value = v;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(env.character.isPlayer()) 
			if(env.character.getPlayer().getLfcGame() != null || env.character.isInTvT() || env.character.isInCtF() || env.character.isInLastHero())
				return false;
		if(env.character.isInOlympiadMode())
			return env.character.isInOlympiadMode() == _value;
		if(env.character.getPlayer().isChaosFestivalParticipant())
			return env.character.getPlayer().isChaosFestivalParticipant() == _value;
		return env.character.isInOlympiadMode() == _value || env.character.getPlayer().isChaosFestivalParticipant() == _value;
	}
}
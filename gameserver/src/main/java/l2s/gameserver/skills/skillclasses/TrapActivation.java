package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.TrapInstance;
import l2s.gameserver.templates.StatsSet;

public class TrapActivation extends Skill
{
	
	public final int _range;
	
	public TrapActivation(StatsSet set)
	{
		super(set);
		_range = set.getInteger("trapRange", 600);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!activeChar.isPlayer())
			return false;
		if(activeChar.getPlayer().getTraps() == null || activeChar.getPlayer().getTraps().isEmpty() || activeChar.getPlayer().getTraps().size() > 1)
			return false;
		TrapInstance trap = activeChar.getPlayer().getFirstTrap();
		if(trap == null || activeChar.getDistance(trap) > _range) //max range to cast.
			return false;
		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void onEndCast(Creature activeChar, List<Creature> targets)
	{
		super.onEndCast(activeChar, targets);

		if(!activeChar.isPlayer())
			return;

		TrapInstance trap = activeChar.getPlayer().getFirstTrap();
		if(trap != null)
			trap.selfDestroy();
	}
}
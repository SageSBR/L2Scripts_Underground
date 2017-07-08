package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.SkillChain;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.utils.Location;

/**
 *
 * @author monithly
 */
public class ChainCall extends Skill
{
	public ChainCall(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!activeChar.isPlayer())
			return false;

		if(activeChar.isPlayable() && activeChar.getPlayer().isInFightClub() && !activeChar.getPlayer().getFightClubEvent().canUsePositiveMagic(activeChar, target))
			return false;
		
		if(target.isInRange(new Location(-114598,-249431,-2984), 5000))
		{
			return false;
		}

		if(!activeChar.getPlayer().getSkillChainDetails().containsKey(getChainIndex()))
			return false;

		if(!activeChar.getPlayer().getSkillChainDetails().get(getChainIndex()).isActive())
			return false;

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void onEndCast(Creature activeChar, List<Creature> targets)
	{
		super.onEndCast(activeChar, targets);

		final SkillChain sc = activeChar.getPlayer().getSkillChainDetails().get(getChainIndex());
		if(sc != null && sc.isActive())
		{
			activeChar.doCast(sc.getChainSkill(), sc.getTarget(), true);
			activeChar.getPlayer().removeChainDetail(getChainIndex());
			sc.getTarget().getEffectList().stopEffects(sc.getCastingSkill());
		}
	}
}

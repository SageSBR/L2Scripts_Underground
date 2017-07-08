package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.StatsSet;

public class Aggression extends Skill
{
	private final boolean _unaggring;
	private final boolean _silent;

	public Aggression(StatsSet set)
	{
		super(set);
		_unaggring = set.getBool("unaggroing", false);
		_silent = set.getBool("silent", false);
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		if(!target.isAutoAttackable(activeChar))
			return;

		int effect = getEffectPoint();
		if(isSSPossible())
		{
			if(isMagic())
				effect *= ((100 + activeChar.getChargedSpiritshotPower()) / 100.);
			else
				effect *= ((100 + activeChar.getChargedSoulshotPower()) / 100.);
		}

		if(target.isNpc())
		{
			if(_unaggring)
			{
				if(target.isNpc() && activeChar.isPlayable())
					((NpcInstance) target).getAggroList().addDamageHate(activeChar, 0, -effect);
			}
			else
			{
				target.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, activeChar, effect);
				if(!_silent)
					target.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, activeChar, 0);
			}
		}
		else if(target.isPlayable() && !target.isDebuffImmune())
		{
			target.setTarget(activeChar);

			// Force attack
			int atkRange = target.getPhysicalAttackRange();
			if(target.getRealDistance(activeChar) <= atkRange)
				target.doAttack(activeChar);
			else
				target.moveToLocation(activeChar.getLoc(), atkRange, true);
		}
	}
}
package npc.model.residences.clanhall;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.residences.clanhall.CTBBossInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import ai.residences.clanhall.MatchTrief;

/**
 * @author VISTALL
 * @date 19:55/22.04.2011
 */
public class MatchTriefInstance extends CTBBossInstance
{
	private static final long serialVersionUID = 1L;

	private long _massiveDamage;

	public MatchTriefInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflectAndAbsorb, boolean transferDamage, boolean isDot, boolean sendReceiveMessage, boolean sendGiveMessage, boolean crit, boolean miss, boolean shld, boolean magic)
	{
		if(_massiveDamage > System.currentTimeMillis())
		{
			damage = 10000;
			if(Rnd.chance(10))
				((MatchTrief)getAI()).hold();
		}
		else if(getCurrentHpPercents() > 50)
		{
			if(attacker.isPlayer())
				damage = ((damage / getMaxHp()) / 0.05) * 100;
			else
				damage = ((damage / getMaxHp()) / 0.05) * 10;
		}
		else if(getCurrentHpPercents() > 30)
		{
			if(Rnd.chance(90))
			{
				if(attacker.isPlayer())
					damage = ((damage / getMaxHp()) / 0.05) * 100;
				else
					damage = ((damage / getMaxHp()) / 0.05) * 10;
			}
			else
				_massiveDamage = System.currentTimeMillis() + 5000L;
		}
		else
			_massiveDamage = System.currentTimeMillis() + 5000L;

		super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflectAndAbsorb, transferDamage, isDot, sendReceiveMessage, sendGiveMessage, crit, miss, shld, magic);
	}
}

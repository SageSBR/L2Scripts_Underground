package ai;

import instances.Kartia;

import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.GuardInstance;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.PositionUtils;

public class KartiaMobs extends Fighter
{

	public KartiaMobs(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		Reflection reflection = actor.getReflection();
		if(reflection != null)
		{
			if(reflection instanceof Kartia)
			{
				final Kartia kartiaInstance = (Kartia) reflection;
				if(kartiaInstance.getMonsterSet().get("ruler").intValue() == actor.getNpcId())
					Functions.npcSay(actor, NpcString.HOW_ITS_IMPOSSIBLE_RETURNING_TO_ABYSS_AGAIN);
				if(kartiaInstance.getStatus() == 1 && kartiaInstance.getMonsterSet().get("overseer").intValue() == actor.getNpcId())
				{
					kartiaInstance.openRaidDoor();
				}
			}
		}
		super.onEvtDead(killer);
	}

	@Override
	protected void onEvtAttacked(final Creature attacker, int damage)
	{
		final NpcInstance actor = getActor();

		Reflection reflection = actor.getReflection();
		if(reflection != null)
		{
			if(reflection instanceof Kartia)
			{
				final Kartia kartiaInstance = (Kartia) reflection;
				if(kartiaInstance.getStatus() == 1 && kartiaInstance.getMonsterSet().get("overseer").intValue()  == actor.getNpcId() && (actor.getCurrentHp() / actor.getMaxHp() <= 0.4D))
				{
					Functions.npcSay(actor, NpcString.YOU_VERY_STRONG_FOR_MORTAL_I_RETREAT);
					Location loc = kartiaInstance.isPartyInstance() ? new Location(-120840, -13944, -11456) : new Location(-111297, -13904, -11440);
					DefaultAI ai = (DefaultAI) actor.getAI();
					ai.addTaskMove(Location.findPointToStay(loc, 50, actor.getGeoIndex()), true);
				}
			}
		}
		super.onEvtAttacked(attacker, damage);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		final NpcInstance actor = getActor();
		actor.setRunning();

		if(actor instanceof GuardInstance)
		{
			actor.setBusy(true);
			actor.setHaveRandomAnim(true);
		}
		else if(actor instanceof MonsterInstance)
		{
			actor.setRandomWalk(false);

			Reflection reflection = actor.getReflection();	
			if(reflection != null)
			{
				if(reflection instanceof Kartia)
				{
					Kartia kartiaInstance = (Kartia) reflection;	
					if(kartiaInstance.getMonsterSet().get("overseer").intValue() == actor.getNpcId())
					{
						if(kartiaInstance.getStatus() == 0)
							Functions.npcSay(actor, NpcString.INTRUDERS_CANNOT_LEAVE_ALIVE);
					}
					if(kartiaInstance.getMonsterSet().get("watcher").intValue() == actor.getNpcId() || kartiaInstance.getMonsterSet().get("keeper").intValue() == actor.getNpcId() ||
							kartiaInstance.getMonsterSet().get("overseer").intValue() == actor.getNpcId())
					{
						if(kartiaInstance.getStatus() == 0)
						{
							actor.setRunning();
							Location loc = kartiaInstance.isPartyInstance() ? new Location(-120888 + Rnd.get(-50, 50), -10424 + Rnd.get(-50, 50), -11710) : new Location(-111352 + Rnd.get(-50, 50), -10408 + Rnd.get(-50, 50), -11710);
							DefaultAI ai = (DefaultAI) actor.getAI();
							ai.addTaskMove(Location.findPointToStay(loc, 100, actor.getGeoIndex()), true);
						}
						else if(kartiaInstance.getStatus() == 2)
						{
							actor.setRunning();
							Location loc = kartiaInstance.isPartyInstance() ? new Location(-120872 + Rnd.get(-50, 50), -14648+ Rnd.get(-50, 50), -11452) : new Location(-111304 + Rnd.get(-50, 50), -14488+ Rnd.get(-50, 50), -11452);
							DefaultAI ai = (DefaultAI) actor.getAI();
							ai.addTaskMove(Location.findPointToStay(loc, 50, actor.getGeoIndex()), true);
						}
					}
				}
			}
		}
	}
	@Override
	protected void returnHome(boolean clearAggro, boolean teleport)
	{
		changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
	}

	@Override
	protected boolean maybeMoveToHome()
	{
		return false;
	}

}

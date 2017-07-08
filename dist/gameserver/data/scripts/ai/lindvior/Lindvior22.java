package ai.lindvior;

import instances.LindviorBoss;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;

/**
 * Created by Akeno on 04.12.2015.
 *
 * Lindvior22 by Akeno ID - 19425
 *
 */
public class Lindvior22 extends Fighter
{
	public Lindvior22(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		double decr = getStageDecr();
		getActor().setCurrentHpMp(getActor().getMaxHp()*decr, getActor().getMaxMp(), false);
		if(decr != 0.2) //end
			getActor().blockReward();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();

		if(actor.isDead())
			return;

		if(actor.getCurrentHpPercents() <= 20.0D) // превращаемся 5ую
		{
			actor.doDie(null);
			Reflection r = getActor().getReflection();
			if (r != null)
			{
				if(r instanceof LindviorBoss)
				{
					LindviorBoss lInst = (LindviorBoss) r;
					lInst.setStageDecr(0.2);
					//TODO Фикс 4й стании
					lInst.scheduleNextSpawnFor(29240, 10000L, actor.getSpawnedLoc(), 1);
					//lInst.scheduleNextSpawnFor(19425, 10000L, actor.getSpawnedLoc(), 1);
					lInst.announceToInstance(NpcString.LINDVIOR_HAS_LANDED);
				}
				else
					System.out.println("WARNING!!!");
			}
		}

		super.onEvtAttacked(attacker, damage);
	}

	private double getStageDecr()
	{
		Reflection r = getActor().getReflection();
		if (r != null)
		{
			if(r instanceof LindviorBoss)
			{
				LindviorBoss lInst = (LindviorBoss) r;
				return lInst.getStageDecr();
			}
		}
		return 1.;
	}
}
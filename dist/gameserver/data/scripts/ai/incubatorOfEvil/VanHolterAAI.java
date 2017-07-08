package ai.incubatorOfEvil;

import java.util.List;

import l2s.commons.util.Rnd;

import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.geodata.GeoEngine;

public class VanHolterAAI extends Fighter
{
	private NpcInstance target = null;
	private static final NpcString[] npcsay = new NpcString[] {
			//NpcString.BE_ON_YOUR_TOES,
			NpcString.SUCH_MONSTERS_IN_A_PLACE_LIKE_THIS_UNBELIEVABLE,
			 };


	public VanHolterAAI(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return false;
	}
	
	@Override
	protected void onEvtSpawn()
	{
		startAttack();
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		
		if(Rnd.chance(7))
		{
			Functions.npcSay(actor, Rnd.get(npcsay));
			return false;
		}

		return startAttack();
	}

	private boolean startAttack()
	{
		NpcInstance actor = getActor();
		if(target == null)
		{
			List<NpcInstance> around = actor.getAroundNpc(3000, 150);
			if(around != null && !around.isEmpty())
			{
				for(NpcInstance npc : around)
				{
					if(checkTarget(npc))
					{
						if(target == null || actor.getDistance3D(npc) < actor.getDistance3D(target))
							target = npc;
					}
				}
			}
		}

		if(target != null && !actor.isAttackingNow() && !actor.isCastingNow() && !target.isDead() && GeoEngine.canSeeTarget(actor, target, false) && target.isVisible())
		{
			actor.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, target, 1);
			return true;
		}

		if(target != null && (!target.isVisible() || target.isDead() || !GeoEngine.canSeeTarget(actor, target, false)))
		{
			target = null;
			return false;
		}
		
		return false;
	}

	private boolean checkTarget(NpcInstance target)
	{
		if(target == null)
			return false;
		int _id = target.getNpcId();

		if(_id == 33170 || _id == 33171 || _id == 33172 || _id == 33173 || _id == 33174 || _id == 33414 || _id == 33415 || _id == 33416 || _id == 34001 || _id == 33979)
			return false;
			
		return true;
	}
}
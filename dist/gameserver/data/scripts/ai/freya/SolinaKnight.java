package ai.freya;

import java.util.List;

import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;

public class SolinaKnight extends Fighter
{
	private static final int SCARECROW_NPC_ID = 18912;	// Чучело

	private NpcInstance scarecrow = null;

	public SolinaKnight(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected boolean thinkActive()
	{
		if(super.thinkActive())
			return true;

		if(scarecrow == null)
		{
			List<NpcInstance> around = getActor().getAroundNpc(300, 100);
			if(around != null && !around.isEmpty())
			{
				for(NpcInstance npc : around)
				{
					if(npc.getNpcId() == SCARECROW_NPC_ID)
					{
						if(scarecrow == null || getActor().getDistance3D(npc) < getActor().getDistance3D(scarecrow))
							scarecrow = npc;
					}
				}
			}
		}

		if(scarecrow != null)
		{
			getActor().getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, scarecrow, 1);
			return true;
		}

		return false;
	}
}
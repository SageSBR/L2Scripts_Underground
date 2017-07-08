package ai;

import java.util.List;

import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.Defender;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;

/**
 * @author Bonux
 */
public class TrainingSoldier extends Defender
{
	private static final int TRAINING_DUMMY_NPC_ID = 33023;	// Чучело Арены
	private static final String SCARECROW_ID_VAR = "scarecrow_id";

	private final int _scarecrowId;

	private NpcInstance _trainingDummy = null;

	public TrainingSoldier(NpcInstance actor)
	{
		super(actor);
		_scarecrowId = actor.getParameter(SCARECROW_ID_VAR, 0);
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

		if(_scarecrowId == 0)
			return false;

		if(_trainingDummy == null)
		{
			List<NpcInstance> around = getActor().getAroundNpc(300, 100);
			if(around != null && !around.isEmpty())
			{
				for(NpcInstance npc : around)
				{
					if(npc.getNpcId() == TRAINING_DUMMY_NPC_ID && npc.getParameter(SCARECROW_ID_VAR, 0) == _scarecrowId)
					{
						_trainingDummy = npc;
						break;
					}
				}
			}
		}

		if(_trainingDummy != null)
		{
			getActor().getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, _trainingDummy, 100);
			return true;
		}

		return false;
	}
}
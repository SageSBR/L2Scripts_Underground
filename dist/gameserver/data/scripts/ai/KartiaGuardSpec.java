package ai;

import instances.Kartia;
import l2s.gameserver.ai.Guard;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;

public class KartiaGuardSpec extends Guard
{

	public KartiaGuardSpec(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
		if(getActor().getNpcId() == 33641 || getActor().getNpcId() == 33643 || getActor().getNpcId() == 33645)
		{
			Reflection reflection = getActor().getReflection();
			Kartia kartiaInstance = (Kartia) reflection;
			if(target.isPlayer() || kartiaInstance.getStatus() == 0)
				return;
		}
		super.onEvtAggression(target, aggro);
	}


	@Override
	protected void onEvtSpawn()
	{
		if(getActor().getNpcId() == 33641 || getActor().getNpcId() == 33643 || getActor().getNpcId() == 33645)
			return;

		super.onEvtSpawn();
		final NpcInstance actor = getActor();
		actor.setRunning();
		actor.setBusy(true);
		actor.setHaveRandomAnim(false);
	}		
}

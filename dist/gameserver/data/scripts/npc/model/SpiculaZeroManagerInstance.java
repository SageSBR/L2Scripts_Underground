package npc.model;

import instances.SpiculaZero;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ReflectionUtils;
import java.util.Calendar;

/**
 * @author Rivelia
 */
public class SpiculaZeroManagerInstance extends NpcInstance
{
	private final static int SPICULA_ZERO_INSTANCE_ID = 231;

	public SpiculaZeroManagerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this)) 
			return;
		
		if(command.startsWith("requestSpiculaZero"))
		{
			Reflection r = player.getActiveReflection(SPICULA_ZERO_INSTANCE_ID);
			if (r != null && player.canReenterInstance(SPICULA_ZERO_INSTANCE_ID)) 
				player.teleToLocation(r.getTeleportLoc(), r);
			else if(player.canEnterInstance(SPICULA_ZERO_INSTANCE_ID)) 
				ReflectionUtils.enterReflection(player, new SpiculaZero(), SPICULA_ZERO_INSTANCE_ID);
		}
		super.onBypassFeedback(player, command);
	}
}

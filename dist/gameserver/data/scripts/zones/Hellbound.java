package zones;

import l2s.gameserver.listener.actor.OnMoveListener;
import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.ReflectionUtils;

/**
 * @author Bonux
 */
public class Hellbound implements ScriptFile
{
	private static class MoveListener implements OnMoveListener
	{
		@Override
		public void onMove(Creature actor, Location loc)
		{
			if(!actor.isPlayer())
				return;

			Player player = actor.getPlayer();
			if(player.getLevel() < 99)
				player.teleToLocation(111382, 219202, -3536);
		}
	}

	private static class ZoneEnterLeaveListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature actor)
		{
			if(!actor.isPlayer())
				return;

			actor.addListener(MOVE_LISTENER);
		}

		@Override
		public void onZoneLeave(Zone zone, Creature actor)
		{
			actor.removeListener(MOVE_LISTENER);
		}
	}

	// Listeners
	private static final MoveListener MOVE_LISTENER = new MoveListener();
	private static final ZoneEnterLeaveListener ZONE_ENTER_LEAVE_LISTENER = new ZoneEnterLeaveListener();

	// Other
	private static final String HELLBOUND_ZONE_NAME = "[hellbound]";

	private void init()
	{
		Zone zone = ReflectionUtils.getZone(HELLBOUND_ZONE_NAME);
		if(zone != null)
			zone.addListener(ZONE_ENTER_LEAVE_LISTENER);
	}

	@Override
	public void onLoad()
	{
		init();
	}

	@Override
	public void onReload()
	{
		//
	}

	@Override
	public void onShutdown()
	{
		//
	}
}
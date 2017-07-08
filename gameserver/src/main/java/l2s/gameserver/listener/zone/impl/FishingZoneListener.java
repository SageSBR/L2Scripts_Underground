package l2s.gameserver.listener.zone.impl;

import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.network.l2.s2c.ExAutoFishAvailable;

/**
 * @author Bonux
 */
public class FishingZoneListener implements OnZoneEnterLeaveListener
{
	public static final OnZoneEnterLeaveListener STATIC = new FishingZoneListener();

	@Override
	public void onZoneEnter(Zone zone, Creature actor)
	{
		if(!actor.isPlayer())
			return;

		Player player = actor.getPlayer();

		/*if(player.isFishing())
			return;

		if(player.isInBoat())
			return;

		if(player.isInWater())
			return;*/

		if(player.isInPeaceZone())
			return;

		// TODO: Проверить условия на оффе.
		if(player.isTransformed() || player.isMounted())
			return;

		//player.sendPacket(ExAutoFishAvailable.SHOW);
	}

	@Override
	public void onZoneLeave(Zone zone, Creature actor)
	{
		if(!actor.isPlayer())
			return;

		//actor.sendPacket(ExAutoFishAvailable.REMOVE);
	}
}

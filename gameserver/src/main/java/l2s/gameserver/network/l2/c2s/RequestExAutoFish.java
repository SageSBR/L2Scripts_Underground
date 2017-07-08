package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone.ZoneType;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExAutoFishAvailable;

public class RequestExAutoFish extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(!activeChar.isInZone(ZoneType.FISHING))
			return;
		
		activeChar.sendPacket(SystemMsg.YOU_CAN_ONLY_FUSH_DURING_THE_PAID_PERIOD);
		activeChar.sendPacket(ExAutoFishAvailable.REMOVE);
	}
}
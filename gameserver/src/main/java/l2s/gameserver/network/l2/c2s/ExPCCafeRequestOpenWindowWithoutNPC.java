package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.NpcHtmlMessagePacket;

/**
 * @author Bonux
**/
public class ExPCCafeRequestOpenWindowWithoutNPC extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(activeChar, null);
		html.setFile("pc_bang_shop.htm");
		activeChar.sendPacket(html);
	}
}
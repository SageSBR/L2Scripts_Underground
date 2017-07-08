package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Config;
import l2s.gameserver.handler.bbs.CommunityBoardManager;
import l2s.gameserver.handler.bbs.ICommunityBoardHandler;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.GameClient;
import l2s.gameserver.network.l2.components.IStaticPacket;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;

public class RequestShowBoard
		extends L2GameClientPacket {
	private int _unknown;

	@Override
	public void readImpl() {
		this._unknown = this.readD();
	}

	@Override
	public void runImpl() {
		Player activeChar = ((GameClient)this.getClient()).getActiveChar();
		if (activeChar == null) {
			return;
		}
		activeChar.isntAfk();
		if (Config.BBS_ENABLED) {
			ICommunityBoardHandler handler = CommunityBoardManager.getInstance().getCommunityHandler(Config.BBS_DEFAULT_PAGE);
			if (handler != null) {
				handler.onBypassCommand(activeChar, Config.BBS_DEFAULT_PAGE);
			}
		} else {
			activeChar.sendPacket((IStaticPacket)new SystemMessagePacket(SystemMsg.THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE));
		}
	}
}


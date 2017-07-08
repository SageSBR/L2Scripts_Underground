package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Config;
import l2s.gameserver.network.l2.GameClient;
import l2s.gameserver.network.l2.c2s.L2GameClientPacket;
import l2s.gameserver.network.l2.s2c.ServerCloseSocketPacket;
import l2s.gameserver.network.l2.s2c.VersionCheckPacket;
import l2s.gameserver.network.l2.s2c.SendStatus;
import ru.akumu.smartguard.GuardConfig;
import ru.akumu.smartguard.SmartGuard;
import ru.akumu.smartguard.network.packets.MsgPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolVersion extends L2GameClientPacket {
	private static final Logger _log = LoggerFactory.getLogger(ProtocolVersion.class);
	private static final String _C__00_PROTOCOLVERSION = "[C] 00 ProtocolVersion (SmartGuard)";
	private static final ServerCloseSocketPacket SERVER_CLOSE = new ServerCloseSocketPacket();
	private static final short BasePacketSize = 260;
	private int protocol;
	private byte[] data;

	public ProtocolVersion() {
	}

	protected void readImpl() {
		this.protocol = this.readD();
		if(GuardConfig.ProtectionEnabled && this._buf.remaining() >= 262) {
			this._buf.position(this._buf.position() + 260);
			int dataLen = this.readH();
			if(this._buf.remaining() >= dataLen) {
				this.data = new byte[dataLen];
				this.readB(this.data);
			}
		}

	}

	protected void runImpl() {
		if(!Config.AVAILABLE_PROTOCOL_REVISIONS.contains(this.protocol)) {
			_log.warn("Unknown protocol revision : " + protocol + ", client : " + _client);
			getClient().close(new VersionCheckPacket(null));
			//((GameClient)this._client).close(SERVER_CLOSE);
		}
		else if(protocol == -2)
		{
			_client.closeNow(false);
			return;
		}
		else if(protocol == -3)
		{
			_log.info("Status request from IP : " + getClient().getIpAddr());
			getClient().close(new SendStatus());
			return;
		}
		else
		{
			if(GuardConfig.ProtectionEnabled) {
				if(this.data == null) {
					getClient().close(SERVER_CLOSE);
					return;
				}

				MsgPacket result = SmartGuard.checkClient(_client, this.data);
				if(result != null) {
					getClient().close(result);
					return;
				}
			}

			getClient().setRevision(protocol);
			sendPacket(new VersionCheckPacket(_client.enableCrypt()));
		}
	}

	public String getType() {
		return "[C] 00 ProtocolVersion (SmartGuard)";
	}
}

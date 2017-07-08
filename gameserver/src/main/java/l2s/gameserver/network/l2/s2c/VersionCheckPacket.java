//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2s.gameserver.network.l2.s2c;

import ru.akumu.smartguard.GuardConfig;
import ru.akumu.smartguard.manager.LicenseManager;

public class VersionCheckPacket extends L2GameServerPacket {
	private byte[] _key;

	public VersionCheckPacket(byte[] key) {
		this._key = key;
		if(GuardConfig.ProtectionEnabled) {
			LicenseManager.getInstance().cryptInternalData(key);
		}

	}

	public void writeImpl() {
		if(_key == null || _key.length == 0)
		{
			writeC(0x00);
			return;
		}
		//if(GuardConfig.ProtectionEnabled)
		//	this.writeC(46);
		this.writeC(0x01);

		for(int i = 0; i < 8; ++i) {
			this.writeC(this._key[i]);
		}

		writeD(0x01);
		writeD(0x00);	// Server ID
		writeC(0x00);
		writeD(0x00); // Seed (obfuscation key)
		writeC(0x00);	// Classic?
		writeC(0x00);	// Classic?
	}

	/* protected boolean writeOpcodes() {
		return true;
	} */
}

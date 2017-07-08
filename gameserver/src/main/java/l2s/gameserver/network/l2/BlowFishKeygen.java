//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2s.gameserver.network.l2;

import ru.akumu.smartguard.GuardConfig;

public class BlowFishKeygen {
	private static final byte[] _static = new byte[]{(byte)-56, (byte)39, (byte)-109, (byte)1, (byte)-95, (byte)108, (byte)49, (byte)-105};

	public BlowFishKeygen() {
	}

	public static byte[] getRandomKey() {
		byte[] result = new byte[16];

		for(int i = 0; i < 8; ++i) {
			result[i] = GuardConfig.ProtectionEnabled ? ru.akumu.smartguard.utils.Rnd.get() : (byte)l2s.commons.util.Rnd.get();
		}

		System.arraycopy(_static, 0, result, 8, 8);
		return result;
	}
}

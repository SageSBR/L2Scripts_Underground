//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2s.gameserver.network.l2;

import ru.akumu.smartguard.GuardConfig;
import ru.akumu.smartguard.manager.LicenseManager;
import ru.akumu.smartguard.manager.LicenseManager.KeyType;
import ru.akumu.smartguard.utils.crypt.KeyObject;
import ru.akumu.smartguard.utils.crypt.SCrypt;

public class GameCrypt {
    private KeyObject _inKo;
    private KeyObject _outKo;
    private byte[] _inKey;
    private byte[] _outKey;
    private boolean _isEnabled;
    private boolean _isProtected;

    public GameCrypt() {
    }

    public void setKey(byte[] key) {
        this._isProtected = GuardConfig.ProtectionEnabled;
        if(this._isProtected) {
            this._inKo = new KeyObject();
            this._outKo = new KeyObject();
            this._inKo = LicenseManager.getInstance().makeNetworkKey(KeyType.IN, key);
            this._outKo = LicenseManager.getInstance().makeNetworkKey(KeyType.OUT, key);
        } else {
            this._inKey = new byte[16];
            this._outKey = new byte[16];
            System.arraycopy(key, 0, this._inKey, 0, 16);
            System.arraycopy(key, 0, this._outKey, 0, 16);
        }

    }

    public void setKey(byte[] key, boolean value) {
        this.setKey(key);
    }

    public boolean decrypt(byte[] raw, int offset, int size) {
        if(!this._isEnabled) {
            return true;
        } else {
            if(this._isProtected) {
                SCrypt.crypt(raw, offset, size, this._inKo);
            } else {
                int temp = 0;

                int old;
                for(old = 0; old < size; ++old) {
                    int temp2 = raw[offset + old] & 255;
                    raw[offset + old] = (byte)(temp2 ^ this._inKey[old & 15] ^ temp);
                    temp = temp2;
                }

                old = this._inKey[8] & 255;
                old |= this._inKey[9] << 8 & '\uff00';
                old |= this._inKey[10] << 16 & 16711680;
                old |= this._inKey[11] << 24 & -16777216;
                old += size;
                this._inKey[8] = (byte)(old & 255);
                this._inKey[9] = (byte)(old >> 8 & 255);
                this._inKey[10] = (byte)(old >> 16 & 255);
                this._inKey[11] = (byte)(old >> 24 & 255);
            }

            return true;
        }
    }

    public void encrypt(byte[] raw, int offset, int size) {
        if(!this._isEnabled) {
            this._isEnabled = true;
        } else {
            if(this._isProtected) {
                SCrypt.crypt(raw, offset, size, this._outKo);
            } else {
                int temp = 0;

                int old;
                for(old = 0; old < size; ++old) {
                    int temp2 = raw[offset + old] & 255;
                    temp ^= temp2 ^ this._outKey[old & 15];
                    raw[offset + old] = (byte)temp;
                }

                old = this._outKey[8] & 255;
                old |= this._outKey[9] << 8 & '\uff00';
                old |= this._outKey[10] << 16 & 16711680;
                old |= this._outKey[11] << 24 & -16777216;
                old += size;
                this._outKey[8] = (byte)(old & 255);
                this._outKey[9] = (byte)(old >> 8 & 255);
                this._outKey[10] = (byte)(old >> 16 & 255);
                this._outKey[11] = (byte)(old >> 24 & 255);
            }

        }
    }
}

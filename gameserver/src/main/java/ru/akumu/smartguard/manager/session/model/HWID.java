//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.manager.session.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.akumu.smartguard.GuardConfig;
import ru.akumu.smartguard.manager.session.model.HWIDParts;

public class HWID implements Serializable {
    private static final long serialVersionUID = -1322322139926390329L;
    public static final Pattern HWID_PATTERN = Pattern.compile("([a-fA-F0-9]{48})");
    public static final int HWID_DATA_LENGTH = 24;
    public static final int HWID_PLAIN_LENGTH = 48;
    public final String plain;
    public final BigInteger HDD;
    public final BigInteger MAC;
    public final BigInteger CPU;

    private HWID(String hwid) throws InvalidParameterException {
        if(hwid == null) {
            throw new InvalidParameterException("hwid string is null");
        } else if(hwid.length() != HWID_PLAIN_LENGTH) {
            throw new InvalidParameterException("hwid string has invalid length");
        } else {
            this.plain = hwid.toLowerCase();
            this.HDD = new BigInteger(hwid.substring(0, 16), 16);
            this.MAC = new BigInteger(hwid.substring(16, 32), 16);
            this.CPU = new BigInteger(hwid.substring(32, 48), 16);
        }
    }

    private HWID(byte[] data) {
        StringBuilder sb = new StringBuilder();
        byte[] arr$ = data;
        int len$ = data.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            byte b = arr$[i$];
            sb.append(String.format("%02x", new Object[]{Byte.valueOf(b)}));
        }

        this.plain = sb.toString();
        this.HDD = new BigInteger(this.plain.substring(0, 16), 16);
        this.MAC = new BigInteger(this.plain.substring(16, 32), 16);
        this.CPU = new BigInteger(this.plain.substring(32, 48), 16);
    }

    public String toString() {
        return this.plain;
    }

    public static HWID fromString(String hwid) {
        if(hwid == null) {
            return null;
        } else {
            Matcher m = HWID_PATTERN.matcher(hwid.toLowerCase());

            try {
                if(m.find()) {
                    return new HWID(m.group());
                }
            } catch (Exception var3) {
                ;
            }

            return null;
        }
    }

    public static HWID fromData(byte[] data) {
        return data != null && data.length == 24?new HWID(data):null;
    }

    public boolean equalsForBan(HWID h) {
        return !this.HDD.equals(h.HDD) && (GuardConfig.BanMask & HWIDParts.HDD.mask) == HWIDParts.HDD.mask?false:(!this.MAC.equals(h.MAC) && (GuardConfig.BanMask & HWIDParts.MAC.mask) == HWIDParts.MAC.mask?false:this.CPU.equals(h.CPU) || (GuardConfig.BanMask & HWIDParts.CPU.mask) != HWIDParts.CPU.mask);
    }

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        } else if(o != null && this.getClass() == o.getClass()) {
            HWID hwid = (HWID)o;
            return this.CPU.equals(hwid.CPU) && this.HDD.equals(hwid.HDD) /*&& this.MAC.equals(hwid.MAC)*/;
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = this.HDD.hashCode();
        result = 31 * result + this.MAC.hashCode();
        result = 31 * result + this.CPU.hashCode();
        return result;
    }
}

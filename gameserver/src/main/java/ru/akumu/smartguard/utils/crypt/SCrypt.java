//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.utils.crypt;

import ru.akumu.smartguard.utils.crypt.KeyObject;

public class SCrypt {
    public SCrypt() {
    }

    public static void init(byte[] key, KeyObject ko) {
        for(short i1 = 0; i1 < 256; ++i1) {
            ko.state[i1] = (byte)(i1 & 255);
        }

        int var6 = 0;
        int i2 = 0;

        for(short i = 0; i < 256; ++i) {
            i2 = ((key[var6] & 255) + (ko.state[i] & 255) + i2) % 256;
            byte tmp = ko.state[i];
            ko.state[i] = ko.state[i2];
            ko.state[i2] = tmp;
            var6 = (var6 + 1) % key.length;
        }

    }

    public static void crypt(byte[] data, KeyObject ko) {
        crypt(data, 0, data.length, ko);
    }

    public static void crypt(byte[] data, int dataOff, int len, KeyObject ko) {
        if(dataOff >= 0 && len != 0) {
            int x = ko.x;
            int y = ko.y;

            for(int i = dataOff; i < dataOff + len; ++i) {
                x = (x + 1) % 256;
                y = ((ko.state[x] & 255) + y) % 256;
                byte tmp = ko.state[x];
                ko.state[x] = ko.state[y];
                ko.state[y] = tmp;
                int idx = ((ko.state[x] & 255) + (ko.state[y] & 255)) % 256;
                data[i] ^= ko.state[idx];
            }

            ko.x = x;
            ko.y = y;
        }
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.utils;

import java.util.Random;

public class Rnd {
    private static final Random _rnd = new Random();

    public Rnd() {
    }

    public static byte get() {
        return (byte)get(255);
    }

    public static int get(int max) {
        return get(0, max);
    }

    public static int get(int min, int max) {
        return _rnd.nextInt(max - min) + min;
    }
}

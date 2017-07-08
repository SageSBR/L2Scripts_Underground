//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.manager.screen.model;

import java.security.InvalidParameterException;

public class Color {
    public int a;
    public int r;
    public int g;
    public int b;

    public Color(int a, int r, int g, int b) {
        if(a >= 0 && a <= 255 && r >= 0 && r <= 255 && g >= 0 && g <= 255 && b >= 0 && b <= 255) {
            this.a = a;
            this.r = r;
            this.g = g;
            this.b = b;
        } else {
            throw new InvalidParameterException();
        }
    }
}

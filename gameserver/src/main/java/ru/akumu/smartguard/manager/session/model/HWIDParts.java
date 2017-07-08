//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.manager.session.model;

public enum HWIDParts {
    HDD(1),
    MAC(2),
    CPU(4);

    public final int mask;

    private HWIDParts(int mask) {
        this.mask = mask;
    }

    public boolean test(int mask) {
        return (mask & this.mask) == this.mask;
    }
}

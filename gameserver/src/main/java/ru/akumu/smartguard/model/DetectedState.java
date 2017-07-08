//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.model;

import java.util.BitSet;

public enum DetectedState {
    VMBOX,
    L2UPDATER;

    private DetectedState() {
    }

    public boolean check(BitSet set) {
        return set.get(this.ordinal());
    }
}

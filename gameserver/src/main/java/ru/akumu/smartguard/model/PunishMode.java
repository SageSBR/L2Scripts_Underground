//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.model;

public enum PunishMode {
    BAN(10),
    DISCONNECT(5),
    LOG(0);

    public final int priority;

    private PunishMode(int p) {
        this.priority = p;
    }
}

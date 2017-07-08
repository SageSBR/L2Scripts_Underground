//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.manager.screen.model;

public enum ScreenPos {
    TopRightRelative(1024),
    TopRight(2),
    TopLeft(0),
    TopCenter(1),
    MiddleRight(6),
    MiddleLeft(4),
    MiddleCenter(5),
    BottomRight(10),
    BottomLeft(8),
    BottomCenter(9);

    public int mask;

    private ScreenPos(int mask) {
        this.mask = mask;
    }
}

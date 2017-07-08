//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.manager.modules.model;

import ru.akumu.smartguard.manager.modules.ModulesManager;

public enum Module {
    CopyPaste("Copy&Paste", 0, true),
    AntiClick("AntiClicker", 1, true),
    D3DXHook("D3DXHook", 2, true),
    InputFilter("InputFilter", 3, true);

    public String name;
    public int id;
    public boolean defaultState;

    private Module(String name, int id, boolean defaultState) {
        this.name = name;
        this.id = id;
        this.defaultState = defaultState;
    }

    public boolean isEnabled() {
        return ModulesManager.isModuleEnabled(this);
    }

    public void setEnabled(boolean state) {
        ModulesManager.setModuleEnabled(this, state);
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.network.packets;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import ru.akumu.smartguard.manager.modules.model.Module;
import ru.akumu.smartguard.network.packets.ISmartPacket;

public class ConfigPacket extends L2GameServerPacket implements ISmartPacket {
    private final char Subcode = 0x01;
    private List<Module> _modules;

    public ConfigPacket(Module... _modules) {
        if(_modules == null) {
            throw new InvalidParameterException("Modules can not be null");
        } else {
            this._modules = new ArrayList(_modules.length);
            Collections.addAll(this._modules, _modules);
        }
    }

    public ConfigPacket(List<Module> _modules) {
        if(_modules == null) {
            throw new InvalidParameterException("Modules can not be null");
        } else {
            this._modules = _modules;
        }
    }

    protected void writeImpl() {
        this.writeC(255);
        this.writeC(1);
        this.writeC(this._modules.size());
        Iterator i$ = this._modules.iterator();

        while(i$.hasNext()) {
            Module sm = (Module)i$.next();
            this.writeC(sm.id);
            this.writeC(sm.isEnabled() ? 1:0);
        }

    }

    protected boolean writeOpcodes() {
        return true;
    }
}

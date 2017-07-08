//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.manager.modules;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.GameClient;
import ru.akumu.smartguard.SmartGuard;
import ru.akumu.smartguard.manager.modules.model.Module;
import ru.akumu.smartguard.network.packets.ConfigPacket;

public class ModulesManager {
    private static ConcurrentHashMap<Module, Boolean> _config = new ConcurrentHashMap(Module.values().length);

    public ModulesManager() {
    }

    public static boolean isModuleEnabled(Module sm) {
        return ((Boolean)_config.get(sm)).booleanValue();
    }

    public static void setModuleEnabled(Module sm, boolean state) {
        boolean broadcast = sm.isEnabled() != state;
        _config.put(sm, Boolean.valueOf(state));
        if(broadcast) {
            onConfigurationChanged();
        }

    }

    public static void onPlayerLogin(GameClient client) {
        if(client != null) {
            ArrayList list = new ArrayList(Module.values().length);
            Module[] arr$ = Module.values();
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Module sm = arr$[i$];
                if(sm.defaultState != sm.isEnabled()) {
                    list.add(sm);
                }
            }

            if(list.size() > 0) {
                client.sendPacket(new ConfigPacket(list));
            }

        }
    }

    public static void onConfigurationChanged() {
        if(SmartGuard.IS_LOADING_FINISHED) {
            ArrayList list = new ArrayList(Module.values().length);
            Module[] cfg = Module.values();
            int e = cfg.length;

            for(int player = 0; player < e; ++player) {
                Module sm = cfg[player];
                if(sm.defaultState != sm.isEnabled()) {
                    list.add(sm);
                }
            }

            ConfigPacket var6 = new ConfigPacket(list);

            try {
                Iterator var7 = GameObjectsStorage.getAllPlayers().iterator();

                while(var7.hasNext()) {
                    Player var8 = (Player)var7.next();
                    var8.sendPacket(var6);
                }
            } catch (Exception var5) {
                ;
            }

        }
    }

    static {
        Module[] arr$ = Module.values();
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Module sm = arr$[i$];
            _config.put(sm, Boolean.valueOf(sm.defaultState));
        }

    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.manager.session;

import java.util.Collection;
import java.util.HashMap;
import l2s.gameserver.network.l2.GameClient;
import ru.akumu.smartguard.manager.session.model.ClientData;
import ru.akumu.smartguard.manager.session.model.ClientSession;
import ru.akumu.smartguard.manager.session.model.HWID;

public class ClientSessionManager {
    private static final HashMap<HWID, ClientSession> _sessions = new HashMap();
    private static final HashMap<GameClient, ClientData> _storage = new HashMap();

    public ClientSessionManager() {
    }

    public static ClientData getClientData(GameClient client) {
        return (ClientData)_storage.get(client);
    }

    public static void setClientData(GameClient client, ClientData cd) {
        _storage.put(client, cd);
    }

    public static ClientSession getSession(GameClient client) {
        return getSession(getClientData(client));
    }

    public static ClientSession getSession(ClientData cd) {
        return getSession(cd.hwid);
    }

    public static ClientSession getSession(HWID hwid) {
        return (ClientSession)_sessions.get(hwid);
    }

    public static void putSession(ClientSession cs) {
        _sessions.put(cs.hwid, cs);
    }

    public static Collection<ClientSession> getAllSessions() {
        return _sessions.values();
    }
}

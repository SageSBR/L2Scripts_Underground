//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.manager.session.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import l2s.gameserver.network.l2.GameClient;
import l2s.gameserver.network.l2.GameClient.GameClientState;
import l2s.gameserver.network.l2.s2c.ServerCloseSocketPacket;
import ru.akumu.smartguard.GuardConfig;
import ru.akumu.smartguard.manager.session.model.ClientData;
import ru.akumu.smartguard.manager.session.model.HWID;

public class ClientSession {
    private final Set<GameClient> _session = new HashSet();
    private final Set<String> _accounts = new HashSet();
    public final HWID hwid;
    public final int langId;

    public ClientSession(ClientData clientData) {
        this.hwid = clientData.hwid;
        this.langId = clientData.langId;
    }

    public List<GameClient> getClients() {
        this.clean();
        ArrayList res = new ArrayList(this._session.size());
        res.addAll(this._session);
        return res;
    }

    public void addClient(ClientData cd, GameClient client) {
        this._session.add(client);
        this._accounts.add(cd.account.toLowerCase());
    }

    public boolean hasAccountSession(String acc) {
        if(acc == null) {
            return false;
        } else {
            Iterator i$ = this._accounts.iterator();

            String account;
            do {
                if(!i$.hasNext()) {
                    return false;
                }

                account = (String)i$.next();
            } while(!account.equalsIgnoreCase(acc));

            return true;
        }
    }

    public boolean canLogin() {
        if(GuardConfig.MaxInstances <= 0) {
            return true;
        } else {
            this.clean();
            return this._session.size() < GuardConfig.MaxInstances;
        }
    }

    public int getCount() {
        this.clean();
        return this._session.size();
    }

    private void clean() {
        Iterator it = this._session.iterator();

        while(true) {
            GameClient client;
            do {
                if(!it.hasNext()) {
                    return;
                }

                client = (GameClient)it.next();
            } while(client != null && client.isConnected() && client.getState() != GameClientState.DISCONNECTED && client.getActiveChar() != null);

            it.remove();
        }
    }

    public void disconnect() {
        this.clean();
        Iterator i$ = this._session.iterator();

        while(i$.hasNext()) {
            GameClient client = (GameClient)i$.next();

            try {
                client.close(ServerCloseSocketPacket.STATIC);
            } catch (Exception var4) {
                ;
            }
        }

    }

    public String hwid() {
        return this.hwid.plain;
    }

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        } else if(o != null && this.getClass() == o.getClass()) {
            ClientSession that = (ClientSession)o;
            return this.langId != that.langId?false:this.hwid.equals(that.hwid);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.hwid.hashCode();
    }

    public String toString() {
        return "ClientSession{hwid=" + this.hwid + ", langId=" + this.langId + '}';
    }
}

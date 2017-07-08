//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Shutdown;
import l2s.gameserver.network.authcomm.AuthServerCommunication;
import l2s.gameserver.network.authcomm.SessionKey;
import l2s.gameserver.network.authcomm.gs2as.PlayerAuthRequest;
import l2s.gameserver.network.l2.GameClient;
import l2s.gameserver.network.l2.c2s.L2GameClientPacket;
import l2s.gameserver.network.l2.s2c.LoginFailPacket;
import l2s.gameserver.network.l2.s2c.ServerCloseSocketPacket;
import l2s.gameserver.utils.Language;
import ru.akumu.smartguard.GuardConfig;
import ru.akumu.smartguard.manager.modules.ModulesManager;
import ru.akumu.smartguard.manager.screen.ScreenTextManager;
import ru.akumu.smartguard.manager.session.ClientSessionManager;
import ru.akumu.smartguard.manager.session.model.ClientSession;
import ru.akumu.smartguard.network.packets.MsgPacket.MsgType;

public class AuthLogin extends L2GameClientPacket {
    private String _loginName;
    private int _playKey1;
    private int _playKey2;
    private int _loginKey1;
    private int _loginKey2;
    private int _lang;

    public AuthLogin() {
    }

    protected void readImpl() {
        this._loginName = this.readS().toLowerCase();
        this._playKey2 = this.readD();
        this._playKey1 = this.readD();
        this._loginKey1 = this.readD();
        this._loginKey2 = this.readD();
        this._lang = this.readD();
    }

    protected void runImpl() {
        if(this._client != null) {
            if(GuardConfig.ProtectionEnabled) {
                ClientSession key = ClientSessionManager.getSession((GameClient)this._client);
                if(!key.hasAccountSession(this._loginName)) {
                    ((GameClient)this._client).close(MsgType.GENERAL_ERROR.paket);
                    return;
                }
            }

            SessionKey key1 = new SessionKey(this._loginKey1, this._loginKey2, this._playKey1, this._playKey2);
            ((GameClient)this._client).setSessionId(key1);
            ((GameClient)this._client).setLoginName(this._loginName);
            ((GameClient)this._client).setLanguage(Language.getLanguage(this._lang));
            if(Shutdown.getInstance().getMode() != -1 && Shutdown.getInstance().getSeconds() <= 15) {
                ((GameClient)this._client).closeNow(false);
            } else {
                if(AuthServerCommunication.getInstance().isShutdown()) {
                    ((GameClient)this._client).close(new LoginFailPacket(LoginFailPacket.SYSTEM_ERROR_LOGIN_LATER));
                    return;
                }

                GameClient oldClient = AuthServerCommunication.getInstance().addWaitingClient((GameClient)this._client);
                if(oldClient != null) {
                    oldClient.close(ServerCloseSocketPacket.STATIC);
                }

                AuthServerCommunication.getInstance().sendPacket(new PlayerAuthRequest((GameClient)this._client));
            }

            if(GuardConfig.ProtectionEnabled) {
                ModulesManager.onPlayerLogin((GameClient)this._client);
                ScreenTextManager.getInstance().onPlayerLogin((GameClient)this._client);
            }

        }
    }
}

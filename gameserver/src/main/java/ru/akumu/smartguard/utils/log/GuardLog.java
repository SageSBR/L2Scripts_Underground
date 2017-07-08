//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.utils.log;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import l2s.gameserver.network.l2.GameClient;
import ru.akumu.smartguard.GuardConfig;
import ru.akumu.smartguard.manager.session.model.ClientData;
import ru.akumu.smartguard.utils.log.LogFormatter;

public class GuardLog {
    private static final Logger _log = Logger.getLogger("GENERAL");
    private static final Logger _logAuth = Logger.getLogger("AUTH");
    private static final LogFormatter _format = new LogFormatter();

    public GuardLog() {
    }

    public static Logger getLogger() {
        return _log;
    }

    public static void logAuth(ClientData cd, GameClient client) {
        if(GuardConfig.LogToFile) {
            _logAuth.info(String.format("Account \'%s\' logged in with HWID \'%s\' and IP \'%s\'", new Object[]{cd.account, cd.hwid.plain, client.getIpAddr()}));
        }
    }

    public static void logException(Exception e) {
        _log.log(Level.SEVERE, "Exception occurred:", e);
    }

    static {
        try {
            FileHandler e = new FileHandler(GuardConfig.SMART_GUARD_DIR + "log/general.log", true);
            e.setFormatter(_format);
            _log.addHandler(e);
            FileHandler auth = new FileHandler(GuardConfig.SMART_GUARD_DIR + "log/auth.log", true);
            auth.setFormatter(_format);
            _logAuth.addHandler(auth);
        } catch (Exception var2) {
            _log.severe("Error! Log handler could not be created!");
        }

    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.utils.log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.network.l2.GameClient;
import ru.akumu.smartguard.GuardConfig;
import ru.akumu.smartguard.manager.session.model.ClientData;
import ru.akumu.smartguard.utils.DBUtils;
import ru.akumu.smartguard.utils.log.GuardLog;

public class DbLogger {
    public DbLogger() {
    }

    public static void logAuth(ClientData cd, GameClient client) {
        if(GuardConfig.LogToDatabase) {
            Connection con = null;
            PreparedStatement stmt = null;

            try {
                con = DatabaseFactory.getInstance().getConnection();
                stmt = con.prepareStatement("INSERT INTO auth_log (date, account, hwid, ip) VALUES(NOW(), ?, ?, ?)");
                stmt.setString(1, cd.account);
                stmt.setString(2, cd.hwid.plain);
                stmt.setString(3, client.getIpAddr());
                stmt.execute();
            } catch (Exception var8) {
                GuardLog.getLogger().info("Error logging auth for client: " + cd);
            } finally {
                DBUtils.closeQuietly(con);
                DBUtils.closeQuietly(stmt);
            }

        }
    }

    static {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            con = DatabaseFactory.getInstance().getConnection();
            stmt = con.prepareStatement("SHOW TABLES LIKE \'auth_log\'");
            rset = stmt.executeQuery();
            if(!rset.next()) {
                stmt = con.prepareStatement("CREATE TABLE `auth_log` (\n  `id` int(11) NOT NULL AUTO_INCREMENT,\n  `date` datetime NOT NULL,\n  `account` varchar(14) NOT NULL,\n  `hwid` varchar(48) NOT NULL,\n  `ip` varchar(16) NOT NULL,\n  PRIMARY KEY (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=utf8;");
                stmt.execute();
            }
        } catch (Exception var7) {
            GuardLog.logException(var7);
        } finally {
            DBUtils.closeQuietly(con);
            DBUtils.closeQuietly(stmt);
            DBUtils.closeQuietly(rset);
        }

    }
}

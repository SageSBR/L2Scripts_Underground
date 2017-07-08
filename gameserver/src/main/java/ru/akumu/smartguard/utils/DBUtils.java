//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBUtils {
    public DBUtils() {
    }

    public static final void closeQuietly(Connection conn) {
        if(conn != null) {
            try {
                conn.close();
            } catch (Exception var2) {
                ;
            }
        }

    }

    public static final void closeQuietly(Statement stmt) {
        if(stmt != null) {
            try {
                stmt.close();
            } catch (Exception var2) {
                ;
            }
        }

    }

    public static final void closeQuietly(ResultSet rset) {
        if(rset != null) {
            try {
                rset.close();
            } catch (Exception var2) {
                ;
            }
        }

    }
}

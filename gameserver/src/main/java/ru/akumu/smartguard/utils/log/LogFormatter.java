//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.utils.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public final class LogFormatter extends Formatter {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public LogFormatter() {
    }

    public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(dateFormat.format(new Date(record.getMillis()))).append("]");
        sb.append("[").append(record.getLevel().getLocalizedName()).append("]");
        sb.append(" ").append(this.formatMessage(record)).append(LINE_SEPARATOR);
        if(record.getThrown() != null) {
            try {
                StringWriter ex = new StringWriter();
                PrintWriter pw = new PrintWriter(ex);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(ex.toString());
            } catch (Exception var5) {
                ;
            }
        }

        return sb.toString();
    }
}

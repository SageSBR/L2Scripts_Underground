//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.Selector;

public class IOUtils {
    public IOUtils() {
    }

    public static void closeQuietly(Reader input) {
        closeQuietly((Closeable)input);
    }

    public static void closeQuietly(Writer output) {
        closeQuietly((Closeable)output);
    }

    public static void closeQuietly(InputStream input) {
        closeQuietly((Closeable)input);
    }

    public static void closeQuietly(OutputStream output) {
        closeQuietly((Closeable)output);
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if(closeable != null) {
                closeable.close();
            }
        } catch (IOException var2) {
            ;
        }

    }

    public static void closeQuietly(Socket sock) {
        if(sock != null) {
            try {
                sock.close();
            } catch (IOException var2) {
                ;
            }
        }

    }

    public static void closeQuietly(Selector selector) {
        if(selector != null) {
            try {
                selector.close();
            } catch (IOException var2) {
                ;
            }
        }

    }

    public static void closeQuietly(ServerSocket sock) {
        if(sock != null) {
            try {
                sock.close();
            } catch (IOException var2) {
                ;
            }
        }

    }
}

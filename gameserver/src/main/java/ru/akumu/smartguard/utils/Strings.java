//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Strings {
    public Strings() {
    }

    public static String joinStrings(String glueStr, String[] strings, int startIdx, int maxCount) {
        String result = "";
        if(startIdx < 0) {
            startIdx += strings.length;
            if(startIdx < 0) {
                return result;
            }
        }

        while(startIdx < strings.length && maxCount != 0) {
            if(!result.isEmpty() && glueStr != null && !glueStr.isEmpty()) {
                result = result + glueStr;
            }

            result = result + strings[startIdx++];
            --maxCount;
        }

        return result;
    }

    public static String joinStrings(String glueStr, String[] strings, int startIdx) {
        return joinStrings(glueStr, strings, startIdx, -1);
    }

    public static String joinStrings(String glueStr, String[] strings) {
        return joinStrings(glueStr, strings, 0);
    }

    public static String stripToSingleLine(String s) {
        if(s.isEmpty()) {
            return s;
        } else {
            s = s.replaceAll("\\\\n", "\n");
            int i = s.indexOf("\n");
            if(i > -1) {
                s = s.substring(0, i);
            }

            return s;
        }
    }

    public static String getStringFromWCHARArray(byte[] data) {
        ByteBuffer buf = ByteBuffer.wrap(data);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        StringBuilder sb = new StringBuilder();

        char c;
        while((c = buf.getChar()) != 0) {
            sb.append(c);
        }

        buf.clear();
        buf = null;
        return sb.toString();
    }
}

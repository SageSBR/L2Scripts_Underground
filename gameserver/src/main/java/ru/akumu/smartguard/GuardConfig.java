//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard;

import java.io.File;
import java.io.FileInputStream;
import java.security.InvalidParameterException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.akumu.smartguard.manager.modules.model.Module;
import ru.akumu.smartguard.manager.session.model.HWIDParts;
import ru.akumu.smartguard.model.PunishAction;
import ru.akumu.smartguard.model.PunishMode;
import ru.akumu.smartguard.model.SoftwareType;
import ru.akumu.smartguard.model.PunishAction.Type;
import ru.akumu.smartguard.utils.log.GuardLog;

public class GuardConfig {
    public static String SMART_GUARD_DIR = "./smartguard/";
    private static final File ConfigurationFile;
    private static final AtomicLong lastModified;
    public static PunishAction[] PunishActions;
    public static boolean ProtectionEnabled;
    public static boolean AllowVirtualization;
    public static boolean OnlyUpdaterRun;
    public static boolean LogToDatabase;
    public static boolean LogToFile;
    public static boolean BanlistAccountBan;
    public static boolean BanlistAccountAppend;
    public static boolean PatchVersionEnabled;
    public static int MaxInstances;
    public static int BanMask;
    public static long PatchVersionMin;
    private static final Pattern ACTION_DELAYED_CFG;
    private static final Pattern ACTION_TEMP_CFG;

    public GuardConfig() {
    }

    private static void setDefaultDetectActions() {
        PunishActions[SoftwareType.BOT.ordinal()] = PunishAction.STATIC_REALTIME_DISCONNECT;
        PunishActions[SoftwareType.RADAR.ordinal()] = PunishAction.STATIC_REALTIME_DISCONNECT;
        PunishActions[SoftwareType.PACKET_HACK.ordinal()] = PunishAction.STATIC_REALTIME_BAN;
    }

    public static void reload() {
        long modified = ConfigurationFile.lastModified();
        if(lastModified.getAndSet(modified) != modified) {
            setDefaultDetectActions();
            load();
        }

    }

    public static void load() {
        try {
            Properties e = new Properties();
            FileInputStream is = new FileInputStream(ConfigurationFile);
            e.load(is);
            is.close();
            ProtectionEnabled = Boolean.parseBoolean(e.getProperty("ProtectionEnabled", "true"));
            AllowVirtualization = Boolean.parseBoolean(e.getProperty("AllowVirtualization", "true"));
            OnlyUpdaterRun = Boolean.parseBoolean(e.getProperty("OnlyUpdaterRun", "false"));
            LogToDatabase = Boolean.parseBoolean(e.getProperty("LogToDatabase", "true"));
            LogToFile = Boolean.parseBoolean(e.getProperty("LogToFile", "false"));
            PatchVersionEnabled = Boolean.parseBoolean(e.getProperty("PatchVersionEnabled", "false"));
            BanlistAccountAppend = Boolean.parseBoolean(e.getProperty("BanlistAccountAppend", "true"));
            BanlistAccountBan = Boolean.parseBoolean(e.getProperty("BanlistAccountBan", "true"));
            PatchVersionMin = Long.parseLong(e.getProperty("PatchVersionMin", "1"));
            MaxInstances = Integer.parseInt(e.getProperty("MaxInstances", "10"));
            String string = e.getProperty("DetectActions");
            String[] e1;
            String[] arr$;
            int len$;
            int i$;
            String sss;
            String[] args;
            if(string != null && string.length() != 0) {
                try {
                    e1 = string.split(";");
                    if(e1.length > 0) {
                        arr$ = e1;
                        len$ = e1.length;

                        for(i$ = 0; i$ < len$; ++i$) {
                            sss = arr$[i$];
                            args = sss.split("=");
                            String sm = args[0].trim().toUpperCase();
                            String value = args[1].trim().toUpperCase();
                            Type type = Type.REALTIME;
                            String[] valueArgs = value.split("_");
                            if(valueArgs.length > 1) {
                                String softwareType = valueArgs[0].trim().toUpperCase();
                                type = Type.valueOf(softwareType);
                                value = valueArgs[1];
                            }

                            SoftwareType var26 = SoftwareType.valueOf(sm);
                            Matcher m;
                            PunishMode mode;
                            switch(type.ordinal()) {
                                case 1:
                                    PunishMode var27 = PunishMode.valueOf(value);
                                    PunishActions[var26.ordinal()] = new PunishAction(var27);
                                    break;
                                case 2:
                                    m = ACTION_DELAYED_CFG.matcher(value);
                                    if(!m.find()) {
                                        throw new InvalidParameterException();
                                    }

                                    mode = PunishMode.valueOf(m.group(1));
                                    PunishActions[var26.ordinal()] = new PunishAction(mode, Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)));
                                    break;
                                case 3:
                                    m = ACTION_TEMP_CFG.matcher(value);
                                    if(!m.find()) {
                                        throw new InvalidParameterException();
                                    }

                                    mode = PunishMode.valueOf(m.group(1));
                                    PunishActions[var26.ordinal()] = new PunishAction(mode, Integer.parseInt(m.group(2)));
                            }
                        }
                    }
                } catch (Exception var18) {
                    GuardLog.logException(var18);
                }

                String var20 = e.getProperty("BanMask");
                if(var20 != null) {
                    var20 = var20.trim();
                    if(!var20.isEmpty()) {
                        arr$ = var20.split("\\|");
                        String[] var21 = arr$;
                        i$ = arr$.length;

                        for(int var22 = 0; var22 < i$; ++var22) {
                            String var23 = var21[var22];
                            var23 = var23.trim();

                            try {
                                HWIDParts var24 = HWIDParts.valueOf(var23);
                                BanMask |= var24.mask;
                            } catch (IllegalArgumentException var16) {
                                GuardLog.logException(var16);
                            }
                        }
                    }
                }

                if(BanMask == 0) {
                    BanMask = HWIDParts.HDD.mask | HWIDParts.MAC.mask | HWIDParts.CPU.mask;
                }
            }

            string = e.getProperty("ModulesState");
            if(string != null && string.length() != 0) {
                try {
                    e1 = string.split(";");
                    if(e1.length > 0) {
                        arr$ = e1;
                        len$ = e1.length;

                        for(i$ = 0; i$ < len$; ++i$) {
                            sss = arr$[i$];
                            args = sss.split("=");
                            Module var25 = Module.valueOf(args[0]);
                            var25.setEnabled(Boolean.valueOf(args[1]).booleanValue());
                        }
                    }
                } catch (Exception var17) {
                    GuardLog.logException(var17);
                }
            }
        } catch (Exception var19) {
            GuardLog.logException(var19);
        }

    }

    static {
        ConfigurationFile = new File(SMART_GUARD_DIR, "config.properties");
        lastModified = new AtomicLong(ConfigurationFile.lastModified());
        PunishActions = new PunishAction[SoftwareType.values().length];
        ACTION_DELAYED_CFG = Pattern.compile("(\\w+)\\(([0-9]+)-([0-9]+)\\)");
        ACTION_TEMP_CFG = Pattern.compile("(\\w+)\\(([0-9]+)\\)");
        setDefaultDetectActions();
        load();
    }
}

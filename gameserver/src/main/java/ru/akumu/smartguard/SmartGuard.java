//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.zip.Adler32;
import l2s.gameserver.handler.admincommands.AdminCommandHandler;
import l2s.gameserver.network.l2.GameClient;
import ru.akumu.smartguard.GuardConfig;
import ru.akumu.smartguard.manager.LicenseManager;
import ru.akumu.smartguard.manager.UpdateManager;
import ru.akumu.smartguard.manager.bans.BanManager;
import ru.akumu.smartguard.manager.bans.model.Ban;
import ru.akumu.smartguard.manager.bans.model.Ban.Type;
import ru.akumu.smartguard.manager.session.ClientSessionManager;
import ru.akumu.smartguard.manager.session.model.ClientData;
import ru.akumu.smartguard.manager.session.model.ClientSession;
import ru.akumu.smartguard.manager.session.model.HWID;
import ru.akumu.smartguard.menu.SmartGuardMenu;
import ru.akumu.smartguard.model.DetectedSoftware;
import ru.akumu.smartguard.model.DetectedState;
import ru.akumu.smartguard.model.PunishAction;
import ru.akumu.smartguard.network.packets.MsgPacket;
import ru.akumu.smartguard.network.packets.MsgPacket.MsgType;
import ru.akumu.smartguard.utils.Rnd;
import ru.akumu.smartguard.utils.Strings;
import ru.akumu.smartguard.utils.log.DbLogger;
import ru.akumu.smartguard.utils.log.GuardLog;

public class SmartGuard {
    public static boolean IS_LOADING_FINISHED = false;

    public SmartGuard() {
    }

    private static BitSet decodeBitSet(long d) {
        BitSet bits = new BitSet();

        for(int i = 0; d != 0L; d >>>= 1) {
            if(d % 2L != 0L) {
                bits.set(i);
            }

            ++i;
        }

        return bits;
    }

    private static Ban checkBan(ClientData cd) {
        if(cd == null) {
            return null;
        } else {
            Ban ban = BanManager.getBan(cd.hwid);
            if(ban == null) {
                Ban accountBan = BanManager.checkAccount(cd.account);
                if(GuardConfig.BanlistAccountBan && accountBan != null) {
                    GuardLog.getLogger().info(String.format("HWID \'%s\' tried to log-in to banned account \'%s\'.", new Object[]{cd.hwid.plain, cd.account}));
                    return accountBan;
                } else {
                    return null;
                }
            } else {
                if(GuardConfig.BanlistAccountAppend && !ban.findAccount(cd.account)) {
                    ban.addAccount(cd.account);
                    GuardLog.getLogger().info(String.format("Banned HWID \'%s\' added account \'%s\' to his banlist.", new Object[]{cd.hwid.plain, cd.account}));
                }

                GuardLog.getLogger().info(String.format("Banned HWID \'%s\' tried login to account \'%s\'", new Object[]{cd.hwid, cd.account}));
                return ban;
            }
        }
    }

    public static MsgPacket checkClient(GameClient client, byte[] data) {
        if(data == null) {
            return MsgType.GENERAL_ERROR.paket;
        } else {
            GuardConfig.reload();
            long patchVersion = 0L;

            String account;
            HWID hwid;
            BitSet mask;
            BitSet pc;
            short langId;
            try {
                if(!LicenseManager.getInstance().cryptInternalData(data)) {
                    throw new IOException("Failed to decrypt internal data.");
                }

                ByteBuffer cd = ByteBuffer.wrap(data);
                cd.order(ByteOrder.LITTLE_ENDIAN);
                cd.position(cd.capacity() - 4);
                long ban = (long)cd.getInt() & 4294967295L;
                cd.position(0);
                Adler32 session = new Adler32();
                session.update(cd.array(), 0, cd.capacity() - 4);
                long i = session.getValue();
                if(ban != i) {
                    throw new IOException("Client data is invalid.");
                }

                int build = cd.getInt();
                if(!UpdateManager.getInstance().checkBuild(build)) {
                    return MsgType.VERSION_MISSMATCH.paket;
                }

                mask = decodeBitSet((long)cd.getInt() & 4294967295L);
                pc = decodeBitSet((long)cd.getInt() & 4294967295L);
                cd.get();
                byte[] result = new byte[24];
                cd.get(result, 0, result.length);
                hwid = HWID.fromData(result);
                byte[] comment = new byte[30];
                cd.get(comment, 0, comment.length);
                account = Strings.getStringFromWCHARArray(comment);
                if(account.length() == 0) {
                    throw new Exception("Client had empty account name.");
                }

                langId = cd.getShort();
                boolean delay = cd.get() == 1;
                if(delay) {
                    patchVersion = (long)cd.getInt() & 4294967295L;
                }

                cd.clear();
            } catch (Exception var20) {
                GuardLog.logException(var20);
                return MsgType.GENERAL_ERROR.paket;
            }

            //ClientData var21 = new ClientData(hwid, account, langId);
			ClientData cd = new ClientData(hwid, account, langId);
            Ban var22 = checkBan(cd);
            if(var22 != null) {
                return var22.type == Type.TEMP?MsgType.TEMP_BAN.paket:MsgType.BANNED_ALREADY.paket;
            } else if(GuardConfig.PatchVersionEnabled && patchVersion < GuardConfig.PatchVersionMin) {
                GuardLog.getLogger().info(String.format("Client \'%s\' tried logging with old patch version. %d < %d.", new Object[]{cd.account, Long.valueOf(patchVersion), Long.valueOf(GuardConfig.PatchVersionMin)}));
                return MsgType.OLD_PATCH.paket;
            } else if(!GuardConfig.AllowVirtualization && DetectedState.VMBOX.check(pc)) {
                GuardLog.getLogger().info(String.format("Client \'%s\' tried logging in from virtual environment", new Object[]{cd.account}));
                return MsgType.VM_LOGIN.paket;
            } else if(GuardConfig.OnlyUpdaterRun && !DetectedState.L2UPDATER.check(pc)) {
                GuardLog.getLogger().info(String.format("Client \'%s\' tried logging in without running updater", new Object[]{cd.account}));
                return MsgType.NO_UPDATER.paket;
            } else {
				/*
                List detects = DetectedSoftware.getList(mask);
                if(detects.size() != 0) {
                    String[] var23 = new String[detects.size()];
                    int var25 = 0;

                    DetectedSoftware var27;
                    for(Iterator using = detects.iterator(); using.hasNext(); var23[var25++] = var27.name) {
                        var27 = (DetectedSoftware)using.next();
                    }

                    String var26 = Strings.joinStrings(", ", var23);
                    GuardLog.getLogger().info(String.format("Client \'%s\' (HWID:%s) is using: %s", new Object[]{var21.account, var21.hwid, var26}));
                    PunishAction var28 = null;
                    Iterator var29 = detects.iterator();

                    label96:
                    while(true) {
                        PunishAction da;
                        do {
                            if(!var29.hasNext()) {
                                if(var28 == null) {
                                    return MsgType.GENERAL_ERROR.paket;
                                }

                                switch(var28._mode.ordinal()) {
                                    case 1:
                                        String var30 = String.format("[%s] acc: \'%s\', using: %s", new Object[]{(new SimpleDateFormat("MM/dd/yy HH:mm")).format(Calendar.getInstance().getTime()), var21.account, var26});
                                        switch(var28._type.ordinal()) {
                                            case 1:
                                                long var33 = System.currentTimeMillis() + (long)(var28._time * '\uea60');
                                                BanManager.addBan(new Ban(var21.hwid, var33, var30));
                                                GuardLog.getLogger().info(String.format("HWID \'%s\' was banned for %d minutes.", new Object[]{var21.hwid, Integer.valueOf(var28._time)}));
                                                return MsgType.TEMP_BAN.paket;
                                            case 2:
                                                int var32 = Rnd.get(var28._delayMin, var28._delayMax);
                                                BanManager.addDelayedBan(new Ban(var21.hwid, var30), (long)(var32 * '\uea60'));
                                                GuardLog.getLogger().info(String.format("HWID \'%s\' will be banned in %d minutes.", new Object[]{var21.hwid, Integer.valueOf(var32)}));
                                                break label96;
                                            case 3:
                                            default:
                                                BanManager.addBan(new Ban(var21.hwid, var30));
                                                GuardLog.getLogger().info(String.format("HWID \'%s\' was banned.", new Object[]{var21.hwid}));
                                                return MsgType.DETECTED_BAN.paket;
                                        }
                                    case 2:
                                        GuardLog.getLogger().info(String.format("Client \'%s\' was disconnected.", new Object[]{var21.account}));
                                        return MsgType.DETECTED_KICK.paket;
                                }
                            }

                            DetectedSoftware var31 = (DetectedSoftware)var29.next();
                            da = GuardConfig.PunishActions[var31.softwareType.ordinal()];
                        } while(var28 != null && (var28._mode.priority > da._mode.priority || var28._type.priority > da._type.priority));

                        var28 = da;
                    }
                }

                ClientSessionManager.setClientData(client, var21);
                ClientSession var24 = ClientSessionManager.getSession(var21);
                if(var24 == null) {
                    var24 = new ClientSession(var21);
                    ClientSessionManager.putSession(var24);
                }

                if(!var24.canLogin()) {
                    GuardLog.getLogger().info(String.format("Client \'%s\' has reached maximum number of game instances. (%d)", new Object[]{var21.account, Integer.valueOf(var24.getCount())}));
                    return MsgType.INSTANCE_LIMIT.paket;
                } else {
                    DbLogger.logAuth(var21, client);
                    GuardLog.logAuth(var21, client);
                    client.setHWID(var21.hwid.plain);
                    var24.addClient(var21, client);
                    return MsgType.NO_ERROR.paket;
                }
                */
				List<DetectedSoftware> detects = DetectedSoftware.getList(mask);
				if(detects.size() != 0)
				{
					// get names for all detected software
					String[] names = new String[detects.size()];

					int i = 0;
					for(DetectedSoftware is : detects)
					{
						names[i++] = is.name;
					}

					// using full string
					String using = Strings.joinStrings(", ", names);

					GuardLog.getLogger().info(String.format("Client '%s' (HWID:%s) is using: %s", cd.account, cd.hwid, using));

					PunishAction result = null;

					for(DetectedSoftware is : detects)
					{
						PunishAction da = GuardConfig.PunishActions[is.softwareType.ordinal()];

						if(result != null)
						{
							// punishment mode compare
							if(result._mode.priority > da._mode.priority)
							{
								continue;
							}

							// punishment type compare
							if(result._type.priority > da._type.priority)
							{
								continue;
							}
						}

						result = da;
					}

					if(result == null)
					{
						GuardLog.getLogger().info(String.format("Client '%s' was ??  GENERAL_ERROR !!", cd.account));
						return MsgPacket.MsgType.GENERAL_ERROR.paket;
					}

					GuardLog.getLogger().info(String.format("Client '%s' was disconnected.", cd.account));
					return MsgPacket.MsgType.DETECTED_KICK.paket;


					/*switch(result._mode)
					{
						case BAN:
						{
							String comment = String.format("[%s] acc: '%s', using: %s", new SimpleDateFormat("MM/dd/yy HH:mm").format(Calendar.getInstance().getTime()), cd.account, using);

							switch(result._type)
							{
								// временный бан
								case TEMPORARY:
								{
									long bannedUntil = System.currentTimeMillis() + (result._time * 60000);
									BanManager.addBan(new Ban(cd.hwid, bannedUntil, comment));
									GuardLog.getLogger().info(String.format("HWID '%s' was banned for %d minutes.", cd.hwid, result._time));
									return MsgPacket.MsgType.TEMP_BAN.paket;
								}

								// отложенный бан
								case DELAYED:
								{
									int delay = Rnd.get(result._delayMin, result._delayMax);
									BanManager.addDelayedBan(new Ban(cd.hwid, comment), delay * 60000);
									GuardLog.getLogger().info(String.format("HWID '%s' will be banned in %d minutes.", cd.hwid, delay));
									break;
								}

								// вечный бан
								case REALTIME:
								default:
								{
									BanManager.addBan(new Ban(cd.hwid, comment));
									GuardLog.getLogger().info(String.format("HWID '%s' was banned.", cd.hwid));
									return MsgPacket.MsgType.DETECTED_BAN.paket;
								}
							}

							break;
						}

						case DISCONNECT:
						{
							GuardLog.getLogger().info(String.format("Client '%s' was disconnected.", cd.account));
							return MsgPacket.MsgType.DETECTED_KICK.paket;
						}
					}*/
				}
				// привязываем данные к клиенту
				ClientSessionManager.setClientData(client, cd);

				// ищем сессию
				ClientSession session = ClientSessionManager.getSession(cd);

				// первый вход с компьютера? создаем новую сессию...
				if(session == null)
				{
					session = new ClientSession(cd);
					ClientSessionManager.putSession(session); // добавляем сессию в общий пул
				}

				// возможен ли вход еще одного окна на сервер?
				if(!session.canLogin())
				{
					GuardLog.getLogger().info(String.format("Client '%s' has reached maximum number of game instances. (%d)", cd.account, session.getCount()));
					return MsgPacket.MsgType.INSTANCE_LIMIT.paket;
				}

				DbLogger.logAuth(cd, client);
				GuardLog.logAuth(cd, client);

				client.setHWID(cd.hwid.plain);

				// добавляем клиента в сессию
				session.addClient(cd, client);

				return MsgPacket.MsgType.NO_ERROR.paket;
            }
        }
    }

    public static void main(String[] args) {
        if(args.length == 0) {
            GuardLog.getLogger().severe("Main class not specified!");
        } else {
            try {
                LicenseManager.getInstance();
                UpdateManager.getInstance();
            } catch (Exception var6) {
                GuardLog.getLogger().severe("Error initializing SmartGuard");
                GuardLog.logException(var6);
                return;
            }

            try {
                Class e = Class.forName(args[0]);
                Method main = e.getDeclaredMethod("main", new Class[]{String[].class});
                String[] mainArgs = (String[])Arrays.copyOfRange(args, 1, args.length);
                main.invoke((Object)null, new Object[]{mainArgs});
                GuardLog.getLogger().info("SmartGuard has been initialized.");
            } catch (Exception var5) {
                GuardLog.getLogger().severe("GameServer failed to start!");
                GuardLog.logException(var5);
                return;
            }

            try {
                AdminCommandHandler.getInstance().registerAdminCommandHandler(new SmartGuardMenu());
            } catch (Exception var4) {
                GuardLog.getLogger().severe("Error initializing SmartGuard AdminCommandHandler!");
                GuardLog.logException(var4);
            }

            IS_LOADING_FINISHED = true;
        }
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.akumu.smartguard.menu;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.GameClient;
import l2s.gameserver.network.l2.GameClient.GameClientState;
import l2s.gameserver.network.l2.s2c.NpcHtmlMessagePacket;
import ru.akumu.smartguard.manager.UpdateManager;
import ru.akumu.smartguard.manager.bans.BanManager;
import ru.akumu.smartguard.manager.bans.model.Ban;
import ru.akumu.smartguard.manager.session.ClientSessionManager;
import ru.akumu.smartguard.manager.session.model.ClientSession;
import ru.akumu.smartguard.manager.session.model.HWID;
import ru.akumu.smartguard.utils.Strings;
import ru.akumu.smartguard.utils.log.GuardLog;

public class SmartGuardMenu implements IAdminCommandHandler {
    private static final int BANS_PER_PAGE = 10;
    private static final SimpleDateFormat banTimeFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private static final SimpleDateFormat licenseTimeFormatter = new SimpleDateFormat("dd-MM-yyyy");

    public SmartGuardMenu() {
    }

    public boolean useAdminCommand(Enum anEnum, String[] strings, String s, Player player)
	{
		try
		{
			if(!player.isGM())
			{
				return false;
			}

			//String[] strings = s.split(" ");
			Commands command = Commands.valueOf(strings[0]);

			switch(command)
			{
				case admin_sg:
				case admin_guard:
				{
					sendMainPage(player);
					break;
				}

				case admin_sg_find:
				{
					if(strings.length != 3)
					{
						player.sendMessage("Usage: //sg_find <hwid|player> <query>");
						sendMainPage(player);
						return false;
					}

					QueryType type = QueryType.valueOf(strings[1]);
					String query = strings[2];

					List<ClientSession> result = new ArrayList<>();

					switch(type)
					{
						case hwid:
						{
							for(ClientSession p : ClientSessionManager.getAllSessions())
							{
								if(p != null && p.hwid().contains(query))
								{
									result.add(p);
								}
							}
							/*
							for(Player p : WorldManager.getInstance().getAllPlayersArray())
							{
								if(p != null && p.isOnline() && p.getClient().getHWID().contains(query))
								{
									result.add(ClientSessionManager.getSession(p.getClient()));
								}
							}*/
							break;
						}

						case player:
						{
							for(ClientSession p : ClientSessionManager.getAllSessions())
							{
								if(p != null && p.hasAccountSession(query))
								{
									result.add(p);
								}
							}
							/*
							for(L2PcInstance p : WorldManager.getInstance().getAllPlayersArray())
							{
								if(p != null && p.isOnline() && p.getName().contains(query))
								{
									result.add(ClientSessionManager.getSession(p.getClient()));
								}
							}
							*/
							break;
						}
					}

					NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(5);

					if(result.size() == 0)
					{
						html.setHtml(buildPage(SmartTemplate.PageMain.load()));
						player.sendMessage("Поиск не дал результатов.");
					}
					else if(result.size() == 1)
					{
						html.setHtml(buildSessionPage(result.get(0)));
					}
					else
					{
						html.setHtml(buildSearchPage(query, result));
					}

					player.sendPacket(html);
					break;
				}

				case admin_sg_bans:
				{
					int page = 0;
					String query = null;

					if(strings.length > 1)
					{
						try
						{
							page = Integer.parseInt(strings[1]);

							if(page < 0)
							{
								page = 0;
							}
						}
						catch(Exception e)
						{
						}
					}

					if(strings.length > 2)
					{
						query = strings[2];

						if(query.isEmpty())
						{
							query = null;
						}
					}

					sendBansPage(player, page, query);
					break;
				}

				case admin_sg_show:
				{
					if(strings.length != 2)
					{
						player.sendMessage("Usage: //sg_show <session_id>");
						sendMainPage(player);
						return false;
					}

					HWID hwid = HWID.fromString(strings[1]);
					ClientSession session = ClientSessionManager.getSession(hwid);

					if(session == null)
					{
						return false;
					}

					NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(5);
					html.setHtml(buildSessionPage(session));
					player.sendPacket(html);
					break;
				}

				case admin_sg_kick_session:
				{
					if(strings.length != 2)
					{
						player.sendMessage("Usage: //sg_kick_session <session_id>");
						sendMainPage(player);
						return false;
					}

					HWID hwid = HWID.fromString(strings[1]);
					ClientSession session = ClientSessionManager.getSession(hwid);

					if(session == null)
					{
						return false;
					}

					session.disconnect();

					player.sendMessage("All players from guard session have been kicked.");
					sendMainPage(player);
					break;
				}

				case admin_sg_ban:
				{
					if(strings.length < 3)
					{
						player.sendMessage("Usage: //sg_ban <hwid|player> <victim> [reason]");
						sendBansPage(player, 0, null);
						return false;
					}

					QueryType type = QueryType.valueOf(strings[1]);

					switch(type)
					{
						case hwid:
						{
							String reason = null;
							String hwid_string = strings[2];
							if(strings.length > 3)
							{
								reason = Strings.joinStrings(" ", strings, 3);
							}

							HWID hwid = HWID.fromString(hwid_string);

							if(hwid == null)
							{
								player.sendMessage(String.format("Hwid '%s' has bad format.", hwid_string));
								sendBansPage(player, 0, null);
								return false;
							}

							Ban ban = new Ban(hwid, reason);
							BanManager.addBan(ban);

							player.sendMessage(String.format("Hwid '%s' has been banned.", hwid));

							ClientSession session = ClientSessionManager.getSession(hwid);

							if(session != null)
							{
								session.disconnect();
							}

							if(reason != null)
							{
								GuardLog.getLogger().info(String.format("Admin '%s' has banned HWID %S, reason: '%s'", player.getName(), hwid, reason));
							}
							else
							{
								GuardLog.getLogger().info(String.format("Admin '%s' has banned HWID %S", player.getName(), hwid));
							}
							break;
						}

						case player:
						{
							String playerName = strings[2];
							String reason = null;

							if(strings.length > 3)
							{
								reason = Strings.joinStrings(" ", strings, 3);
							}

							Player pc = GameObjectsStorage.getPlayer(playerName);

							if(pc == null)
							{
								player.sendMessage(String.format("Player '%s' was not found!", playerName));
								break;
							}

							ClientSession session = ClientSessionManager.getSession(pc.getNetConnection());

							if(session == null)
							{
								String error = String.format("Error! Session for player '%s' does not exist!", playerName);
								player.sendMessage(error);
								GuardLog.getLogger().severe(error);
								break;
							}

							Ban ban = new Ban(session.hwid, reason);
							BanManager.addBan(ban);

							player.sendMessage(String.format("Hwid %s has been banned.", session.hwid()));

							session.disconnect();

							GuardLog.getLogger().info(String.format("Admin '%s' has banned player '%s' by hwid. HWID: %S", player.getName(), playerName, session.hwid()));
							break;
						}
					}

					sendBansPage(player, 0, null);
					break;
				}

				case admin_sg_unban:
				{
					if(strings.length != 2)
					{
						player.sendMessage("Usage: //sg_unban <hwid>");
						sendBansPage(player, 0, null);
						return false;
					}

					String hwid = strings[1];

					BanManager.removeBan(hwid);

					player.sendMessage(String.format("Hwid %s has been un-banned.", hwid));

					GuardLog.getLogger().info(String.format("Admin '%s' has removed ban from HWID %S", player.getName(), hwid));
					sendMainPage(player);
					break;
				}
			}
		}
		catch(Exception e)
		{
			GuardLog.getLogger().severe("Error in SmartGuard menu");
			GuardLog.logException(e);
			player.sendMessage("SmartGuard menu has crashed! See game server logs!");
			return false;
		}

		return true;
		/*
        try {
            SmartGuardMenu.Commands e = (SmartGuardMenu.Commands)anEnum;
            String playerName;
            SmartGuardMenu.QueryType hwid1;
            HWID hwid2;
            ClientSession playerName1;
            ClientSession ban2;
            switch(e.ordinal()) {
                case 1:
                case 2:
                    this.sendMainPage(player);
                    break;
                case 3:
                    if(strings.length != 3) {
                        player.sendMessage("Usage: //sg_find <hwid|player> <query>");
                        this.sendMainPage(player);
                        return false;
                    }

                    hwid1 = SmartGuardMenu.QueryType.valueOf(strings[1]);
                    playerName = strings[2];
                    playerName = playerName.toLowerCase();
                    ArrayList reason2 = new ArrayList();
                    Collection pc2 = ClientSessionManager.getAllSessions();
                    Iterator session2 = pc2.iterator();

                    while(true) {
                        while(true) {
                            while(session2.hasNext()) {
                                ban2 = (ClientSession)session2.next();
                                switch(hwid1.ordinal()) {
                                    case 1:
                                        if(ban2.hwid().contains(playerName)) {
                                            reason2.add(ban2);
                                        }
                                        break;
                                    case 2:
                                        Iterator i$ = ban2.getClients().iterator();

                                        while(i$.hasNext()) {
                                            GameClient client = (GameClient)i$.next();
                                            if(client.getState() == GameClientState.IN_GAME) {
                                                Player p = client.getActiveChar();
                                                if(p != null && p.getName().toLowerCase().contains(playerName)) {
                                                    reason2.add(ban2);
                                                    break;
                                                }
                                            }
                                        }
                                }
                            }

                            NpcHtmlMessagePacket session3;
                            if(reason2.size() == 0) {
                                session3 = new NpcHtmlMessagePacket(5);
                                session3.setHtml(this.buildPage(SmartGuardMenu.SmartTemplate.PageMain.load()));
                                player.sendMessage("Поиск не дал результатов.");
                            } else if(reason2.size() == 1) {
                                session3 = new NpcHtmlMessagePacket(5);
                                session3.setHtml(this.buildSessionPage((ClientSession)reason2.get(0)));
                            } else {
                                session3 = new NpcHtmlMessagePacket(5);
                                session3.setHtml(this.buildSearchPage(playerName, reason2));
                            }

                            player.sendPacket(session3);
                            return true;
                        }
                    }
                case 4:
                    int hwid3 = 0;
                    playerName = null;
                    if(strings.length > 1) {
                        try {
                            hwid3 = Integer.parseInt(strings[1]);
                            if(hwid3 < 0) {
                                hwid3 = 0;
                            }
                        } catch (Exception var15) {
                            ;
                        }
                    }

                    if(strings.length > 2) {
                        playerName = strings[2];
                        if(playerName.isEmpty()) {
                            playerName = null;
                        }
                    }

                    this.sendBansPage(player, hwid3, playerName);
                    break;
                case 5:
                    if(strings.length != 2) {
                        player.sendMessage("Usage: //sg_show <session_id>");
                        this.sendMainPage(player);
                        return false;
                    }

                    hwid2 = HWID.fromString(strings[1]);
                    playerName1 = ClientSessionManager.getSession(hwid2);
                    if(playerName1 == null) {
                        return false;
                    }

                    NpcHtmlMessagePacket reason1 = new NpcHtmlMessagePacket(5);
                    reason1.setHtml(this.buildSessionPage(playerName1));
                    player.sendPacket(reason1);
                    break;
                case 6:
                    if(strings.length != 2) {
                        player.sendMessage("Usage: //sg_kick_session <session_id>");
                        this.sendMainPage(player);
                        return false;
                    }

                    hwid2 = HWID.fromString(strings[1]);
                    playerName1 = ClientSessionManager.getSession(hwid2);
                    if(playerName1 == null) {
                        return false;
                    }

                    playerName1.disconnect();
                    player.sendMessage("All players from guard session have been kicked.");
                    this.sendMainPage(player);
                    break;
                case 7:
                    if(strings.length < 3) {
                        player.sendMessage("Usage: //sg_ban <hwid|player> <victim> [reason]");
                        this.sendBansPage(player, 0, null);
                        return false;
                    }

                    hwid1 = SmartGuardMenu.QueryType.valueOf(strings[1]);
                    String reason;
                    switch(hwid1.ordinal()) {
                        case 1:
                            playerName = null;
                            reason = strings[2];
                            if(strings.length > 3) {
                                playerName = Strings.joinStrings(" ", strings, 3);
                            }

                            HWID pc1 = HWID.fromString(reason);
                            if(pc1 == null) {
                                player.sendMessage(String.format("Hwid \'%s\' has bad format.", new Object[]{reason}));
                                this.sendBansPage(player, 0, null);
                                return false;
                            }

                            Ban session1 = new Ban(pc1, playerName);
                            BanManager.addBan(session1);
                            player.sendMessage(String.format("Hwid \'%s\' has been banned.", new Object[]{pc1}));
                            ban2 = ClientSessionManager.getSession(pc1);
                            if(ban2 != null) {
                                ban2.disconnect();
                            }

                            if(playerName != null) {
                                GuardLog.getLogger().info(String.format("Admin \'%s\' has banned HWID %S, reason: \'%s\'", new Object[]{player.getName(), pc1, playerName}));
                            } else {
                                GuardLog.getLogger().info(String.format("Admin \'%s\' has banned HWID %S", new Object[]{player.getName(), pc1}));
                            }
                            break;
                        case 2:
                            playerName = strings[2];
                            reason = null;
                            if(strings.length > 3) {
                                reason = Strings.joinStrings(" ", strings, 3);
                            }

                            Player pc = GameObjectsStorage.getPlayer(playerName);
                            if(pc == null) {
                                player.sendMessage(String.format("Player \'%s\' was not found!", new Object[]{playerName}));
                            } else {
                                ClientSession session = ClientSessionManager.getSession(pc.getNetConnection());
                                if(session == null) {
                                    String ban = String.format("Error! Session for player \'%s\' does not exist!", new Object[]{playerName});
                                    player.sendMessage(ban);
                                    GuardLog.getLogger().severe(ban);
                                } else {
                                    Ban ban1 = new Ban(session.hwid, reason);
                                    BanManager.addBan(ban1);
                                    player.sendMessage(String.format("Hwid %s has been banned.", new Object[]{session.hwid()}));
                                    session.disconnect();
                                    GuardLog.getLogger().info(String.format("Admin \'%s\' has banned player \'%s\' by hwid. HWID: %S", new Object[]{player.getName(), playerName, session.hwid()}));
                                }
                            }
                    }

                    this.sendBansPage(player, 0, null);
                    break;
                case 8:
                    if(strings.length != 2) {
                        player.sendMessage("Usage: //sg_unban <hwid>");
                        this.sendBansPage(player, 0, null);
                        return false;
                    }

                    String hwid = strings[1];
                    BanManager.removeBan(hwid);
                    player.sendMessage(String.format("Hwid %s has been un-banned.", new Object[]{hwid}));
                    GuardLog.getLogger().info(String.format("Admin \'%s\' has removed ban from HWID %S", new Object[]{player.getName(), hwid}));
                    this.sendMainPage(player);
            }

            return true;
        } catch (Exception var16) {
            GuardLog.getLogger().severe("Error in SmartGuard menu");
            GuardLog.logException(var16);
            player.sendMessage("SmartGuard menu has crashed! See game server logs!");
            return false;
        }*/
    }

    public Enum[] getAdminCommandEnum() {
        return SmartGuardMenu.Commands.values();
    }

    private String buildBansPage(Player player, int pageNumSys, String query) {
        int count = 0;
        StringBuilder records = new StringBuilder();
        String bansRecord = SmartGuardMenu.SmartTemplate.RecordBan.load();
        if(query != null) {
            query = query.toLowerCase();
        }

        int pageNumVis = pageNumSys + 1;
        int itemsToSkip = pageNumSys * BANS_PER_PAGE;
        Collection bans = BanManager.getBans();
        Iterator bansSize;
        Ban mod;
        String page;
        if(query != null) {
            bansSize = bans.iterator();

            while(bansSize.hasNext()) {
                mod = (Ban)bansSize.next();
                boolean totalPages = true;
                if(mod.hwid.plain.contains(query)) {
                    totalPages = false;
                }

                if(mod.comment != null && mod.comment.toLowerCase().contains(query)) {
                    totalPages = false;
                }

                String[] prev_page = mod.getAccountNames();
                if(prev_page.length > 0) {
                    String[] next_page = prev_page;
                    int cur_page = prev_page.length;

                    for(int max_page = 0; max_page < cur_page; ++max_page) {
                        page = next_page[max_page];
                        if(page.toLowerCase().contains(query)) {
                            totalPages = false;
                            break;
                        }
                    }
                }

                if(totalPages) {
                    bansSize.remove();
                }
            }
        }

        bansSize = bans.iterator();

        while(bansSize.hasNext()) {
            mod = (Ban)bansSize.next();
            if(itemsToSkip <= 0 || itemsToSkip-- <= 0) {
                String var21 = bansRecord.replaceAll("%hwid%", mod.hwid.plain);
                var21 = var21.replaceAll("%reason%", mod.comment != null?mod.comment:"-");
                var21 = var21.replaceAll("%date%", banTimeFormatter.format(new Date(mod.time)));
                var21 = var21.replaceAll("%end%", mod.bannedUntil > 0L?banTimeFormatter.format(new Date(mod.bannedUntil)):"-");
                String var20 = "Защита";
                if(mod.gmObjId > 0) {
                    Player var22 = GameObjectsStorage.getPlayer(mod.gmObjId);
                    if(var22 == null) {
                        var22 = Player.restore(mod.gmObjId, false);
                    }

                    if(var22 != null) {
                        var20 = var22.getName();
                    } else {
                        var20 = String.format("? (objId %d)", new Object[]{Integer.valueOf(mod.gmObjId)});
                    }
                }

                String var24 = Strings.joinStrings(", ", mod.getAccountNames());
                if(var24.length() == 0) {
                    var24 = mod.hwid.plain;
                }

                var21 = var21.replaceAll("%gm%", var20);
                var21 = var21.replaceAll("%ban%", var24);
                records.append(var21);
                ++count;
                if(count == BANS_PER_PAGE) {
                    break;
                }
            }
        }

        if(records.length() == 0) {
            player.sendMessage("Banlist is empty!");
            return this.buildPage(SmartGuardMenu.SmartTemplate.PageMain.load());
        } else {
            int var18 = bans.size();
            int var19 = var18 % BANS_PER_PAGE;
            int var25 = (var18 - var19) / BANS_PER_PAGE;
            if(var19 > 0) {
                ++var25;
            }

            player.sendMessage(String.format("Displaying ban page %d of %d. Total bans: %d", new Object[]{Integer.valueOf(pageNumVis), Integer.valueOf(var25), Integer.valueOf(var18)}));
            int var23 = 0;
            if(pageNumSys > 0) {
                var23 = pageNumSys - 1;
            }

            int var27;
            if(var25 - 1 > pageNumSys) {
                var27 = pageNumSys + 1;
            } else {
                var27 = var25 - 1;
            }

            page = SmartGuardMenu.SmartTemplate.PageBans.load();
            page = page.replace("%count%", String.valueOf(bans.size()));
            page = page.replace("%records%", records);
            page = page.replace("%query%", query == null?"":query);
            page = page.replace("%page_prev%", String.valueOf(var23));
            page = page.replace("%page_next%", String.valueOf(var27));
            page = page.replace("%page_cur%", String.valueOf(pageNumVis));
            page = page.replace("%page_max%", String.valueOf(var25));
            return this.buildPage(page);
        }
    }

    private String buildSearchPage(String query, List<ClientSession> results) {
        StringBuilder sb = new StringBuilder();
        String searchRecord = SmartGuardMenu.SmartTemplate.RecordSearch.load();
        Iterator page = results.iterator();

        while(page.hasNext()) {
            ClientSession session = (ClientSession)page.next();
            int i = 0;
            int max = session.getCount();
            StringBuilder players = new StringBuilder();

            for(Iterator record = session.getClients().iterator(); record.hasNext(); ++i) {
                GameClient client = (GameClient)record.next();
                players.append(client.getActiveChar().getName());
                if(i < max) {
                    players.append(",");
                }
            }

            String var12 = searchRecord.replace("%hwid%", session.hwid());
            var12 = var12.replace("%sid%", session.hwid());
            var12 = var12.replace("%players%", players);
            sb.append(var12);
        }

        String var13 = SmartGuardMenu.SmartTemplate.PageSearch.load();
        var13 = var13.replace("%count%", String.valueOf(results.size()));
        var13 = var13.replace("%records%", sb);
        var13 = var13.replace("%query%", query);
        return this.buildPage(var13);
    }

    private String buildSessionPage(ClientSession session) {
        StringBuilder sb = new StringBuilder();
        String sessionRecord = SmartGuardMenu.SmartTemplate.RecordSession.load();
        Iterator page = session.getClients().iterator();

        while(page.hasNext()) {
            GameClient client = (GameClient)page.next();
            if(client.getState() == GameClientState.IN_GAME) {
                String record = sessionRecord.replaceAll("%player_name%", client.getActiveChar().getName());
                record = record.replaceAll("%acc_name%", client.getActiveChar().getAccountName());
                sb.append(record);
            }
        }

        String page1 = SmartGuardMenu.SmartTemplate.PageSession.load();
        page1 = page1.replace("%online%", String.valueOf(session.getCount()));
        page1 = page1.replaceAll("%hwid%", session.hwid());
        page1 = page1.replace("%records%", sb);
        page1 = page1.replace("%sid%", session.hwid());
        return this.buildPage(page1);
    }

    private String buildPage(CharSequence content) {
        String date = licenseTimeFormatter.format(new Date(UpdateManager.getInstance().LicenseExpiry));
        return SmartGuardMenu.SmartTemplate.Main.load().replace("%content%", content).replace("%expire_time%", date);
    }

    private void sendMainPage(Player player) {
        NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(5);
        html.setHtml(this.buildPage(SmartGuardMenu.SmartTemplate.PageMain.load()));
        player.sendPacket(html);
    }

    private void sendBansPage(Player player, int page, String query) {
        NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(5);
        html.setHtml(this.buildBansPage(player, page, query));
        player.sendPacket(html);
    }

    private static String loadTemplate(String templateName) {
        return loadTemplate(new File("./smartguard/html/", templateName));
    }

    private static String loadTemplate(File file) {
        if(file != null && file.exists()) {
            SmartGuardMenu.TplFilter tf = new SmartGuardMenu.TplFilter();
            if(!tf.accept(file)) {
                return null;
            } else {
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                String content;

                Object raw;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    int e = bis.available();
                    byte[] raw1 = new byte[e];
                    bis.read(raw1);
                    content = new String(raw1, "UTF-8");
                    content = content.replaceAll("\r\n", "\n");
                    return content;
                } catch (Exception var20) {
                    GuardLog.logException(var20);
                    raw = null;
                } finally {
                    try {
                        if(fis != null) {
                            fis.close();
                        }
                    } catch (Exception var19) {
                        ;
                    }

                    try {
                        if(bis != null) {
                            bis.close();
                        }
                    } catch (Exception var18) {
                        ;
                    }

                }

                return (String)raw;
            }
        } else {
            return null;
        }
    }

    static class TplFilter implements FileFilter {
        TplFilter() {
        }

        public boolean accept(File file) {
            return !file.isDirectory() && file.getName().endsWith(".tpl");
        }
    }

    private static enum SmartTemplate {
        Main(null, "index.tpl", false),
        PageBans("%content%", "page_bans.tpl", false),
        PageMain("%content%", "page_main.tpl", false),
        PageSearch("%content%", "page_search.tpl", false),
        PageSession("%content%", "page_session.tpl", false),
        RecordBan("%records%", "record_ban.tpl", true),
        RecordSearch("%records%", "record_search.tpl", true),
        RecordSession("%records%", "record_session.tpl", true);

        public final String tag;
        public final String name;
        public final boolean isRecord;

        private SmartTemplate(String tag, String name, boolean isRecord) {
            this.tag = tag;
            this.name = name;
            this.isRecord = isRecord;
        }

        public String load() {
            return SmartGuardMenu.loadTemplate(this.name);
        }
    }

    private static enum QueryType {
        player,
        hwid;

        private QueryType() {
        }
    }

    private static enum Commands {
        admin_sg,
        admin_guard,
        admin_sg_ban,
        admin_sg_unban,
        admin_sg_find,
        admin_sg_bans,
        admin_sg_show,
        admin_sg_kick_session;

        private Commands() {
        }

        public static String[] names() {
            SmartGuardMenu.Commands[] states = values();
            String[] names = new String[states.length];

            for(int i = 0; i < states.length; ++i) {
                names[i] = states[i].name();
            }

            return names;
        }
    }
}

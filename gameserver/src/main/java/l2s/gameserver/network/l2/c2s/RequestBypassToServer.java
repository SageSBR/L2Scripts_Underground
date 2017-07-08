
package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.EventHolder;
import l2s.gameserver.data.xml.holder.MultiSellHolder;
import l2s.gameserver.handler.admincommands.AdminCommandHandler;
import l2s.gameserver.handler.bbs.CommunityBoardManager;
import l2s.gameserver.handler.bbs.ICommunityBoardHandler;
import l2s.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2s.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2s.gameserver.instancemanager.BypassManager;
import l2s.gameserver.instancemanager.OlympiadHistoryManager;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Hero;
import l2s.gameserver.model.entity.events.EventType;
import l2s.gameserver.model.entity.events.impl.ChaosFestivalEvent;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.instances.OlympiadManagerInstance;
import l2s.gameserver.model.quest.dynamic.DynamicQuestController;
import l2s.gameserver.network.l2.GameClient;
import l2s.gameserver.network.l2.components.IStaticPacket;
import l2s.gameserver.network.l2.s2c.NpcHtmlMessagePacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.scripts.Scripts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.StringTokenizer;

public class RequestBypassToServer
		extends L2GameClientPacket {
	private static final Logger _log = LoggerFactory.getLogger((Class)RequestBypassToServer.class);
	private String bypass;

	@Override
	protected void readImpl() {
		this.bypass = this.readS();
	}

	@Override
	protected void runImpl() {
		Player activeChar = ((GameClient)this.getClient()).getActiveChar();
		if (activeChar == null) {
			return;
		}
		BypassManager.DecodedBypass bp = activeChar.decodeBypass(this.bypass);
		if (bp == null) {
			return;
		}
		try {
			NpcInstance npc = activeChar.getLastNpc();
			GameObject target = activeChar.getTarget();
			if (npc == null && target != null && target.isNpc()) {
				npc = (NpcInstance)target;
			}
			if (bp.bypass.startsWith("admin_")) {
				AdminCommandHandler.getInstance().useAdminCommandHandler(activeChar, bp.bypass);
			} else if (bp.bypass.equals("come_here") && activeChar.isGM()) {
				RequestBypassToServer.comeHere((GameClient)this.getClient());
			} else if (bp.bypass.startsWith("player_help ")) {
				String[] helpParams = bp.bypass.split(" ");
				if (helpParams.length > 2) {
					RequestBypassToServer.playerHelp(activeChar, helpParams[1], Integer.parseInt(helpParams[2]));
				}
			} else if (bp.bypass.startsWith("pcbang?")) {
				String command = bp.bypass.substring(7).trim();
				StringTokenizer st = new StringTokenizer(command, "_");
				String cmd = st.nextToken();
				if (cmd.equalsIgnoreCase("multisell")) {
					int multisellId = Integer.parseInt(st.nextToken());
					if (!Config.ALT_ALLOWED_MULTISELLS_IN_PCBANG.contains(multisellId)) {
						_log.warn("Unknown multisell list use in PC-Bang shop! List ID: " + multisellId + ", player ID: " + activeChar.getObjectId() + ", player name: " + activeChar.getName());
						return;
					}
					MultiSellHolder.getInstance().SeparateAndSend(multisellId, activeChar, 0.0);
				}
			} else if (bp.bypass.startsWith("scripts_")) {
				String command = bp.bypass.substring(8).trim();
				String[] word = command.split("\\s+");
				String[] args = command.substring(word[0].length()).trim().split("\\s+");
				String[] path = word[0].split(":");
				if (path.length != 2) {
					_log.warn("Bad Script bypass!");
					return;
				}
				HashMap<String, Object> variables = null;
				if (npc != null) {
					variables = new HashMap<String, Object>(1);
					variables.put("npc", npc.getRef());
				}
				if (word.length == 1) {
					Scripts.getInstance().callScripts(activeChar, path[0], path[1], variables);
				} else {
					Scripts.getInstance().callScripts(activeChar, path[0], path[1], new Object[]{args}, variables);
				}
			} else if (bp.bypass.startsWith("user_")) {
				String command = bp.bypass.substring(5).trim();
				String word = command.split("\\s+")[0];
				String args = command.substring(word.length()).trim();
				IVoicedCommandHandler vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler(word);
				if (vch != null) {
					vch.useVoicedCommand(word, activeChar, args);
				} else {
					_log.warn("Unknow voiced command '" + word + "'");
				}
			} else if (bp.bypass.startsWith("npc_")) {
				int endOfId = bp.bypass.indexOf(95, 5);
				String id = endOfId > 0 ? bp.bypass.substring(4, endOfId) : bp.bypass.substring(4);
				GameObject object = activeChar.getVisibleObject(Integer.parseInt(id));
				if (object != null && object.isNpc() && endOfId > 0 && activeChar.isInRange(object.getLoc(), 200)) {
					activeChar.setLastNpc((NpcInstance)object);
					((NpcInstance)object).onBypassFeedback(activeChar, bp.bypass.substring(endOfId + 1));
				}
			} else if (bp.bypass.startsWith("_olympiad?")) {
				String[] ar = bp.bypass.replace("_olympiad?", "").split("&");
				String firstVal = ar[0].split("=")[1];
				String secondVal = ar[1].split("=")[1];
				if (firstVal.equalsIgnoreCase("move_op_field")) {
					NpcInstance lastNpc = activeChar.getLastNpc();
					if (lastNpc == null) {
						return;
					}
					int arenaID = Integer.parseInt(secondVal) - 1;
					if (lastNpc.getNpcId() == 135 && lastNpc.isInRange(activeChar, 200) || activeChar.getFightBattleObserverArena() != null) {
						activeChar.enterFightBattleObserverMode(arenaID);
						return;
					}
					if (!Config.ENABLE_OLYMPIAD_SPECTATING) {
						return;
					}
					if (lastNpc instanceof OlympiadManagerInstance && lastNpc.isInRange(activeChar, 200) || activeChar.getOlympiadObserveGame() != null) {
						Olympiad.addSpectator(arenaID, activeChar);
					}
				}
			} else if (bp.bypass.startsWith("_diary")) {
				String params = bp.bypass.substring(bp.bypass.indexOf("?") + 1);
				StringTokenizer st = new StringTokenizer(params, "&");
				int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
				int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
				int heroid = Hero.getInstance().getHeroByClass(heroclass);
				if (heroid > 0) {
					Hero.getInstance().showHeroDiary(activeChar, heroclass, heroid, heropage);
				}
			} else if (bp.bypass.startsWith("_match")) {
				String params = bp.bypass.substring(bp.bypass.indexOf("?") + 1);
				StringTokenizer st = new StringTokenizer(params, "&");
				int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
				int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
				OlympiadHistoryManager.getInstance().showHistory(activeChar, heroclass, heropage);
			} else if (bp.bypass.startsWith("manor_menu_select?")) {
				GameObject object = activeChar.getTarget();
				if (object != null && object.isNpc()) {
					((NpcInstance)object).onBypassFeedback(activeChar, bp.bypass);
				}
			} else if (bp.bypass.startsWith("menu_select?")) {
				if (npc != null) {
					String params = bp.bypass.substring(bp.bypass.indexOf("?") + 1);
					StringTokenizer st = new StringTokenizer(params, "&");
					int ask = Integer.parseInt(st.nextToken().split("=")[1]);
					int reply = Integer.parseInt(st.nextToken().split("=")[1]);
					npc.onMenuSelect(activeChar, ask, reply);
				}
			} else if (bp.bypass.startsWith("Quest ")) {
				String p = bp.bypass.substring(6).trim();
				int idx = p.indexOf(32);
				if (idx < 0) {
					activeChar.processQuestEvent(p, "", npc);
				} else {
					activeChar.processQuestEvent(p.substring(0, idx), p.substring(idx).trim(), npc);
				}
			} else if (bp.bypass.startsWith("Campaign ")) {
				String p = bp.bypass.substring(9).trim();
				int idx = p.indexOf(32);
				if (idx > 0) {
					String campaignName = p.substring(0, idx);
					DynamicQuestController.getInstance().processDialogEvent(campaignName, p.substring(idx).trim(), activeChar);
				}
			} else if (bp.bypass.startsWith("chaosfestival_")) {
				String p = bp.bypass.substring(14).trim();
				ChaosFestivalEvent event = (ChaosFestivalEvent)((Object)EventHolder.getInstance().getEvent(EventType.PVP_EVENT, 5));
				if (event != null) {
					event.onBypassCommand(activeChar, p);
				}
			} else if (bp.bbs) {
				if (!Config.BBS_ENABLED) {
					activeChar.sendPacket((IStaticPacket)new SystemMessage(938));
				} else {
					ICommunityBoardHandler handler = CommunityBoardManager.getInstance().getCommunityHandler(bp.bypass);
					if (handler != null) {
						handler.onBypassCommand(activeChar, bp.bypass);
					}
				}
			}
		}
		catch (Exception e) {
			String st = "Bad RequestBypassToServer: " + bp.bypass;
			GameObject target = activeChar.getTarget();
			if (target != null && target.isNpc()) {
				st = st + " via NPC #" + ((NpcInstance)target).getNpcId();
			}
			_log.error(st, (Throwable)e);
		}
	}

	private static void comeHere(GameClient client) {
		GameObject obj = client.getActiveChar().getTarget();
		if (obj != null && obj.isNpc()) {
			NpcInstance temp = (NpcInstance)obj;
			Player activeChar = client.getActiveChar();
			temp.setTarget(activeChar);
			temp.moveToLocation(activeChar.getLoc(), 0, true);
		}
	}

	private static void playerHelp(Player activeChar, String path, int itemId) {
		NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(5);
		html.setFile(path);
		html.setItemId(itemId);
		activeChar.sendPacket((IStaticPacket)html);
	}
}


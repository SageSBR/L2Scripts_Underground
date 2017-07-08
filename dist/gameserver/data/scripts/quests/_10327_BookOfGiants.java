package quests;

import java.util.List;

import instances.GiantBook;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.utils.ReflectionUtils;

public class _10327_BookOfGiants extends Quest implements ScriptFile
{
	private static final int panteleon = 32972;
	private static final int table = 33126;
	private static final int assassin = 23121;
	private static final int tairen = 33004;
	private static final int book = 17575;
	private int killedassasin = 0;
	private static final int INSTANCE_ID = 182;

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10327_BookOfGiants()
	{
		super(false);
		addStartNpc(panteleon);
		addTalkId(panteleon);
		addFirstTalkId(table);
		addQuestItem(book);
		addSkillUseId(assassin);
		addFirstTalkId(tairen);
		addKillId(assassin);
		addAttackId(assassin);

		addLevelCheck(1, 20);
		addQuestCompletedCheck(_10326_RespectYourElders.class);
		addRaceCheck(true, true, true, true, true, true, false);
	}

	@Override
	public String onAttack(NpcInstance npc, QuestState st)
	{
		GiantBook gb = (GiantBook) st.getPlayer().getActiveReflection();

		Functions.npcSayToPlayer(gb.getTairen(), st.getPlayer(), NpcString.ENOUGH_OF_THIS_COME_AT_ME);
		if(npc.getNpcId() == assassin)
		{
			if(gb.getTairen() != null)
			{
				gb.Attack(npc);
				if(killedassasin >= 2)
				{
					st.setCond(3);
					st.cancelQuestTimer("attak");
					st.playSound(SOUND_MIDDLE);
					killedassasin = 0;
				}
				else
				{
					killedassasin++;
				}
			}
		}
		return null;
	}

	private boolean enterInstance(Player player)
	{
		Reflection reflection = player.getActiveReflection(INSTANCE_ID);
		if(reflection != null)
		{
			if(player.canReenterInstance(INSTANCE_ID))
				player.teleToLocation(reflection.getTeleportLoc(), reflection);
		}
		else if(player.canEnterInstance(INSTANCE_ID))
			ReflectionUtils.enterReflection(player, new GiantBook(player), INSTANCE_ID);
		else
			return false;
		return true;
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();
		if(event.equalsIgnoreCase("quest_ac"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
			htmltext = "0-3.htm";
		}
		if(event.equalsIgnoreCase("enter_instance"))
		{
			if(!enterInstance(st.getPlayer()))
				return "you cannot enter this instance";
			st.playSound(SOUND_MIDDLE);

			GiantBook gb = (GiantBook) player.getActiveReflection();
			if(gb.getTairen() != null)
				gb.getTairen().setRunning();
			return null;
		}
		if(event.equalsIgnoreCase("qet_rev"))
		{
			player.sendPacket(new ExShowScreenMessage(NpcString.ACCESSORIES_HAVE_BEEN_ADDED_TO_YOUR_INVENTORY, 4500, ScreenMessageAlign.TOP_CENTER));
			htmltext = "0-5.htm";
			st.getPlayer().addExpAndSp(7800, 3500);
			st.giveItems(57, 16000);
			st.giveItems(112, 2);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		if(event.equalsIgnoreCase("attak"))
		{
			htmltext = "";
			st.startQuestTimer("attak", 5000);
			GiantBook gb = (GiantBook) player.getActiveReflection();
			if(gb != null && gb.getTairen() != null)
			{
				gb.getTairen().moveToLocation(st.getPlayer().getLoc(), Rnd.get(0, 100), true);
				if(Rnd.chance(33))
					Functions.npcSayToPlayer(gb.getTairen(), st.getPlayer(), NpcString.LOOKS_LIKE_ONLY_SKILL_BASED_ATTACKS_DAMAGE_THEM);
				if(Rnd.chance(33))
					Functions.npcSayToPlayer(gb.getTairen(), st.getPlayer(), NpcString.YOUR_NORMAL_ATTACKS_ARENT_WORKING);
				if(Rnd.chance(33))
					Functions.npcSayToPlayer(gb.getTairen(), st.getPlayer(), NpcString.USE_YOUR_SKILL_ATTACKS_AGAINST_THEM);
			}
		}
		if(event.equalsIgnoreCase("spawnas"))
		{
			GiantBook gb = (GiantBook) player.getActiveReflection();
			gb.stage2(player);
			return null;
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		if(npcId == panteleon)
		{
			if(st.isCompleted())
				htmltext = "0-c.htm";
			else if (cond == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					htmltext = "start.htm";
				else
					htmltext = "0-nc.htm";
			}
			else if(cond == 1)
				htmltext = "0-3.htm";
			else if(cond == 3 && st.getQuestItemsCount(book) >= 1)
				htmltext = "0-4.htm";
			else if(cond == 2)
			{
				htmltext = "0-3.htm";
				st.setCond(1);
				st.takeAllItems(book);
			}
		}
		return htmltext;
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		String htmltext = "noquest";
		QuestState st = player.getQuestState(getClass());
		int npcId = npc.getNpcId();

		if(npcId == table)
		{
		if(st == null)
			return htmltext;

			GiantBook gb = (GiantBook) player.getActiveReflection();

			if(npc.getObjectId() == gb.getbookdesk() && !gb.getTaken())
			{
				gb.setTaken();
				player.sendPacket(new ExShowScreenMessage(NpcString.WATCH_OUT_YOU_ARE_BEING_ATTACKED, 4500, ScreenMessageAlign.TOP_CENTER));
				htmltext = "2-2.htm";
				st.takeAllItems(book);
				st.giveItems(book, 1, false);
				st.setCond(2);
				st.startQuestTimer("attak", 5000);
				st.startQuestTimer("spawnas", 50);
			}
			else
				htmltext = "2-1.htm";
		}
		if(npcId == tairen)
		{
			if(st == null || st.getCond() == 0)
				return "";
			if(st.getCond() == 1)
				htmltext = "3-1.htm";
			else if(st.getCond() == 2)
				htmltext = "3-2.htm";
			else if(st.getCond() == 3)
				htmltext = "3-3.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 2)
			return null;

		if(killedassasin >= 2)
		{
			qs.setCond(3);
			qs.cancelQuestTimer("attak");
			qs.playSound(SOUND_MIDDLE);
			killedassasin = 0;
		}
		else
		{
			killedassasin++;
		}
		return null;
	}
}
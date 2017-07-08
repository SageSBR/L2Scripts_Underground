package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _767_BanishingEvil extends Quest implements ScriptFile
{
	// NPC's
	private static final int CHARLEN = 32655;

	// Monster's
	private static final int[] MONSTERS = { 22691, 22692, 22693, 22694, 22695, 22696, 22697, 22698, 22699 };

	// Item's
	private static final int ORCSAMULET = 36700;
	private static final int GREATERAMUL = 36701;


	public _767_BanishingEvil()
	{
		super(PARTY_ONE);
		addStartNpc(CHARLEN);
		addKillId(MONSTERS);
		addQuestItem(ORCSAMULET, GREATERAMUL);
		addLevelCheck(81, 84);
		addRaceCheck(true, true, true, true, true, true, false);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("32655-4.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("32655-9.htm"))
		{
			st.takeItems(ORCSAMULET, -1);
			if(st.getQuestItemsCount(GREATERAMUL) < 100)
			{
				st.giveItems(37394, 1);
				st.getPlayer().addExpAndSp(28240800, 282408);
			}
			else if(st.getQuestItemsCount(GREATERAMUL) >= 100 && st.getQuestItemsCount(GREATERAMUL) <= 199)
			{
				st.giveItems(37394, 2);
				st.getPlayer().addExpAndSp(56481600, 564816);
			}
			else if(st.getQuestItemsCount(GREATERAMUL) >= 200 && st.getQuestItemsCount(GREATERAMUL) <= 299)
			{
				st.giveItems(37394, 3);
				st.getPlayer().addExpAndSp(84722400, 847224);
			}
			else if(st.getQuestItemsCount(GREATERAMUL) >= 300 && st.getQuestItemsCount(GREATERAMUL) <= 399)
			{
				st.giveItems(37394, 4);
				st.getPlayer().addExpAndSp(112963200, 1129632);
			}
			else if(st.getQuestItemsCount(GREATERAMUL) >= 400 && st.getQuestItemsCount(GREATERAMUL) <= 499)
			{
				st.giveItems(37394, 5);
				st.getPlayer().addExpAndSp(141204000, 1412040);
			}
			else if(st.getQuestItemsCount(GREATERAMUL) >= 500 && st.getQuestItemsCount(GREATERAMUL) <= 599)
			{
				st.giveItems(37394, 6);
				st.getPlayer().addExpAndSp(169444800, 1694448);
			}
			else if(st.getQuestItemsCount(GREATERAMUL) >= 600 && st.getQuestItemsCount(GREATERAMUL) <= 699)
			{
				st.giveItems(37394, 7);
				st.getPlayer().addExpAndSp(197685600, 1976856);
			}
			else if(st.getQuestItemsCount(GREATERAMUL) >= 700 && st.getQuestItemsCount(GREATERAMUL) <= 799)
			{
				st.giveItems(37394, 8);
				st.getPlayer().addExpAndSp(225926400, 2259264);
			}
			else if(st.getQuestItemsCount(GREATERAMUL) >= 800 && st.getQuestItemsCount(GREATERAMUL) <= 899)
			{
				st.giveItems(37394, 9);
				st.getPlayer().addExpAndSp(254167200,  2541672);
			}
			else if(st.getQuestItemsCount(GREATERAMUL) >= 900)
			{
				st.giveItems(37394, 10);
				st.getPlayer().addExpAndSp(282408000, 2824080);
			}
			st.takeItems(GREATERAMUL, -1);
			st.exitCurrentQuest(this);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		String htmltext = "noquest";
			switch (npcId)
			{
				case CHARLEN:
					if(cond == 0)
					{
						if(!st.isNowAvailable())
							htmltext = "32655-10.htm";
						else if(checkStartCondition(st.getPlayer()))
							htmltext = "32655-1.htm";
						else
							htmltext = "32655-0.htm";
					}
					else if(cond == 1)
						htmltext = "32655-5.htm";
					else if(cond == 2 || cond == 3)
						htmltext = "32655-6.htm";
					break;
			}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 1)
		{
			st.rollAndGive(ORCSAMULET, 1, 1, 50, 10);
			if(st.getQuestItemsCount(ORCSAMULET) >= 50)
			{
				st.setCond(2);
			}
		}
		else if(cond == 2)
		{
			st.rollAndGive(GREATERAMUL, 1, 1, 900, 20);
			if(st.getQuestItemsCount(GREATERAMUL) >= 900)
				st.setCond(3);
		}
		return null;
	}

	@Override
	public void onLoad()
	{
		//
	}

	@Override
	public void onReload()
	{
		//
	}

	@Override
	public void onShutdown()
	{
		//
	}
}
package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _786_AwaitingtheVoiceoftheGods extends Quest implements ScriptFile
{
	// NPC's
	private static final int HERMIT = 31616;

	// Monster's
	private static final int[] MONSTERS = { 21294, 21295, 21296, 21297, 21299, 21304, 21313, 21300, 21301, 21302, 21305, 21298, 21303, 21307 };

	// Item's
	private static final int EYEOFDARK = 39734;
	private static final int MALICE = 39735;


	public _786_AwaitingtheVoiceoftheGods()
	{
		super(PARTY_ONE);
		addStartNpc(HERMIT);
		addKillId(MONSTERS);
		addQuestItem(EYEOFDARK, MALICE);
		addLevelCheck(70, 75);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("31616-4.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("31616-9.htm"))
		{
			st.takeItems(EYEOFDARK, -1);
			if(st.getQuestItemsCount(MALICE) < 100)
			{
				st.giveItems(39727, 1);
				st.getPlayer().addExpAndSp(14140350, 3393);
			}
			else if(st.getQuestItemsCount(MALICE) >= 100 && st.getQuestItemsCount(MALICE) <= 199)
			{
				st.giveItems(39727, 2);
				st.getPlayer().addExpAndSp(28280700, 6786);
			}
			else if(st.getQuestItemsCount(MALICE) >= 200 && st.getQuestItemsCount(MALICE) <= 299)
			{
				st.giveItems(39727, 3);
				st.getPlayer().addExpAndSp(42421050, 10179);
			}
			else if(st.getQuestItemsCount(MALICE) >= 300 && st.getQuestItemsCount(MALICE) <= 399)
			{
				st.giveItems(39727, 4);
				st.getPlayer().addExpAndSp(56561400, 13572);
			}
			else if(st.getQuestItemsCount(MALICE) >= 400 && st.getQuestItemsCount(MALICE) <= 499)
			{
				st.giveItems(39727, 5);
				st.getPlayer().addExpAndSp(70701750, 16965);
			}
			else if(st.getQuestItemsCount(MALICE) >= 500 && st.getQuestItemsCount(MALICE) <= 599)
			{
				st.giveItems(39727, 6);
				st.getPlayer().addExpAndSp(84842100, 20358);
			}
			else if(st.getQuestItemsCount(MALICE) >= 600 && st.getQuestItemsCount(MALICE) <= 699)
			{
				st.giveItems(39727, 7);
				st.getPlayer().addExpAndSp(98982450, 23751);
			}
			else if(st.getQuestItemsCount(MALICE) >= 700 && st.getQuestItemsCount(MALICE) <= 799)
			{
				st.giveItems(39727, 8);
				st.getPlayer().addExpAndSp(113122800, 27144);
			}
			else if(st.getQuestItemsCount(MALICE) >= 800 && st.getQuestItemsCount(MALICE) <= 899)
			{
				st.giveItems(39727, 9);
				st.getPlayer().addExpAndSp(127263150, 30537);
			}
			else if(st.getQuestItemsCount(MALICE) >= 900)
			{
				st.giveItems(39727, 10);
				st.getPlayer().addExpAndSp(141403500, 33930);
			}
			st.takeItems(MALICE, -1);
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
				case HERMIT:
					if(cond == 0)
					{
						if(!st.isNowAvailable())
							htmltext = "31616-10.htm";
						else if(checkStartCondition(st.getPlayer()))
							htmltext = "31616-1.htm";
						else
							htmltext = "31616-0.htm";
					}
					else if(cond == 1)
						htmltext = "31616-5.htm";
					else if(cond == 2 || cond == 3)
						htmltext = "31616-6.htm";
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
			st.rollAndGive(EYEOFDARK, 1, 1, 50, 10);
			if(st.getQuestItemsCount(EYEOFDARK) >= 50)
			{
				st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.YOU_CAN_GATHER_MORE_POWERFUL_DARK_MALICE, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
				st.setCond(2);
			}
		}
		else if(cond == 2)
		{
			st.rollAndGive(MALICE, 1, 1, 900, 20);
			if(st.getQuestItemsCount(MALICE) >= 900)
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
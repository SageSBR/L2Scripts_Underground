package quests;

import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _763_ADauntingTask extends Quest implements ScriptFile
{
    private static final int YANIT = 33851;
    private static final int[] Mobs = new int[] {21294, 21295, 21296, 21297,21298, 21299, 21300, 21301, 21302, 21303, 21304,
			21305, 21307};
    private static final int EYEOFDARKNESS = 36672;
    private static final int MALICE= 36673;
    private static final int Steel_Door_Guild_Reward_Box = 37392;
	
	public _763_ADauntingTask()
	{
		super(false);
		addStartNpc(YANIT);
		addTalkId(YANIT);
		addQuestItem(EYEOFDARKNESS);
		addQuestItem(MALICE);
		
		addKillId(Mobs);
		addRaceCheck(true, true, true, true, true, true, false);
		addLevelCheck(70, 75);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("yanit_q0763_06.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("back"))
		{
			return null;
		}
		else if(event.equalsIgnoreCase("reward"))
		{
			st.takeItems(EYEOFDARKNESS, -1);
			if(st.getQuestItemsCount(MALICE) < 100)
			{
				st.giveItems(Steel_Door_Guild_Reward_Box, 1);
				st.getPlayer().addExpAndSp(16329600, 163296);
			}
			else if(st.getQuestItemsCount(MALICE) >= 100 && st.getQuestItemsCount(MALICE) <= 199)
			{
				st.giveItems(Steel_Door_Guild_Reward_Box, 2);
				st.getPlayer().addExpAndSp(32659200, 326592);
			}
			else if(st.getQuestItemsCount(MALICE) >= 200 && st.getQuestItemsCount(MALICE) <= 299)
			{
				st.giveItems(Steel_Door_Guild_Reward_Box, 3);
				st.getPlayer().addExpAndSp(48988800, 489888);
			}
			else if(st.getQuestItemsCount(MALICE) >= 300 && st.getQuestItemsCount(MALICE) <= 399)
			{
				st.giveItems(Steel_Door_Guild_Reward_Box, 4);
				st.getPlayer().addExpAndSp(65318400, 653184);
			}
			else if(st.getQuestItemsCount(MALICE) >= 400 && st.getQuestItemsCount(MALICE) <= 499)
			{
				st.giveItems(Steel_Door_Guild_Reward_Box, 5);
				st.getPlayer().addExpAndSp(81648000, 816480);
			}
			else if(st.getQuestItemsCount(MALICE) >= 500 && st.getQuestItemsCount(MALICE) <= 599)
			{
				st.giveItems(Steel_Door_Guild_Reward_Box, 6);
				st.getPlayer().addExpAndSp(97977600, 979776);
			}
			else if(st.getQuestItemsCount(MALICE) >= 600 && st.getQuestItemsCount(MALICE) <= 699)
			{
				st.giveItems(Steel_Door_Guild_Reward_Box, 7);
				st.getPlayer().addExpAndSp(114307200, 1143072);
			}
			else if(st.getQuestItemsCount(MALICE) >= 700 && st.getQuestItemsCount(MALICE) <= 799)
			{
				st.giveItems(Steel_Door_Guild_Reward_Box, 8);
				st.getPlayer().addExpAndSp(130636800, 1306368);
			}
			else if(st.getQuestItemsCount(MALICE) >= 800 && st.getQuestItemsCount(MALICE) <= 899)
			{
				st.giveItems(Steel_Door_Guild_Reward_Box, 9);
				st.getPlayer().addExpAndSp(146966400, 1469664);
			}
			else if(st.getQuestItemsCount(MALICE) >= 900)
			{
				st.giveItems(Steel_Door_Guild_Reward_Box, 10);
				st.getPlayer().addExpAndSp(163296000, 1632960);

			}
			st.takeItems(MALICE, -1);
			st.exitCurrentQuest(this);
			return null;
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
			case YANIT:
				if(cond == 0)
				{
					if(!st.isNowAvailable())
						htmltext = "yanit_q0763_11.htm";
					else if(checkStartCondition(st.getPlayer()))
						htmltext = "yanit_q0763_01.htm";
					else
						htmltext = "yanit_q0763_02.htm";
				}
				else if(cond == 1)
				{
					htmltext = "yanit_q0763_07.htm";
				}
				else if(cond == 2)
				{
					htmltext = "yanit_q0763_08.htm";
				}
				else if(cond == 3)
					htmltext = "yanit_q0763_09.htm";
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
			st.rollAndGive(EYEOFDARKNESS, 1, 1, 50, 10);
			if(st.getQuestItemsCount(EYEOFDARKNESS) >= 50)
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
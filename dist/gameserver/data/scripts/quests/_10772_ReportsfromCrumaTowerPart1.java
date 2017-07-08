package quests;

import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10772_ReportsfromCrumaTowerPart1 extends Quest implements ScriptFile
{
	// NPC's
	private static final int YANSEN = 30484;
	private static final int MAGICOWL = 33991;

	//ITEMS
	private static final int DOORCOIN = 37045;
	private static final int ENCHANTARMOR = 23420;

	private NpcInstance owl = null;

	public _10772_ReportsfromCrumaTowerPart1()
	{
		super(PARTY_NONE);
		addStartNpc(YANSEN);
		addTalkId(YANSEN);
		addTalkId(MAGICOWL);
		addQuestCompletedCheck(_10771_Volatile_Power.class);
		addLevelCheck(45);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30484-5.htm"))
		{
			owl = null;
			st.setState(STARTED);
			st.setCond(1);
		}

		else if(event.equalsIgnoreCase("30484-9.htm"))
		{
			st.getPlayer().addExpAndSp(127575, 30);
			st.giveItems(DOORCOIN, 4);
			st.giveItems(ENCHANTARMOR, 2);
			st.exitCurrentQuest(false);
		}

		else if(event.equalsIgnoreCase("summonbird"))
		{
			if (owl == null)
				owl = st.addSpawn(MAGICOWL, 17640, 114968, -11753, 0, 0, 120000);
			return null;
		}
		else if(event.equalsIgnoreCase("send"))
		{
			st.setCond(2);
			Functions.npcSay(owl, NpcString.TO_QUEEN_NAVARI_OF_FAERON);
			ThreadPoolManager.getInstance().schedule(() ->
			{
				npc.doDie(null);
				npc.endDecayTask();
			}, 6000L);

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
			case YANSEN:
				if(cond == 0)
				{
					if(checkStartCondition(st.getPlayer()))
						htmltext = "30484-1.htm";
					else
						htmltext = "30484-0.htm";
				}
				else if (cond == 1)
					htmltext = "30484-7.htm";
				else if (cond == 2)
					htmltext = "30484-8.htm";
				break;

			case MAGICOWL:
				if (cond == 1)
					htmltext = "33991-1.htm";
			break;
		}
		return htmltext;
	}


	@Override
	public void onLoad()
	{
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
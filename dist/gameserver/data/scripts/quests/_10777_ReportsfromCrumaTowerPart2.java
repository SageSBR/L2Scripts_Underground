package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10777_ReportsfromCrumaTowerPart2 extends Quest implements ScriptFile
{
	// NPC's
	private static final int BELKATI = 30485;
	private static final int MAGICOWL = 33991;

	//ITEMS
	private static final int DOORCOIN = 37045;
	private static final int ENCHANTARMOR = 23420;

	private NpcInstance owl = null;

	public _10777_ReportsfromCrumaTowerPart2()
	{
		super(PARTY_NONE);
		addStartNpc(BELKATI);
		addTalkId(BELKATI);
		addTalkId(MAGICOWL);
		addQuestCompletedCheck(_10776_TheWrathoftheGiants.class);
		addLevelCheck(49);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30485-5.htm"))
		{
			owl = null;
			st.setState(STARTED);
			st.setCond(1);
		}

		else if(event.equalsIgnoreCase("30485-8.htm"))
		{
			st.getPlayer().addExpAndSp(151263, 36);
			st.giveItems(DOORCOIN, 4);
			st.giveItems(ENCHANTARMOR, 2);
			st.exitCurrentQuest(false);
		}

		else if(event.equalsIgnoreCase("summonowl"))
		{
			if (owl == null)
				owl = st.addSpawn(MAGICOWL, 17666, 108589, -9072, 0, 0, 120000);
			return null;
		}
		else if(event.equalsIgnoreCase("sendowl"))
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
			case BELKATI:
				if(cond == 0)
				{
					if(checkStartCondition(st.getPlayer()))
						htmltext = "30485-1.htm";
					else
						htmltext = "30485-0.htm";
				}
				else if (cond == 1)
					htmltext = "30485-6.htm";
				else if (cond == 2)
					htmltext = "30485-7.htm";
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
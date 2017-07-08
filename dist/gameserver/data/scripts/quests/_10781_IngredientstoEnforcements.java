package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10781_IngredientstoEnforcements extends Quest implements ScriptFile
{
	// NPC's
	private static final int BAIKON = 33846;

	// Monster's
	private static final int[] MONSTER = {23310, 23309};

	// Item's
	private static final int DOORCOIN = 37045;
	private static final int SPIRITFRAGMENT = 39721;
	private static final int ENCHANTARMOR = 23419;

	public _10781_IngredientstoEnforcements()
	{
		super(PARTY_ONE);
		addStartNpc(BAIKON);
		addTalkId(BAIKON);
		addKillId(MONSTER);
		addQuestCompletedCheck(_10780_AWeakenedBarrier.class);
		addLevelCheck(52, 58);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33846-4.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("33846-7.htm"))
		{
			st.giveItems(DOORCOIN, 37);
			st.takeItems(SPIRITFRAGMENT, -1);
			st.giveItems(ENCHANTARMOR, 5);
			st.getPlayer().addExpAndSp(3811500, 914);
			st.exitCurrentQuest(false);
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
			case BAIKON:
				if(cond == 0)
				{
					if(checkStartCondition(st.getPlayer()))
						htmltext = "33846-1.htm";
					else
						htmltext = "33846-0.htm";
				}
				else if (cond == 1)
					htmltext = "33846-5.htm";
				else if (cond == 2)
					htmltext = "33846-6.htm";
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
			st.giveItems(SPIRITFRAGMENT, 1, false);
			if(st.getQuestItemsCount(SPIRITFRAGMENT) >= 20)
				st.setCond(2);
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
package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10797_CrossingFate extends Quest implements ScriptFile
{
	// NPC's
	private static final int EYEAGROS = 31683;

	//Mobs
	private static final int DAIMON = 27499;
	// Item's
	private static final int DOORCOIN = 37045;
	private static final int ENCHANTARMOR = 23418;


	public _10797_CrossingFate()
	{
		super(PARTY_ONE);
		addStartNpc(EYEAGROS);
		addTalkId(EYEAGROS);
		addKillId(DAIMON);
		addQuestCompletedCheck(_10796_TheEyethatDefiedtheGods.class);
		addLevelCheck(70, 75);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("31683-4.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("31683-7.htm"))
		{
			st.giveItems(DOORCOIN, 26);
			st.giveItems(ENCHANTARMOR, 5);
			st.getPlayer().addExpAndSp(2721600, 653);
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
			case EYEAGROS:
				if(cond == 0)
				{
					if(checkStartCondition(st.getPlayer()))
						htmltext = "31683-1.htm";
					else
						htmltext = "31683-0.htm";
				}
				else if (cond == 1)
					htmltext = "31683-5.htm";
				else if (cond == 2)
					htmltext = "31683-6.htm";
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 1)
			st.setCond(2);
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
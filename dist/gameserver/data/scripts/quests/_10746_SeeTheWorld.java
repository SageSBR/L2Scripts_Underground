package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10746_SeeTheWorld extends Quest implements ScriptFile
{
	// NPC's
	private static final int KALLI = 33933;
	private static final int LEVIAN = 30037;
	private static final int ASTIEL = 33948;

	//ITEMS
	private static final int FORFIGHTERS = 40264;
	private static final int FORWIZARDS = 40265;

	public _10746_SeeTheWorld()
	{
		super(PARTY_NONE);
		addStartNpc(KALLI);
		addTalkId(LEVIAN, ASTIEL);
		addLevelCheck(19, 25);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33933-2.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("33948-2.htm"))
		{
			st.getPlayer().teleToLocation(-80712, 149992, -3069);
			st.setCond(2);
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
			case KALLI:
				if(cond == 0)
				{
					if(checkStartCondition(st.getPlayer()))
						htmltext = "33933-1.htm";
					else
						htmltext = "33933-0.htm";
				}
			break;

			case ASTIEL:
				if (cond == 1)
					htmltext = "33948-1.htm";
			break;

			case LEVIAN:
				if (cond == 2)
				{
					st.giveItems(57, 43000);
					st.getPlayer().addExpAndSp(53422, 5);
					if (st.getPlayer().isMageClass())
						st.giveItems(FORWIZARDS, 1, false);
					else
						st.giveItems(FORFIGHTERS, 1, false);
					st.exitCurrentQuest(false);
					htmltext = "30037-1.htm";
				}
			break;
		}
		return htmltext;
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
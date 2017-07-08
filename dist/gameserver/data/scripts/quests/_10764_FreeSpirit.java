package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10764_FreeSpirit extends Quest implements ScriptFile
{
	// NPC's
	private static final int BORBO = 33966;
	private static final int TREE = 33964;
	private static final int WIND = 33965;
	// Item's
	private static final int DOORCOIN = 37045;
	private static final int BUNDLEKEY = 39490;
	private static final int LOOSENCHEIN = 39518;

	public _10764_FreeSpirit()
	{
		super(PARTY_ONE);
		addStartNpc(BORBO);
		addTalkId(BORBO);
		addTalkId(TREE);
		addTalkId(WIND);
		addQuestCompletedCheck(_10763_TerrifyingChertuba.class);
		addLevelCheck(38);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33966-3.htm"))
		{
			st.setCond(1);
			st.giveItems(BUNDLEKEY, 10, false);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("33966-6.htm"))
		{
			st.giveItems(DOORCOIN, 10);
			st.takeItems(LOOSENCHEIN, -1);
			st.getPlayer().addExpAndSp(1312934, 315);
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
			case BORBO:
				if(cond == 0)
				{
					if(checkStartCondition(st.getPlayer()))
						htmltext = "33966-1.htm";
					else
						htmltext = "33966-0.htm";
				}
				else if (cond == 1)
					htmltext = "33966-4.htm";
				else if (cond == 2)
					htmltext = "33966-5.htm";
			break;
			case WIND:
			case TREE:
			 if (cond == 1)
			{
				st.takeItems(BUNDLEKEY, 1);
				npc.doDie(null);
				npc.endDecayTask();
				st.giveItems(LOOSENCHEIN, 1, false);
				if(st.getQuestItemsCount(LOOSENCHEIN) >= 10)
					st.setCond(2);
				return null;
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
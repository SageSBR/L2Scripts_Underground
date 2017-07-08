package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10757_QuietingtheStorm extends Quest implements ScriptFile
{
	// NPC's
	private static final int FIO = 33963;

	// Monster's
	private static final int VORTEXWIND = 23417;
	private static final int[] GIANT = {23419, 23420};

	private static final String GigantW = "GigantW";
	private static final String Vortex = "VORTEX";
	// Item's
	private static final int DOORCOIN = 37045;

	public _10757_QuietingtheStorm()
	{
		super(PARTY_ONE);
		addStartNpc(FIO);
		addTalkId(FIO);
		addKillId(VORTEXWIND);
		addKillId(GIANT);
		addKillNpcWithLog(1, 1023417, Vortex, 5, VORTEXWIND);
		addKillNpcWithLog(1, 575711, GigantW, 1, GIANT);
		addQuestCompletedCheck(_10756_AnInterdimensionalDraft.class);
		addLevelCheck(24);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33963-5.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("33963-8.htm"))
		{
			st.giveItems(DOORCOIN, 8);
			st.getPlayer().addExpAndSp(632051, 151);
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
			case FIO:
				if(cond == 0)
				{
					if(checkStartCondition(st.getPlayer()))
						htmltext = "33963-1.htm";
					else
						htmltext = "33963-0.htm";
				}
				else if (cond == 1)
					htmltext = "33963-4.htm";
				else if (cond == 2)
					htmltext = "33963-7.htm";
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
			boolean doneKill = updateKill(npc, st);
			if(doneKill)
			{
				st.unset(GigantW);
				st.unset(Vortex);
				st.setCond(2);
			}
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
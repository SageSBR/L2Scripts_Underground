package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10793_SavetheSouls extends Quest implements ScriptFile
{
	// NPC's
	private static final int HATUVA = 33849;

	// Monster's
	private static final int[] MONSTERS = {21547, 21547, 21549, 21550, 21553, 21554, 21555, 21556, 18119, 21548, 21551, 21552, 21557, 21558, 21559, 21560, 21561};

	// Item's
	private static final int DOORCOIN = 37045;
	private static final int ENCHANTARMOR = 23418;

	private static final String Forestofdead = "forestofdead";

	public _10793_SavetheSouls()
	{
		super(PARTY_ONE);
		addStartNpc(HATUVA);
		addTalkId(HATUVA);
		addKillId(MONSTERS);
		addLevelCheck(65, 70);
		addClassIdCheck(185);
		addKillNpcWithLog(1, 579311, Forestofdead, 50, MONSTERS);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33849-4.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("33849-7.htm"))
		{
			st.giveItems(DOORCOIN, 3);
			st.giveItems(ENCHANTARMOR, 3);
			st.getPlayer().addExpAndSp(942690, 1578);
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
			case HATUVA:
				if(cond == 0)
				{
					if(checkStartCondition(st.getPlayer()))
						htmltext = "33849-1.htm";
					else
						htmltext = "33849-0.htm";
				}
				else if (cond == 1)
					htmltext = "33849-5.htm";
				else if (cond == 2)
					htmltext = "33849-6.htm";
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
				st.unset(Forestofdead);
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
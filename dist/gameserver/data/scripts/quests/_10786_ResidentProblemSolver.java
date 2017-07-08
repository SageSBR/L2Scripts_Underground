package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10786_ResidentProblemSolver extends Quest implements ScriptFile
{
	// NPC's
	private static final int ZUBAN = 33867;

	// Monster's
	private static final int[] MONSTERS = {21001, 21002, 21003, 21004, 21005, 21006, 21007, 21008, 21009, 21010, 20674, 20974, 20975, 20976};

	// Item's
	private static final int DOORCOIN = 37045;
	private static final int ENCHANTARMOR = 23419;

	private static final String Massacr = "Massacr";

	public _10786_ResidentProblemSolver()
	{
		super(PARTY_ONE);
		addStartNpc(ZUBAN);
		addTalkId(ZUBAN);
		addKillId(MONSTERS);
		addLevelCheck(61, 65);
		addKillNpcWithLog(1, 578611, Massacr, 50, MONSTERS);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33867-4.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("33867-7.htm"))
		{
			st.giveItems(DOORCOIN, 40);
			st.giveItems(ENCHANTARMOR, 5);
			st.getPlayer().addExpAndSp(6579090, 1578);
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
			case ZUBAN:
				if(cond == 0)
				{
					if(checkStartCondition(st.getPlayer()))
						htmltext = "33867-1.htm";
					else
						htmltext = "33867-0.htm";
				}
				else if (cond == 1)
					htmltext = "33867-5.htm";
				else if (cond == 2)
					htmltext = "33867-6.htm";
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
				st.unset(Massacr);
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
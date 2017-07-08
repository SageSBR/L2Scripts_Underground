package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

import java.util.ArrayList;

//By Evil_dnk

public class _10787_ASpyMission extends Quest implements ScriptFile
{
	// NPC's
	private static final int ZUBAN = 33867;
	private static final int STRANGCHEST = 33994;

	// Item's
	private static final int DOORCOIN = 37045;
	private static final int ENCHANTARMOR = 23419;
	private static final int EMBRYOMASSIVES = 39724;

	private static final String Massacr = "Massacr";
	private static ArrayList<NpcInstance> fighters = new ArrayList<NpcInstance>();


	public _10787_ASpyMission()
	{
		super(PARTY_ONE);
		addStartNpc(ZUBAN);
		addTalkId(ZUBAN);
		addTalkId(STRANGCHEST);
		addQuestCompletedCheck(_10786_ResidentProblemSolver.class);
		addLevelCheck(61, 65);
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
			st.giveItems(DOORCOIN, 29);
			st.giveItems(ENCHANTARMOR, 5);
			st.getPlayer().addExpAndSp(3125586, 750);
			st.exitCurrentQuest(false);
		}
		else if(event.equalsIgnoreCase("33994-2.htm"))
		{
			if(Rnd.chance(15))
			{
				st.setCond(2);
				st.giveItems(EMBRYOMASSIVES, 1, false);
				npc.doDie(null);
				npc.endDecayTask();
				return "33994-2.htm";
			}
			if(fighters != null)
				fighters.clear();
			fighters.add(st.addSpawn(27540, npc.getX() + Rnd.get(50, 100), npc.getY() + Rnd.get(50, 100), npc.getZ(), 0, 0, 180000));
			fighters.add(st.addSpawn(27541, npc.getX() + Rnd.get(50, 100), npc.getY() + Rnd.get(50, 100), npc.getZ(), 0, 0, 180000));
			for (NpcInstance fighter : fighters)
			{
				fighter.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 1000);
			}
			npc.doDie(null);
			npc.endDecayTask();
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

			case STRANGCHEST:
				if (cond == 1)
					htmltext = "33994-1.htm";
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
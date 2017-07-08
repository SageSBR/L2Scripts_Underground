package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10762_MarionetteSpirit extends Quest implements ScriptFile
{
	// NPC's
	private static final int Borbo = 33966;

	// Monster's
	private static final int MONSTER = 23418;
	private static final String Prisenedspirit = "spiritpris";
	// Item's
	private static final int DOORCOIN = 37045;
	private static final int CHAINKEY = 39488;

	public _10762_MarionetteSpirit()
	{
		super(PARTY_ONE);
		addStartNpc(Borbo);
		addTalkId(Borbo);
		addKillId(MONSTER);
		addKillNpcWithLog(1, 1023418, Prisenedspirit, 1, MONSTER);
		addQuestCompletedCheck(_10761_AnOrcinLove.class);
		addLevelCheck(34);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33966-3.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("33966-6.htm"))
		{
			st.giveItems(DOORCOIN, 5);
			st.takeItems(CHAINKEY, -1);
			st.getPlayer().addExpAndSp(896996, 215);
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
			case Borbo:
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
				st.giveItems(CHAINKEY, 1, false);
				st.unset(Prisenedspirit);
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
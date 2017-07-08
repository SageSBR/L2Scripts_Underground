package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10761_AnOrcinLove extends Quest implements ScriptFile
{
	// NPC's
	private static final int Borbo = 33966;

	// Monster's
	private static final int[] MONSTERS = {20495, 20496, 20497, 20498, 20499, 20500, 20501, 20546, 20494};
	private static final String OrcTurek = "TUREK";
	// Item's
	private static final int DOORCOIN = 37045;

	public _10761_AnOrcinLove()
	{
		super(PARTY_ONE);
		addStartNpc(Borbo);
		addTalkId(Borbo);
		addKillId(MONSTERS);
		addKillNpcWithLog(1, 576111, OrcTurek, 30, MONSTERS);
		addLevelCheck(30);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33966-5.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("33966-8.htm"))
		{
			st.giveItems(DOORCOIN, 20);
			st.getPlayer().addExpAndSp(354546, 85);
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
					htmltext = "33966-6.htm";
				else if (cond == 2)
					htmltext = "33966-7.htm";
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
				st.unset(OrcTurek);
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
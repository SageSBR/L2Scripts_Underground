package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _768_TerrorofTown extends Quest implements ScriptFile
{
	// NPC's
	private static final int RUA = 33841;

	// Monster's
	private static final int[] MONSTERS = { 22775, 22776, 22777, 22778, 22779, 22780, 22781, 22782, 22783, 22784, 22785, 22786, 22787, 22788, 18908};

	// Item's
	private static final int SIGNET = 36693;
	private static final int IDTAG = 36698;


	public _768_TerrorofTown()
	{
		super(PARTY_ONE);
		addStartNpc(RUA);
		addKillId(MONSTERS);
		addQuestItem(SIGNET, IDTAG);
		addLevelCheck(81, 84);
		addRaceCheck(true, false, false, true, true, false, false);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33841-4.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("33841-9.htm"))
		{
			st.takeItems(SIGNET, -1);
			if(st.getQuestItemsCount(IDTAG) < 100)
			{
				st.giveItems(37394, 1);
				st.getPlayer().addExpAndSp(28240800, 6777);
			}
			else if(st.getQuestItemsCount(IDTAG) >= 100 && st.getQuestItemsCount(IDTAG) <= 899)
			{
				st.giveItems(37394, 2);
				st.getPlayer().addExpAndSp(84722400,  20333);
			}
			else if(st.getQuestItemsCount(IDTAG) >= 900)
			{
				st.giveItems(37394, 10);
				st.getPlayer().addExpAndSp(536575200, 128778);
			}
			st.takeItems(IDTAG, -1);
			st.exitCurrentQuest(this);
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
				case RUA:
					if(cond == 0)
					{
						if(!st.isNowAvailable())
							htmltext = "33841-10.htm";
						else if(checkStartCondition(st.getPlayer()))
							htmltext = "33841-1.htm";
						else
							htmltext = "33841-0.htm";
					}
					else if(cond == 1)
						htmltext = "33841-5.htm";
					else if(cond == 2)
						htmltext = "33841-6.htm";
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
			st.rollAndGive(SIGNET, 1, 1, 50, 10);
			if(st.getQuestItemsCount(SIGNET) >= 50)
			{
				st.setCond(2);
			}
		}
		else if(cond == 2)
		{
			st.rollAndGive(IDTAG, 1, 1, 900, 20);
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
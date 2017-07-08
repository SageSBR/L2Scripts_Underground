package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10743_StrangeFungus extends Quest implements ScriptFile
{
	// NPC's
	private static final int REICHEL = 33952;
	private static final int MILHE = 33953;

	// Monster's
	private static final int[] MONSTERS = {23455, 23486, 23456};
	private static final String Shriker = "shriker";

	// Item's
	private static final int SPORE = 39530;

	private int shrikercounter;

	public _10743_StrangeFungus()
	{
		super(PARTY_ONE);
		addStartNpc(REICHEL);
		addTalkId(MILHE);
		addKillId(MONSTERS);
		addLevelCheck(13, 20);
		addRaceCheck(false, false, false, false, false, false, true);
		addKillNpcWithLog(1, 1023455, Shriker, 99999, MONSTERS);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33952-3.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("33953-3.htm"))
		{
			st.takeItems(SPORE, -1);
			st.giveItems(57, 62000);
			st.giveItems(37, 1);
			st.getPlayer().addExpAndSp(62876, 0);
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
			case REICHEL:
				if(cond == 0)
				{
					if(checkStartCondition(st.getPlayer()))
						htmltext = "33952-1.htm";
					else
						htmltext = "33952-0.htm";
				}
				else if (cond == 1)
					htmltext = "33952-4.htm";
			break;

			case MILHE:
				if (cond == 2)
					htmltext = "33953-1.htm";
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(ArrayUtils.contains(MONSTERS, npcId))
		{
			if(cond == 1)
			{
				if(npcId != 23456)
				{
					shrikercounter++;
					updateKill(npc, st);
					if(shrikercounter >= 3)
					{
						st.addSpawn(23456, npc.getX(), npc.getY(), npc.getZ());
						shrikercounter = 0;
					}
				}
				else
					st.giveItems(SPORE, 1);
			}
			if(st.getQuestItemsCount(SPORE) >= 10)
				st.unset(Shriker);
				st.setCond(2);
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
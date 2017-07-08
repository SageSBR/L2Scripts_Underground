package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10775_InSearchofanAncientGiant extends Quest implements ScriptFile
{
	// NPC's
	private static final int ROMBEL = 30487;
	private static final int BELKATI = 30485;

	// Monster's
	private static final int[] MONSTERS = {20753, 20754, 21040, 21037, 21038, 23153, 23154, 23155};
	// Item's
	private static final int DOORCOIN = 37045;
	private static final int ENERGYOFREGENERATION = 39715;
	private static final int ENCHANTARMOR = 23420;

	public _10775_InSearchofanAncientGiant()
	{
		super(PARTY_ONE);
		addStartNpc(ROMBEL);
		addTalkId(BELKATI);
		addKillId(MONSTERS);
		addLevelCheck(46);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30487-5.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("30485-4.htm"))
		{
			st.giveItems(DOORCOIN, 46);
			st.giveItems(ENCHANTARMOR, 9);
			st.takeItems(ENERGYOFREGENERATION, -1);
			st.getPlayer().addExpAndSp(4443600, 1066);
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
			case ROMBEL:
				if(cond == 0)
				{
					if(checkStartCondition(st.getPlayer()))
						htmltext = "30487-1.htm";
					else
						htmltext = "30487-0.htm";
				}
				else if (cond == 1)
					htmltext = "30487-6.htm";
			break;

			case BELKATI:
				if(cond == 2)
					htmltext = "30485-1.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int cond = st.getCond();
		if(cond == 1)
		{
		  st.rollAndGive(ENERGYOFREGENERATION, 1, 1, 20, 90);
			if (st.getQuestItemsCount(ENERGYOFREGENERATION) >= 20)
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
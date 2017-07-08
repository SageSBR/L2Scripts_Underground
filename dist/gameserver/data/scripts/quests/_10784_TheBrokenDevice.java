package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10784_TheBrokenDevice extends Quest implements ScriptFile
{
	// NPC's
	private static final int NOVAIN = 33866;

	// Monster's
	private static final int[] MONSTER = {20650, 20648, 20647, 20649};

	// Item's
	private static final int DOORCOIN = 37045;
	private static final int BRIKENDEVICEFRAGMENT = 39723;
	private static final int ENCHANTARMOR = 23419;

	public _10784_TheBrokenDevice()
	{
		super(PARTY_ONE);
		addStartNpc(NOVAIN);
		addTalkId(NOVAIN);
		addKillId(MONSTER);
		addQuestCompletedCheck(_10783_TracesofanAmbush.class);
		addLevelCheck(58, 61);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33866-4.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("33866-7.htm"))
		{
			st.giveItems(DOORCOIN, 40);
			st.takeItems(BRIKENDEVICEFRAGMENT, -1);
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
			case NOVAIN:
				if(cond == 0)
				{
					if(checkStartCondition(st.getPlayer()))
						htmltext = "33866-1.htm";
					else
						htmltext = "33866-0.htm";
				}
				else if (cond == 1)
					htmltext = "33866-5.htm";
				else if (cond == 2)
					htmltext = "33866-6.htm";
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
				st.giveItems(BRIKENDEVICEFRAGMENT, 1);
			if(st.getQuestItemsCount(BRIKENDEVICEFRAGMENT) >= 20)
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
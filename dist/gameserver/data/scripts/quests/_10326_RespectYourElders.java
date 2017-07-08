package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _10326_RespectYourElders extends Quest implements ScriptFile
{
	private static final int GALLINT = 32980;
	private static final int PANTEON = 32972;

	public _10326_RespectYourElders()
	{
		super(false);
		addStartNpc(GALLINT);
		addTalkId(PANTEON);
		addLevelCheck(1, 20);
		addQuestCompletedCheck(_10325_SearchingForNewPower.class);
		addRaceCheck(true, true, true, true, true, true, false);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("3.htm"))
		{
			st.set("cond", "1", true);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("5.htm"))
		{
			st.giveItems(57, 14000);
			st.addExpAndSp(5300, 2800);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		Player player = st.getPlayer();
		if(npcId == GALLINT)
		{
			if(cond == 0)
				if(checkStartCondition(player))
					htmltext = "1.htm";
		}
		else if(npcId == PANTEON)
		{
			if(cond == 1) 
				htmltext = "4.htm";
		}
		return htmltext;
	}

	@Override
	public boolean checkStartCondition(Player player)
	{
		QuestState qs = player.getQuestState(_10325_SearchingForNewPower.class);
		return player.getLevel() <= 20 && qs != null && qs.getState() == COMPLETED;
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

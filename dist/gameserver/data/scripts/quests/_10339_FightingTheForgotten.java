package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _10339_FightingTheForgotten extends Quest implements ScriptFile
{
	private static final String A_LIST = "A_LIST";

    //Квест НПЦ
    private static final int THEODOR = 32975;
	private static final int HADEL = 33344;

    //Квест монстры
    private static final int[] MOBS = new int[]{22935,22936,22937,22931,22934,22933,23349,22938,22932};
	

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10339_FightingTheForgotten()
	{
		super(false);
		addStartNpc(THEODOR);
		addTalkId(THEODOR);
		addTalkId(HADEL);
		
		addLevelCheck(85);
		addKillNpcWithLog(2, 533912, A_LIST, 12, MOBS);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("accept"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			return "accept.htm";
		}	
		else if(event.equalsIgnoreCase("cod2"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
			return "gl1.htm";
		}	
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int state = st.getState();
		int cond = st.getCond();
		
		if(state == COMPLETED)
			return "Already completed this quest!";

		if(st.getPlayer().getLevel() < 85)
			return "no_level.htm";		
			
		if(npcId == THEODOR)
		{
			if(cond == 0)
				return "1.htm";
			else if(cond == 1)
				return "5.htm";	
		}
		else if(npcId == HADEL)
		{
			if(cond == 1)
				return "1-1.htm";
			else if(cond == 2)
				return "gl1.htm";
			else if(cond == 3)
			{
				st.addExpAndSp(238423500, 2384235);
				st.giveItems(57, 528210);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);	
				return "endquest.htm";
			}
		}
		return "noquest";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 2)
			return null;

		if(updateKill(npc, qs))
		{
			qs.unset(A_LIST);
			qs.setCond(3);
		}
		return null;
	}	
}
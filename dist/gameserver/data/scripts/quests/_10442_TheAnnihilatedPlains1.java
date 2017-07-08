package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Iqman
 */
public class _10442_TheAnnihilatedPlains1 extends Quest implements ScriptFile
{
	//npc
	private static final int MATHIAS = 31340;
	private static final int TUSKA = 33839;
	
	public _10442_TheAnnihilatedPlains1()
	{
		super(false);
		addStartNpc(MATHIAS);
		addTalkId(MATHIAS);
		addTalkId(TUSKA);
		
		addLevelCheck(99);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("accept.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}	
		else if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.addExpAndSp(15436575, 154365);	
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		
		if(npcId == MATHIAS)
		{
			if(!checkStartCondition(st.getPlayer()))
				htmltext = "no_level.htm";
			else if(cond == 0)
				htmltext = "1.htm";
			else if(cond == 1)
				htmltext = "4.htm";
		}
		else if(npcId == TUSKA)
		{
			if(cond == 1)
				return "1-1.htm";		
		}			
		return htmltext;
	}
	
	@Override
	public void onLoad()
	{
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}
}
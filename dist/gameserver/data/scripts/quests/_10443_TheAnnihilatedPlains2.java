package quests;

import org.apache.commons.lang3.ArrayUtils;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Iqman
 */
public class _10443_TheAnnihilatedPlains2 extends Quest implements ScriptFile
{
	//npc
	private static final int TUSKA = 33839;
	private static final int TRUP = 33837;
	private static final int FOLK = 33843;
	
	private static final int NECK = 36678;
	
	public _10443_TheAnnihilatedPlains2()
	{
		super(false);
		addStartNpc(TUSKA);
		addTalkId(TUSKA);
		addTalkId(TRUP);
		addTalkId(FOLK);
		addQuestItem(NECK);
		
		addLevelCheck(99);
		addQuestCompletedCheck(_10442_TheAnnihilatedPlains1.class);
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
			st.addExpAndSp(308731500, 3087315);	
			st.takeItems(NECK, -1);
			st.giveItems(30357, 50);
			st.giveItems(30358, 50);
			st.giveItems(34609, 10000);
			st.giveItems(34616, 10000);
			st.giveItems(37018, 1);
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
		
		if(npcId == TUSKA)
		{
			if(!checkStartCondition(st.getPlayer()))
				htmltext = "no_level.htm";
			else if(cond == 0)
				htmltext = "1.htm";
			else if(cond == 1)
				htmltext = "3.htm";
		}
		else if(npcId == TRUP)
		{
			if(cond == 1)
			{
				st.giveItems(NECK, 1);
				st.setCond(2);
				return "1-1.htm";	
			}	
		}	
		else if(npcId == FOLK)
		{
			if(cond == 2)
				return "2-1.htm";
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
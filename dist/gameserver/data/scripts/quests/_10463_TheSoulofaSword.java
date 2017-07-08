package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Iqman
 */
public class _10463_TheSoulofaSword extends Quest implements ScriptFile
{
	//npc
	private static final int FLUTTER = 30677;
	//quest_items
	private static final int PRACTICE_STORMBRINGER = 36720;
	private static final int PRACTICE_STORMBRINGER_SA = 36723;
	private static final int PRACTICE_SA = 36721;
	private static final int PRACTICE_GEM = 36722;
	//rewards
	private static final int C_GEM = 2131;
	private static final int SA_RED = 4634;
	private static final int SA_GREEN = 4645;
	private static final int SA_BLUE = 4656;
	
	public _10463_TheSoulofaSword()
	{
		super(false);
		addStartNpc(FLUTTER);
		addTalkId(FLUTTER);
		addQuestItem(PRACTICE_STORMBRINGER, PRACTICE_STORMBRINGER_SA, PRACTICE_SA, PRACTICE_GEM);
		
		addLevelCheck(52,58);
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
			st.giveItems(PRACTICE_STORMBRINGER, 1);
			st.giveItems(PRACTICE_SA, 1);
			st.giveItems(PRACTICE_GEM, 97);
		}
		
		else if(event.equalsIgnoreCase("sa1.htm"))
		{
			st.addExpAndSp(504210, 5042);
			st.giveItems(SA_RED, 1);	
			st.giveItems(C_GEM, 97);		
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		
		else if(event.equalsIgnoreCase("sa2.htm"))
		{
			st.addExpAndSp(504210, 5042);
			st.giveItems(SA_GREEN, 1);	
			st.giveItems(C_GEM, 97);		
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}	

		else if(event.equalsIgnoreCase("sa3.htm"))
		{
			st.addExpAndSp(504210, 5042);
			st.giveItems(SA_BLUE, 1);	
			st.giveItems(C_GEM, 97);		
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
		
		if(npcId == FLUTTER)
		{
			if(st.getPlayer().getLevel() < 52 || st.getPlayer().getLevel() > 58)
				htmltext = "no_level.htm";
			else if(cond == 0)
				htmltext = "1.htm";
			else if(cond == 1 && st.getQuestItemsCount(PRACTICE_STORMBRINGER_SA) > 0)
				htmltext = "choose.htm";
			else if(cond == 1 && st.getQuestItemsCount(PRACTICE_STORMBRINGER_SA) <= 0)
				htmltext = "2.htm";
		}
		return htmltext;
	}
	
	@Override
	public void onAbort(QuestState st)
	{
		if(st.getQuestItemsCount(PRACTICE_STORMBRINGER) > 0)
			st.takeItems(PRACTICE_STORMBRINGER, -1);
		else if(st.getQuestItemsCount(PRACTICE_STORMBRINGER_SA) > 0)
			st.takeItems(PRACTICE_STORMBRINGER_SA, -1);
		else if(st.getQuestItemsCount(PRACTICE_SA) > 0)
			st.takeItems(PRACTICE_SA, -1);			
		else if(st.getQuestItemsCount(PRACTICE_GEM) > 0)
			st.takeItems(PRACTICE_GEM, -1);					
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
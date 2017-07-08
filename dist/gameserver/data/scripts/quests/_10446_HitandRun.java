package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Iqman
 */
public class _10446_HitandRun extends Quest implements ScriptFile
{
	public static final String A_LIST = "A_LIST";
	//npc
	private static final int BURINU = 33840;
	//mob
	private static final int NERVA_ORC = 23322;
	//rewards
	private static final int ETERNAL_ENHANCEMENT_STONE = 35569;
	private static final int ELMORE_SUPPORT_BOX = 37020;	
	
	public _10446_HitandRun()
	{
		super(false);
		addStartNpc(BURINU);
		addTalkId(BURINU);
		addKillNpcWithLog(1, A_LIST, 10, NERVA_ORC);
		
		addLevelCheck(99);
		addQuestCompletedCheck(_10445_AnImpendingThreat.class);
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
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		
		if(npcId == BURINU)
		{
			QuestState prevSt = st.getPlayer().getQuestState(_10445_AnImpendingThreat.class);
			if(st.getPlayer().getLevel() < 99 || prevSt == null || !prevSt.isCompleted())
				htmltext = "no_level.htm";
			else if(cond == 0)
				htmltext = "1.htm";
			else if(cond == 1)
				htmltext = "4.htm";
			else if(cond == 2)
			{
				st.giveItems(ETERNAL_ENHANCEMENT_STONE, 1);
				st.giveItems(ELMORE_SUPPORT_BOX, 1);			
				st.exitCurrentQuest(false);
				st.playSound(SOUND_FINISH);			
				htmltext = "endquest.htm";
			}	
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 1)
			return null;

		if(updateKill(npc, qs))
		{
			qs.unset(A_LIST);
			qs.setCond(2);
		}
		return null;
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
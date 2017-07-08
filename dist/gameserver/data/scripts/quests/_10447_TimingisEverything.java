package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Iqman
 */
public class _10447_TimingisEverything extends Quest implements ScriptFile
{
	//npc
	private static final int BURINU = 33840;
	//mobs
	private static final int[] MOBS = {23314, 23315, 23316, 23317, 23318, 23319, 23320, 23321, 23322, 23323, 23324, 23325, 23326, 23327, 23328, 23329};
	//q_items
	private static final int KEY = 36665;
	
	public _10447_TimingisEverything()
	{
		super(false);
		addStartNpc(BURINU);
		addTalkId(BURINU);
		addKillId(MOBS);
		addQuestItem(KEY);
		
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
		
		if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.addExpAndSp(2147483647L, 22228668L);
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
		
		if(npcId == BURINU)
		{
			QuestState prevSt = st.getPlayer().getQuestState(_10445_AnImpendingThreat.class);
			if(st.getPlayer().getLevel() < 99 || prevSt == null || !prevSt.isCompleted() || st.getPlayer().isNoble())
				htmltext = "no_level.htm";
			else if(cond == 0)
				htmltext = "1.htm";
			else if(cond == 1)
				htmltext = "3.htm";
			else if(cond == 2)
				htmltext = "endquest.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs == null)
			return null;
		if(qs.getState() != STARTED)
			return null;
		if(qs.getCond() != 1)
			return null;
		if(Rnd.chance(1))
		{
			qs.playSound(SOUND_MIDDLE);
			qs.giveItems(KEY, 1);
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
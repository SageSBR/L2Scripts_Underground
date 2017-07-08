package quests;

import java.util.Calendar;

import org.apache.commons.lang3.ArrayUtils;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _10351_OwnerOfHall extends Quest implements ScriptFile
{
	//npc
	private static final int TIPIA_NORMAL = 32892;

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10351_OwnerOfHall()
	{
		super(PARTY_ALL);
		addStartNpc(TIPIA_NORMAL);
		addTalkId(TIPIA_NORMAL);
		addKillId(29194, 29212); //octavius
		
		addQuestCompletedCheck(_10318_DecayingDarkness.class);

		addLevelCheck(95);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("32892-7.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}	
		if(event.equalsIgnoreCase("32892-10.htm"))
		{
			st.giveItems(57, 23655000);
			st.giveItems(19461, 1);
			st.addExpAndSp(897850000, 416175000);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);	
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
		if(player.getLevel() < 95)
			return "32892-lvl.htm";
		QuestState qs = st.getPlayer().getQuestState(_10318_DecayingDarkness.class);
		if(qs == null || !qs.isCompleted())
			return "32892-lvl.htm";				
		if(npcId == TIPIA_NORMAL)
		{
			if(cond == 0)
				return "32892.htm";
			if(cond == 1)
				return "32892-8.htm";	
			if(cond == 2)				
				return "32892-9.htm";
		}
		return "noquest";
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		switch(npc.getNpcId())
		{
			case 29194:
			case 29212:
				if(st.getCond() == 1)
					st.setCond(2);
				break;
		}
		return null;
	}	
}
package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Iqman
 */
public class _10392_FailureAndItsConsequences extends Quest implements ScriptFile
{
	//npc
	private static final int IASON_HEINE = 33859;
	private static final int ELLI = 33858;
	//mob
	private static final int[] MOBS = { 20991, 20992, 20993 };
	//rewards
	private static final int STEEL_DOOR_GUILD_COIN = 37045;
	//q items
	private static final int SUSPICIOUS_FRAGMENT = 36709;
	
	public _10392_FailureAndItsConsequences()
	{
		super(false);
		addStartNpc(IASON_HEINE);
		addTalkId(IASON_HEINE);
		addTalkId(ELLI);
		addKillId(MOBS);
		addQuestItem(SUSPICIOUS_FRAGMENT);
		addRaceCheck(true, true, true, true, true, true, false);
		addLevelCheck(40, 46);
		addQuestCompletedCheck(_10391_ASuspiciousHelper.class);
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
		
		if(event.equalsIgnoreCase("advance1.htm"))
		{
			st.setCond(3);
			st.takeItems(SUSPICIOUS_FRAGMENT, -1);
			st.playSound(SOUND_MIDDLE);		
		}	
		
		else if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.addExpAndSp(2329740, 93);
			st.giveItems(STEEL_DOOR_GUILD_COIN, 17);
			st.giveItems(952, 5);
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
		
		if(npcId == IASON_HEINE)
		{
			if(cond == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					htmltext = "1.htm";
				else
					htmltext = "no_level.htm";
			}
			else if(cond == 2)
				htmltext = "4.htm";		
		}
		else if(npcId == ELLI)
		{
			if(cond == 3)
				htmltext = "1-1.htm";
		}
		return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st == null)
			return null;
		if(st.getState() != STARTED)
			return null;
		if(st.getCond() != 1)
			return null;
		st.giveItems(SUSPICIOUS_FRAGMENT, 1);
		if(st.getQuestItemsCount(SUSPICIOUS_FRAGMENT) >= 30)
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);			
		}
		else
			st.playSound(SOUND_ITEMGET);
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
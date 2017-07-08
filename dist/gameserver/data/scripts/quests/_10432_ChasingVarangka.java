package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Iqman
 */
public class _10432_ChasingVarangka extends Quest implements ScriptFile
{
	//npc
	private static final int CHEIREN = 32655;
	private static final int JOKEL = 33868;
	//mob
	private static final int FARANGA_RAID = 18802;
	//rewards
	private static final int IRON_GATE_COIN = 37045;
	
	private static final int[] CLASS_LIMITS = { 88, 90, 91, 93, 99, 100, 101, 106, 107, 108, 114, 131, 132, 133, 136 };
	
	public _10432_ChasingVarangka()
	{
		super(false);
		addStartNpc(CHEIREN);
		addTalkId(CHEIREN);
		addTalkId(JOKEL);
		addKillId(FARANGA_RAID);
		
		addLevelCheck(81, 84);
		addQuestCompletedCheck(_10431_TheSealofPunishmentDenofEvil.class);
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
			st.addExpAndSp(14120400, 3388);
			st.giveItems(IRON_GATE_COIN, 30L);
			st.giveItems(960, 5L);
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
		
		if(npcId == CHEIREN)
		{
			if(st.getCond() == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					htmltext = "1.htm";
				else
					htmltext = "nocomp.htm";
			}
			else if(cond == 1)
				htmltext = "4.htm";
		}
		else if(npcId == JOKEL)
		{
			if(cond == 2)
				htmltext = "1-1.htm";
		}
		return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		int npcId = npc.getNpcId();
		if(qs == null)
			return null;
		if(qs.getState() != STARTED)
			return null;
		if(qs.getCond() != 1)
			return null;
		for(Player player : World.getAroundPlayers(npc, 1500, 200))
		{
			QuestState thisqs = player.getQuestState(_10432_ChasingVarangka.class);
			if(thisqs == null || thisqs.getCond() != 1)
				continue;
			thisqs.setCond(2);	
			thisqs.playSound(SOUND_MIDDLE);
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
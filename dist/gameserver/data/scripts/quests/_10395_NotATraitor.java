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
public class _10395_NotATraitor extends Quest implements ScriptFile
{
	//npc
	private static final int LEO = 33863;
	private static final int KELIOS = 33862;
	//mob
	private static final int[] MOBS = { 20161, 20575, 20576, 20261 };
	//rewards
	private static final int STEEL_DOOR_GUILD_COIN = 37045;
	private static final String A_LIST = "A_LIST";

	public _10395_NotATraitor()
	{
		super(PARTY_ALL);
		
		addStartNpc(LEO);
		addTalkId(LEO);
		addTalkId(KELIOS);
		addKillId(MOBS);
		addKillNpcWithLog(1, 539511, A_LIST, 50, MOBS);
		addRaceCheck(true, true, true, true, true, true, false);
		addLevelCheck(46, 52);
		addQuestCompletedCheck(_10394_MutualBenefit.class);
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
			st.giveItems(STEEL_DOOR_GUILD_COIN, 32L);
			st.giveItems(952, 5);
			st.addExpAndSp(3781574, 907);
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
		
		if(npcId == LEO)
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
		else if(npcId == KELIOS)
		{
			if(cond == 2)
				htmltext = "1-1.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		boolean doneKill = updateKill(npc, st);
		if(doneKill)
		{
			if (st.getCond() == 1)
			{
				st.unset(A_LIST);
				st.setCond(2);
			}
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
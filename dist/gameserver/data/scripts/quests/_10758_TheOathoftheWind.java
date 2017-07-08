package quests;

import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.scripts.Functions;
import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

import java.util.function.Function;

//By Evil_dnk

public class _10758_TheOathoftheWind extends Quest implements ScriptFile
{
	// NPC's
	private static final int FIO = 33963;
	private NpcInstance clonw = null;

	// Monster's
	private static final int CLONE = 27522;

	// Item's
	private static final int DOORCOIN = 37045;

	public _10758_TheOathoftheWind()
	{
		super(PARTY_ONE);
		addStartNpc(FIO);
		addTalkId(FIO);
		addKillId(CLONE);
		addQuestCompletedCheck(_10757_QuietingtheStorm.class);
		addLevelCheck(20);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33963-3.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("33963-5.htm"))
		{
			st.giveItems(DOORCOIN, 3);
			st.getPlayer().addExpAndSp(561645, 134);
			st.exitCurrentQuest(false);
		}
		else if(event.equalsIgnoreCase("summon"))
		{
			clonw = st.addSpawn(CLONE, st.getPlayer().getX() + 100, st.getPlayer().getY() + 100, st.getPlayer().getZ(), 0, 0, 180000);
			clonw.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 1000);
			return null;
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		String htmltext = "noquest";
		switch (npcId)
		{
			case FIO:
				if(cond == 0)
				{
					if(checkStartCondition(st.getPlayer()))
						htmltext = "33963-1.htm";
					else
						htmltext = "33963-0.htm";
				}
				else if (cond == 1)
					htmltext = "33963-3.htm";
				else if (cond == 2)
					htmltext = "33963-4.htm";
			break;

		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == CLONE)
		{
			if(cond == 1)
			{
				Functions.npcSay(clonw, NpcString.I_AM_LOYAL_TO_YO_MASTER_OF_THE_WINDS_AND_LOYAL_I_SHALL_REMAIN_IF_MY_VERY_SOUL_BETRAYS_ME);
				st.setCond(2);
			}
		}
		return null;
	}

	@Override
	public void onLoad()
	{
		//
	}

	@Override
	public void onReload()
	{
		//
	}

	@Override
	public void onShutdown()
	{
		//
	}
}
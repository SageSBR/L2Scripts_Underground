package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Iqman
 */
public class _10441_ChasingMeccadan extends Quest implements ScriptFile
{
	//npc
	private static final int GAKA = 32641;
	//rewards
	private static final int STEEL_DOOR_GUILD_COIN = 37045;
	private static final int ENCHANT = 960;
	//mobs
	private static final int[] MOBS = {27505};
	
	public _10441_ChasingMeccadan()
	{
		super(false);
		addStartNpc(GAKA);
		addTalkId(GAKA);
		addKillId(MOBS);
		
		addLevelCheck(81, 84);
		addClassIdCheck(94, 95, 96, 97, 98, 103, 104, 105, 110, 111, 112, 115, 116);
		addQuestCompletedCheck(_10440_TheSealOfPunishmentTheFields.class);
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
		else if(event.equalsIgnoreCase("rud1"))
		{
			st.giveItems(9546, 15);
			return onEvent("endquest.htm", st, npc);
		}

		else if(event.equalsIgnoreCase("rud2"))
		{
			st.giveItems(9547, 15);
			return onEvent("endquest.htm", st, npc);
		}

		else if(event.equalsIgnoreCase("rud3"))
		{
			st.giveItems(9548, 15);
			return onEvent("endquest.htm", st, npc);
		}

		else if(event.equalsIgnoreCase("rud4"))
		{
			st.giveItems(9549, 15);
			return onEvent("endquest.htm", st, npc);
		}

		else if(event.equalsIgnoreCase("rud5"))
		{
			st.giveItems(9550, 15);
			return onEvent("endquest.htm", st, npc);
		}

		else if(event.equalsIgnoreCase("rud6"))
		{
			st.giveItems(9551, 15);
			return onEvent("endquest.htm", st, npc);
		}			
		else if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.addExpAndSp(14120400, 3388);
			st.giveItems(STEEL_DOOR_GUILD_COIN, 30L);
			st.giveItems(ENCHANT, 5L);		
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
		
		if(npcId == GAKA)
		{
			if(st.getCond() == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					htmltext = "1.htm";
				else
					htmltext = "no_level.htm";
			}
			else if(cond == 1)
				htmltext = "4.htm";
			else if(cond == 2)
				return "5.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{	
		if(qs.getCond() == 1)
		{
			qs.playSound(SOUND_MIDDLE);
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
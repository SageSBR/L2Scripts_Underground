package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _10305_UnstoppableFutileEfforts extends Quest implements ScriptFile
{
	public static final String A_LIST = "A_LIST";
	//npc
	private static final int NOETI = 32895;

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10305_UnstoppableFutileEfforts()
	{
		super(false);
		addStartNpc(NOETI);
		addTalkId(NOETI);

		addLevelCheck(88);
		addQuestCompletedCheck(_10302_UnsettlingShadowAndRumors.class);
		addKillNpcWithLog(1, 1032919, A_LIST, 5, 32919);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("32895-05.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("rud1"))
		{
			st.giveItems(9546, 15);
			return onEvent("32895-08.htm", st, npc);
		}

		else if(event.equalsIgnoreCase("rud2"))
		{
			st.giveItems(9547, 15);
			return onEvent("32895-08.htm", st, npc);
		}

		else if(event.equalsIgnoreCase("rud3"))
		{
			st.giveItems(9548, 15);
			return onEvent("32895-08.htm", st, npc);
		}

		else if(event.equalsIgnoreCase("rud4"))
		{
			st.giveItems(9549, 15);
			return onEvent("32895-08.htm", st, npc);
		}

		else if(event.equalsIgnoreCase("rud5"))
		{
			st.giveItems(9550, 15);
			return onEvent("32895-08.htm", st, npc);
		}

		else if(event.equalsIgnoreCase("rud6"))
		{
			st.giveItems(9551, 15);
			return onEvent("32895-08.htm", st, npc);
		}
		if(event.equalsIgnoreCase("32895-08.htm"))
		{
			st.addExpAndSp(34971975, 8393);
			st.giveItems(57, 1007735);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}

		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int state = st.getState();
		int cond = st.getCond();
		
		if(state == COMPLETED)
			return "32895-comp.htm";

		if(npcId == NOETI)
		{
			if(cond == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					return "32895.htm";
				else
					return "32895-00.htm";
			}

			else if(cond == 1)
				return "32895-06.htm";
			else if(cond == 2)
				return "32895-07.htm";
		}
		return "noquest";
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
}
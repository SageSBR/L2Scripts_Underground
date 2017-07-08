package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;
import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.network.l2.components.NpcString;

public class _10421_AssasinationOfTheVarkaSilenosComm extends Quest implements ScriptFile
{
    //Квестовые персонажи
    private static final int HANSEN = 33853;

    //Монстры
	private static final int MOBS = 27502;
    //Награда за квест
    private static final int STEEL_DOOR_GUILD_COIN = 37045;
    private static final int SCROLL_ENCHANT_ARMOR_A_GRADE = 730;
	

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10421_AssasinationOfTheVarkaSilenosComm()
	{
		super(false);
		addStartNpc(HANSEN);
		addTalkId(HANSEN);
		addRaceCheck(true, true, true, true, true,true, false);
		addLevelCheck(76, 80);
		addKillId(MOBS);
		addQuestCompletedCheck(_10420_TheVarkaSilenosSupporters.class);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("accept"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			return "accept.htm";
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
		if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.addExpAndSp(7665840, 1839);
			st.giveItems(STEEL_DOOR_GUILD_COIN, 30);
			st.giveItems(SCROLL_ENCHANT_ARMOR_A_GRADE, 5);
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
		int cond = st.getCond();
		
		if(npcId == HANSEN)
		{
			if(st.getCond() == 0)
			{
				if(player.getClassId().isMage())
					return "Only warrior classes can take this quest";
				else if(checkStartCondition(st.getPlayer()))
					return "1.htm";
				else
					return "no_level.htm";
			}
			else if(cond == 1)
				return "4.htm";	
			else if(cond == 2)	
				return "5.htm";
		}
		return "noquest";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 1)
			return null;
		
		qs.setCond(2);
		qs.playSound(SOUND_MIDDLE);
		return null;
	}	
}
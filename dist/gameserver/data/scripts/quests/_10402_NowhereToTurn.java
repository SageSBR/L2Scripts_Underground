package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _10402_NowhereToTurn extends Quest implements ScriptFile
{
	public static final String A_LIST = "A_LIST";
    //Квест НПЦ
    private static final int EBLUNE = 33865;

    //Квест монстры
    private static final int[] MOBS = new int[]{20679, 20680, 21017, 21018, 21019, 21020, 21021, 21022};
    //Награда
    private static final int STEEL_DOOR_GUILD_COIN = 37045;
    private static final int SCROLL_ENCHANT_ARMOR_B_GRADE = 948;
	

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10402_NowhereToTurn()
	{
		super(false);
		addStartNpc(EBLUNE);
		addTalkId(EBLUNE);
		addRaceCheck(true, true, true, true, true,true, false);
		addLevelCheck(58, 61);
		addKillNpcWithLog(1, 540211, A_LIST, 40, MOBS);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("accept"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			return "giants_minion_eblune_q10402_04.htm";
		}	
		if(event.equalsIgnoreCase("endquest"))
		{
			st.addExpAndSp(5482574, 1315);
			st.giveItems(STEEL_DOOR_GUILD_COIN, 34);
			st.giveItems(SCROLL_ENCHANT_ARMOR_B_GRADE, 5);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);	
			return "giants_minion_eblune_q10402_06.htm";
		}		
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		if(npcId == EBLUNE)
		{
			if(cond == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					return "giants_minion_eblune_q10402_01.htm";
				else
					return "Only characters with level above 58 and below 61 can take this quest!";
			}
			else if(cond == 1)
				return "giants_minion_eblune_q10402_04.htm";	
			else if(cond == 2)	
				return "giants_minion_eblune_q10402_05.htm";
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
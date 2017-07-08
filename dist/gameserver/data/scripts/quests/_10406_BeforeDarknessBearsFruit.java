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

public class _10406_BeforeDarknessBearsFruit extends Quest implements ScriptFile
{
	public static final String A_LIST = "A_LIST";
    //Квестовые персонажи
    private static final int SUBAN = 33867;

    //Монстры
    private static final int[] MOBS = new int[]{19470};
    private static final int BEARS_FRUIT_DEFENDER = 27517 ;

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

	public _10406_BeforeDarknessBearsFruit()
	{
		super(false);
		addStartNpc(SUBAN);
		addTalkId(SUBAN);
		
		addQuestCompletedCheck(_10405_KartiasSeed.class);
		addRaceCheck(true, true, true, true, true, true, false);
		addLevelCheck(61, 65);
		addKillNpcWithLog(1, 1019470, A_LIST, 10, BEARS_FRUIT_DEFENDER);
		addKillId(MOBS);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("accept"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			return "shuvann_q10406_04.htm";
		}	
		if(event.equalsIgnoreCase("endquest"))
		{
			st.addExpAndSp(3125586, 750);
			st.giveItems(STEEL_DOOR_GUILD_COIN, 10);
			st.giveItems(SCROLL_ENCHANT_ARMOR_A_GRADE, 3);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);	
			return "shuvann_q10406_06.htm";
		}		
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		
		if(npcId == SUBAN)
		{
			if(st.getCond() == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					return "shuvann_q10406_01.htm";
				else
					return "Only characters with level above 61 and below 65, with completed Kartia's seed quest can take this quest!(Not for Ertheia race)";
			}
			else if(cond == 1)
				return "shuvann_q10406_04.htm";	
			else if(cond == 2)	
				return "shuvann_q10406_05.htm";
		}
		return "noquest";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 1)
			return null;
		
		if(ArrayUtils.contains(MOBS,npc.getNpcId()))
		{
			NpcInstance scout = qs.addSpawn(BEARS_FRUIT_DEFENDER, qs.getPlayer().getX() + 100, qs.getPlayer().getY() + 100, qs.getPlayer().getZ(), 0, 0, 360000);
			scout.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, qs.getPlayer(), 100000);			
			Functions.npcSay(scout, getRndString());
		}
		
		else if(updateKill(npc, qs))
		{
			qs.unset(A_LIST);
			qs.setCond(2);
		}
		return null;
	}	
	
	private static NpcString getRndString()
	{
		switch(Rnd.get(1, 5))
		{
			case 1:
				return NpcString.THERE_IS_ONLY_DEATH_FOR_INTRUDERS;
			case 2:
				return NpcString.YOU_DIG_YOUR_OWN_GRAVE_COMING_HERE; 
			case 3:
				return NpcString.DIE_2; 			
			case 4:
				return NpcString.DO_NOT_TOUCH_THAT_FLOWER; 				
			case 5:
				return NpcString.HAH_YOU_BELIEVE_THAT_IS_ENOUGH_TO_STAND_IN_THE_PATH_OF_DARKNESS; 					
		}
		return null;
	}
	
	@Override
	public boolean checkStartCondition(Player player)
	{
		QuestState wd = player.getQuestState(_10405_KartiasSeed.class);
		if(wd == null)
			return false;
		return wd.isCompleted();
	}	
}
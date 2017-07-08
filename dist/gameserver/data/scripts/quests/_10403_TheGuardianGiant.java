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

public class _10403_TheGuardianGiant extends Quest implements ScriptFile
{
	public static final String A_LIST = "A_LIST";
	
    //НПЦ
    private static final int Novain_Geographer = 33866;
    //Мобы
    private static final int [] MOBS = new int [] {20650, 20648, 20647, 20649};
    private static final int Guardian_Giant_Akum = 27504;
    //Квест Итем
    private static final int Guardian_Giants_Nucleus_Fragment = 36713;
    //Награда
    private static final int Enchant_Armor_B = 948;
    private static final int Steel_Coin = 37045;
	

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10403_TheGuardianGiant()
	{
		super(false);
		addStartNpc(Novain_Geographer);
		addTalkId(Novain_Geographer);
		
		addQuestCompletedCheck(_10402_NowhereToTurn.class);
		addRaceCheck(true, true, true, true, true,true, false);
		addLevelCheck(58, 61);
		addKillNpcWithLog(2, A_LIST, 1, Guardian_Giant_Akum);
		addKillId(MOBS);
		addQuestItem(Guardian_Giants_Nucleus_Fragment);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("accept"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			return "4.htm";
		}	
		if(event.equalsIgnoreCase("endquest"))
		{
			st.addExpAndSp(16968420, 1578);
			st.giveItems(Steel_Coin, 40);
			st.giveItems(Enchant_Armor_B, 5);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);	
			return "7.htm";
		}		
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		
		if(npcId == Novain_Geographer)
		{
			if(cond == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					return "1.htm";
				else
					return "no_level.htm";
			}
			else if(cond == 1 || cond == 2)
				return "5.htm";	
			else if(cond == 3)
				return "6.htm";				
		}
		return "noquest";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 1 && qs.getCond() != 2)
			return null;
		
		Player player = qs.getPlayer();	
		NpcInstance scout = null;
		if(qs.getCond() == 1)
		{
			if(ArrayUtils.contains(MOBS, npc.getNpcId()) && Rnd.chance(90))
			{
				qs.giveItems(Guardian_Giants_Nucleus_Fragment, 1);
				qs.playSound(SOUND_ITEMGET);
				if(qs.getQuestItemsCount(Guardian_Giants_Nucleus_Fragment) >= 50)
				{
					qs.setCond(2);
					qs.playSound(SOUND_MIDDLE);
					scout = qs.addSpawn(Guardian_Giant_Akum, qs.getPlayer().getX() + 100, qs.getPlayer().getY() + 100, qs.getPlayer().getZ(), 0, 0, 360000);
					scout.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, qs.getPlayer(), 100000);		
				}
			}
		}	
		else if(updateKill(npc, qs))
		{
			qs.unset(A_LIST);
			qs.setCond(3);
			if(scout != null)
				Functions.npcSay(scout, NpcString.YOUWITH_THE_POWER_OF_THE_GODSCEASE_YOUR_MASQUERADING_AS_OUR_MASTERS_OR_ELSE);
		}
		return null;
	}	
	
	@Override
	public boolean checkStartCondition(Player player)
	{
		QuestState wd = player.getQuestState(_10402_NowhereToTurn.class);
		if(wd == null)
			return false;
		return wd.isCompleted();
	}	
}
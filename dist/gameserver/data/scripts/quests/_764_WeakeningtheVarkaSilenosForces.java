package quests;

import java.util.Map;
import java.util.HashMap;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Iqman
 */
public class _764_WeakeningtheVarkaSilenosForces extends Quest implements ScriptFile
{
	//npc
	private static final int HANSEN = 33853;
	//q items
	private static final int SOLDAT_SIGN = 36674;
	private static final int GENERAL_SIGN = 36675;
	//rewards
	private static final int BOX = 37393;
	
	private static final Map<Integer, Integer> MOBS = new HashMap<Integer, Integer>();
	
	static
	{
		MOBS.put(Integer.valueOf(21350), Integer.valueOf(500));
		MOBS.put(Integer.valueOf(21353), Integer.valueOf(510));
		MOBS.put(Integer.valueOf(21354), Integer.valueOf(522));
		MOBS.put(Integer.valueOf(21355), Integer.valueOf(519));
		MOBS.put(Integer.valueOf(21357), Integer.valueOf(529));
		MOBS.put(Integer.valueOf(21358), Integer.valueOf(529));
		MOBS.put(Integer.valueOf(21360), Integer.valueOf(539));
		MOBS.put(Integer.valueOf(21362), Integer.valueOf(539));
		MOBS.put(Integer.valueOf(21364), Integer.valueOf(558));
		MOBS.put(Integer.valueOf(21365), Integer.valueOf(568));
		MOBS.put(Integer.valueOf(21366), Integer.valueOf(568));
		MOBS.put(Integer.valueOf(21368), Integer.valueOf(568));
		MOBS.put(Integer.valueOf(21369), Integer.valueOf(664));
		MOBS.put(Integer.valueOf(21371), Integer.valueOf(713));
		MOBS.put(Integer.valueOf(21373), Integer.valueOf(738));
	}	

	public _764_WeakeningtheVarkaSilenosForces()
	{
		super(false);
		addStartNpc(HANSEN);
		addTalkId(HANSEN);
		for(int npcId : MOBS.keySet())
			addKillId(npcId);
		addQuestItem(SOLDAT_SIGN);
		addQuestItem(GENERAL_SIGN);	
		addClassIdCheck(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 18, 19, 20, 21, 22, 23, 24, 31, 32, 33, 34, 35, 36, 37, 44, 45, 46, 47, 48, 53, 54, 55, 56, 57, 88, 89, 90, 91, 92, 93, 99, 100, 101, 102, 106, 107, 108, 109, 113, 114, 117, 118, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 139, 140, 141, 142, 144);
		addRaceCheck(true, true, true, true, true,true, false);
		addLevelCheck(76, 80);
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
		
		else if(event.equalsIgnoreCase("endquest1.htm"))
		{
			calculateReward(st);
			st.exitCurrentQuest(this);
			st.playSound(SOUND_FINISH);
		}
		else if(event.equalsIgnoreCase("endquest2.htm"))
		{
			calculateReward(st);
			st.exitCurrentQuest(this);
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

		if(st.getPlayer().getClassId().isMage())
			return "Only warrior classes can take this quest";

		if(st.isNowAvailable())
		{
			if(npcId == HANSEN)
			{

				if(cond == 0)
				{
					if(checkStartCondition(st.getPlayer()))
						return "1.htm";
					else
						return "no_level.htm";
				}
				else if(cond == 2)
					htmltext = "4.htm";
				else if(cond == 3)
					htmltext = "6.htm";
			}
		}
		else
			htmltext = "0.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() != 1 && st.getCond() != 2)
			return null;
		if(Rnd.get(1, 1000) < MOBS.get(npc.getNpcId()))
		{
			if(st.getCond() == 1)
			{
				st.giveItems(SOLDAT_SIGN, 1);
				st.playSound(SOUND_ITEMGET);
				if(st.getQuestItemsCount(SOLDAT_SIGN) >= 50L)
				{
					st.setCond(2);
					st.playSound(SOUND_MIDDLE);
				}
			}
			else if(st.getCond() == 2)
			{
				st.giveItems(GENERAL_SIGN, 1);
				st.playSound(SOUND_ITEMGET);
				if(st.getQuestItemsCount(GENERAL_SIGN) >= 900L)
				{
					st.setCond(3);
					st.playSound(SOUND_MIDDLE);
				}
			}
		}	
		return null;
	}
	
	private void calculateReward(QuestState st)
	{
		int boxCount = 0;
		int exp = 0;
		int sp = 0;
    
		long itemCount = st.getQuestItemsCount(GENERAL_SIGN);
    
		if (itemCount < 100L)
		{
			exp = 19164600;
			sp = 191646;
			boxCount = 1;
		}
		else if(itemCount >= 100L && itemCount < 200L)
		{
			exp = 38329200;
			sp = 383292;
			boxCount = 2;
		}
		else if(itemCount >= 200L && itemCount < 300L)
		{
			exp = 57493800;
			sp = 574938;
			boxCount = 3;
		}
		else if(itemCount >= 300L && itemCount < 400L)
		{
			exp = 76658400;
			sp = 766584;
			boxCount = 4;
		}
		else if(itemCount >= 400L && itemCount < 500L)
		{
			exp = 95823000;
			sp = 958230;
			boxCount = 5;
		}
		else if(itemCount >= 500L && itemCount < 600L)
		{
			exp = 114987600;
			sp = 1149876;
			boxCount = 6;
		}
		else if(itemCount >= 600L && itemCount < 700L)
		{
			exp = 134152200;
			sp = 1341522;
			boxCount = 7;
		}
		else if(itemCount >= 700L && itemCount < 800L)
		{
			exp = 153316800;
			sp = 1533168;
			boxCount = 8;
		}
		else if(itemCount >= 800L && itemCount < 900L)
		{
			exp = 172481400;
			sp = 1724814;
			boxCount = 9;
		}
		else if(itemCount >= 900L)
		{
			exp = 191646000;
			sp = 1916460;
			boxCount = 10;
		}
		st.addExpAndSp(exp, sp);
		st.takeItems(SOLDAT_SIGN, -1L);
		st.takeItems(GENERAL_SIGN, -1L);
		st.giveItems(BOX, boxCount);
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
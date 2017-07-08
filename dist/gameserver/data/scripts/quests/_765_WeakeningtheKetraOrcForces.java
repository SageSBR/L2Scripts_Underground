package quests;

import org.apache.commons.lang3.ArrayUtils;

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
public class _765_WeakeningtheKetraOrcForces extends Quest implements ScriptFile
{
	//npc
	private static final int RUGONESS = 33852;
	//q items
	private static final int SOLDAT_SIGN = 36676;
	private static final int GENERAL_SIGN = 36677;
	//rewards
	private static final int BOX = 37393;
	
	private static final Map<Integer, Integer> MOBS = new HashMap<Integer, Integer>();
	
	static
	{
		MOBS.put(Integer.valueOf(21324), Integer.valueOf(500));
		MOBS.put(Integer.valueOf(21327), Integer.valueOf(510));
		MOBS.put(Integer.valueOf(21328), Integer.valueOf(522));
		MOBS.put(Integer.valueOf(21329), Integer.valueOf(519));
		MOBS.put(Integer.valueOf(21331), Integer.valueOf(529));
		MOBS.put(Integer.valueOf(21332), Integer.valueOf(529));
		MOBS.put(Integer.valueOf(21334), Integer.valueOf(539));
		MOBS.put(Integer.valueOf(21336), Integer.valueOf(548));
		MOBS.put(Integer.valueOf(21338), Integer.valueOf(558));
		MOBS.put(Integer.valueOf(21339), Integer.valueOf(568));
		MOBS.put(Integer.valueOf(21340), Integer.valueOf(568));
		MOBS.put(Integer.valueOf(21342), Integer.valueOf(578));
		MOBS.put(Integer.valueOf(21343), Integer.valueOf(664));
		MOBS.put(Integer.valueOf(21345), Integer.valueOf(713));
		MOBS.put(Integer.valueOf(21347), Integer.valueOf(738));
	}	

	public _765_WeakeningtheKetraOrcForces()
	{
		super(false);
		addStartNpc(RUGONESS);
		addTalkId(RUGONESS);
		for(int npcId : MOBS.keySet())
			addKillId(npcId);
		addQuestItem(SOLDAT_SIGN);
		addQuestItem(GENERAL_SIGN);	
		addClassIdCheck(10, 11, 12, 13, 14, 15, 16, 17, 25, 26, 27, 28, 29, 30, 38, 39, 40, 41, 42, 43, 49, 50, 51, 52, 94, 95, 96, 97, 98, 103, 104, 105, 110, 111, 112, 115, 116, 143, 145, 146);
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

		if(!st.getPlayer().getClassId().isMage())
			return "Only mage classes can take this quest";

		if(st.isNowAvailable())
		{
			if(npcId == RUGONESS)
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
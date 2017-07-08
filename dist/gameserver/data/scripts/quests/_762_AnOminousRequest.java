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
public class _762_AnOminousRequest extends Quest implements ScriptFile
{
    private static final int Mysterious_Wizard = 31522;
    private static final int[] Mobs = new int[] {18119,21579,21576,21548,21549,21555,21547};
    private static final int Monster_Bone = 36670;
    private static final int Monster_Blood = 36671;
    private static final int Steel_Door_Guild_Reward_Box = 37391;
	
	public _762_AnOminousRequest()
	{
		super(false);
		addStartNpc(Mysterious_Wizard);
		addTalkId(Mysterious_Wizard);
		addQuestItem(Monster_Bone);
		addQuestItem(Monster_Blood);
		
		addKillId(Mobs);
		addRaceCheck(true, true, true, true, true, true, false);
		addLevelCheck(65, 70);
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
			htmltext = "4.htm";
		}
		
		if(event.equalsIgnoreCase("endquest"))
		{
			if(st.getQuestItemsCount(Monster_Bone) >= 50L)
			{
				st.takeItems(Monster_Bone, 50L);
				calcReward(st, st.getQuestItemsCount(Monster_Blood));
			}
			if(st.getQuestItemsCount(Monster_Blood) > 0)
			{
				st.takeItems(Monster_Blood, -1L);
			}
			st.exitCurrentQuest(this);
			st.playSound(SOUND_FINISH);
			htmltext = "ended.htm";
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		if(st.isNowAvailable())
		{
			if(npcId == Mysterious_Wizard)
			{
				if(cond == 0)
				{
					if(!st.getPlayer().getClassId().isMage())
						return "Only mage classes can take this quest";
					else if(checkStartCondition(st.getPlayer()))
						return "1.htm";
					else
						return "no_level.htm";
				}
				else if(cond == 1)
					htmltext = "5.htm";
				else if(cond == 2)
					htmltext = "6.htm";
				else if(cond == 3)
				{
					htmltext = "ended_comp.htm";
					st.exitCurrentQuest(this);
					st.playSound(SOUND_FINISH);
					calcReward(st, st.getQuestItemsCount(Monster_Blood));
					st.takeItems(Monster_Bone, -1L);
					st.takeItems(Monster_Blood, -1L);
					
				}	
			}
		}
		else
			htmltext = "0.htm";
			
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		int npcId = npc.getNpcId();
		if(qs == null)
			return null;
		if(qs.getState() != STARTED)
			return null;
		if(qs.getCond() != 1 && qs.getCond() != 2)
			return null;
		if(!ArrayUtils.contains(Mobs, npcId))	
			return null;
		if(qs.getCond() == 1)
		{
			qs.rollAndGive(Monster_Bone, 1, 50, 20);
			qs.playSound(SOUND_ITEMGET);
			if(qs.getQuestItemsCount(Monster_Bone) >= 50L)
			{
				qs.setCond(2);
				qs.playSound(SOUND_MIDDLE);
			}
		}
		else if(qs.getCond() == 2)
		{
			qs.rollAndGive(Monster_Blood, 1, 900, 20);
			qs.playSound(SOUND_ITEMGET);
			if(qs.getQuestItemsCount(Monster_Blood) >= 900L)
			{
				qs.setCond(3);
				qs.playSound(SOUND_MIDDLE);
			}
		}		
		return null;
	}
	
	private static void calcReward(QuestState st, long count)
	{
		long exp;
		long sp;
		long count_g;
		if(count > 0 && count < 100)
		{
			exp = 14140350L;
			sp = 141405;
			count_g = 1;
		}
		else if(count >= 100 && count < 200)
		{
			exp = 28280700;
			sp = 282810;
			count_g = 2;
		}		
		else if(count >= 200 && count < 300)
		{
			exp = 42421050;
			sp = 424215;
			count_g = 3;
		}			
		else if(count >= 300 && count < 400)
		{
			exp = 56561400;
			sp = 565620;
			count_g = 4;
		}	
		else if(count >= 400 && count < 500)
		{
			exp = 70701750;
			sp = 707025;
			count_g = 5;
		}	
		else if(count >= 500 && count < 600)
		{
			exp = 84842100;
			sp = 848430;
			count_g = 6;
		}	
		else if(count >= 600 && count < 700)
		{
			exp = 98982450;
			sp = 989835;
			count_g = 7;
		}		
		else if(count >= 700 && count < 800)
		{
			exp = 113122800;
			sp = 1131240;
			count_g = 8;
		}		
		else if(count >= 800 && count < 900)
		{
			exp = 127263150;
			sp = 1272645;
			count_g = 9;
		}	
		else
		{
			exp = 141403500;
			sp = 1414030;
			count_g = 10;
		}		
		st.addExpAndSp(exp, sp);
		st.giveItems(Steel_Door_Guild_Reward_Box, count_g);
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
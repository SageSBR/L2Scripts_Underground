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
public class _758_TheFallenKingsMen extends Quest implements ScriptFile
{
	//npc
	private static final int INTENDANT = 33407;
	//q_items
	private static final int TRAVIS_MARK = 36392;
	private static final int REPATRIAT_SOUL = 36393;
	//rewards
	private static final int EscortBox = 36394;
	//mobs
	private static final int[] MOBS = { 19455, 23296, 23294, 23292, 23291, 23290, 23300, 23299, 23298, 23297, 23295, 23293 };
	
	public _758_TheFallenKingsMen()
	{
		super(false);
		addStartNpc(INTENDANT);
		addTalkId(INTENDANT);
		addQuestItem(TRAVIS_MARK);
		addQuestItem(REPATRIAT_SOUL);
		
		addKillId(MOBS);
		
		addLevelCheck(97);
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
			htmltext = "3.htm";
		}
		
		if(event.equalsIgnoreCase("endquest.htm"))
		{
			if(st.getQuestItemsCount(TRAVIS_MARK) >= 50L)
			{
				st.takeItems(TRAVIS_MARK, 50L);
				st.giveItems(EscortBox, 1);
			}
			if(st.getQuestItemsCount(36393) > 0)
			{
				st.getPlayer().setVitality((int)(st.getPlayer().getVitality() + st.getQuestItemsCount(REPATRIAT_SOUL)), true);	
				st.takeItems(36393, -1L);
			}
			st.exitCurrentQuest(this);
			st.playSound(SOUND_FINISH);
			htmltext = "nexttime.htm";
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
			if(npcId == INTENDANT)
			{
				if(st.getPlayer().getLevel() < 97)
					return "no_level.htm";
				else if(cond == 0)
					return "1.htm";
				else if(cond == 1)
				{
					if(st.getQuestItemsCount(TRAVIS_MARK) < 50)
						return "no_ingrid.htm";
				}		
				else if(cond == 2)
				{
					if(st.getQuestItemsCount(REPATRIAT_SOUL) > 0)
					{
						st.getPlayer().setVitality((int)(st.getPlayer().getVitality() + st.getQuestItemsCount(REPATRIAT_SOUL)), true);	
						st.takeItems(REPATRIAT_SOUL, -1L);	
						return "5.htm";	
					}	
					else
					{
						return "4.htm";
					}	
				}
				else if(cond == 3)
				{
					st.exitCurrentQuest(this);
					st.playSound(SOUND_FINISH);
					st.takeItems(REPATRIAT_SOUL, -1L);
					st.takeItems(TRAVIS_MARK, -1L);
					st.giveItems(EscortBox, 1);
					return "7.htm";
				}	
			}
		}
		else
			return "no_aval.htm";
			
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
		if(!ArrayUtils.contains(MOBS, npcId))	
			return null;
		if(qs.getCond() == 1)
		{
			qs.giveItems(TRAVIS_MARK, 1);
			qs.playSound(SOUND_ITEMGET);
			if(qs.getQuestItemsCount(TRAVIS_MARK) >= 50L)
			{
				qs.setCond(2);
				qs.playSound(SOUND_MIDDLE);
			}
		}
		else if(qs.getCond() == 2)
		{
			qs.giveItems(REPATRIAT_SOUL, 1);
			qs.playSound(SOUND_ITEMGET);
			if(qs.getQuestItemsCount(REPATRIAT_SOUL) >= 1200L)
			{
				qs.setCond(3);
				qs.playSound(SOUND_MIDDLE);
			}
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
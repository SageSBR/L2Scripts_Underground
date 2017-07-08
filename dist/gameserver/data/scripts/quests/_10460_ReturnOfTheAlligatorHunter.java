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
public class _10460_ReturnOfTheAlligatorHunter extends Quest implements ScriptFile
{
	//npc
	private static final int ELLON = 33860;
	//mob
	private static final int[] MOBS1 = { 20135 };
	private static final int[] MOBS2 = { 20804, 20805, 20806 };
	private static final int[] MOBS3 = { 20807, 20808 };
	//q items
	private static final int ALLIGATOR_SKIN = 4337;
	private static final int BLUE_ALLIGATOR_SKIN = 4338;
	private static final int PRECIOUS_ALLIGATOR_SKIN = 4339;
	//rewards
	private static final int STEEL_DOOR_GUILD_COIN = 37045;		
	public _10460_ReturnOfTheAlligatorHunter()
	{
		super(false);
		addStartNpc(ELLON);
		addTalkId(ELLON);
		addKillId(MOBS1);
		addKillId(MOBS2);
		addKillId(MOBS3);
		addQuestItem(ALLIGATOR_SKIN);
		addQuestItem(BLUE_ALLIGATOR_SKIN);
		addQuestItem(PRECIOUS_ALLIGATOR_SKIN);
		addRaceCheck(true, true, true, true, true,true, false);
		addLevelCheck(40, 46);
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
		
		else if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.takeItems(ALLIGATOR_SKIN, 30L);
			st.takeItems(BLUE_ALLIGATOR_SKIN, 20L);
			st.takeItems(PRECIOUS_ALLIGATOR_SKIN, 10L);		
			st.giveItems(STEEL_DOOR_GUILD_COIN, 26);
			st.addExpAndSp(2795688, 27956);	
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
		
		if(npcId == ELLON)
		{
			if(cond == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					htmltext = "1.htm";
				else
					htmltext = "no_level.htm";
			}
			else if(cond == 2)
				htmltext = "4.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if(st.getCond() != 1)
			return null;
		if(ArrayUtils.contains(MOBS1, npcId) && st.getQuestItemsCount(ALLIGATOR_SKIN) < 30L)
		{
			st.giveItems(ALLIGATOR_SKIN, 1L);
			st.playSound(SOUND_ITEMGET);
		}
		if(ArrayUtils.contains(MOBS2, npcId) && st.getQuestItemsCount(BLUE_ALLIGATOR_SKIN) < 20L)
		{
			st.giveItems(BLUE_ALLIGATOR_SKIN, 1L);
			st.playSound(SOUND_ITEMGET);
		}
		if(ArrayUtils.contains(MOBS3, npcId) && st.getQuestItemsCount(PRECIOUS_ALLIGATOR_SKIN) < 10L)
		{
			st.giveItems(PRECIOUS_ALLIGATOR_SKIN, 1L);
			st.playSound(SOUND_ITEMGET);
		}
		if(st.getQuestItemsCount(ALLIGATOR_SKIN) >= 30L && st.getQuestItemsCount(BLUE_ALLIGATOR_SKIN) >= 20L && st.getQuestItemsCount(PRECIOUS_ALLIGATOR_SKIN) >= 10L)
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
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
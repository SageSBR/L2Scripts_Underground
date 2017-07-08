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
public class _10399_TheAlphabetOfTheGiants extends Quest implements ScriptFile
{
	//npc
	private static final int BACON = 33846;
	//quest_items
	private static final int BYKVA = 36667;
	//rewards
	private static final int STEEL_DOOR_GUILD_COIN = 37045;
	//mobs
	private static final int[] MOBS = {23309, 23310};
	public _10399_TheAlphabetOfTheGiants()
	{
		super(false);
		addStartNpc(BACON);
		addTalkId(BACON);
		addQuestItem(BYKVA);
		addKillId(MOBS);
		addRaceCheck(true, true, true, true, true,true, false);
		addLevelCheck(52,58);
		addQuestCompletedCheck(_10398_ASuspiciousBadge.class);
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

		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		
		if(npcId == BACON)
		{
			if(cond == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					htmltext = "1.htm";
				else
					htmltext = "This quest is only for 52-58 level and completed quest A Suspisious Badge";
			}
			else if(cond == 1)
				htmltext = "3.htm";
			else if(cond == 2)
			{
				st.addExpAndSp(3811500, 914);
				st.takeItems(BYKVA, 20L);
				st.giveItems(STEEL_DOOR_GUILD_COIN, 37L);
				st.giveItems(948, 5);
				st.exitCurrentQuest(false);
				st.playSound(SOUND_FINISH);
				return "endquest.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs == null)
			return null;

		if(qs.getState() != STARTED)
			return null;

		int npcId = npc.getNpcId();
		int cond = qs.getCond();

		if(ArrayUtils.contains(MOBS, npcId))
		{
			if(cond == 1 && Rnd.chance(50))
			{
				qs.giveItems(BYKVA, 1L);
				if(qs.getQuestItemsCount(BYKVA) >= 20L)
				{
					qs.playSound(SOUND_MIDDLE);
					qs.setCond(2);
				}
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
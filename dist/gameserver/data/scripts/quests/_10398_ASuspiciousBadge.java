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
public class _10398_ASuspiciousBadge extends Quest implements ScriptFile
{
	//npc
	private static final int ANDY = 33845;
	private static final int BACON = 33846;
	//quest_items
	private static final int UNIDENTIFIED_SUSPICIOUS_BADGE = 36666;
	//rewards
	private static final int STEEL_DOOR_GUILD_COIN = 37045;
	//mobs
	private static final int[] MOBS = {20555, 20558, 23305, 23306, 23307, 23308};
	public _10398_ASuspiciousBadge()
	{
		super(false);
		addStartNpc(ANDY);
		addTalkId(ANDY);
		addTalkId(BACON);
		addRaceCheck(true, true, true, true, true,true, false);
		addQuestItem(UNIDENTIFIED_SUSPICIOUS_BADGE);
		addKillId(MOBS);
		addRaceCheck(true, true, true, true, true, true, false);
		addLevelCheck(52,58);
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
			st.addExpAndSp(3811500, 914);
			st.takeItems(UNIDENTIFIED_SUSPICIOUS_BADGE, 20L);
			st.giveItems(STEEL_DOOR_GUILD_COIN, 36L);
			st.giveItems(948, 5);
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
		
		if(npcId == ANDY)
		{
			if(cond == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					htmltext = "1.htm";
				else
					htmltext = "no_level.htm";
			}
			else if(cond == 1)
				htmltext = "4.htm";
		}
		else if(npcId == BACON)
		{
			if(cond == 2)
				htmltext = "1-1.htm";		
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
				qs.giveItems(UNIDENTIFIED_SUSPICIOUS_BADGE, 1L);
				if(qs.getQuestItemsCount(UNIDENTIFIED_SUSPICIOUS_BADGE) >= 20L)
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
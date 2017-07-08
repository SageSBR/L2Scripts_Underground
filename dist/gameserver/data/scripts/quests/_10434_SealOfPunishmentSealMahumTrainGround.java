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
public class _10434_SealOfPunishmentSealMahumTrainGround extends Quest implements ScriptFile
{
	//npc
	private static final int RYA = 33841;
	//quest_items
	private static final int PROOF = 36692;
	//rewards
	private static final int STEEL_DOOR_GUILD_COIN = 37045;
	//mobs
	private static final int[] MOBS = {22776, 22778, 22775, 22775, 22779, 22780, 22783, 22782, 22781, 22785, 22786, 22787, 22788, 18908};
	
	public _10434_SealOfPunishmentSealMahumTrainGround()
	{
		super(false);
		addStartNpc(RYA);
		addTalkId(RYA);
		addQuestItem(PROOF);
		addKillId(MOBS);
		
		addLevelCheck(81,84);
		addClassIdCheck(89, 113, 117, 118);
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
		else if(event.equalsIgnoreCase("rud1"))
		{
			st.giveItems(9546, 15);
			
			return onEvent("endquest.htm", st, npc);
		}

		else if(event.equalsIgnoreCase("rud2"))
		{
			st.giveItems(9547, 15);
			
			return onEvent("endquest.htm", st, npc);
		}

		else if(event.equalsIgnoreCase("rud3"))
		{
			st.giveItems(9548, 15);
			
			return onEvent("endquest.htm", st, npc);
		}

		else if(event.equalsIgnoreCase("rud4"))
		{
			st.giveItems(9549, 15);
			
			return onEvent("endquest.htm", st, npc);
		}

		else if(event.equalsIgnoreCase("rud5"))
		{
			st.giveItems(9550, 15);
			
			return onEvent("endquest.htm", st, npc);
		}

		else if(event.equalsIgnoreCase("rud6"))
		{
			st.giveItems(9551, 15);
			
			return onEvent("endquest.htm", st, npc);
		}			
		else if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.addExpAndSp(28240800, 6777);
			st.takeItems(PROOF, 50L);
			st.giveItems(STEEL_DOOR_GUILD_COIN, 60L);			
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
		
		if(npcId == RYA)
		{
			if(st.getCond() == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					htmltext = "1.htm";
				else
					htmltext = "no_level.htm";
			}
			else if(cond == 1)
				htmltext = "4.htm";
			else if(cond == 2)
				return "5.htm";
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
				qs.giveItems(PROOF, 1L);
				if(qs.getQuestItemsCount(PROOF) >= 50L)
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
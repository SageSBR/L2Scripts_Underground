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
public class _10431_TheSealofPunishmentDenofEvil extends Quest implements ScriptFile
{
	//npc
	private static final int JOKEL = 33868;
	private static final int CHEIREN = 32655;
	//mob
	private static final int[] MONSTERS_LIST = { 22691, 22692, 22693, 22694, 22695, 22696, 22697, 22698, 22699, 22701 };
	//q items
	private static final int EVIL_SOUL = 36715;
	//rewards
	private static final int IRON_GATE_COIN = 37045;

	public _10431_TheSealofPunishmentDenofEvil()
	{
		super(false);
		addStartNpc(JOKEL);
		addTalkId(JOKEL);
		addTalkId(CHEIREN);
		addKillId(MONSTERS_LIST);
		addQuestItem(EVIL_SOUL);
		addClassIdCheck(88, 90, 91, 93, 99, 100, 101, 106, 107, 108, 114, 131, 132, 133, 136);
		addLevelCheck(81, 84);
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
		
		if(event.equalsIgnoreCase("advance1.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}	
		
		else if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.addExpAndSp(28240800, 6777);
			st.giveItems(IRON_GATE_COIN, 60);
			st.takeItems(EVIL_SOUL, -1);
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
		
		if(npcId == JOKEL)
		{
			if(st.getCond() == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					return "1.htm";
				else
					htmltext = "nocomp.htm";
			}
			else if(cond == 1)
				htmltext = "4.htm";
		}
		else if(npcId == CHEIREN)
		{
			if(cond == 1)
				htmltext = "1-1.htm";
			else if(cond == 2)
				htmltext = "1-3.htm";
			else if(cond == 3)
				htmltext = "1-4.htm";
		}
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
		if(qs.getCond() != 2 || !Rnd.chance(50))
			return null;
		qs.giveItems(EVIL_SOUL, 1);
		if(qs.getQuestItemsCount(EVIL_SOUL) >= 50)
		{
			qs.setCond(3);
			qs.playSound(SOUND_MIDDLE);			
		}
		else
			qs.playSound(SOUND_ITEMGET);
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
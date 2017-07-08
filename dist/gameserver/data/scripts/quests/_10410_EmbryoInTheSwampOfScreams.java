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

public class _10410_EmbryoInTheSwampOfScreams extends Quest implements ScriptFile
{
	public static final String A_LIST = "A_LIST";
    //Квестовые персонажи
    private static final int TRACKER_DOKARA = 33847;

    //Монстры
    private static final int[] MOBS = new int[]{21508, 21509, 21510, 21511, 21512, 21513, 21514, 21515, 21516, 21517, 21518, 21519};
    private static final int SWAMP_OF_SCREAMS_SCOUT = 27508;

    //Награда за квест
    private static final int STEEL_DOOR_GUILD_COIN = 37045;
    private static final int SCROLL_ENCHANT_ARMOR_A_GRADE = 730;
	

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10410_EmbryoInTheSwampOfScreams()
	{
		super(false);
		addStartNpc(TRACKER_DOKARA);
		addTalkId(TRACKER_DOKARA);
		addRaceCheck(true, true, true, true, true, true, false);
		addQuestCompletedCheck(_10409_ASuspiciousVagabondInTheSwamp.class);
		addLevelCheck(65, 70);
		addKillNpcWithLog(1, A_LIST, 50, SWAMP_OF_SCREAMS_SCOUT);
		addKillId(MOBS);
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
			return "tracker_dokara_q10410_04.htm";
		}	
		if(event.equalsIgnoreCase("endquest"))
		{
			st.addExpAndSp(16968420, 4072);
			st.giveItems(STEEL_DOOR_GUILD_COIN, 63);
			st.giveItems(SCROLL_ENCHANT_ARMOR_A_GRADE, 5);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);	
			return "tracker_dokara_q10410_06.htm";
		}		
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		
		if(npcId == TRACKER_DOKARA)
		{
			if(st.getCond() == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					return "tracker_dokara_q10410_01.htm";
				else
					return "Only characters with level above 65 and below 70  with completed Suspissious Vagabond quest can take this quest!(Not for Ertheia race)";
			}
			else if(cond == 1)
				return "tracker_dokara_q10410_04.htm";	
			else if(cond == 2)	
				return "tracker_dokara_q10410_05.htm";
		}
		return "noquest";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 1)
			return null;
		
		if(ArrayUtils.contains(MOBS,npc.getNpcId()))
		{
			NpcInstance scout = qs.addSpawn(SWAMP_OF_SCREAMS_SCOUT, qs.getPlayer().getX() + 50, qs.getPlayer().getY() + 50, qs.getPlayer().getZ(), 0, 0, 360000);
			scout.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, qs.getPlayer(), 100000);			
			Functions.npcSay(scout, NpcString.YOU_DARE_INTERFERE_WITH_EMBRYO_SURELY_YOU_WISH_FOR_DEATH);
		}
		
		else if(updateKill(npc, qs))
		{
			qs.unset(A_LIST);
			qs.setCond(2);
		}
		return null;
	}	
	
	@Override
	public boolean checkStartCondition(Player player)
	{
		QuestState wd = player.getQuestState(_10409_ASuspiciousVagabondInTheSwamp.class);
		if(wd == null)
			return false;
		return wd.isCompleted();
	}	
}
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

public class _10413_EmbryoInTheForestOfTheDead extends Quest implements ScriptFile
{
	public static final String A_LIST = "A_LIST";
	
	private static final int Hatuba = 33849;
	private static final int[] Mobs = new int[] {21549, 21547, 21548, 21550, 21551, 21552, 21553, 21554, 21555, 21556,
			21557, 21558, 21559, 21560, 21561, 21562, 21563, 21564, 21565, 21566, 21567, 21568, 21569, 21570,
			21571, 21572, 21573, 21574, 21575, 21576, 21577, 21578, 21579, 21580, 21581, 21582, 21583, 21584,
			21585, 21586, 21587, 21588, 21589, 21590, 21591, 21592, 21593, 21594, 21595, 21596, 21597, 21599, 18119};

	private static final int Forest_The_Dead_Scout_Embryo = 27509;
	private static final int Enchant_Armor_A = 730;
	private static final int Steel_Coin = 37045;
	

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10413_EmbryoInTheForestOfTheDead()
	{
		super(false);
		addStartNpc(Hatuba);
		addTalkId(Hatuba);
		addRaceCheck(true, true, true, true, true, true, false);
		addQuestCompletedCheck(_10412_ASuspiciousVagabondInTheForest.class);
		addLevelCheck(65, 70);
		addKillNpcWithLog(1, A_LIST, 50, Forest_The_Dead_Scout_Embryo);
		addKillId(Mobs);
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
			return "33847-04.htm";
		}	
		if(event.equalsIgnoreCase("endquest"))
		{
			st.addExpAndSp(16968420, 4072);
			st.giveItems(Steel_Coin, 63);
			st.giveItems(Enchant_Armor_A, 5);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);	
			return "33847-06.htm";
		}		
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		
		if(npcId == Hatuba)
		{
			if(st.getCond() == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					return "33847-01.htm";
				else
					return "Only characters with level above 65 and below 70  with completed Suspissious Vagabond quest can take this quest!(Not for Ertheia race)";
			}
			else if(cond == 1)
				return "33847-04.htm";	
			else if(cond == 2)	
				return "33847-05.htm";
		}
		return "noquest";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 1)
			return null;
		
		if(ArrayUtils.contains(Mobs,npc.getNpcId()))
		{
			NpcInstance scout = qs.addSpawn(Forest_The_Dead_Scout_Embryo, qs.getPlayer().getX() + 100, qs.getPlayer().getY() + 100, qs.getPlayer().getZ(), 0, 0, 360000);
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
		QuestState wd = player.getQuestState(_10412_ASuspiciousVagabondInTheForest.class);
		if(wd == null)
			return false;
		return wd.isCompleted();
	}	
}
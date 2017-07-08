package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _785_ASuspiciousIngredient extends Quest implements ScriptFile
{
	// NPC's
	private static final int WIZARD = 31522;

	// Monster's
	private static final int[] MONSTERS = {21549, 21547, 21548, 21550, 21551, 21552, 21553, 21554, 21555, 21556,
			21557, 21558, 21559, 21560, 21561, 21562, 21563, 21564, 21565, 21566, 21567, 21568, 21569, 21570,
			21571, 21572, 21573, 21574, 21575, 21576, 21577, 21578, 21579, 21580, 21581, 21582, 21583, 21584,
			21585, 21586, 21587, 21588, 21589, 21590, 21591, 21592, 21593, 21594, 21595, 21596, 21597, 21599, 18119};

	// Item's
	private static final int FLESH = 39732;
	private static final int MONSTERSBLOOD = 39733;


	public _785_ASuspiciousIngredient()
	{
		super(PARTY_ONE);
		addStartNpc(WIZARD);
		addKillId(MONSTERS);
		addQuestCompletedCheck(_10793_SavetheSouls.class);
		addClassIdCheck(185);
		addLevelCheck(65, 70);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("31522-4.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("31522-9.htm"))
		{
			st.takeItems(FLESH, -1);
			if(st.getQuestItemsCount(MONSTERSBLOOD) < 100)
			{
				st.giveItems(37391, 1);
				st.getPlayer().addExpAndSp(14140350, 3393);
			}
			else if(st.getQuestItemsCount(MONSTERSBLOOD) >= 100 && st.getQuestItemsCount(MONSTERSBLOOD) <= 199)
			{
				st.giveItems(37391, 2);
				st.getPlayer().addExpAndSp(28280700, 6786);
			}
			else if(st.getQuestItemsCount(MONSTERSBLOOD) >= 200 && st.getQuestItemsCount(MONSTERSBLOOD) <= 299)
			{
				st.giveItems(37391, 3);
				st.getPlayer().addExpAndSp(42421050, 10179);
			}
			else if(st.getQuestItemsCount(MONSTERSBLOOD) >= 300 && st.getQuestItemsCount(MONSTERSBLOOD) <= 399)
			{
				st.giveItems(37391, 4);
				st.getPlayer().addExpAndSp(56561400, 13572);
			}
			else if(st.getQuestItemsCount(MONSTERSBLOOD) >= 400 && st.getQuestItemsCount(MONSTERSBLOOD) <= 499)
			{
				st.giveItems(37391, 5);
				st.getPlayer().addExpAndSp(70701750, 16965);
			}
			else if(st.getQuestItemsCount(MONSTERSBLOOD) >= 500 && st.getQuestItemsCount(MONSTERSBLOOD) <= 599)
			{
				st.giveItems(37391, 6);
				st.getPlayer().addExpAndSp(84842100, 20358);
			}
			else if(st.getQuestItemsCount(MONSTERSBLOOD) >= 600 && st.getQuestItemsCount(MONSTERSBLOOD) <= 699)
			{
				st.giveItems(37391, 7);
				st.getPlayer().addExpAndSp(98982450, 23751);
			}
			else if(st.getQuestItemsCount(MONSTERSBLOOD) >= 700 && st.getQuestItemsCount(MONSTERSBLOOD) <= 799)
			{
				st.giveItems(37391, 8);
				st.getPlayer().addExpAndSp(113122800, 27144);
			}
			else if(st.getQuestItemsCount(MONSTERSBLOOD) >= 800 && st.getQuestItemsCount(MONSTERSBLOOD) <= 899)
			{
				st.giveItems(37391, 9);
				st.getPlayer().addExpAndSp(127263150, 30537);
			}
			else if(st.getQuestItemsCount(MONSTERSBLOOD) >= 900)
			{
				st.giveItems(37391, 10);
				st.getPlayer().addExpAndSp(141403500, 33930);
			}
			st.takeItems(MONSTERSBLOOD, -1);
			st.exitCurrentQuest(this);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		String htmltext = "noquest";
			switch (npcId)
			{
				case WIZARD:
					if(cond == 0)
					{
						if(!st.isNowAvailable())
							htmltext = "31522-10.htm";
						else if(checkStartCondition(st.getPlayer()))
							htmltext = "31522-1.htm";
						else
							htmltext = "31522-0.htm";
					}
					else if(cond == 1)
						htmltext = "31522-5.htm";
					else if(cond == 2 || cond == 3)
						htmltext = "31522-6.htm";
					break;
			}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 1)
		{
			st.rollAndGive(FLESH, 1, 1, 50, 10);
			if(st.getQuestItemsCount(FLESH)>= 50)
			{
				st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.YOU_CAN_GATHER_MORE_MONSTER_BLOOD, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
				st.setCond(2);
			}
		}
		if(cond == 2)
		{
			st.rollAndGive(MONSTERSBLOOD, 1, 1, 900, 10);
			if(st.getQuestItemsCount(FLESH)>= 900)
				st.setCond(3);
		}
		return null;
	}

	@Override
	public void onLoad()
	{
		//
	}

	@Override
	public void onReload()
	{
		//
	}

	@Override
	public void onShutdown()
	{
		//
	}
}
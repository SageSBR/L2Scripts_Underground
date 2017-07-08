package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10799_StrangeThingsAfootintheValley extends Quest implements ScriptFile
{
	// NPC's
	private static final int NAMO = 33973;
	//Mobs
	private static final int[] MONSTERS = { 23423, 23424, 23425, 23427, 23428, 23429, 23436, 23437, 23438, 23439, 23440};
	// Item's
	private static final int DOORCOIN = 37045;
	private static final int ENCHANTARMOR = 23417;

	private static final String Dragonvally = "dragonvally";

	public _10799_StrangeThingsAfootintheValley()
	{
		super(PARTY_ONE);
		addStartNpc(NAMO);
		addTalkId(NAMO);
		addKillNpcWithLog(1, 579911, Dragonvally, 100, MONSTERS);
		addLevelCheck(76, 85);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33973-4.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("fire"))
		{
			st.giveItems(DOORCOIN, 196);
			st.giveItems(ENCHANTARMOR, 10);
			st.getPlayer().addExpAndSp(76658400, 18398);
			st.giveItems(9546, 30);
			htmltext = "33973-8.htm";
			st.exitCurrentQuest(false);
		}
		else if(event.equalsIgnoreCase("water"))
		{
			st.giveItems(DOORCOIN, 196);
			st.giveItems(ENCHANTARMOR, 10);
			st.getPlayer().addExpAndSp(76658400, 18398);
			st.giveItems(9547, 30);
			htmltext = "33973-8.htm";
			st.exitCurrentQuest(false);
		}
		else if(event.equalsIgnoreCase("earth"))
		{
			st.giveItems(DOORCOIN, 196);
			st.giveItems(ENCHANTARMOR, 10);
			st.getPlayer().addExpAndSp(76658400, 18398);
			st.giveItems(9548, 30);
			htmltext = "33973-8.htm";
			st.exitCurrentQuest(false);
		}
		else if(event.equalsIgnoreCase("wind"))
		{
			st.giveItems(DOORCOIN, 196);
			st.giveItems(ENCHANTARMOR, 10);
			st.getPlayer().addExpAndSp(76658400, 18398);
			st.giveItems(9549, 30);
			htmltext = "33973-8.htm";
			st.exitCurrentQuest(false);
		}
		else if(event.equalsIgnoreCase("dark"))
		{
			st.giveItems(DOORCOIN, 196);
			st.giveItems(ENCHANTARMOR, 10);
			st.giveItems(9550, 30);
			st.getPlayer().addExpAndSp(76658400, 18398);
			htmltext = "33973-8.htm";
			st.exitCurrentQuest(false);
		}
		else if(event.equalsIgnoreCase("divine"))
		{
			st.giveItems(DOORCOIN, 196);
			st.giveItems(ENCHANTARMOR, 10);
			st.getPlayer().addExpAndSp(76658400, 18398);
			st.giveItems(9551, 30);
			htmltext = "33973-8.htm";
			st.exitCurrentQuest(false);
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
			case NAMO:
				if(cond == 0)
				{
					if(checkStartCondition(st.getPlayer()))
						htmltext = "33973-1.htm";
					else
						htmltext = "33973-0.htm";
				}
				else if (cond == 1)
					htmltext = "33973-5.htm";
				else if (cond == 2)
					htmltext = "33973-6.htm";
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
			boolean doneKill = updateKill(npc, st);
			if(doneKill)
			{
				st.unset(Dragonvally);
				st.setCond(2);
			}
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
package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _119_LastImperialPrince extends Quest implements ScriptFile
{
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

	// NPC
	private static final int SPIRIT = 31453; // Nameless Spirit
	private static final int DEVORIN = 32009; // Devorin

	// ITEM
	private static final int BROOCH = 7262; // Antique Brooch

	// REWARD
	private static final int AMOUNT = 407970; // Amount

	public _119_LastImperialPrince()
	{
		super(false);
		addStartNpc(SPIRIT);
		addTalkId(DEVORIN);
		addLevelCheck(74, 80);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("31453-4.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("32009-2.htm"))
		{
			if(st.getQuestItemsCount(BROOCH) < 1)
			{
				htmltext = "noquest";
				st.exitCurrentQuest(true);
			}
		}
		else if(event.equalsIgnoreCase("32009-3.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("31453-7.htm"))
		{
			st.giveItems(ADENA_ID, AMOUNT, true);
			st.addExpAndSp(1919448, 2100933);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		// confirm that quest can be executed.
		if(st.getPlayer().getLevel() < 74 || st.getPlayer().getLevel() > 80)
		{
			htmltext = "<html><body>Quest for characters level 74 and 80.</body></html>";
			st.exitCurrentQuest(true);
			return htmltext;
		}
		else if(st.getQuestItemsCount(BROOCH) < 1)
		{
			htmltext = "noquest";
			st.exitCurrentQuest(true);
			return htmltext;
		}

		if(npcId == SPIRIT)
		{
			if(cond == 0)
				return "31453-1.htm";
			else if(cond == 2)
				return "31453-5.htm";
			else
				return "noquest";
		}
		else if(npcId == DEVORIN && cond == 1)
			htmltext = "32009-1.htm";
		return htmltext;
	}
}
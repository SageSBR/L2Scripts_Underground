package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10798_LettersfromtheQueenDragonValley extends Quest implements ScriptFile
{
	// NPC's
	private static final int MAXIMIL = 30120;
	private static final int NAMO = 33973;

	//ITEMS
	private static final int DOORCOIN = 37045;
	private static final int ENCHANTWEAPON = 23411;

	public _10798_LettersfromtheQueenDragonValley()
	{
		super(PARTY_NONE);
		addTalkId(MAXIMIL);
		addTalkId(NAMO);
		addLevelCheck(76, 84);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30120-3.htm"))
		{
			st.giveItems(39587, 1, false);
			st.setCond(2);
		}

		else if(event.equalsIgnoreCase("33973-3.htm"))
		{
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.YOU_HAVE_FINISHED_ALL_OF_QUEEN_NAVARIS_LETTERS_GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_LETTERS_FROM_A_MINSTREL_AT_LV_85, 5000, ScreenMessageAlign.TOP_CENTER, false));
			st.getPlayer().addExpAndSp(1277640, 306);
			st.giveItems(DOORCOIN, 182);
			st.giveItems(ENCHANTWEAPON, 2);
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
			case MAXIMIL:
				if(cond == 1)
					htmltext = "30120-1.htm";
				else if(cond == 2)
					htmltext = "30120-4.htm";
			break;

			case NAMO:
				if (cond == 2)
					htmltext = "33973-1.htm";
			break;
		}
		return htmltext;
	}

	@Override
	public void onLoad()
	{
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
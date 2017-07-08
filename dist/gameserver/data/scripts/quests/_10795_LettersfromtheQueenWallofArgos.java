package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10795_LettersfromtheQueenWallofArgos extends Quest implements ScriptFile
{
	// NPC's
	private static final int GREGORI = 31279;
	private static final int HERMIT = 31616;

	//ITEMS
	private static final int DOORCOIN = 37045;
	private static final int ENCHANTARMOR = 23418;

	public _10795_LettersfromtheQueenWallofArgos()
	{
		super(PARTY_NONE);
		addTalkId(GREGORI);
		addTalkId(HERMIT);
		addLevelCheck(70, 75);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("31279-3.htm"))
		{
			st.giveItems(39585, 1, false);
			st.setCond(2);
		}

		else if(event.equalsIgnoreCase("31616-3.htm"))
		{
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_QUEEN_NAVARI_AT_LV_76, 5000, ScreenMessageAlign.TOP_CENTER, false));
			st.getPlayer().addExpAndSp(1088640, 261);
			st.giveItems(DOORCOIN, 123);
			st.giveItems(ENCHANTARMOR, 5);
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
			case GREGORI:
				if(cond == 1)
					htmltext = "31279-1.htm";
				else if(cond == 2)
					htmltext = "31279-4.htm";
			break;

			case HERMIT:
				if (cond == 2)
					htmltext = "31616-1.htm";
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
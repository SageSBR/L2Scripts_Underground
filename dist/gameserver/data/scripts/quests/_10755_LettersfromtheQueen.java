package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10755_LettersfromtheQueen extends Quest implements ScriptFile
{
	// NPC's
	private static final int LEVIAN = 30037;
	private static final int PIO = 33963;

	//ITEMS
	private static final int DOORCOIN = 37045;

	public _10755_LettersfromtheQueen()
	{
		super(PARTY_NONE);
		addTalkId(LEVIAN);
		addTalkId(PIO);
		addLevelCheck(20, 29);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30037-2.htm"))
		{
			st.giveItems(39492, 1, false);
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.TRY_USING_THE_TELEPORT_SCROLL_LEVIAN_GAVE_YOU, 5000, ScreenMessageAlign.TOP_CENTER, false));
			st.setCond(2);
		}

		else if(event.equalsIgnoreCase("33963-2.htm"))
		{
			//st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_QUEEN_NAVARI_AT_LV_30, 5000, ScreenMessageAlign.TOP_CENTER, false));
			st.getPlayer().addExpAndSp(120960, 29);
			st.giveItems(DOORCOIN, 5);
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
			case LEVIAN:
				if(cond == 1)
					htmltext = "30037-1.htm";
			break;

			case PIO:
				if (cond == 2)
					htmltext = "33963-1.htm";
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
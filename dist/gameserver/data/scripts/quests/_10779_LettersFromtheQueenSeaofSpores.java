package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10779_LettersFromtheQueenSeaofSpores extends Quest implements ScriptFile
{
	// NPC's
	private static final int HOLINT = 30191;
	private static final int ENDI = 33845;

	//ITEMS
	private static final int DOORCOIN = 37045;
	private static final int ENCHANTWEAPON = 23413;

	public _10779_LettersFromtheQueenSeaofSpores()
	{
		super(PARTY_NONE);
		addTalkId(HOLINT);
		addTalkId(ENDI);
		addLevelCheck(52, 57);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30191-3.htm"))
		{
			st.giveItems(39575, 1, false);
			st.setCond(2);
		}

		else if(event.equalsIgnoreCase("33845-3.htm"))
		{
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_QUEEN_NAVARI_AT_LV_58, 5000, ScreenMessageAlign.TOP_CENTER, false));
			st.getPlayer().addExpAndSp(635250, 152);
			st.giveItems(DOORCOIN, 37);
			st.giveItems(ENCHANTWEAPON, 3);
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
			case HOLINT:
				if(cond == 1)
					htmltext = "30191-1.htm";
				else if(cond == 2)
					htmltext = "30191-4.htm";
			break;

			case ENDI:
				if (cond == 2)
					htmltext = "33845-1.htm";
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
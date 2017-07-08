package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10789_LettersfromtheQueenSwampofScreams extends Quest implements ScriptFile
{
	// NPC's
	private static final int INNOSENTIN = 31328;
	private static final int TAKARA = 33847;

	//ITEMS
	private static final int DOORCOIN = 37045;
	private static final int ENCHANTWEAPON = 23412;

	public _10789_LettersfromtheQueenSwampofScreams()
	{
		super(PARTY_NONE);
		addTalkId(INNOSENTIN);
		addTalkId(TAKARA);
		addClassIdCheck(182, 184, 186, 188, 190 );
		addLevelCheck(65, 69);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("highpriest_innocentin_q10789_03.htm"))
		{
			st.giveItems(39581, 1, false);
			st.setCond(2);
		}

		else if(event.equalsIgnoreCase("33847-3.htm"))
		{
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_QUEEN_NAVARI_AT_LV_70, 5000, ScreenMessageAlign.TOP_CENTER, false));
			st.getPlayer().addExpAndSp(942690, 226);
			st.giveItems(DOORCOIN, 91);
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
			case INNOSENTIN:
				if(cond == 1)
					htmltext = "highpriest_innocentin_q10789_01.htm";
				else if(cond == 2)
					htmltext = "highpriest_innocentin_q10789_04.htm";
			break;

			case TAKARA:
				if (cond == 2)
					htmltext = "33847-1.htm";
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
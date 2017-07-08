package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10769_Lettersfromth_QueenCrumaTowerPart1 extends Quest implements ScriptFile
{
	// NPC's
	private static final int SILVIAN = 30070;
	private static final int LOREIN = 30673;

	//ITEMS
	private static final int DOORCOIN = 37045;
	private static final int ENCHANTARMOR = 23420;
	private static final int ENCHANTWEAPON = 23414;

	public _10769_Lettersfromth_QueenCrumaTowerPart1()
	{
		super(PARTY_NONE);
		addTalkId(LOREIN);
		addTalkId(SILVIAN);
		addLevelCheck(40, 49);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30070-3.htm"))
		{
			st.giveItems(39594, 1, false);
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.TRY_USING_THE_TELEPORT_SCROLL_SYLVAIN_GAVE_YOU_TO_GO_TO_CRUMA_TOWER, 5000, ScreenMessageAlign.TOP_CENTER, false));
			st.setCond(2);
		}

		else if(event.equalsIgnoreCase("30673-3.htm"))
		{
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_QUEEN_NAVARI_AT_LV_46, 5000, ScreenMessageAlign.TOP_CENTER, false));
			st.getPlayer().addExpAndSp(370440, 88);
			st.giveItems(DOORCOIN, 11);
			st.giveItems(ENCHANTWEAPON, 1);
			st.giveItems(ENCHANTARMOR, 1);
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
			case SILVIAN:
				if(cond == 1)
					htmltext = "30070-1.htm";
				else if(cond == 2)
					htmltext = "30070-4.htm";
			break;

			case LOREIN:
				if (cond == 2)
					htmltext = "30673-1.htm";
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
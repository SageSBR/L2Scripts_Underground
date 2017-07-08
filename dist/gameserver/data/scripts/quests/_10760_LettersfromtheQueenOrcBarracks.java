package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10760_LettersfromtheQueenOrcBarracks extends Quest implements ScriptFile
{
	// NPC's
	private static final int LEVIAN = 30037;
	private static final int PIOTUR = 30597;

	//ITEMS
	private static final int DOORCOIN = 37045;

	public _10760_LettersfromtheQueenOrcBarracks()
	{
		super(PARTY_NONE);
		addTalkId(PIOTUR);
		addTalkId(LEVIAN);
		addLevelCheck(30, 39);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30037-3.htm"))
		{
			st.giveItems(39487, 1, false);
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.TRY_USING_THE_TELEPORT_SCROLL_LEVIAN_GAVE_YOU_TO_GO_TO_ORC_BARRACKS, 5000, ScreenMessageAlign.TOP_CENTER, false));
			st.setCond(2);
		}

		else if(event.equalsIgnoreCase("30597-3.htm"))
		{
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.TRY_TALKING_TO_VORBOS_BY_THE_WELLNYOU_CAN_RECEIVE_QUEEN_NAVARIS_NEXT_LETTER_AT_LV_40, 5000, ScreenMessageAlign.TOP_CENTER, false));
			st.getPlayer().addExpAndSp(242760, 58);
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

			case PIOTUR:
				if (cond == 2)
					htmltext = "30597-1.htm";
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
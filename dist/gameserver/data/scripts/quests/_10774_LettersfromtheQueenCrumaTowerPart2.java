package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10774_LettersfromtheQueenCrumaTowerPart2 extends Quest implements ScriptFile
{
	// NPC's
	private static final int SILVIAN = 30070;
	private static final int ROMBEL = 30487;

	//ITEMS
	private static final int DOORCOIN = 37045;
	private static final int ENCHANTARMOR = 23420;

	public _10774_LettersfromtheQueenCrumaTowerPart2()
	{
		super(PARTY_NONE);
		addTalkId(ROMBEL);
		addTalkId(SILVIAN);
		addLevelCheck(46, 51);
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

		else if(event.equalsIgnoreCase("30487-3.htm"))
		{
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_QUEEN_NAVARI_AT_LV_52, 5000, ScreenMessageAlign.TOP_CENTER, false));
			st.getPlayer().addExpAndSp(483840, 116);
			st.giveItems(DOORCOIN, 11);
			st.giveItems(ENCHANTARMOR, 2);
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
			break;

			case ROMBEL:
				if (cond == 2)
					htmltext = "30487-1.htm";
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
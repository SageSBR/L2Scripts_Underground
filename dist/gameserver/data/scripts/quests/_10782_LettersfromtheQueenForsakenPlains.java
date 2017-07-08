package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10782_LettersfromtheQueenForsakenPlains extends Quest implements ScriptFile
{
	// NPC's
	private static final int ORVEN = 30857;
	private static final int NOVAIN = 33866;

	//ITEMS
	private static final int DOORCOIN = 37045;
	private static final int ENCHANTARMOR = 23419;

	public _10782_LettersfromtheQueenForsakenPlains()
	{
		super(PARTY_NONE);
		addTalkId(ORVEN);
		addTalkId(NOVAIN);
		addLevelCheck(58, 60);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30857-3.htm"))
		{
			st.giveItems(39577, 1, false);
			st.setCond(2);
		}

		else if(event.equalsIgnoreCase("33866-3.htm"))
		{
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_QUEEN_NAVARI_AT_LV_61, 5000, ScreenMessageAlign.TOP_CENTER, false));
			st.getPlayer().addExpAndSp(731010, 175);
			st.giveItems(DOORCOIN, 60);
			st.giveItems(ENCHANTARMOR, 10);
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
			case ORVEN:
				if(cond == 1)
					htmltext = "30857-1.htm";
				else if(cond == 2)
					htmltext = "30857-4.htm";
			break;

			case NOVAIN:
				if (cond == 2)
					htmltext = "33866-1.htm";
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
package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.s2c.TutorialShowHtmlPacket;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10767_AWholeNewLevelofAlchemy extends Quest implements ScriptFile
{
	// NPC's
	private static final int BEROYA = 33977;

	public _10767_AWholeNewLevelofAlchemy()
	{
		super(PARTY_ONE);
		addStartNpc(BEROYA);
		addLevelCheck(97);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("beroni_de_khan_q10767_05.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("beroni_de_khan_q10767_08.htm"))
		{
			st.takeItems(39469, 1000);
			st.takeItems(39474, 1000);
			st.takeItems(39479, 1000);
			st.giveItems(39482, 3);
			st.getPlayer().addExpAndSp(14819175, 3556);
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
			case BEROYA:
				if(cond == 0)
				{
					if(checkStartCondition(st.getPlayer()))
						htmltext = "beroni_de_khan_q10767_01.htm";
					else if(!st.getPlayer().getClassId().isOfRace(Race.ERTHEIA))
						htmltext = "beroni_de_khan_q10767_02a.htm";
					else
						htmltext = "beroni_de_khan_q10767_02.htm";
				}
				else if (cond == 1)
				{
					if(st.getQuestItemsCount(39469) >= 1000 && st.getQuestItemsCount(39474) >= 1000 && st.getQuestItemsCount(39479) >= 1000)
						htmltext = "beroni_de_khan_q10767_07.htm";
					else
						htmltext = "beroni_de_khan_q10767_06.htm";
				}

			break;

		}
		return htmltext;
	}

	@Override
	public void onLoad()
	{
		//
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
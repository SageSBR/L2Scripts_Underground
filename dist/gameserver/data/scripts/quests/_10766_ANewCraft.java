package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.s2c.TutorialShowHtmlPacket;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10766_ANewCraft extends Quest implements ScriptFile
{
	// NPC's
	private static final int ARIS = 33942;
	private static final int ZEFIRA = 33978;

	public _10766_ANewCraft()
	{
		super(PARTY_ONE);
		addStartNpc(ARIS);
		addTalkId(ZEFIRA);
		addLevelCheck(40);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33942-4.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("33978-7.htm"))
		{
			st.giveItems(39466, 50);
			st.giveItems(39471, 50);
			st.getPlayer().addExpAndSp(168000, 40);
			st.exitCurrentQuest(false);
		}
		else if(event.equalsIgnoreCase("33978-3.htm"))
		{
			st.getPlayer().sendPacket(new TutorialShowHtmlPacket(TutorialShowHtmlPacket.LARGE_WINDOW, "..\\L2Text\\QT_026_alchemy_01.htm"));
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("33978-5.htm"))
		{
			st.setCond(3);
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
			case ARIS:
				if(cond == 0)
				{
					if(checkStartCondition(st.getPlayer()))
						htmltext = "33942-1.htm";
					else
						htmltext = "33942-0.htm";
				}
				else if (cond == 1)
					htmltext = "33942-5.htm";
			break;

			case ZEFIRA:
				if (cond == 1)
					htmltext = "33978-1.htm";
				else if (cond == 2)
					htmltext = "33978-4.htm";
				else if (cond == 3)
				{
					if(st.haveQuestItem(39466) && st.haveQuestItem(39461))
						htmltext = "33978-6.htm";
					else
						htmltext = "33978-5.htm";
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
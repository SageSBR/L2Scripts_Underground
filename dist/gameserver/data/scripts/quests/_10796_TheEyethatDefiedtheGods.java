package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10796_TheEyethatDefiedtheGods extends Quest implements ScriptFile
{
	// NPC's
	private static final int HERMIT = 31616;
	private static final int EYEAGROS = 31683;

	// Item's
	private static final int DOORCOIN = 37045;
	private static final int ENCHANTARMOR = 23418;

	public _10796_TheEyethatDefiedtheGods()
	{
		super(PARTY_ONE);
		addStartNpc(HERMIT);
		addTalkId(EYEAGROS);
		addLevelCheck(70, 75);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("31616-4.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("31683-2.htm"))
		{
			st.giveItems(DOORCOIN, 2);
			st.giveItems(ENCHANTARMOR, 2);
			st.getPlayer().addExpAndSp(1088640, 261);
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
			case HERMIT:
				if(cond == 0)
				{
					if(checkStartCondition(st.getPlayer()))
						htmltext = "31616-1.htm";
					else
						htmltext = "31616-0.htm";
				}
				else if (cond == 1)
					htmltext = "31616-5.htm";
			break;

			case EYEAGROS:
				if (cond == 1)
					htmltext = "31683-1.htm";
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
package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _906_TheCallofValakas extends Quest implements ScriptFile
{
	private static final int Klein = 31540;
	private static final int LavasaurusAlphaFragment = 21993;
	private static final int ValakasMinion = 29029;

	public _906_TheCallofValakas()
	{
		super(PARTY_ALL);
		addStartNpc(Klein);
		addTalkId(Klein);
		addKillId(ValakasMinion);
		addQuestItem(LavasaurusAlphaFragment);
		addLevelCheck(83);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("klein_q906_03.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		if(npc.getNpcId() == Klein)
		{
			switch(st.getState())
			{
				case CREATED:
					if(st.isNowAvailable())
					{
						if(st.getPlayer().getLevel() >= 83)
						{
							if(st.getQuestItemsCount(7267) > 0)
								htmltext = "klein_q906_01.htm";
							else
								htmltext = "klein_q906_00b.htm";
						}
						else
						{
							htmltext = "klein_q906_00.htm";
							st.exitCurrentQuest(true);
						}
					}
					else
						htmltext = "klein_q906_00a.htm";
					break;
				case STARTED:
					if(cond == 1)
						htmltext = "klein_q906_04.htm";
					break;
			}
		}
		else if(cond == 2)
		{
			htmltext = "klein_q906_05.htm";
			st.takeAllItems(LavasaurusAlphaFragment);
			st.giveItems(21895, 1); // Scroll: Valakas Call
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(this);
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 1)
		{
			if(npc.getNpcId() == ValakasMinion)
			{
				st.giveItems(LavasaurusAlphaFragment, 1);
				st.setCond(2);
			}
		}
		return null;
	}

	@Override
	public void onLoad()
	{
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}
}
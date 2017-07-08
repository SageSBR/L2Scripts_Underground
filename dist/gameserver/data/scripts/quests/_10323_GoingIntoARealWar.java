package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.s2c.TutorialShowHtmlPacket;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;
import l2s.gameserver.utils.ReflectionUtils;

//By Evil_dnk

public class _10323_GoingIntoARealWar extends Quest implements ScriptFile
{
	private final static int shenon = 32974;
	private final static int evain = 33464;
	private final static int holden = 33194;
	private final static int golem = 27532;
	private static final String A_LIST = "A_LIST";

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10323_GoingIntoARealWar()
	{
		super(false);
		addStartNpc(evain);
		addTalkId(shenon);
		addTalkId(holden);
		addKillNpcWithLog(2, 532311, A_LIST, 4, golem);
		addKillNpcWithLog(6, 532312, A_LIST, 4, golem);
		addKillNpcWithLog(7, 532312, A_LIST, 4, golem);

		addLevelCheck(1, 20);
		addQuestCompletedCheck(_10322_SearchingForTheMysteriousPower.class);
		addRaceCheck(true, true, true, true, true, true, false);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();

		if(event.equalsIgnoreCase("quest_ac"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
			htmltext = "33464-03.htm";
		}

		if(event.equalsIgnoreCase("qet_rev"))
		{
			htmltext = "32974-02.htm";
			st.getPlayer().addExpAndSp(1700, 5);
			st.giveItems(57, 9000);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
	
		if(event.equalsIgnoreCase("step2"))
		{
				st.setCond(2);
				htmltext = "33194-3.htm";
		}

		if(event.equalsIgnoreCase("step9"))
		{
			st.setCond(9);
			htmltext = "33194-9.htm";
		}
		if(event.equalsIgnoreCase("getshots"))
		{
			st.playSound(SOUND_MIDDLE);
			player.sendPacket(new TutorialShowHtmlPacket(TutorialShowHtmlPacket.LARGE_WINDOW, "..\\L2Text\\QT_003_bullet_01.htm"));
			//st.showTutorialHTML(TutorialShowHtmlPacket.LARGE_WINDOW, "..\\L2Text\\QT_003_bullet_01.htm");
			if(!player.isMageClass() || player.getRace() == Race.ORC)
			{
				player.sendPacket(new ExShowScreenMessage(NpcString.SOULSHOT_HAVE_BEEN_ADDED_TO_YOUR_INVENTORY, 4500, ScreenMessageAlign.TOP_CENTER));
				st.startQuestTimer("soul_timer", 4000);
				st.giveItems(5789, 500);
				st.setCond(4);
			}
			else
			{
				player.sendPacket(new ExShowScreenMessage(NpcString.SPIRITSHOT_HAVE_BEEN_ADDED_TO_YOUR_INVENTORY, 4500, ScreenMessageAlign.TOP_CENTER));
				st.startQuestTimer("spirit_timer", 4000);
				st.giveItems(5790, 500);
				st.setCond(5);
			}
			return null;
		}
		if(event.equalsIgnoreCase("soul_timer"))
		{
			htmltext = "33194-6.htm";
			player.sendPacket(new ExShowScreenMessage(NpcString.AUTOMATE_SOULSHOT_AS_SHOWN_IN_THE_TUTORIAL, 4500, ScreenMessageAlign.TOP_CENTER));
		}
		if(event.equalsIgnoreCase("spirit_timer"))
		{
			htmltext = "33194-6m.htm";
			player.sendPacket(new ExShowScreenMessage(NpcString.AUTOMATE_SPIRITSHOT_AS_SHOWN_IN_THE_TUTORIAL, 4500, ScreenMessageAlign.TOP_CENTER));
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		Player player = st.getPlayer();

		String htmltext = "noquest";
		if(npcId == evain) 
		{
			if(st.isCompleted())
				htmltext = "33464-05.htm";
			QuestState qs = st.getPlayer().getQuestState(_10322_SearchingForTheMysteriousPower.class);
			if(qs == null || !qs.isCompleted())
				return "You should complete another quest to start this!";
			if(st.getPlayer().getLevel() > 20)
				return "Your Level is too high for this quest!";
			if(!checkStartCondition(st.getPlayer()))
				return "Not for your class";
			else if(cond == 0)
				htmltext = "33464-01.htm";
			else if(cond == 1)
				htmltext = "33464-04.htm";
			else
				htmltext = "noqu.htm";
		}

		if(npcId == holden)
		{
			if (cond == 1)
			{
				htmltext = "33194-1.htm";
			}
			else if (cond == 2)
				htmltext = "33194-4.htm";

			else if(st.getCond() == 3)
			{
				if(!player.isMageClass() || player.getRace() == Race.ORC)
					htmltext = "33194-5.htm";
				else
					htmltext = "33194-5m.htm";
			}
			else if(st.getCond() == 4 || st.getCond() == 5)
			{

				if(!player.isMageClass() || player.getRace() == Race.ORC)
				{
					htmltext = "33194-7.htm";
					st.setCond(6);
				}
				else
				{
					st.setCond(7);
					htmltext = "33194-7m.htm";
				}
			}
			else if(st.getCond() == 8)
			{
				htmltext = "33194-8.htm";
			}
			else if(st.getCond() == 9)
			{
				htmltext = "33194-9.htm";
			}
		}

		else if(npcId == shenon)
		{
			if(st.isCompleted())
				htmltext = "32974-03.htm";
			else if(cond == 9)
			{
				if (!player.isMageClass() || player.getRace() == Race.ORC)
					htmltext = "32974-01.htm";
				else
					htmltext = "32974-01m.htm";
			}
		}
		return htmltext;
	}


	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		boolean doneKill = updateKill(npc, st);
		if(doneKill)
		{
			if (st.getCond() == 2)
			{
				st.unset(A_LIST);
				st.setCond(3);
			}
			else if (st.getCond() == 6 || st.getCond() == 7)
			{
				st.unset(A_LIST);
				st.setCond(8);
			}
		}
		return null;
	}
}
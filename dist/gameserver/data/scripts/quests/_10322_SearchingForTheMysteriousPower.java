package quests;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.network.l2.s2c.TutorialShowHtmlPacket;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

//By Evil_dnk

public class _10322_SearchingForTheMysteriousPower extends Quest implements ScriptFile
{
	private static final int evan = 33464;
	private static final int shenon = 32974;
	private static final int helper = 32981;
	private static final int crow = 27457;

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10322_SearchingForTheMysteriousPower()
	{
		super(false);
		addStartNpc(shenon);
		addTalkId(shenon);
		addTalkId(helper);
		addTalkId(evan);
		addKillId(crow);

		addLevelCheck(1, 20);
		addQuestCompletedCheck(_10321_QualificationsOfTheSeeker.class);
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
			htmltext = "0-3.htm";
		}

		if(event.equalsIgnoreCase("bufs"))
		{
			SkillHolder.getInstance().getSkill(4322, 1).getEffects(player, player);
			SkillHolder.getInstance().getSkill(4323, 1).getEffects(player, player);
			SkillHolder.getInstance().getSkill(5637, 1).getEffects(player, player);
			if(!player.isMageClass() || player.getRace() == Race.ORC)
			{
				SkillHolder.getInstance().getSkill(4324, 1).getEffects(player, player);
				SkillHolder.getInstance().getSkill(4327, 1).getEffects(player, player);
				SkillHolder.getInstance().getSkill(4325, 1).getEffects(player, player);
				SkillHolder.getInstance().getSkill(4326, 1).getEffects(player, player);
			}
			else
			{
				SkillHolder.getInstance().getSkill(4328, 1).getEffects(player, player);
				SkillHolder.getInstance().getSkill(4329, 1).getEffects(player, player);
				SkillHolder.getInstance().getSkill(4330, 1).getEffects(player, player);
				SkillHolder.getInstance().getSkill(4331, 1).getEffects(player, player);
			}
			st.setCond(5);
			st.playSound(SOUND_MIDDLE);
			htmltext = "2-2.htm";
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = "noquest";

		if(npcId == shenon)
		{
			if(st.isCompleted())
				htmltext = "0-c.htm";
			if(!checkStartCondition(st.getPlayer()))
				return "noqu.htm";
			else if(cond == 0)
				htmltext = "start.htm";
			else if(cond >= 1)
				htmltext = "0-4.htm";
			else
				htmltext = "noqu.htm";
		}
		else if(npcId == evan)
		{
			if(st.isCompleted())
				htmltext = "1-c.htm";
			else if(cond == 0)
				return htmltext;
			else if(cond == 1)
			{
				htmltext = "1-1.htm";
				st.setCond(2);
				st.playSound(SOUND_MIDDLE);
			}
			else if(cond == 2)
				htmltext = "1-3.htm";
			else if(cond == 3)
			{
				htmltext = "1-2.htm";
				st.setCond(4);
				st.playSound(SOUND_MIDDLE);
			}
			else if(cond == 4)
				htmltext = "1-4.htm";
			else if(cond == 5)
				htmltext = "1-6.htm";
			else if(cond == 6)
			{
				htmltext = "1-5.htm";
				Functions.npcSayToPlayer(npc, st.getPlayer(), NpcString.WEAPONS_HAVE_BEEN_ADDED_TO_YOUR_INVENTORY);
				st.getPlayer().addExpAndSp(300, 800);
				st.giveItems(57, 7000);
				st.giveItems(17, 500);
				st.giveItems(7816, 1);
				st.giveItems(7817, 1);
				st.giveItems(7818, 1);
				st.giveItems(7819, 1);
				st.giveItems(7820, 1);
				st.giveItems(7821, 1);
				st.giveItems(1060, 50);
				st.exitCurrentQuest(false);
				st.playSound(SOUND_FINISH);
			}
		}
		else if(npcId == helper)
		{
			if(st.isCompleted() || cond == 0 || cond == 1 || cond == 2 || cond == 3 || cond == 6)
				htmltext = "2-nc.htm";
			else if(cond == 4)
			{
				htmltext = "2-1.htm";
				st.getPlayer().sendPacket(new TutorialShowHtmlPacket(TutorialShowHtmlPacket.LARGE_WINDOW, "..\\L2Text\\QT_002_Guide_01.htm"));	
				//st.showTutorialHTML(TutorialShowHtmlPacket.LARGE_WINDOW, "..\\L2Text\\QT_002_Guide_01.htm");
			}
			else if(cond == 5)
				htmltext = "2-3.htm";
		}

		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if(st.getCond() == 2 && npcId == crow)
		{
			st.playSound(SOUND_MIDDLE);
			st.setCond(3);
		}
		else if(st.getCond() == 5 && npcId == crow)
		{
			st.setCond(6);
			st.playSound(SOUND_MIDDLE);
		}
		return null;
	}
	
	@Override
	public boolean checkStartCondition(Player player)
	{
		QuestState wd = player.getQuestState(_10321_QualificationsOfTheSeeker.class);
		if(wd == null)
			return false;
		return player.getLevel() <= 20 && wd.isCompleted();
	}		
}
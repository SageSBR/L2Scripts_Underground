package quests;

import l2s.gameserver.instancemanager.QuestManager;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

import java.util.List;

public class _245_ComeToMe extends Quest implements ScriptFile
{
	private static final int FERRIS = 30847;
	
	private static final int SNAKE = 21111;
	private static final int SWAMP = 21110;
	
	private static final int ORCHAMES = 21112;
	private static final int ORC_SNIPER = 21113;
	private static final int SHAMAN_ORC = 21115;
	private static final int VLADUK_ORC = 21116;

	private static final int ASHES = 30322;
	private static final int CRYSTAL_EXP = 30323;

	public _245_ComeToMe()
	{
		super(false);
		addStartNpc(FERRIS);
		addTalkId(FERRIS);
		
		addKillId(SNAKE);
		addKillId(SWAMP);

		addKillId(ORCHAMES);
		addKillId(ORC_SNIPER);
		addKillId(SHAMAN_ORC);		
		addKillId(VLADUK_ORC);	
		
		addQuestItem(ASHES);
		addQuestItem(CRYSTAL_EXP);
		addLevelCheck(70, 75);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();
		
		if(event.equalsIgnoreCase("30847-04.htm"))
		{
			if(!checkSponsorNear(player, 100))
				return "30847-08.htm";
			st.setCond(1);
			SetCondSponsor(player, true);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);			
		}

		if(event.equalsIgnoreCase("30847-07.htm"))
		{
			if(!checkSponsorNear(player, 100))
				return "30847-08.htm";
			st.takeItems(ASHES, -1);
			st.setCond(3);
			player.setVar("isFinishAlready", true);
		}

		if(event.equalsIgnoreCase("crystals"))
		{
			if(!checkApperiensNear(player))
				return "30847-12.htm";
			else if(player.getInventory().getCountOf(1461) >= 100)
			{
				st.takeItems(1461, 100);
				condOfApperiens(player, true, 4);
				return "30847-13.htm";
			}
			else
				return "30847-14.htm";
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		int id = st.getState();
		Player player = st.getPlayer();

		if(id == COMPLETED)
			return "30847-03.htm";

		if(npcId == FERRIS)
		{
			if(cond == 0)
			{
				if(player.getApprentice() != 0)
				{
					if(!checkApperiensNear(player))
						return "30847-12.htm";
					else if(condOfApperiens(player, false, 0) == 3)
						return "30847-11.htm";
					else
						return "30847-15.htm";
				}
				else
				{
					if (player.getVar("isFinishAlready") != null)
					{
						st.setState(COMPLETED);
						return "30847-03.htm";
					}
					else if(!checkSponsorNear(player, 100))
						return "30847-08.htm";
					else if(checkStartCondition(player))
						return "30847-01.htm";
					else
						return "30847-02.htm";
				}
			}
			else if(cond == 1)
				return "30847-05.htm";
			else if(cond == 2)
				return "30847-06.htm";
			else if(cond == 3)
			{
				if (player.getVar("isSponsorTalk") == null)
					return "30847-07.htm";
				else if (!checkSponsorNear(player, 100))
					return "30847-08.htm";
				else
					return "30847-10.htm";
			}
			else if(cond == 4)
				return "30847-17.htm";
			if(cond == 5)
			{
				if (!checkSponsorNear(player, 100))
					return "30847-08.htm";
				st.takeItems(CRYSTAL_EXP, -1);
				if(player.getClan() != null && player.getClan().getLevel() >= 5)
					player.getClan().incReputation(1000, false, "_245_ComeToMe");
				st.giveItems(30383, 1, false); //ring
				st.addExpAndSp(2018733, 484);
				st.setState(COMPLETED);
				st.exitCurrentQuest(false);
				player.setVar("isFinishAlready", true);
				SetCondSponsor(player, false);
				return "30847-19.htm";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int _id = npc.getNpcId();
		if(st.getCond() == 1)
		{
			if(_id == SNAKE || _id == SWAMP)
				st.rollAndGive(ASHES, 1, 25);
			if(st.getQuestItemsCount(ASHES) >= 15)	
				st.setCond(2);
		}	

		if(st.getCond() == 4 && checkSponsorNear(st.getPlayer(), 1000))
		{
			if(_id == ORCHAMES || _id == ORC_SNIPER || _id == SHAMAN_ORC || _id == VLADUK_ORC)
				st.rollAndGive(CRYSTAL_EXP, 1, 100);
			if(st.getQuestItemsCount(CRYSTAL_EXP) >= 12)	
				st.setCond(5);
		}		
		return null;	
			
	}	

	private static void SetCondSponsor(Player player, boolean set)
	{
		Player sponsorPlayer = World.getPlayer(player.getSponsor());
		if(sponsorPlayer == null || !sponsorPlayer.isOnline())
			return;
		Quest quest = QuestManager.getQuest2("_245_ComeToMe");
		QuestState qs245 = sponsorPlayer.getQuestState(quest.getName());
	    if (set)
		{
			if(qs245 != null)
				qs245.abortQuest();
			qs245 = quest.newQuestState(sponsorPlayer, Quest.STARTED);
			qs245.setState(Quest.STARTED);
		}
		else
		{
			if(qs245 != null)
				qs245.abortQuest();
			qs245 = quest.newQuestState(sponsorPlayer, Quest.COMPLETED);
			qs245.setState(Quest.COMPLETED);
		}
	}
	
	public boolean checkSponsorNear(Player player, int radius)
	{
		List<GameObject> objects = World.getAroundObjects(player, radius, radius);
		for(GameObject object : objects)
		{
			player.getDistance(object);
			if(object instanceof Player)
			{
				if (player.getSponsor() == object.getObjectId())
				return true;
			}
		}
		return false;
	}

	public boolean checkApperiensNear(Player player)
	{
		Player aperriensPlayer = World.getPlayer(player.getApprentice());
		if(aperriensPlayer == null || !aperriensPlayer.isOnline())
			return false;

		List<GameObject> objects = World.getAroundObjects(player, 100, 100);
		for(GameObject object : objects)
		{
			if(object instanceof Player)
			{
				if (player.getApprentice() == object.getObjectId())
					return true;
			}
		}
		return false;
	}

	private static int condOfApperiens(Player player, boolean setCond, int cond)
	{
		Player aperriensPlayer = World.getPlayer(player.getApprentice());
		if(aperriensPlayer == null || !aperriensPlayer.isOnline())
			return 0;

		Quest quest = QuestManager.getQuest2("_245_ComeToMe");
		QuestState qs245 = aperriensPlayer.getQuestState(quest.getName());
		if(qs245 == null)
			return 0;
		if(setCond)
		{
			qs245.setCond(cond);
			if(aperriensPlayer.getVar("isSponsorTalk") != null)
				aperriensPlayer.unsetVar("isSponsorTalk");
		}
		return qs245.getCond();
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

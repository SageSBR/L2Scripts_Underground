package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.utils.NpcUtils;

import java.util.ArrayList;

//By Evil_dnk

public class _10771_Volatile_Power extends Quest implements ScriptFile
{
	// NPC's
	private static final int YANSEN = 30484;
	private static final int HIDENCRUSHER = 33990;
	private static ArrayList<NpcInstance> eaters = new ArrayList<NpcInstance>();
	// Mobs
	private static final int FRAGMENTEATER = 27533;
	// Item's
	private static final int DOORCOIN = 37045;
	private static final int NORMALSHINEFRAGMENT = 39714;
	private static final int SHINEMYSTFRAGMENT = 39713;
	private static final int ENCHANTARMOR = 23420;


	public _10771_Volatile_Power()
	{
		super(PARTY_ONE);
		addStartNpc(YANSEN);
		addTalkId(YANSEN);
		addTalkId(HIDENCRUSHER);
		addQuestCompletedCheck(_10770_InSearchoftheGrail.class);
		addLevelCheck(44);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30484-5.htm"))
		{
			st.setCond(1);
			st.giveItems(SHINEMYSTFRAGMENT, 20, false);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("30484-8.htm"))
		{
			st.giveItems(DOORCOIN, 20);
			st.takeItems(NORMALSHINEFRAGMENT, -1);
			st.takeItems(SHINEMYSTFRAGMENT, -1);
			st.giveItems(ENCHANTARMOR, 5);
			st.getPlayer().addExpAndSp(2708350, 650);
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
			case YANSEN:
				if(cond == 0)
				{
					if(checkStartCondition(st.getPlayer()))
						htmltext = "30484-1.htm";
					else
						htmltext = "30484-0.htm";
				}
				else if (cond == 1 || cond == 2)
					htmltext = "30484-6.htm";
				else if (cond == 3)
					htmltext = "30484-7.htm";
			break;

			case HIDENCRUSHER:
			 if (cond == 1 || cond == 2)
			{
				if(eaters != null)
					eaters.clear();
				int count = Rnd.get(1, 3);
				st.takeItems(SHINEMYSTFRAGMENT, count);
				Functions.npcSay(npc, NpcString.THE_CRUSHER_IS_ACTIVATED);

				ThreadPoolManager.getInstance().schedule(() ->
				{
					st.giveItems(NORMALSHINEFRAGMENT, count, false);
					Functions.npcSay(npc, NpcString.S1_OBJECTS_DESTROYED, String.valueOf(count));
					if(st.getQuestItemsCount(NORMALSHINEFRAGMENT) >= 20)
						st.setCond(3);
					ThreadPoolManager.getInstance().schedule(() ->
					{
						Functions.npcSay(npc, NpcString.THE_DEVICE_RAN_OUT_OF_MAGIC);
						st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.THE_DEVICE_RAN_OUT_OF_MAGIC_TRY_LOOKING_FOR_ANOTHER, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
						npc.doDie(null);
						npc.endDecayTask();
						if (cond == 1)
							st.setCond(2);
					}, 5000L);

				}, 2500L);


				eaters.add(st.addSpawn(FRAGMENTEATER, st.getPlayer().getX() + Rnd.get(100, 200), st.getPlayer().getY() + Rnd.get(100, 200), st.getPlayer().getZ(), 0, 0, 180000));
				eaters.add(st.addSpawn(FRAGMENTEATER, st.getPlayer().getX() + Rnd.get(100, 200), st.getPlayer().getY() + Rnd.get(100, 200), st.getPlayer().getZ(), 0, 0, 180000));
				eaters.add(st.addSpawn(FRAGMENTEATER, st.getPlayer().getX() + Rnd.get(100, 200), st.getPlayer().getY() + Rnd.get(100, 200), st.getPlayer().getZ(), 0, 0, 180000));
				for (NpcInstance eater : eaters)
				{
					Functions.npcSay(eater, NpcString.KILL_THEM_DONT_LET_THEM_GET_AWAY_WITH_THE_FRAGMENT);
					eater.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 1000);
				}
				return null;
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
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

public class _10776_TheWrathoftheGiants extends Quest implements ScriptFile
{
	// NPC's
	private static final int BELKATI = 30485;
	private static final int DEVICE = 32366;
	private static final int NARSID = 33992;
	private static ArrayList<NpcInstance> fighters = new ArrayList<NpcInstance>();
	// Mobs
	private static final int ENRAGEDNARSID = 27534;
	// Item's
	private static final int DOORCOIN = 37045;

	private static final int REGENERATIONCORE = 39716;
	private static final int ENCHANTARMOR = 23420;

	public _10776_TheWrathoftheGiants()
	{
		super(PARTY_ONE);
		addStartNpc(BELKATI);
		addTalkId(DEVICE);
		addTalkId(NARSID);
		addKillId(ENRAGEDNARSID);
		addQuestCompletedCheck(_10775_InSearchofanAncientGiant.class);
		addLevelCheck(48);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30485-3.htm"))
		{
			st.setCond(1);
			st.giveItems(REGENERATIONCORE, 1, false);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("30485-7.htm"))
		{
			st.giveItems(DOORCOIN, 20);
			st.giveItems(ENCHANTARMOR, 4);
			st.getPlayer().addExpAndSp(4838400, 1161);
			st.exitCurrentQuest(false);
		}
		else if(event.equalsIgnoreCase("startdevice"))
		{
			npc.setTitle(st.getPlayer().getName());
			npc.broadcastCharInfoImpl();
			st.setCond(2);
			st.addSpawn(NARSID, 16422, 113281, -9064, 80000);
			st.startQuestTimer("despawnnarsid", 120000, npc);
			ThreadPoolManager.getInstance().schedule(() ->
			{
				npc.setTitle(null);
				npc.broadcastCharInfoImpl();
			}, 80000L);
			return null;
		}
		else if(event.equalsIgnoreCase("despawnnarsid"))
		{
		 if (st.getCond() == 2 || st.getCond() == 3)
				st.setCond(1);
			return null;
		}
		else if(event.equalsIgnoreCase("beginfight"))
		{
			if(fighters != null)
				fighters.clear();
			fighters.add(st.addSpawn(27535, npc.getX() + Rnd.get(50, 100), npc.getY() + Rnd.get(50, 100), npc.getZ(), 0, 0, 180000));
			fighters.add(st.addSpawn(27535, npc.getX() + Rnd.get(50, 100), npc.getY() + Rnd.get(50, 100), npc.getZ(), 0, 0, 180000));
			fighters.add(st.addSpawn(ENRAGEDNARSID, 16422, 113281, -9064, 0, 0, 180000));
			for (NpcInstance fighter : fighters)
			{
				if (fighter.getNpcId() == ENRAGEDNARSID)
				{
					Functions.npcSay(fighter, NpcString.CURSED_ERTHEIA_I_WILL_KILL_YOU_ALL);
				}
				fighter.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 1000);
			}
			npc.deleteMe();
			st.setCond(3);
			return null;
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
			case BELKATI:
				if(cond == 0)
				{
					if(checkStartCondition(st.getPlayer()))
						htmltext = "30485-1.htm";
					else
						htmltext = "30485-0.htm";
				}
				else if (cond == 1 || cond == 2 || cond == 3)
					htmltext = "30485-4.htm";
				else if (cond == 4)
					htmltext = "30485-5.htm";
			break;

			case NARSID:
			 if (cond == 2)
				htmltext = "33992-1.htm";
			break;

			case DEVICE:
				if(cond == 1)
				{
					if(npc.getTitle().isEmpty())
						htmltext = "32366-1.htm";
					else
						htmltext = "32366-2.htm";
				}
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 3)
			st.setCond(4);
		return null;
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
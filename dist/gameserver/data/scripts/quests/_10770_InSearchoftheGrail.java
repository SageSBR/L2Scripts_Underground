package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10770_InSearchoftheGrail extends Quest implements ScriptFile
{
	// NPC's
	private static final int LOREIN = 30673;
	private static final int YANSEN = 30484;

	// Monster's
	private static final int[] MONSTERS = {20213, 20214, 20216, 20217, 21036};
	// Item's
	private static final int DOORCOIN = 37045;
	private static final int SHINEFRAGMENT = 39711;
	private static final int ENCHANTARMOR = 23420;
	private static final int ENCHANTWEAPON = 23414;

	public _10770_InSearchoftheGrail()
	{
		super(PARTY_ONE);
		addStartNpc(LOREIN);
		addTalkId(YANSEN);
		addKillId(MONSTERS);
		addLevelCheck(40);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30673-5.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("30484-2.htm"))
		{
			st.takeItems(SHINEFRAGMENT, -1);
		}
		else if(event.equalsIgnoreCase("30484-4.htm"))
		{
			st.giveItems(DOORCOIN, 30);
			st.giveItems(ENCHANTARMOR, 5);
			st.giveItems(ENCHANTWEAPON, 2);
			st.getPlayer().addExpAndSp(2342300, 562);
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
			case LOREIN:
				if(cond == 0)
				{
					if(checkStartCondition(st.getPlayer()))
						htmltext = "30673-1.htm";
					else
						htmltext = "30673-0.htm";
				}
				else if (cond == 1)
					htmltext = "30673-6.htm";
				else if (cond == 2)
					htmltext = "30673-7.htm";
			break;

			case YANSEN:
				if(cond == 2)
				{
					if(st.haveQuestItem(SHINEFRAGMENT))
						htmltext = "30484-1.htm";
					else
						htmltext = "30484-3.htm";
				}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int cond = st.getCond();
		if(cond == 1)
		{
		  st.rollAndGive(SHINEFRAGMENT, 1, 1, 30, 60);
			if (st.getQuestItemsCount(SHINEFRAGMENT) >= 30)
				st.setCond(2);
		}
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
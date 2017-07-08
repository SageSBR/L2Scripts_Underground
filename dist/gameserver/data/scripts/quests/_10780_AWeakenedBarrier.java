package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _10780_AWeakenedBarrier extends Quest implements ScriptFile
{
	// NPC's
	private static final int ENDI = 33845;
	private static final int BAIKON = 33846;

	// Monster's
	private static final int[] MONSTERS = {20555, 20558, 23305, 23306, 23307, 23308};
	private static final String Spores = "spores";

	// Item's
	private static final int DOORCOIN = 37045;
	private static final int ENCHANTARMOR = 23419;

	public _10780_AWeakenedBarrier()
	{
		super(PARTY_ONE);
		addStartNpc(ENDI);
		addTalkId(BAIKON);
		addKillId(MONSTERS);
		addKillNpcWithLog(1, 578011, Spores, 20, MONSTERS);
		addLevelCheck(52, 58);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33845-4.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("33846-2.htm"))
		{
			st.giveItems(DOORCOIN, 36);
			st.giveItems(ENCHANTARMOR, 5);
			st.getPlayer().addExpAndSp(3811500, 914);
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
			case ENDI:
				if(cond == 0)
				{
					if(checkStartCondition(st.getPlayer()))
						htmltext = "33845-1.htm";
					else
						htmltext = "33845-0.htm";
				}
				else if (cond == 1 || cond == 2)
					htmltext = "33845-5.htm";
			break;

			case BAIKON:
				if (cond == 2)
					htmltext = "33846-1.htm";
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int cond = st.getCond();
		if(cond == 1)
		{
			boolean doneKill = updateKill(npc, st);
			if(doneKill)
			{
				st.unset(Spores);
				st.setCond(2);
			}
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
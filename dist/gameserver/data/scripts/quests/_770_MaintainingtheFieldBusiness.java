package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _770_MaintainingtheFieldBusiness extends Quest implements ScriptFile
{
	// NPC's
	private static final int KAUN = 32646;

	// Monster's
	private static final int[] MONSTERS = { 22650, 22651, 22652, 22653, 22654, 22655};

	// Item's
	private static final int SIGNET = 36702;
	private static final int IDTAG = 36703;


	public _770_MaintainingtheFieldBusiness()
	{
		super(PARTY_ONE);
		addStartNpc(KAUN);
		addKillId(MONSTERS);
		addQuestItem(SIGNET, IDTAG);
		addLevelCheck(81, 84);
		addRaceCheck(true, true, true, true, true, true, false);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("contract_worker_q0770_06.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("contract_worker_q0770_09.htm"))
		{
			if(st.getQuestItemsCount(IDTAG) == 1)
			{
				st.giveItems(37394, 1);
				st.getPlayer().addExpAndSp(28240800, 6777);
			}
			else if(st.getQuestItemsCount(IDTAG) == 2)
			{
				st.giveItems(37394, 2);
				st.getPlayer().addExpAndSp(56481600,  13554);
			}
			else if(st.getQuestItemsCount(IDTAG) == 3)
			{
				st.giveItems(37394, 3);
				st.getPlayer().addExpAndSp(84722400, 20331);
			}
			else if(st.getQuestItemsCount(IDTAG) == 4)
			{
				st.giveItems(37394, 4);
				st.getPlayer().addExpAndSp(112963200, 27108);
			}
			else if(st.getQuestItemsCount(IDTAG) == 5)
			{
				st.giveItems(37394, 5);
				st.getPlayer().addExpAndSp(141204000, 33885);
			}
			else if(st.getQuestItemsCount(IDTAG) == 6)
			{
				st.giveItems(37394, 6);
				st.getPlayer().addExpAndSp(169444800, 40662);
			}
			else if(st.getQuestItemsCount(IDTAG) == 7)
			{
				st.giveItems(37394, 7);
				st.getPlayer().addExpAndSp(197685600, 47439);
			}
			else if(st.getQuestItemsCount(IDTAG) == 8)
			{
				st.giveItems(37394, 8);
				st.getPlayer().addExpAndSp(225926400, 54216);
			}
			else if(st.getQuestItemsCount(IDTAG) == 9)
			{
				st.giveItems(37394, 9);
				st.getPlayer().addExpAndSp(254167200, 60993);
			}
			st.takeItems(IDTAG, -1);
			st.takeItems(SIGNET, -1);
			st.exitCurrentQuest(this);
		}
		else if(event.equalsIgnoreCase("contract_worker_q0770_13.htm"))
		{
			st.giveItems(37394, 10);
			st.getPlayer().addExpAndSp(282408000, 67770);
			st.takeItems(IDTAG, -1);
			st.takeItems(SIGNET, -1);
			st.exitCurrentQuest(this);
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
				case KAUN:
					if(cond == 0)
					{
						QuestState wd = st.getPlayer().getQuestState(_771_PartakinginthePurificationCampaign.class);

						if(!st.isNowAvailable())
							htmltext = "contract_worker_q0770_11.htm";
						else if (wd != null && wd.getState() == STARTED)
							htmltext = "contract_worker_q0770_03.htm";
						else if(checkStartCondition(st.getPlayer()))
							htmltext = "contract_worker_q0770_01.htm";
						else if(st.getPlayer().getClassId().isOfRace(Race.ERTHEIA))
							htmltext = "contract_worker_q0770_02a.htm";
						else
							htmltext = "contract_worker_q0770_02.htm";
					}
					else if(cond == 1)
					{
						if(!st.haveQuestItem(IDTAG))
							htmltext = "contract_worker_q0770_06.htm";
						else
							htmltext = "contract_worker_q0770_08.htm";
					}
					else if(cond == 2)
						htmltext = "contract_worker_q0770_12.htm";
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
			st.rollAndGive(SIGNET, 1, 1, 50, 10);
			if(st.getQuestItemsCount(SIGNET) >= 50)
			{
				st.takeItems(SIGNET, 50);
				st.giveItems(IDTAG, 1);
			}
			if(st.getQuestItemsCount(IDTAG) >= 10)
			{
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
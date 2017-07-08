package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk

public class _769_TheTruthConcealedinBeauty extends Quest implements ScriptFile
{
	// NPC's
	private static final int ANKUMI = 32741;

	// Monster's
	private static final int[] MONSTERS = { 22768, 22769, 22770, 22771, 22772, 22773, 22774 };

	// Item's
	private static final int LIZARDBLOOD = 36691;
	private static final int BLOODSTONE = 36697;


	public _769_TheTruthConcealedinBeauty()
	{
		super(PARTY_ONE);
		addStartNpc(ANKUMI);
		addKillId(MONSTERS);
		addQuestItem(LIZARDBLOOD, BLOODSTONE);
		addLevelCheck(81, 84);
		addRaceCheck(true, true, true, false, false, true, false);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("32741-4.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("32741-9.htm"))
		{
			st.takeItems(LIZARDBLOOD, -1);
			if(st.getQuestItemsCount(BLOODSTONE) < 100)
			{
				st.giveItems(37394, 1);
				st.getPlayer().addExpAndSp(28240800, 6777);
			}
			else if(st.getQuestItemsCount(BLOODSTONE) >= 100 && st.getQuestItemsCount(BLOODSTONE) <= 899)
			{
				st.giveItems(37394, 2);
				st.getPlayer().addExpAndSp(84722400,  20333);
			}
			else if(st.getQuestItemsCount(BLOODSTONE) >= 900)
			{
				st.giveItems(37394, 10);
				st.getPlayer().addExpAndSp(536575200, 128778);
			}
			st.takeItems(BLOODSTONE, -1);
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
				case ANKUMI:
					if(cond == 0)
					{
						if(!st.isNowAvailable())
							htmltext = "32741-10.htm";
						else if(checkStartCondition(st.getPlayer()))
							htmltext = "32741-1.htm";
						else
							htmltext = "32741-0.htm";
					}
					else if(cond == 1)
						htmltext = "32741-5.htm";
					else if(cond == 2 || cond == 3)
						htmltext = "32741-6.htm";
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
			st.rollAndGive(LIZARDBLOOD, 1, 1, 50, 10);
			if(st.getQuestItemsCount(LIZARDBLOOD) >= 50)
			{
				st.setCond(2);
			}
		}
		else if(cond == 2)
		{
			st.rollAndGive(BLOODSTONE, 1, 1, 900, 20);
			if(st.getQuestItemsCount(BLOODSTONE) >= 900)
				st.setCond(3);
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
package quests;

import org.apache.commons.lang3.ArrayUtils;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Iqman
 */
public class _778_OperationRoaringFlame extends Quest implements ScriptFile
{
	//npc
	private static final int BURINU = 33840;
	//mob
	private static final int[] MOBS = { 23314, 23315, 23316, 23317, 23318, 23319, 23320, 23321, 23322 };
	//q items
	private static final int TURAKANS_SECRET_LETTER = 36682;
	private static final int BROKEN_WEAPON_FRAGMENT = 36683;
	//rewards
	private static final int SCROLL_OF_ESCAPE_RAIDERS_CROSSROAD = 37017;
	private static final int ELIXIR_OF_BLESSING = 32316;
	private static final int ELIXIR_OF_MIND = 30358;
	private static final int ELIXIR_OF_LIFE = 30357;
	private static final int ELMORE_NOBLE_BOX = 37022;
	private static final int ENERGY_OF_DESTRUCTION = 35562;
	
	public _778_OperationRoaringFlame()
	{
		super(false);
		addStartNpc(BURINU);
		addTalkId(BURINU);
		addKillId(MOBS);
		addQuestItem(TURAKANS_SECRET_LETTER);
		addQuestItem(BROKEN_WEAPON_FRAGMENT);	
		
		addLevelCheck(99);
		addQuestCompletedCheck(_10445_AnImpendingThreat.class);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("accept.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		
		if(npcId == BURINU)
		{
			QuestState st2 = st.getPlayer().getQuestState(_10445_AnImpendingThreat.class);
			if(st2 == null || !st.isCompleted())
				htmltext = "You need to pass An Impending Thread quest first!";
			if(!st.isNowAvailable())
				htmltext = "no_aval.htm";
			else if(st.getPlayer().getLevel() < 99)
				htmltext = "no_level.htm";
			else if(cond == 0)
				htmltext = "1.htm";			
			else if(cond == 2)
			{
				st.takeItems(TURAKANS_SECRET_LETTER, -1);
				st.takeItems(BROKEN_WEAPON_FRAGMENT, -1);
				st.giveItems(SCROLL_OF_ESCAPE_RAIDERS_CROSSROAD, 1);
				st.giveItems(ELIXIR_OF_BLESSING, 5);
				st.giveItems(ELIXIR_OF_MIND, 5);
				st.giveItems(ELIXIR_OF_LIFE, 5);
				st.giveItems(ELMORE_NOBLE_BOX, 1);
				st.giveItems(ENERGY_OF_DESTRUCTION, 1);
				st.exitCurrentQuest(this);
				st.playSound(SOUND_FINISH);
				htmltext = "endquest.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if(st.getCond() != 1)
			return null;
			
		if(ArrayUtils.contains(MOBS, npcId) && st.getQuestItemsCount(TURAKANS_SECRET_LETTER) < 500L && Rnd.chance(70))
		{
			st.giveItems(TURAKANS_SECRET_LETTER, Rnd.get(1,3));
			st.playSound(SOUND_ITEMGET);
		}
		if(ArrayUtils.contains(MOBS, npcId) && st.getQuestItemsCount(BROKEN_WEAPON_FRAGMENT) < 500L)
		{
			st.giveItems(BROKEN_WEAPON_FRAGMENT, Rnd.get(1,3));
			st.playSound(SOUND_ITEMGET);
		}
		if(st.getQuestItemsCount(TURAKANS_SECRET_LETTER) >= 500L && st.getQuestItemsCount(BROKEN_WEAPON_FRAGMENT) >= 500L)
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
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
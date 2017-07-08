package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.model.items.ItemInstance;

/**
 * @author Iqman
 */
public class _10462_TemperaRustingBlade extends Quest implements ScriptFile
{
	//npc
	private static final int FLUTTER = 30677;
	//quest_items
	private static final int PRACTICE_WEAPON = 36717;
	private static final int PRACTICE_LIFE_STONE = 36718;
	private static final int PRACTICE_LIFE_GEMSTONE = 36719;
	//rewards
	private static final int LIFESTONE = 45929;
	private static final int GEMSTONE = 2132;
	
	public _10462_TemperaRustingBlade()
	{
		super(false);
		addStartNpc(FLUTTER);
		addTalkId(FLUTTER);
		addQuestItem(PRACTICE_WEAPON, PRACTICE_LIFE_STONE, PRACTICE_LIFE_GEMSTONE);
		
		addLevelCheck(46,52);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("head_blacksmith_flutter_q10462_06.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(PRACTICE_WEAPON, 1);
			st.giveItems(PRACTICE_LIFE_STONE, 1);
			st.giveItems(PRACTICE_LIFE_GEMSTONE, 20L);
		}
		
		else if(event.equalsIgnoreCase("head_blacksmith_flutter_q10462_09.htm"))
		{
			st.addExpAndSp(504210, 5042);
			st.giveItems(LIFESTONE, 1);	
			st.giveItems(GEMSTONE, 25);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		
		if(npcId == FLUTTER)
		{
			if(st.getPlayer().getLevel() < 46 || st.getPlayer().getLevel() > 52)
				htmltext = "head_blacksmith_flutter_q10462_02.htm";
			else if(cond == 0)
				htmltext = "head_blacksmith_flutter_q10462_01.htm";
			else if(cond == 1 && isAugmentWpn(st))
				htmltext = "head_blacksmith_flutter_q10462_08.htm";
			else if(cond == 1 && !isAugmentWpn(st))
				htmltext = "head_blacksmith_flutter_q10462_07.htm";
		}
		return htmltext;
	}
	
	@Override
	public void onAbort(QuestState st)
	{
		if(st.getQuestItemsCount(PRACTICE_WEAPON) > 0)
			st.takeItems(PRACTICE_WEAPON, -1);
		else if(st.getQuestItemsCount(PRACTICE_LIFE_STONE) > 0)
			st.takeItems(PRACTICE_LIFE_STONE, -1);
		else if(st.getQuestItemsCount(PRACTICE_LIFE_GEMSTONE) > 0)
			st.takeItems(PRACTICE_LIFE_GEMSTONE, -1);			
	}
	
	private static boolean isAugmentWpn(QuestState st)
	{
		ItemInstance item = st.getPlayer().getInventory().getItemByItemId(PRACTICE_WEAPON);
		if(item != null && item.isAugmented())
			return true;
		return false;	
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
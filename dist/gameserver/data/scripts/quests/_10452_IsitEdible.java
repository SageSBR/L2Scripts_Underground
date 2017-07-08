package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Iqman
 */
public class _10452_IsitEdible extends Quest implements ScriptFile
{
	//npc
	private static final int HARRY = 32743;
	//mob
	private static final int[] MOBS = { 18864, 18865, 18868 };
	//q items
	private static final int FANTASY_SPORE = 36688;
	private static final int STICKY_SPORE = 36689;
	private static final int LEAF_POUCH = 36690;
	//rewards
	private static final int FANTASY_MUSHROOM = 18864;
	private static final int STICKY_MUSHROOM = 18865;
	private static final int VITALITY_PLANT = 18868;
	
	public _10452_IsitEdible()
	{
		super(false);
		addStartNpc(HARRY);
		addTalkId(HARRY);
		addKillId(MOBS);
		addQuestItem(FANTASY_SPORE);
		addQuestItem(STICKY_SPORE);	
		addQuestItem(LEAF_POUCH);			
		
		addLevelCheck(81);
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
		
		else if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.takeItems(FANTASY_SPORE, -1);
			st.takeItems(STICKY_SPORE, -1);
			st.takeItems(LEAF_POUCH, -1);
			st.addExpAndSp(14120400, 141204);
			st.giveItems(57, 299940L);
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
		
		if(npcId == HARRY)
		{
			if(st.getPlayer().getLevel() < 81)
				htmltext = "no_level.htm";
			else if(cond == 0)
				htmltext = "1.htm";			
			else if(cond == 1)
				htmltext = "3.htm";					
			else if(cond == 2)
				htmltext = "5.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() != 1)
			return null;

		if (npc.getNpcId() == FANTASY_MUSHROOM)
		{
			if (st.getQuestItemsCount(FANTASY_SPORE) == 0)
			{
				st.giveItems(FANTASY_SPORE, 1);
				st.playSound(SOUND_ITEMGET);
			}
		}
		else if (npc.getNpcId() == STICKY_MUSHROOM)
		{
			if (st.getQuestItemsCount(STICKY_SPORE) == 0)
			{
				st.giveItems(STICKY_SPORE, 1);
				st.playSound(SOUND_ITEMGET);
			}
		}
		else if (npc.getNpcId() == VITALITY_PLANT)
		{
			if (st.getQuestItemsCount(LEAF_POUCH) == 0)
			{
				st.giveItems(LEAF_POUCH, 1);
				st.playSound(SOUND_MIDDLE);
			}
		}
      
		if(st.getQuestItemsCount(36688) > 0 && st.getQuestItemsCount(STICKY_SPORE) > 0 && st.getQuestItemsCount(LEAF_POUCH) > 0)
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
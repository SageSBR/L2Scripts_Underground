package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Iqman
 */
public class _10393_ACluesComplete extends Quest implements ScriptFile
{
	//npc
	private static final int FLUTTER = 30677;
	private static final int KELIOS = 33862;
	//rewards
	private static final int STEEL_DOOR_GUILD_COIN = 37045;
	
	public _10393_ACluesComplete()
	{
		super(false);
		addTalkId(FLUTTER);
		addTalkId(KELIOS);
		addRaceCheck(true, true, true, true, true, true, false);
		addLevelCheck(46, 51);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		
		if(event.equalsIgnoreCase("accept.htm"))
		{
			st.setCond(2);
			st.giveItems(37026, 1);
			st.playSound(SOUND_MIDDLE);
		}		
		
		else if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_KEKROPUS_AT_LV_52, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
			st.takeItems(37026, 1);
			st.addExpAndSp(483840, 116);
			st.giveItems(STEEL_DOOR_GUILD_COIN, 15);
			st.giveItems(952, 4);
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
			if(cond == 1)
				htmltext = "1.htm";
		}
		else if(npcId == KELIOS)
		{
			if(cond == 2)
				htmltext = "1-1.htm";
		}
		return htmltext;
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
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
public class _10424_WhereIsBelus extends Quest implements ScriptFile
{
    //Квестовые персонажи
    private static final int ANDREI = 31292;
    private static final int LUKONES = 33852;

    //Награда за квест
    private static final int STEEL_DOOR_GUILD_COIN = 37045;
    private static final int SCROLL_ENCHANT_WEAPON_S_GRADE = 959;
	
	public _10424_WhereIsBelus()
	{
		super(false);
		addTalkId(ANDREI);
		addTalkId(LUKONES);
		addRaceCheck(true, true, true, true, true,true, false);
		addLevelCheck(76, 80);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		
		if(event.equalsIgnoreCase("accept.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
			st.giveItems(37035, 1);
		}				
		
		else if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_KEKROPUS_AT_LV_81, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
			st.addExpAndSp(1277640, 306);
			st.giveItems(STEEL_DOOR_GUILD_COIN, 182);
			st.giveItems(SCROLL_ENCHANT_WEAPON_S_GRADE, 2);
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
		
		if(npcId == ANDREI)
		{
			if(cond == 1)
				htmltext = "captain_mathias_q10408_01.htm";
			else if(cond == 2)
				htmltext = "accept.htm";
		}
		else if(npcId == LUKONES)
		{
			if(cond == 2)
				htmltext = "tracker_dokara_q10408_01.htm";
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
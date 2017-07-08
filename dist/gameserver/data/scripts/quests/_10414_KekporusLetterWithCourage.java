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
public class _10414_KekporusLetterWithCourage extends Quest implements ScriptFile
{
    //Квестовые персонажи
    private static final int ANDREI = 31292;
    private static final int JANIT = 33851;

    //Награда за квест
    private static final int STEEL_DOOR_GUILD_COIN = 37045;
    private static final int SCROLL_ENCHANT_WEAPON_A_GRADE = 729;
	
	public _10414_KekporusLetterWithCourage()
	{
		super(false);
		addTalkId(ANDREI);
		addTalkId(JANIT);
		addRaceCheck(true, true, true, true, true, true, false);
		addLevelCheck(70, 75);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		
		if(event.equalsIgnoreCase("accept.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
			st.giveItems(37032, 1);
		}				
		
		else if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_KEKROPUS_AT_LV_76, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
			st.addExpAndSp(1088640, 261);
			st.giveItems(STEEL_DOOR_GUILD_COIN, 123);
			st.giveItems(SCROLL_ENCHANT_WEAPON_A_GRADE, 5);
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
		else if(npcId == JANIT)
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
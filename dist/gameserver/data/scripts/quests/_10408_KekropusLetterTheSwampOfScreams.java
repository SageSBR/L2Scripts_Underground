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
public class _10408_KekropusLetterTheSwampOfScreams extends Quest implements ScriptFile
{
    //Квестовые персонажи
    private static final int CAPTAIN_MATHIAS = 31340;
    private static final int TRACKER_DOKARA = 33847;

    //Награда за квест
    private static final int STEEL_DOOR_GUILD_COIN = 37045;
    private static final int SCROLL_ENCHANT_WEAPON_A_GRADE = 729;
	
	public _10408_KekropusLetterTheSwampOfScreams()
	{
		super(false);
		addTalkId(CAPTAIN_MATHIAS);
		addTalkId(TRACKER_DOKARA);
		addRaceCheck(true, true, true, true, true, true, false);
		addLevelCheck(65, 69);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		
		if(event.equalsIgnoreCase("accept.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
			st.giveItems(37030, 1);
		}				
		
		else if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_KEKROPUS_AT_LV_70, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
			st.addExpAndSp(942690, 226);
			st.giveItems(STEEL_DOOR_GUILD_COIN, 91);
			st.giveItems(SCROLL_ENCHANT_WEAPON_A_GRADE, 2);
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
		
		if(npcId == CAPTAIN_MATHIAS)
		{
			if(cond == 1)
				htmltext = "captain_mathias_q10408_01.htm";
			else if(cond == 2)
				htmltext = "accept.htm";
		}
		else if(npcId == TRACKER_DOKARA)
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
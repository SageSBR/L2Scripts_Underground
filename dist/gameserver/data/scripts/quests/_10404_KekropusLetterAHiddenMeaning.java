package quests;

import org.apache.commons.lang3.ArrayUtils;

import l2s.commons.util.Rnd;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.listener.actor.player.OnLevelChangeListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.NpcHtmlMessagePacket;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Iqman
 */
public class _10404_KekropusLetterAHiddenMeaning extends Quest implements ScriptFile
{
    //Квестовые персонажи
    private static final int PETERSON = 33864;
    private static final int SHUVANN = 33867;

    //Награда за квест
    private static final int STEEL_DOOR_GUILD_COIN = 37045;
    private static final int SCROLL_ENCHANT_ARMOR_A_GRADE = 730;
	
	public _10404_KekropusLetterAHiddenMeaning()
	{
		super(false);
		addTalkId(PETERSON);
		addTalkId(SHUVANN);
		addRaceCheck(true, true, true, true, true,true, false);
		addLevelCheck(61, 64);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		
		if(event.equalsIgnoreCase("accept.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
			st.giveItems(37029, 1);
		}				
		
		else if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_KEKROPUS_AT_LV_65, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
			st.addExpAndSp(807240, 193);
			st.giveItems(STEEL_DOOR_GUILD_COIN, 71L);
			st.giveItems(SCROLL_ENCHANT_ARMOR_A_GRADE, 1);
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
		
		if(npcId == PETERSON)
		{
			if(cond == 1)
				htmltext = "1.htm";
			else if(cond == 2)
				htmltext = "10.htm";
		}
		else if(npcId == SHUVANN)
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
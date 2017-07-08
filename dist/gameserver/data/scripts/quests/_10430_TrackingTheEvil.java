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
public class _10430_TrackingTheEvil extends Quest implements ScriptFile
{
    //Квестовые персонажи
    private static final int VISHOTSKI = 31981;
    private static final int IOKEL = 33868;

    //Награда за квест
    private static final int STEEL_DOOR_GUILD_COIN = 37045;
    private static final int SCROLL_ENCHANT_WEAPON_S_GRADE = 959;
	private static final int SCROLL_ENCHANT_ARMOR_S_GRADE = 960;
	
	public _10430_TrackingTheEvil()
	{
		super(false);
		addTalkId(VISHOTSKI);
		addTalkId(IOKEL);
		
		addLevelCheck(81, 84);
		addClassIdCheck(88, 90, 91, 93, 99, 100, 101, 106, 107, 108, 114, 131, 132, 133, 136);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		
		if(event.equalsIgnoreCase("accept.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
			st.giveItems(37036, 1);
		}				
		
		else if(event.equalsIgnoreCase("rud1"))
		{
			st.giveItems(9546, 15);
			
			return onEvent("endquest.htm", st, npc);
		}

		else if(event.equalsIgnoreCase("rud2"))
		{
			st.giveItems(9547, 15);
			
			return onEvent("endquest.htm", st, npc);
		}

		else if(event.equalsIgnoreCase("rud3"))
		{
			st.giveItems(9548, 15);
			return onEvent("endquest.htm", st, npc);
		}

		else if(event.equalsIgnoreCase("rud4"))
		{
			st.giveItems(9549, 15);
			return onEvent("endquest.htm", st, npc);
		}

		else if(event.equalsIgnoreCase("rud5"))
		{
			st.giveItems(9550, 15);
			return onEvent("endquest.htm", st, npc);
		}

		else if(event.equalsIgnoreCase("rud6"))
		{
			st.giveItems(9551, 15);
			return onEvent("endquest.htm", st, npc);
		}
		else if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.YOU_HAVE_FINISHED_ALL_OF_KEKROPUS_LETTERS_GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_LETTERS_FROM_A_MINSTREL_AT_LV_85, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
			st.addExpAndSp(1412040, 338);
			st.giveItems(STEEL_DOOR_GUILD_COIN, 235);
			st.giveItems(SCROLL_ENCHANT_WEAPON_S_GRADE, 1);
			st.giveItems(SCROLL_ENCHANT_ARMOR_S_GRADE, 10);
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
		
		if(npcId == VISHOTSKI)
		{
			if(cond == 1)
				htmltext = "captain_mathias_q10408_01.htm";
			else if(cond == 2)
				htmltext = "accept.htm";
		}
		else if(npcId == IOKEL)
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
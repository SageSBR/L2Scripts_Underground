package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _10409_ASuspiciousVagabondInTheSwamp extends Quest implements ScriptFile
{
    //Квестовые персонажи
    private static final int TRACKER_DOKARA = 33847;
    private static final int SUSPICIOUS_VAGABOND = 33848;
    //Награда
    private static final int Enchant_Armor_A = 730;
    private static final int Steel_Coin = 37045;
	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10409_ASuspiciousVagabondInTheSwamp()
	{
		super(false);
		addStartNpc(TRACKER_DOKARA);
		addTalkId(TRACKER_DOKARA, SUSPICIOUS_VAGABOND);
		addRaceCheck(true, true, true, true, true, true, false);
		addLevelCheck(65, 70);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("accept"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			return "4.htm";
		}
		if(event.equalsIgnoreCase("endquest"))	
		{
			st.giveItems(Enchant_Armor_A, 3);
			st.giveItems(Steel_Coin, 3);
			st.addExpAndSp(942690, 226);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);		
			return "6.htm";	
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();

		if(npcId == TRACKER_DOKARA)
		{
			if(st.getCond() == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					return "1.htm";
				else
					return "Only 65-75 level may take this quest!";
			}
			if(st.getCond() == 1)
				return "4.htm";
			if(st.getCond() == 2)
				return "5.htm";
		}		
		else if(npc.getNpcId() == SUSPICIOUS_VAGABOND)
		{
			if(st.getCond() == 1)
			{
				st.setCond(2);
				return "2-1.htm";
			}	
		}	
		return "noquest";
	}
}
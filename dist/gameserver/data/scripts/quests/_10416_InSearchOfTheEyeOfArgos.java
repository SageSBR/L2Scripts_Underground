package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _10416_InSearchOfTheEyeOfArgos extends Quest implements ScriptFile
{
    private static final int Janitt = 33851;
    private static final int Eye = 31683;
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

	public _10416_InSearchOfTheEyeOfArgos()
	{
		super(false);
		addStartNpc(Janitt);
		addTalkId(Janitt);
		addTalkId(Eye);
		addLevelCheck(70, 75);
		addRaceCheck(true, true, true, true, true, true, false);
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
			st.giveItems(Enchant_Armor_A, 2);
			st.giveItems(Steel_Coin, 2);
			st.addExpAndSp(1088640, 261);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);		
			return "2-2.htm";	
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();

		if(npcId == Janitt)
		{
			if(st.getCond() == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					return "1.htm";
				else
					return "no_level.htm";
			}
			else if(st.getCond() == 1)
				return "5.htm";
		}		
		else if(npc.getNpcId() == Eye)
		{
			if(st.getCond() == 1)
				return "2-1.htm";
		}	
		return "noquest";
	}
}
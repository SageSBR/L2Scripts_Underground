package quests;

import l2s.gameserver.model.base.Race;
import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _10412_ASuspiciousVagabondInTheForest extends Quest implements ScriptFile
{
    private static final int Hatuba_Tracker = 33849;
    private static final int Suspicious_Vagabond_Mortally_Endangered = 33850;
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

	public _10412_ASuspiciousVagabondInTheForest()
	{
		super(false);
		addStartNpc(Hatuba_Tracker);
		addTalkId(Hatuba_Tracker, Suspicious_Vagabond_Mortally_Endangered);
		addLevelCheck(65, 70);
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

		if(npcId == Hatuba_Tracker)
		{
			if(st.getCond() == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					return "1.htm";
				else if (st.getPlayer().getRace() == Race.ERTHEIA)
					return "33849-0.htm";
				else
					return "Only 65-75 level may take this quest!";
			}
			else if(st.getCond() == 1)
				return "4.htm";
			else if(st.getCond() == 2)
				return "5.htm";
		}		
		else if(npc.getNpcId() == Suspicious_Vagabond_Mortally_Endangered)
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
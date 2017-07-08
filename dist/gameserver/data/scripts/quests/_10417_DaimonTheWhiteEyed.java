package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _10417_DaimonTheWhiteEyed extends Quest implements ScriptFile
{
    private static final int Eye = 31683;
	private static final int Janitt = 33851;
    //Награда
    private static final int Enchant_Armor_A = 730;
    private static final int Steel_Coin = 37045;
	//mob
	private static final int DAEMON = 27499;
	
	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10417_DaimonTheWhiteEyed()
	{
		super(false);
		addStartNpc(Eye);
		addTalkId(Eye);
		addTalkId(Janitt);
		addKillId(DAEMON);
		addLevelCheck(70, 75);
		addQuestCompletedCheck(_10416_InSearchOfTheEyeOfArgos.class);
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
			return "accept.htm";
		}
		else if(event.equalsIgnoreCase("cod3"))
		{
			st.setCond(3);
			st.playSound(SOUND_MIDDLE);
			return "gl1.htm";
		}			
		if(event.equalsIgnoreCase("endquest"))	
		{
			st.giveItems(Enchant_Armor_A, 5);
			st.giveItems(Steel_Coin, 26);
			st.addExpAndSp(2721600, 653);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);		
			return "endquest.htm";	
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();

		if(npcId == Eye)
		{
			if(st.getCond() == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					return "1.htm";
				else
					return "no_level.htm";
			}
			else if(st.getCond() == 1)
				return "4.htm";
			else if(st.getCond() == 2)
				return "5.htm";
		}			
		else if(npcId == Janitt)
		{
			if(st.getCond() < 3)
				return "1-1.htm";
			if(st.getCond() == 3)
				return "1-2.htm";
		}
		return "noquest";
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 1)
		{
			qs.playSound(SOUND_MIDDLE);
			qs.setCond(2);
		}
		return null;
	}	
}
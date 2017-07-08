package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author: Krash
 */
public class _10737_GrakonsWarehouse extends Quest implements ScriptFile
{
	// Npcs
	private static final int Grakon = 33947;
	private static final int Katalin = 33943;
	private static final int Ayanthe = 33942;
	// Items
	private static final int Apprentice_Support_Box = 39520;
	private static final int Apprentice_Adventurer_Staff = 7816;
	private static final int Apprentice_Adventurer_Fists = 7819;
	
	public _10737_GrakonsWarehouse()
	{
		super(false);
		addStartNpc(Katalin, Ayanthe);
		addTalkId(Katalin, Ayanthe, Grakon);
		addQuestItem(Apprentice_Support_Box);
		addLevelCheck(5, 20);
	}
	
	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		String htmltext = event;
		
		switch(event)
		{
			case "quest_fighter_cont":
				qs.setState(STARTED);
				qs.setCond(1);
				qs.playSound(SOUND_ACCEPT);
				qs.giveItems(Apprentice_Support_Box, 1);
				htmltext = "33943-3.htm";
				break;
			
			case "quest_wizard_cont":
				qs.setState(STARTED);
				qs.setCond(1);
				qs.playSound(SOUND_ACCEPT);
				qs.giveItems(Apprentice_Support_Box, 1);
				htmltext = "33242-3.htm";
				break;
			
			case "qet_rev":
				if(qs.getPlayer().getClassId().getId() == 182) // Ertheia Fighter
				{
					qs.giveItems(57, 11000);
					qs.getPlayer().addExpAndSp(2650, 0);
					qs.giveItems(Apprentice_Adventurer_Fists, 1);
					htmltext = "33947-4.htm";
				}
				else if(qs.getPlayer().getClassId().getId() == 183) // Ertheia Wizard
				{
					qs.giveItems(57, 11000);
					qs.getPlayer().addExpAndSp(2650, 0);
					qs.giveItems(Apprentice_Adventurer_Staff, 1);
					htmltext = "33947-8.htm";
				}
				qs.takeItems(Apprentice_Support_Box, 1);
				qs.playSound(SOUND_FINISH);
				qs.exitCurrentQuest(false);
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(NpcInstance npc, QuestState qs)
	{
		if(qs.isCompleted())
		{
			return "completed";
		}
		String htmltext = "noquest";
		final int cond = qs.getCond();
		
		switch(npc.getNpcId())
		{
			case Grakon:
				if((cond == 1) && (qs.getQuestItemsCount(Apprentice_Support_Box) > 0))
				{
					if(qs.getPlayer().getClassId().getId() == 182) // Ertheia Fighter
					{
						htmltext = "33947-1.htm";
					}
					else if(qs.getPlayer().getClassId().getId() == 183) // Ertheia Wizard
					{
						htmltext = "33947-5.htm";
					}
				}
				break;
			
			case Katalin:
				if ((cond == 0) && (qs.getPlayer().getClassId().getId() == 182))
				{
					htmltext = "33943-1.htm";
				}
				break;
			
			case Ayanthe:
				if ((cond == 0) && (qs.getPlayer().getClassId().getId() == 183))
				{
					htmltext = "33942-1.htm";
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public void onLoad()
	{
		//
	}
	
	@Override
	public void onReload()
	{
		//
	}
	
	@Override
	public void onShutdown()
	{
		//
	}
}
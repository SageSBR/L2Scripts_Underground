package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author blacksmoke
 */
public class _10741_ADraughtForTheCold extends Quest implements ScriptFile
{
	// Npcs
	private static final int Sivanthe = 33951;
	private static final int Leira = 33952;
	// Monsters
	private static final int Honeybee = 23452;
	private static final int Kiku = 23453;
	private static final int RobustHoneybee = 23484;
	// Items
	private static final int EmptyHoneyJar = 39527;
	private static final int SweetHoney = 39528;
	private static final int NutritiousMeat = 39529;
	
	public _10741_ADraughtForTheCold()
	{
		super(false);
		addStartNpc(Sivanthe);
		addTalkId(Sivanthe, Leira);
		addKillId(Honeybee, Kiku, RobustHoneybee);
		addQuestItem(EmptyHoneyJar, SweetHoney, NutritiousMeat);
		addLevelCheck(10, 20);
		addClassIdCheck(182, 183);
	}
	
	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		String htmltext = event;
		
		switch(event)
		{
			case "quest_ac":
				qs.setState(STARTED);
				qs.setCond(1);
				qs.giveItems(EmptyHoneyJar, 10);
				qs.playSound(SOUND_ACCEPT);
				htmltext = "33951-3.htm";
				break;
			
			case "qet_rev":
				qs.giveItems(57, 2000);
				qs.takeItems(SweetHoney, 10);
				qs.takeItems(NutritiousMeat, 10);
				htmltext = "33952-2.htm";
				qs.getPlayer().addExpAndSp(22973, 2);
				qs.exitCurrentQuest(false);
				qs.playSound(SOUND_FINISH);
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
			case Sivanthe:
				switch(cond)
				{
					case 0:
						if(checkStartCondition(qs.getPlayer()))
						{
							htmltext = "33951-1.htm";
						}
						break;
					
					case 1:
						htmltext = "33951-4.htm";
						break;
					
					default:
						htmltext = "noqu.htm";
						break;
				}
				break;
			
			case Leira:
				if(cond == 2)
				{
					htmltext = "33952-1.htm";
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 1)
		{
			switch(npc.getNpcId())
			{
				case Honeybee:
				case RobustHoneybee:
					if(qs.getQuestItemsCount(EmptyHoneyJar) > 0)
					{
						qs.takeItems(EmptyHoneyJar, 1);
						qs.giveItems(SweetHoney, 1);
						qs.playSound(SOUND_ITEMGET);
					}
					break;
				
				case Kiku:
					if(qs.getQuestItemsCount(NutritiousMeat) < 10)
					{
						qs.giveItems(NutritiousMeat, 1);
						qs.playSound(SOUND_ITEMGET);
					}
					break;
			}
			
			if((qs.getQuestItemsCount(SweetHoney) >= 10) && (qs.getQuestItemsCount(NutritiousMeat) >= 10))
			{
				qs.setCond(2);
				qs.playSound(SOUND_MIDDLE);
			}
		}
		return null;
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

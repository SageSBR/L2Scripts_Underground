package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Krash
 */
public class _10744_StrongerThanSteel extends Quest implements ScriptFile
{
	// Npcs
	private static final int Leira = 33952;
	private static final int Dolkin = 33954;
	// Monsters
	private static final int Treant = 23457;
	private static final int Leafie = 23458;
	// Drops
	private static final int Treant_leaf = 39532;
	private static final int Leafie_leaf = 39531;
	
	public _10744_StrongerThanSteel()
	{
		super(false);
		addStartNpc(Leira);
		addTalkId(Leira, Dolkin);
		addQuestItem(Treant_leaf, Leafie_leaf);
		addKillId(Treant, Leafie);
		addLevelCheck(15, 20);
		addClassIdCheck(182, 183);
	}
	
	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		String htmltext = event;
		
		switch (event)
		{
			case "quest_ac":
				qs.setState(STARTED);
				qs.setCond(1);
				qs.playSound(SOUND_ACCEPT);
				htmltext = "33952-3.htm";
				break;
			
			case "quest_middle":
				qs.setCond(2);
				qs.playSound(SOUND_MIDDLE);
				htmltext = "33954-2.htm";
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(NpcInstance npc, QuestState qs)
	{
		if (qs.isCompleted())
		{
			return "completed";
		}
		String htmltext = "noquest";
		final int cond = qs.getCond();
		
		switch (npc.getNpcId())
		{
			case Leira:
				switch (cond)
				{
					case 0:
						if (checkStartCondition(qs.getPlayer()))
						{
							htmltext = "33952-1.htm";
						}
						break;
					
					default:
						htmltext = "noqu.htm";
						break;
				}
				break;
			
			case Dolkin:
				switch (cond)
				{
					case 1:
						htmltext = "33954-1.htm";
						break;
					
					case 3:
						htmltext = "33954-3.htm";
						qs.takeItems(Treant_leaf, 20);
						qs.takeItems(Leafie_leaf, 15);
						qs.giveItems(57, 34000);
						qs.getPlayer().addExpAndSp(112001, 5);
						qs.exitCurrentQuest(false);
						break;
				}
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if (qs.getCond() == 2)
		{
			switch (npc.getNpcId())
			{
				case Treant:
					if (qs.getQuestItemsCount(Treant_leaf) < 20)
					{
						qs.giveItems(Treant_leaf, 1);
						qs.playSound(SOUND_ITEMGET);
					}
					break;
				
				case Leafie:
					if (qs.getQuestItemsCount(Leafie_leaf) < 15)
					{
						qs.giveItems(Leafie_leaf, 1);
						qs.playSound(SOUND_ITEMGET);
					}
					break;
			}
			
			if ((qs.getQuestItemsCount(Treant_leaf) >= 20) && (qs.getQuestItemsCount(Leafie_leaf) >= 15))
			{
				qs.setCond(3);
				qs.playSound(SOUND_MIDDLE);
			}
		}
		return null;
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
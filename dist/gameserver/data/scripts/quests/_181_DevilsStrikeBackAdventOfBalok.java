package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _181_DevilsStrikeBackAdventOfBalok extends Quest implements ScriptFile
{
	// Npc
	private static final int FIOREN = 33044;
	// Monster
	private static final int BALOK = 29218;
	// Items
	private static final int CONTRACT = 17592;
	private static final int EAR = 17527;
	private static final int EWR = 17526;
	private static final int POUCH = 34861;
	
	public _181_DevilsStrikeBackAdventOfBalok()
	{
		super(false);
		addStartNpc(FIOREN);
		addTalkId(FIOREN);
		addKillId(BALOK);
		addQuestItem(CONTRACT);
		addLevelCheck(97, 99);
	}
	
	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		String htmltext = event;
		
		switch (event)
		{
			case "33044-06.htm":
				qs.setState(STARTED);
				qs.setCond(1);
				qs.playSound(SOUND_ACCEPT);
				break;
			
			case "reward":
				qs.addExpAndSp(886750000, 414855000);
				qs.giveItems(57, 37128000L);
				qs.playSound("SOUND_FINISH");
				qs.exitCurrentQuest(false);
				final int rnd = Rnd.get(2);
				switch (rnd)
				{
					case 0:
						qs.giveItems(EWR, 2);
						return "33044-09.htm";
						
					case 1:
						qs.giveItems(EAR, 2);
						return "33044-10.htm";
						
					case 2:
						qs.giveItems(POUCH, 2);
						return "33044-11.htm";
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(NpcInstance npc, QuestState qs)
	{
		if(qs.isCompleted())
			return "completed";

		String htmltext = "noquest";
		final int cond = qs.getCond();
		final Player player = qs.getPlayer();
		
		switch (qs.getState())
		{
			case CREATED:
				if(!checkStartCondition(player))
				{
					htmltext = "33044-02.htm";
					qs.exitCurrentQuest(true);
				}
				else
					htmltext = "33044-01.htm";
				break;
			case STARTED:
				if(cond == 1)
					htmltext = "33044-07.htm";
				else if(cond == 2)
					htmltext = "33044-08.htm";
				break;
			case COMPLETED:
				htmltext = "33044-03.htm";
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 1)
		{
			qs.setCond(2);
			qs.giveItems(CONTRACT, 1);
			qs.playSound(SOUND_MIDDLE);
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

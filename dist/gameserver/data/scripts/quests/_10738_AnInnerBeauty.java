package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author blacksmoke
 */
public class _10738_AnInnerBeauty extends Quest implements ScriptFile
{
	private static final int Grakon = 33947;
	private static final int Evna = 33935;
	private static final int GrakonsNote = 39521;
	
	public _10738_AnInnerBeauty()
	{
		super(false);
		addStartNpc(Grakon);
		addTalkId(Grakon, Evna);
		addQuestItem(GrakonsNote);
		addLevelCheck(5, 20);
		addClassIdCheck(182, 183);
		addQuestCompletedCheck(_10737_GrakonsWarehouse.class);
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
				qs.playSound(SOUND_ACCEPT);
				qs.giveItems(GrakonsNote, 1);
				htmltext = "33947-4.htm";
				break;
			
			case "qet_rev":
				qs.takeItems(GrakonsNote, 1);
				htmltext = "33935-3.htm";
				qs.giveItems(57, 12000);
				qs.getPlayer().addExpAndSp(2625, 1);
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
			case Grakon:
				if(checkStartCondition(qs.getPlayer()))
				{
					if(cond == 0)
					{
						htmltext = "33947-1.htm";
					}
					else if(cond == 1)
					{
						htmltext = "33947-4.htm";
					}
					else
					{
						htmltext = "noqu.htm";
					}
				}
				break;
			
			case Evna:
				if(checkStartCondition(qs.getPlayer()) && (cond == 1))
				{
					htmltext = "33935-1.htm";
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
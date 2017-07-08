package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.UsmVideo;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author blacksmoke
 */
public class _10732_AForeignLand extends Quest implements ScriptFile
{
	// Npcs
	private static final int Navari = 33931;
	private static final int Gereth = 33932;
	
	public _10732_AForeignLand()
	{
		super(false);
		addStartNpc(Navari);
		addTalkId(Navari);
		addTalkId(Gereth);
		addLevelCheck(1, 20);
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
				qs.getPlayer().sendPacket(UsmVideo.Q014.packet(qs.getPlayer()));
				htmltext = "33931-3.htm";
				break;
			
			case "qet_rev":
				qs.showTutorialClientHTML("QT_001_Radar_01");
				htmltext = "33932-2.htm";
				qs.giveItems(57, 3000);
				qs.getPlayer().addExpAndSp(75, 2);
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
			case Navari:
				if((cond == 0) && checkStartCondition(qs.getPlayer()))
				{
					htmltext = "33931-1.htm";
				}
				else if(cond == 1)
				{
					htmltext = "33931-4.htm";
				}
				break;
			
			case Gereth:
				if(cond == 0)
				{
					htmltext = "33932-3.htm";
				}
				else if(cond == 1)
				{
					htmltext = "33932-1.htm";
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
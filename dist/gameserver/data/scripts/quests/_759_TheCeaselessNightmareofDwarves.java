package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Iqman
 */
public class _759_TheCeaselessNightmareofDwarves extends Quest implements ScriptFile
{
	//npc
	private static final int DAICHIR = 30537;
	//mob
	private static final int TRASKEN = 29197;
	
	public _759_TheCeaselessNightmareofDwarves()
	{
		super(false);
		addStartNpc(DAICHIR);
		addTalkId(DAICHIR);
		addKillId(TRASKEN);
		
		addLevelCheck(98);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("daichir_head_priest_q759_03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("daichir_head_priest_q759_06.htm"))
		{
			calculateReward(st);
			st.exitCurrentQuest(this);
			st.playSound(SOUND_FINISH);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		if(npcId == DAICHIR)
		{
			if(cond == 0)
			{
				if(checkStartCondition(st.getPlayer()))
				{
					if(st.isNowAvailable())
						htmltext = "daichir_head_priest_q759_01.htm";
					else
						htmltext = "daichir_head_priest_q759_00a.htm";
				}
				else
					htmltext = "daichir_head_priest_q759_00.htm";
			}
			else if(cond == 1)
				htmltext = "daichir_head_priest_q759_04.htm";
			else if(cond == 2)
				htmltext = "daichir_head_priest_q759_05.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		int npcId = npc.getNpcId();
		if(qs == null)
			return null;
		if(qs.getState() != STARTED)
			return null;
		if(qs.getCond() != 1)
			return null;
		for(Player player : World.getAroundPlayers(npc, 1500, 200))
		{
			QuestState thisqs = player.getQuestState(_759_TheCeaselessNightmareofDwarves.class);
			if(thisqs == null || thisqs.getCond() != 1)
				continue;
			thisqs.setCond(2);	
			thisqs.playSound(SOUND_MIDDLE);
		}
		return null;
	}
	
	public void calculateReward(QuestState st)
	{
		int rndNum = Rnd.get(1, 18);
		int itemID = 0;
		switch (rndNum)
		{
			case 1: 
				itemID = 17623;
				break;
			case 2: 
				itemID = 35389;
				break;
			case 3: 
				itemID = 35390;
				break;
			case 4: 
				itemID = 35391;
				break;
			case 5: 
				itemID = 35392;
				break;
			case 6: 
				itemID = 35393;
				break;
			case 7: 
				itemID = 35394;
				break;
			case 8: 
				itemID = 35395;
				break;
			case 9: 
				itemID = 35396;
				break;
			case 10: 
				itemID = 35397;
				break;
			case 11: 
				itemID = 35398;
				break;
			case 12: 
				itemID = 35399;
				break;
			case 13: 
				itemID = 9552;
				break;
			case 14: 
				itemID = 9553;
				break;
			case 15: 
				itemID = 9554;
				break;
			case 16: 
				itemID = 9555;
				break;
			case 17: 
				itemID = 9556;
				break;
			case 18: 
				itemID = 9557;
		} 
		st.giveItems(itemID, 1);
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
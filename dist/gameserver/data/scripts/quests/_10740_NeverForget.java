package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author blacksmoke
 */
public class _10740_NeverForget extends Quest implements ScriptFile
{
	private static final int Sivanthe = 33951;
	private static final int RemembranceTower = 33989;
	
	private static final int UnnamedRelics = 39526;
	
	private static final int KeenFloato = 23449;
	private static final int Ratel = 23450;
	private static final int RobustRatel = 23451;
	private int Relics;
	
	public _10740_NeverForget()
	{
		super(false);
		addStartNpc(Sivanthe);
		addTalkId(Sivanthe, RemembranceTower);
		addQuestItem(UnnamedRelics);
		addKillId(KeenFloato, Ratel, RobustRatel);
		addLevelCheck(8, 20);
		addClassIdCheck(182, 183);
		// addQuestCompletedCheck(_10739_SupplyAndDemand.class);
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
				htmltext = "33951-3.htm";
				break;
			
			case "quest_cont":
				qs.takeItems(UnnamedRelics, 20);
				qs.setCond(3);
				htmltext = "33989-2.htm";
				qs.playSound(SOUND_MIDDLE);
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
					case 2:
						htmltext = "33951-5.htm";
						break;
					case 3:
						htmltext = "33951-6.htm";
						qs.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.CHECK_YOUR_EQUIPMENT_IN_YOUR_INVENTORY, 4500, ScreenMessageAlign.TOP_CENTER));
						qs.giveItems(57, 1600);
						qs.giveItems(875, 1); // Ring of Knowledge
						qs.giveItems(875, 1); // Ring of Knowledge
						qs.giveItems(1060, 100); // 100x Healing Potion
						qs.getPlayer().addExpAndSp(16851, 0);
						qs.exitCurrentQuest(false);
						qs.playSound(SOUND_FINISH);
						break;
					default:
						htmltext = "noqu.htm";
						break;
				}
				break;
			
			case RemembranceTower:
				switch(cond)
				{
					case 1:
						htmltext = "FIND HTML";
						break;
					case 2:
						htmltext = "33989-1.htm";
						break;
					case 3:
						htmltext = "33989-3.htm";
						break;
					default:
						htmltext = "noqu.htm";
						break;
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
			qs.giveItems(UnnamedRelics, 1);
			qs.playSound(SOUND_ITEMGET);
			Relics++;
			if(Relics >= 20)
			{
				qs.setCond(2);
				qs.playSound(SOUND_MIDDLE);
				Relics = 0;
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

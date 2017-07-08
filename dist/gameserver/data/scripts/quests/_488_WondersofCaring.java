package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.instancemanager.DailyQuestsManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk dev.fairytale-world.ru
public class _488_WondersofCaring extends Quest implements ScriptFile
{
	//Шанс дропа
	private static final int chance = 39;
	//Квест итем
	private static final int Box = 19500;
	//Монстры
	private static final int[] mobstohunt = {20965, 20970, 20971, 20972, 20966, 20967, 20973, 20968, 20969};
	//НПСы
	private static final int Dolphr = 32880;
	private static final int Adventurequid = 33463;


	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _488_WondersofCaring()
	{
		super(false);
		addStartNpc(Adventurequid);
		addTalkId(Adventurequid);
		addTalkId(Dolphr);
		addKillId(mobstohunt);
		addQuestItem(Box);

		addLevelCheck(75, 79);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("quest_ac"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
			htmltext = "0-4.htm";
		}
		else if(event.equalsIgnoreCase("qet_rev"))
			htmltext = "1-3.htm";
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		if(npcId == Adventurequid)
		{
			if(cond == 0)
			{
				if(checkStartCondition(st.getPlayer()))
				{
					if(st.isNowAvailable())
						htmltext = "0-1.htm";
					else
						htmltext = "0-c.htm";
				}
				else
					htmltext = "0-nc.htm";
			}
			else if(cond == 1 || cond == 2)
				htmltext = "0-5.htm";
		}
		else if(npcId == Dolphr)
		{
			if(cond == 0)
			{
				if(checkStartCondition(st.getPlayer()))
				{
					if(st.isNowAvailable())
						return htmltext;
					else
						htmltext = "1-c.htm";
				}
				else
					htmltext = "1-nc.htm";
			}
			else if(cond == 1)
				return htmltext;
			else if(cond == 2)
			{
				htmltext = "1-1.htm";
				st.getPlayer().addExpAndSp(22901550, 26024550);
				st.giveItems(57, 490545);
				st.takeAllItems(Box);
				st.exitCurrentQuest(this);
				st.playSound(SOUND_FINISH);
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if(st.getCond() == 1 && ArrayUtils.contains(mobstohunt, npcId) && st.getQuestItemsCount(Box) < 50)
		{
			st.rollAndGive(Box, 1, chance);
			st.playSound(SOUND_ITEMGET);
		}
		if(st.getQuestItemsCount(Box) >= 50)
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		return null;
	}

	@Override
	public boolean isVisible(Player player)
	{
		if(DailyQuestsManager.isQuestDisabled(getQuestIntId()))
			return false;
		return true;
	}
	
	@Override
	public boolean checkStartCondition(Player player)
	{
		return player.getLevel() >= 75 && player.getLevel() <= 79;
	}		
}
package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Iqman
 */
public class _10445_AnImpendingThreat extends Quest implements ScriptFile
{
	//npc
	private static final int MATHIAS = 31340;
	private static final int TUSKA = 33839;
	private static final int BURINU = 33840;
	//quest_items
	private static final int BADGE = 36685;
	//rewards
	private static final int SOI = 37017;
	private static final int SOULSHOT = 34609;
	private static final int SPIRITSHOT = 34616;
	private static final int ELIXIR_LIFE = 30357;
	private static final int ELIXIR_MIND = 30358;
	
	public _10445_AnImpendingThreat()
	{
		super(false);
		addStartNpc(MATHIAS);
		addTalkId(MATHIAS);
		addTalkId(TUSKA);
		addTalkId(BURINU);
		addQuestItem(BADGE);
		
		addLevelCheck(99);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("accept.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("badge.htm"))
		{
			st.setCond(2);
			st.giveItems(36685, 1);
			st.playSound(SOUND_MIDDLE);
			htmltext = "1-2.htm";
		}		
		else if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.addExpAndSp(30873150, 308731);
			st.giveItems(SOI, 1);
			if(st.getPlayer().isMageClass())
			{
				st.giveItems(SPIRITSHOT, 10000L);
				st.giveItems(ELIXIR_MIND, 50L);
			}
			else
			{
				st.giveItems(SOULSHOT, 10000L);
				st.giveItems(ELIXIR_LIFE, 50L);
			}		
			st.exitCurrentQuest(false);
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
		
		if(npcId == MATHIAS)
		{
			if(st.getPlayer().getLevel() < 99)
				htmltext = "no_level.htm";
			else if(cond == 0)
				htmltext = "1.htm";
		}
		else if(npcId == TUSKA)
		{
			if(cond == 1)
				htmltext = "1-1.htm";		
			else if(cond == 2)
				htmltext = "1-3.htm";
		}
		else if(npcId == BURINU)
		{
			if(cond == 2)
				htmltext = "2-1.htm";
		}
		return htmltext;
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
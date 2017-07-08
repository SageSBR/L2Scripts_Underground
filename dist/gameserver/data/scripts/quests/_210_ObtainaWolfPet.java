package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _210_ObtainaWolfPet extends Quest implements ScriptFile
{
	// Квестовые персонажи
	private static final int LUNDY = 30827;
	private static final int BELLADONA = 30256;
	private static final int BRYNNER = 30335;
	private static final int SYDNEY = 30321;

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _210_ObtainaWolfPet()
	{
		super(false);
		addStartNpc(LUNDY);
		addTalkId(LUNDY, BELLADONA, BRYNNER, SYDNEY);
		addLevelCheck(15);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("accept"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			return "pet_manager_lundy_q0210_04.htm";
		}
		if(event.equalsIgnoreCase("endquest"))	
		{
			st.giveItems(2375, 1);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);		
			return "pet_manager_lundy_q0210_10.htm";	
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();

		int cond = st.getCond();
		if(npcId == LUNDY)
		{
			if(player.getLevel() < 15)
				return "pet_manager_lundy_q0210_02.htm";
			if(st.getCond() == 0)
				return "pet_manager_lundy_q0210_01.htm";
			if(st.getCond() == 1)
				return "pet_manager_lundy_q0210_05.htm";
			if(st.getCond() == 4)
				return "pet_manager_lundy_q0210_06.htm";
		}		
		else if(npc.getNpcId() == BELLADONA)
		{
			if(st.getCond() == 1)
			{
				st.setCond(2);
				st.playSound(SOUND_MIDDLE);
				return "gatekeeper_belladonna_q0210_01.htm";			
			}
		}
		else if(npc.getNpcId() == BRYNNER)
		{
			if(st.getCond() == 2)
			{
				st.setCond(3);
				st.playSound(SOUND_MIDDLE);
				return "guard_brynner_q0210_01.htm";
			}		
		}
		else if(npc.getNpcId() == SYDNEY)
		{
			if(st.getCond() == 3)
			{
				st.setCond(4);
				st.playSound(SOUND_MIDDLE);
				return "trader_sydney_q0210_01";
			}		
		}		
		return "noquest";
	}
}
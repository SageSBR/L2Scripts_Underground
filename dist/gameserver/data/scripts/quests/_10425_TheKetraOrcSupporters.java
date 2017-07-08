package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;
import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.network.l2.components.NpcString;

public class _10425_TheKetraOrcSupporters extends Quest implements ScriptFile
{
	public static final String A_LIST = "A_LIST";
	public static final String B_LIST = "B_LIST";
    //Квестовые персонажи
    private static final int LUKONES = 33852;

    //Монстры
    private static final int[] MOBS = new int[]{21324, 21325, 21326, 21327, 21328, 21329, 21330, 21331, 21332, 21333,
			21334, 21335, 21336, 21337, 21338, 21339, 21340, 21341, 21342, 21343, 21344, 21345, 21346, 21347, 21348, 21349};
    private static final int ZAPAS_STREL = 27511;
	private static final int ZAPAS_MAG = 27512 ;

    //Награда за квест
    private static final int STEEL_DOOR_GUILD_COIN = 37045;
    private static final int SCROLL_ENCHANT_ARMOR_A_GRADE = 730;
	

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10425_TheKetraOrcSupporters()
	{
		super(false);
		addStartNpc(LUKONES);
		addTalkId(LUKONES);
		addRaceCheck(true, true, true, true, true,true, false);
		addLevelCheck(76, 80);
		addKillNpcWithLog(1, A_LIST, 50, ZAPAS_STREL);
		addKillNpcWithLog(1, B_LIST, 50, ZAPAS_MAG);
		addKillId(MOBS);
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
			return "accept.htm";
		}	
		if(event.equalsIgnoreCase("endquest"))
		{
			st.addExpAndSp(22997520, 5519);
			st.giveItems(STEEL_DOOR_GUILD_COIN, 89);
			st.giveItems(SCROLL_ENCHANT_ARMOR_A_GRADE, 5);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);	
			return "enquest.htm";
		}		
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		
		if(npcId == LUKONES)
		{
			if(st.getCond() == 0)
			{
				if(!player.getClassId().isMage())
					return "Only mage classes can take this quest";
				else if(checkStartCondition(st.getPlayer()))
					return "1.htm";
				else
					return "no_level.htm";
			}
			else if(cond == 1)
				return "4.htm";	
			else if(cond == 2)	
				return "5.htm";
		}
		return "noquest";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 1)
			return null;
		
		Player player = qs.getPlayer();	
		if(ArrayUtils.contains(MOBS,npc.getNpcId()))
		{
			if(Rnd.chance(50))
			{
				NpcInstance scout = qs.addSpawn(ZAPAS_STREL, qs.getPlayer().getX() + 100, qs.getPlayer().getY() + 100, qs.getPlayer().getZ(), 0, 0, 360000);
				scout.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, qs.getPlayer(), 100000);			
			}
			else
			{
				NpcInstance scout = qs.addSpawn(ZAPAS_MAG, qs.getPlayer().getX() + 100, qs.getPlayer().getY() + 100, qs.getPlayer().getZ(), 0, 0, 360000);
				scout.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, qs.getPlayer(), 100000);			
			}	
		}
		
		else if(updateKill(npc, qs))
		{
			qs.unset(A_LIST);
			qs.unset(B_LIST);
			qs.setCond(2);
		}
		return null;
	}	
}
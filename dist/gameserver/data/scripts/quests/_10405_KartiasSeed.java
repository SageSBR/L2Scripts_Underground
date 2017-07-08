package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _10405_KartiasSeed extends Quest implements ScriptFile
{
    //Квестовые персонажи
    private static final int SUBAN = 33867;
    
    //Монстры
    private static final int[] MOBS = new int[] {20974, 20975, 20976, 21001, 21002, 21003, 21004, 21005};
    
    //Квест итем
    private static final int KARTIAS_MUTATED_SEED = 36714;
    
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

	public _10405_KartiasSeed()
	{
		super(false);
		addStartNpc(SUBAN);
		addTalkId(SUBAN);
		addKillId(MOBS);
		addRaceCheck(true, true, true, true, true, true, false);
		addQuestItem(KARTIAS_MUTATED_SEED);
		addLevelCheck(61, 65);
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
			return "4.htm";
		}
		if(event.equalsIgnoreCase("endquest"))	
		{
			st.giveItems(SCROLL_ENCHANT_ARMOR_A_GRADE, 5);
			st.giveItems(STEEL_DOOR_GUILD_COIN, 57);
			st.addExpAndSp(6251174, 1500);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);		
			return "6.htm";	
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();

		if(npcId == SUBAN)
		{
			if(st.getCond() == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					return "1.htm";
				else
					return "Can taken only by characters level's above 61 and bellow 65!(Not for Ertheia race)";
			}
			else if(st.getCond() == 1)
				return "4.htm";
			else if(st.getCond() == 2)
				return "5.htm";
		}		
		return "noquest";
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(Rnd.chance(60) && st.getCond() == 1)
		{
			st.giveItems(KARTIAS_MUTATED_SEED, 1);
			if(st.getQuestItemsCount(KARTIAS_MUTATED_SEED) >= 50)
			{
				st.playSound(SOUND_MIDDLE);
				st.setCond(2);
			}
			else
				st.playSound(SOUND_ITEMGET);
		}
		return null;
	}	
}
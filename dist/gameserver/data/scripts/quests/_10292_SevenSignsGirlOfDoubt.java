package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Bonux
 * @date 02/06/2011
 */
public class _10292_SevenSignsGirlOfDoubt extends Quest implements ScriptFile
{
	// NPC
	private static final int WOOD = 32593;
	private static final int FRANZ = 32597;
	private static final int ELCARDIA = 32784;
	private static final int HARDIN = 30832;

	// MOBD
	private static final int[] MOBS_1 = {22801, 22802, 22803, 22804, 22805, 22806};
	private static final int CREATURE_OF_THE_DUSK_1 = 27422;
	private static final int CREATURE_OF_THE_DUSK_2 = 27424;

	// ITEMS
	private static int ELCARDIAS_MARK = 17226;

	public _10292_SevenSignsGirlOfDoubt()
	{
		super(false);

		addStartNpc(WOOD);
		addTalkId(WOOD, FRANZ, ELCARDIA, HARDIN);
		addKillId(MOBS_1);
		addKillId(CREATURE_OF_THE_DUSK_1, CREATURE_OF_THE_DUSK_2);
		addQuestItem(ELCARDIAS_MARK);
		addLevelCheck(81);
		addQuestCompletedCheck(_198_SevenSignsEmbryo.class);			
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("priest_wood_q10292_3.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("priest_wood_q10292_4.htm"))
			enterInstance(st, 145);
		else if(event.equalsIgnoreCase("witness_of_dawn_q10292_2.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("elcadia_abyssal_saintess_q10292_2.htm"))
		{
			st.setCond(3);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("elcadia_abyssal_saintess_q10292_9.htm"))
		{
			st.setCond(7);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("hardin_q10292_1.htm"))
		{
			st.setCond(8);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("spawnTestMobs"))
		{
			int reflectId = player.getReflectionId();
			st.set("CreatureOfTheDusk1", 1);
			st.set("CreatureOfTheDusk2", 1);
			addSpawnToInstance(CREATURE_OF_THE_DUSK_1, 89416, -237992, -9632, 0, 0, reflectId);
			addSpawnToInstance(CREATURE_OF_THE_DUSK_2, 89416, -238136, -9632, 0, 0, reflectId);
			return null;
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		int id = st.getState();
		Player player = st.getPlayer();
		if(!player.isBaseClassActive())
			return "no_subclass_allowed.htm";
		switch(npcId)
		{
			case WOOD:
				if(cond == 0)
				{
					QuestState qs = player.getQuestState(_198_SevenSignsEmbryo.class);
					if(player.getLevel() >= 81 && qs != null && qs.isCompleted())
						htmltext = "priest_wood_q10292_0.htm";
					else
					{
						htmltext = "priest_wood_q10292_0n.htm";
						st.exitCurrentQuest(true);
					}
				}
				else if(cond == 1)
					htmltext = "priest_wood_q10292_3.htm";
				else if(cond > 1 && !st.isCompleted())
					htmltext = "priest_wood_q10292_5.htm"; //TODO: Отредактировать диалог по оффу
				else if(st.isCompleted())
					htmltext = "priest_wood_q10292_6.htm";
				break;
			case FRANZ:
				if(cond == 1)
					htmltext = "witness_of_dawn_q10292_0.htm";
				else if(cond == 2)
					htmltext = "witness_of_dawn_q10292_4.htm";
				break;
			case ELCARDIA:
				if(cond == 2)
					htmltext = "elcadia_abyssal_saintess_q10292_0.htm";
				else if(cond == 3)
					htmltext = "elcadia_abyssal_saintess_q10292_2.htm";
				else if(cond == 4)
				{
					htmltext = "elcadia_abyssal_saintess_q10292_3.htm";
					st.takeItems(ELCARDIAS_MARK, -1);
					st.playSound(SOUND_MIDDLE);
					st.setCond(5);
				}
				else if(cond == 5)
					htmltext = "elcadia_abyssal_saintess_q10292_5.htm";
				else if(cond == 6)
					htmltext = "elcadia_abyssal_saintess_q10292_6.htm";
				else if(cond == 7)
					htmltext = "elcadia_abyssal_saintess_q10292_9.htm";
				else if(cond == 8)
				{
					htmltext = "elcadia_abyssal_saintess_q10292_10.htm";
					st.addExpAndSp(10000000, 1000000);
					st.setState(COMPLETED);
					st.exitCurrentQuest(false);
					st.playSound(SOUND_FINISH);
				}
				break;
			case HARDIN:
				if(cond == 7)
					htmltext = "hardin_q10292_0.htm";
				else if(cond == 8)
					htmltext = "hardin_q10292_2.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		if(cond == 3 && ArrayUtils.contains(MOBS_1, npcId) && Rnd.chance(70))
		{
			st.giveItems(ELCARDIAS_MARK, 1);
			if(st.getQuestItemsCount(ELCARDIAS_MARK) < 10)
				st.playSound(SOUND_ITEMGET);
			else
			{
				st.playSound(SOUND_MIDDLE);
				st.setCond(4);
			}
		}
		else if(npcId == CREATURE_OF_THE_DUSK_1)
		{
			st.set("CreatureOfTheDusk1", 2);
			if(st.get("CreatureOfTheDusk2") != null && Integer.parseInt(st.get("CreatureOfTheDusk2")) == 2)
			{
				st.playSound(SOUND_MIDDLE);
				st.setCond(6);
			}
		}
		else if(npcId == CREATURE_OF_THE_DUSK_2)
		{
			st.set("CreatureOfTheDusk2", 2);
			if(st.get("CreatureOfTheDusk1") != null && Integer.parseInt(st.get("CreatureOfTheDusk1")) == 2)
			{
				st.playSound(SOUND_MIDDLE);
				st.setCond(6);
			}
		}
		return null;
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
package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author coldy
 * @date 04.09.2012
 * TODO: offlike EN HTMLs; временная зона Кимериана 2 - http://www.linedia.ru/wiki/The_Corrupted_Leader:_His_Truth
 */
public class _10307_TheCorruptedLeaderHisTruth extends Quest implements ScriptFile
{

	private static final int NPC_NAOMI_KASHERON = 32896;
	private static final int NPC_MIMILEAD = 32895;
	private static final int[] MOB_KIMERIAN = { 25745, 25747 };
	private static final int REWARD_ENCHANT_ARMOR_R = 17527;

	public _10307_TheCorruptedLeaderHisTruth()
	{
		super(false);
		addStartNpc(NPC_NAOMI_KASHERON);
		addTalkId(NPC_MIMILEAD);
		addKillId(MOB_KIMERIAN);

		addQuestCompletedCheck(_10306_TheCorruptedLeader.class);
		addLevelCheck(90);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(st == null)
			return "noquest";

		if(event.equalsIgnoreCase("32896-05.htm"))
			st.setCond(1);
		else if(event.equalsIgnoreCase("32896-08.htm"))
		{
			st.setCond(3);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("32896-08.htm"))
		{
			st.playSound(SOUND_FINISH);
			st.addExpAndSp(11779522, 5275253);
			st.giveItems(REWARD_ENCHANT_ARMOR_R, 1);
			st.exitCurrentQuest(false);
		}

		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		if(st == null)
			return htmltext;

		Player player = st.getPlayer();

		QuestState prevst = player.getQuestState(_10306_TheCorruptedLeader.class);

		if(npc.getNpcId() == NPC_NAOMI_KASHERON)
		{
			switch(st.getState())
			{
				case COMPLETED:
					htmltext = "32896-02.htm";
					break;
				case CREATED:
					if(player.getLevel() >= 90)
					{
						if((prevst != null) && (prevst.isCompleted()))
							htmltext = "32896-01.htm";
						else
						{
							st.exitCurrentQuest(true);
							htmltext = "32896-03.htm";
						}
					}
					else
					{
						st.exitCurrentQuest(true);
						htmltext = "32896-03.htm";
					}
					break;
				case STARTED:
					if(st.getCond() == 1)
						htmltext = "32896-05.htm";
					else
					{
						if(st.getCond() != 2)
							break;
						htmltext = "32896-06.htm";
					}
			}

		}
		else if(npc.getNpcId() == NPC_MIMILEAD)
		{
			if(st.isStarted())
			{
				if(st.getCond() == 3)
					htmltext = "32895-01.htm";
			}
			else if(st.isCompleted())
				htmltext = "32895-05.htm";
		}
		return htmltext;
	}

	public String onKill(NpcInstance npc, QuestState st)
	{
		if((npc == null) || (st == null) || (st.getCond() != 1))
			return null;

		if(ArrayUtils.contains(MOB_KIMERIAN, npc.getNpcId()))
		{
			if(st.getCond() == 1)
			{
				st.playSound(SOUND_MIDDLE);
				st.setCond(2);
			}
		}
		return null;
	}

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
}

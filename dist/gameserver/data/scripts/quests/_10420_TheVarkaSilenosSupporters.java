package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;
import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.ai.CtrlEvent;

public class _10420_TheVarkaSilenosSupporters extends Quest implements ScriptFile
{
	public static final String A_LIST = "A_LIST";
	public static final String B_LIST = "B_LIST";
	//Квестовые персонажи
	private static final int HANSEN = 33853;

	//Монстры
	private static final int[] MOBSW = new int[]{21350, 21351, 21353, 21354, 21358, 21362, 21366, 21369, 21370,  21372,  21374, 21375};
	private static final int[] MOBSM = new int[]{21355, 21357, 21360, 21361, 21364, 21365,  21368, 21371, 21373};
	private static final int ZAPAS_STREL = 27514;
	private static final int ZAPAS_WIZ = 27515;

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

	public _10420_TheVarkaSilenosSupporters()
	{
		super(false);
		addStartNpc(HANSEN);
		addTalkId(HANSEN);
		addRaceCheck(true, true, true, true, true,true, false);
		addLevelCheck(76, 80);
		addKillNpcWithLog(1, A_LIST, 50, ZAPAS_STREL);
		addKillNpcWithLog(1, B_LIST, 50, ZAPAS_WIZ);
		addKillId(MOBSW);
		addKillId(MOBSM);
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
			return "endquest.htm";
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		if(npcId == HANSEN)
		{
			if(st.getCond() == 0)
			{
				if(player.getClassId().isMage())
					return "Only warrior classes can take this quest";
				else if(checkStartCondition(st.getPlayer()))
					return "1.htm";
				else
					return "no_level.htm";
			}
			else if(cond == 1)
				return "3.htm";
			else if(cond == 2)
				return "4.htm";
		}
		return "noquest";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 1)
			return null;

		if(ArrayUtils.contains(MOBSW, npc.getNpcId()))
		{
			NpcInstance scout = qs.addSpawn(ZAPAS_STREL, npc.getX() + 100, npc.getY() + 100, npc.getZ(), 0, 0, 360000);
			scout.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, qs.getPlayer(), 100000);
		}
		else if(ArrayUtils.contains(MOBSM, npc.getNpcId()))
		{
			NpcInstance scout = qs.addSpawn(ZAPAS_WIZ, npc.getX() + 100, npc.getY() + 100, npc.getZ(), 0, 0, 360000);
			scout.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, qs.getPlayer(), 100000);
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
package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author coldy
 * @date 04.09.2012
 * TODO: offlike EN HTMLs ; Временная зона кимериана http://www.linedia.ru/wiki/The_Corrupted_Leader
 */
public class _10306_TheCorruptedLeader extends Quest implements ScriptFile {

	private static final int NPC_NAOMI_KASHERON = 32896;
	private static final int MOB_KIMERIAN = 32896;
	private static final int[] CRYSTALS = { 9552, 9553, 9554, 9555, 9556, 9557 };

	public _10306_TheCorruptedLeader()
	{
		super(false);
		addStartNpc(NPC_NAOMI_KASHERON);
		addTalkId(NPC_NAOMI_KASHERON);
		addKillId(MOB_KIMERIAN);

		addQuestCompletedCheck(_10305_UnstoppableFutileEfforts.class);
		addLevelCheck(90);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(st == null)
			return "noquest";

		if(event.equalsIgnoreCase("32896-05.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("32896-08.htm")){
			st.playSound(SOUND_FINISH);
			st.addExpAndSp(9479594, 4104484);
			st.giveItems(CRYSTALS[Rnd.get(0, CRYSTALS.length - 1)],1);
			st.exitCurrentQuest(false);
		}

		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";

		if(npc.getNpcId() == NPC_NAOMI_KASHERON)
		{
			if(st != null && st.getState() == COMPLETED)
				return "32896-02.htm";

			else if(st.getCond() == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					return "32896-01.htm";
				else
					return "32896-03.htm";
			}

			else if(st.getCond() == 1)
				return "32896-06.htm";
			else if(st.getCond() == 2)
				return "32896-07.htm";
		}
		return htmltext;
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

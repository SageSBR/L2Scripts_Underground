package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * Created by Akeno on 29.11.2015.
 */
public class _10811_ExaltedOneWhoFacesTheLimit extends Quest implements ScriptFile
{
	private static final int LEONEL_HANTER = 33907;
	private static final int HEAD_FOREST_ELRICA = 31620;

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10811_ExaltedOneWhoFacesTheLimit()
	{
		super(false);
		addStartNpc(LEONEL_HANTER);
		addTalkId(LEONEL_HANTER);

		addLevelCheck(99);
		//addQuestCompletedCheck(_10369_NoblesseSoulTesting.class); //TODO добаить проверку на простое дворянство
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();

		if(st == null)
			return "noquest";

		//TODO прописать события квеста

		if(event.equalsIgnoreCase("startquest"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			return "";
		}

		if(event.equalsIgnoreCase("set2"))
		{
			st.setCond(2);
			return "";
		}
		if(event.equalsIgnoreCase("set3"))
		{
			st.setCond(3);
			return "";
		}

		return event;
	}

	@Override
	 public String onTalk(NpcInstance npc, QuestState st)
	{
		Player pl = st.getPlayer();
		String htmltext = "noquest";
		int cond = st.getCond();

		if(npc.getNpcId() == LEONEL_HANTER)
		{
			if(st != null && st.getState() == COMPLETED)
				return "complite.htm";

			else if(cond == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					return "1.htm";
				else
					return "test.htm";
			}
			if(cond == 1)
				return "1-3.htm";
		}
		if(npc.getNpcId() == HEAD_FOREST_ELRICA)
		{
			if(cond == 2)
				if(pl.getRace().ordinal() == 16)
					return "2-1.htm";
				else
					return "noordians.htm";

			if(cond == 3)
				if(pl.getRace().ordinal() == 0)
					return "2-2.htm";
				else
					return "nouseordians.htm";

			if(cond == 4)
			{    //TODO проверка, пройден ли квест "Противостоять унынию"
				return htmltext;
			}
		}

		return htmltext;
	}

}

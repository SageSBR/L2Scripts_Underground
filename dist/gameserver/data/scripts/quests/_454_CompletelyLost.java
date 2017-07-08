package quests;

import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;


/**
 * @author pchayka
 */

public class _454_CompletelyLost extends Quest implements ScriptFile
{
	private static final int WoundedSoldier = 32738;
	private static final int Ermian = 32736;
	private static final int[][] rewards = {{36794, 1}, {36795, 1}, {36796, 1}, {36797, 1}, {36798, 1}, {36799, 1}, {36800, 1}, {36801, 1}, {36802, 1}, {36803, 1}, {36804, 1}, {36825, 1}, {36826, 1}, {36827, 1}, {36828, 1}, {36829, 1}, {36830, 1}, {36831, 1}, {36832, 1}, {36833, 1}, {36834, 1}, {36835, 1}, {17526, 1}, {17527, 1}, {9546, 1}, {9547, 1}, {9548, 1}, {9549, 1}, {9550, 1}, {9551, 1}, {9552, 1}, {9553, 1}, {9554, 1}, {9555, 1}, {9556, 1}, {9557, 1}};

	public _454_CompletelyLost()
	{
		super(false);
		addStartNpc(WoundedSoldier);
		addTalkId(Ermian);
		addLevelCheck(85);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("wounded_soldier_q454_02.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("wounded_soldier_q454_03.htm"))
		{
			if(seeSoldier(npc, st.getPlayer()) == null)
			{
				npc.setFollowTarget(st.getPlayer());
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, st.getPlayer(), Config.FOLLOW_RANGE);
			}
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		if(npc.getNpcId() == WoundedSoldier)
		{
			switch(st.getState())
			{
				case CREATED:
					if(st.isNowAvailable())
					{
						if(st.getPlayer().getLevel() >= 85)
							htmltext = "wounded_soldier_q454_01.htm";
						else
						{
							htmltext = "wounded_soldier_q454_00.htm";
							st.exitCurrentQuest(true);
						}
					}
					else
						htmltext = "wounded_soldier_q454_00a.htm";
					break;
				case STARTED:
					if(st.getCond() == 1)
					{
						htmltext = "wounded_soldier_q454_04.htm";
						if(seeSoldier(npc, st.getPlayer()) == null)
						{
							npc.setFollowTarget(st.getPlayer());
							npc.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, st.getPlayer(), Config.FOLLOW_RANGE);
						}
					}
					break;
			}
		}
		else if(npc.getNpcId() == Ermian)
		{
			if(st.getCond() == 1)
			{
				if(seeSoldier(npc, st.getPlayer()) != null)
				{
					htmltext = "ermian_q454_01.htm";
					NpcInstance soldier = seeSoldier(npc, st.getPlayer());
					soldier.doDie(null);
					soldier.endDecayTask();
					giveReward(st);
					st.playSound(SOUND_FINISH);
					st.exitCurrentQuest(this);
				}
				else
					htmltext = "ermian_q454_02.htm";
			}
		}

		return htmltext;
	}

	private NpcInstance seeSoldier(NpcInstance npc, Player player)
	{
		List<NpcInstance> around = npc.getAroundNpc(Config.FOLLOW_RANGE * 2, 300);
		if(around != null && !around.isEmpty())
			for(NpcInstance n : around)
				if(n.getNpcId() == WoundedSoldier && n.getFollowTarget() != null)
					if(n.getFollowTarget().getObjectId() == player.getObjectId())
						return n;

		return null;
	}

	private void giveReward(QuestState st)
	{
		int row = Rnd.get(0, rewards.length - 1);
		int id = rewards[row][0];
		int count = rewards[row][1];
		st.giveItems(id, count);
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
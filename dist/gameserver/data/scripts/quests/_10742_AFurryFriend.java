package quests;

import java.util.List;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.s2c.ExSendUIEventPacket;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

/**
 * @author blacksmoke
 */
public class _10742_AFurryFriend extends Quest implements ScriptFile
{
	private static final int Leira = 33952;
	private static final int KikusCave = 33995;
	private static int KikusCout;
	private static final int Ricky = 19552;
	private static final int Kiku = 23453;
	
	protected static Location[] POINTS =
	{
		new Location(-78152, 237352, -3569),
		new Location(-79176, 236792, -3440),
		new Location(-80072, 237064, -3311),
		new Location(-80440, 237320, -3313)
	};
	
	public _10742_AFurryFriend()
	{
		super(false);
		addStartNpc(Leira);
		addTalkId(Leira, KikusCave);
		addLevelCheck(11, 20);
		addClassIdCheck(182, 183);
	}
	
	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		NpcInstance ricky = GameObjectsStorage.getByNpcId(Ricky);
		String htmltext = event;
		switch(event)
		{
			case "quest_ac":
				qs.setState(STARTED);
				qs.setCond(1);
				qs.playSound(SOUND_ACCEPT);
				qs.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.FOLLOW_RICKY, 4500, ScreenMessageAlign.TOP_CENTER));
				ricky = NpcUtils.spawnSingle(Ricky, new Location(-78138, 237328, -3548));
				ricky.setRunning();
				ThreadPoolManager.getInstance().schedule(new RickyMoveTask(ricky), 1000L);
				htmltext = "33952-3.htm";
				KikusCout = 0;
				break;
			
			case "quest_cont":
				qs.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.TAKE_RICKY_TO_LEIRA_IN_UNDER_2_MINUTES, 4500, ScreenMessageAlign.TOP_CENTER));
				ricky = NpcUtils.spawnSingle(Ricky, new Location(qs.getPlayer().getX(), qs.getPlayer().getY(), qs.getPlayer().getZ()));
				
				if(seeRicky(ricky, qs.getPlayer()) == null)
				{
					// TODO: Ricky's title don't work
					ricky.setTitle(qs.getPlayer().getName());
					ricky.setRunning();
					ricky.setFollowTarget(qs.getPlayer());
					ricky.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, qs.getPlayer(), 50);
				}
				
				ThreadPoolManager.getInstance().schedule(new CheckRickyDistance(GameObjectsStorage.getByNpcId(Ricky), GameObjectsStorage.getByNpcId(Leira), qs), 500L);
				qs.getPlayer().sendPacket(new ExSendUIEventPacket(qs.getPlayer(), 0, 0, 120, 0, NpcString.REMAINING_TIME));
				qs.startQuestTimer("despawnRicky", 120 * 1000L, ricky);
				htmltext = "33995-3.htm";
				break;
			
			case "despawnRicky":
				if(ricky != null)
				{
					KikusCout = 0;
					ricky.deleteMe();
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(NpcInstance npc, QuestState qs)
	{
		if(qs.isCompleted())
		{
			return "completed";
		}
		String htmltext = "noquest";
		final int cond = qs.getCond();
		
		switch(npc.getNpcId())
		{
			case Leira:
				switch(cond)
				{
					case 0:
						if(checkStartCondition(qs.getPlayer()))
						{
							htmltext = "33952-1.htm";
						}
						break;
					
					case 2:
						htmltext = "33952-4.htm";
						qs.giveItems(57, 2500);
						qs.addExpAndSp(52516, 5);
						qs.exitCurrentQuest(false);
						qs.playSound(SOUND_FINISH);
						break;
					
					default:
						htmltext = "noqu.htm";
						break;
				}
				break;
			
			case KikusCave:
				if(cond == 1)
				{
					if (qs.get("RikkyCave") == null)
						qs.set("RikkyCave", Rnd.get(0, 2));

					final NpcInstance ricky = GameObjectsStorage.getByNpcId(Ricky);
					if(ricky == null && KikusCout == Integer.valueOf(qs.get("RikkyCave")))
					{
						KikusCout = 0;
						qs.unset("RikkyCave");
						htmltext = "33995-1.htm";
					}
					else
					{
						if (ricky == null)
						{
							final NpcInstance kiku = qs.addSpawn(Kiku, qs.getPlayer().getX() - Rnd.get(50), qs.getPlayer().getY() - Rnd.get(50), qs.getPlayer().getZ());
							kiku.getAggroList().addDamageHate(qs.getPlayer(), 0, 10000);
							kiku.setAggressionTarget(qs.getPlayer());
						}
						qs.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.RICKY_IS_NOT_HERENTRY_SEARCHING_ANOTHER_KIKUS_CAVE, 4500, ScreenMessageAlign.TOP_CENTER));
						htmltext = "33995-2.htm";
						KikusCout++;
					}
				}
				break;
		}
		return htmltext;
	}
	
	class CheckRickyDistance implements Runnable
	{
		final NpcInstance ricky;
		final NpcInstance leira;
		final QuestState qs;
		
		public CheckRickyDistance(NpcInstance npcRicky, NpcInstance npcLeira, QuestState state)
		{
			ricky = npcRicky;
			leira = npcLeira;
			qs = state;
		}
		
		@Override
		public void run()
		{
			while (true)
			{
				if((ricky != null) && (leira != null) && (ricky.getDistance(leira) < 100))
				{
					qs.setCond(2);
					qs.getPlayer().sendPacket(new ExSendUIEventPacket(qs.getPlayer(), 0, 0, 0, 0, NpcString.REMAINING_TIME));
					qs.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.RICKY_HAS_FOUND_LEIRA, 4500, ScreenMessageAlign.TOP_CENTER));

					ThreadPoolManager.getInstance().schedule(new RunnableImpl(){
						@Override
						public void runImpl() throws Exception
						{
							ricky.deleteMe();
						}
					}, 3000);
					return;
				}
				
				if(ricky == null)
				{
					return;
				}
				
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	class RickyMoveTask implements Runnable
	{
		final NpcInstance ricky;
		int _step = 0;
		
		RickyMoveTask(NpcInstance npc)
		{
			ricky = npc;
		}
		
		@Override
		public void run()
		{
			while (true)
			{
				if(_step < POINTS.length)
				{
					if(ricky.isMoving)
					{
						try
						{
							Thread.sleep(500L);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
						continue;
					}
					ricky.moveToLocation(POINTS[_step], 0, true);
					_step++;
				}
				else
				{
					ricky.deleteMe();
					return;
				}
			}
		}
	}
	
	private NpcInstance seeRicky(NpcInstance npc, Player player)
	{
		final List<NpcInstance> around = npc.getAroundNpc(Config.FOLLOW_RANGE * 2, 300);
		
		if((around != null) && !around.isEmpty())
		{
			for (NpcInstance n : around)
			{
				if(((n.getNpcId() == Ricky) && (n.getFollowTarget() != null)) && (n.getFollowTarget().getObjectId() == player.getObjectId()))
				{
					return n;
				}
			}
		}
		
		return null;
	}
	
	@Override
	public void onLoad()
	{
		//
	}
	
	@Override
	public void onReload()
	{
		//
	}
	
	@Override
	public void onShutdown()
	{
		//
	}
}

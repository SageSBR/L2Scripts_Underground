package l2s.gameserver.network.l2.c2s;

import l2s.commons.lang.reference.HardReference;
import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.listener.actor.OnMoveListener;
import l2s.gameserver.instancemanager.QuestManager;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.ClassType2;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.model.instances.AwakeningManagerInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.components.UsmVideo;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;

/**
 * @author Bonux
**/
public class RequestChangeToAwakenedClass extends L2GameClientPacket
{
	private static class MoveListener implements OnMoveListener
	{
		@Override
		public void onMove(Creature actor, Location loc)
		{
			actor.removeListener(this);

			if(!actor.isPlayer())
				return;

			ThreadPoolManager.getInstance().schedule(new ShowUsmMovie(actor.getPlayer()), 3000);
		}
	}

	private static class ShowUsmMovie extends RunnableImpl
	{
		private final HardReference<Player> _playerRef;

		public ShowUsmMovie(Player player)
		{
			_playerRef = player.getRef();
		}

		public void runImpl()
		{
			Player player = _playerRef.get();
			if(player == null)
				return;

			player.sendPacket(UsmVideo.Q010.packet(player));
		}
	}

	private static MoveListener MOVE_LISTENER = new MoveListener();

	private boolean _change;

	@Override
	protected void readImpl()
	{
		_change = readD() == 1;
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		NpcInstance npc = activeChar.getLastNpc();
		if(npc == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.getRace() == Race.ERTHEIA)
		{
			if(!_change)
			{
				activeChar.sendActionFailed();
				return;
			}

			Quest q = QuestManager.getQuest(10753);
			if(q != null)
				activeChar.processQuestEvent(q.getName(), "request_change_class", npc);
		}
		else if(npc instanceof AwakeningManagerInstance)
		{
			if(!_change)
			{
				activeChar.sendActionFailed();
				return;
			}

			if(activeChar.getServitors().length > 0)
			{
				activeChar.sendActionFailed();
				return;
			}

			AwakeningManagerInstance awakeningManager = (AwakeningManagerInstance) npc;
			ClassId classId = activeChar.getClassId();
			int requestAwakeningId = activeChar.getVarInt(AwakeningManagerInstance.getAwakeningRequestVar(classId));
			if(requestAwakeningId == 0)
			{
				activeChar.sendActionFailed();
				return;
			}

			ClassId awakedClassId = ClassId.VALUES[requestAwakeningId];
			if(!awakedClassId.isOfType2(awakeningManager.getClassTypeByNpc()))
			{
				activeChar.sendActionFailed();
				return;
			}

			if(ItemFunctions.getItemCount(activeChar, ItemTemplate.ITEM_ID_SCROLL_OF_AFTERLIFE) < 1)
			{
				activeChar.sendActionFailed();
				return;
			}

			if(ItemFunctions.getItemCount(activeChar, ItemTemplate.ITEM_ID_STONE_OF_AWEKENING) < 1)
			{
				activeChar.sendActionFailed();
				return;
			}

			if(!awakedClassId.childOf(classId))
			{
				activeChar.sendActionFailed();
				return;
			}

			if(!activeChar.isQuestContinuationPossible(false))
			{
				activeChar.sendPacket(SystemMsg.YOU_CANNOT_AWAKEN_DUE_TO_WEIGHT_LIMITS_PLEASE_TRY_AWAKEN_AGAIN_AFTER_INCREASING_THE_ALLOWED_WEIGHT_BY_ORGANIZING_THE_INVENTORY);
				return;
			}

			if(activeChar.isTransformed() || activeChar.isMounted())
			{
				activeChar.sendPacket(SystemMsg.YOU_CANNOT_AWAKEN_WHILE_YOURE_TRANSFORMED_OR_RIDING);
				return;
			}

			ItemFunctions.removeItem(activeChar, ItemTemplate.ITEM_ID_SCROLL_OF_AFTERLIFE, 1, true);
			ItemFunctions.removeItem(activeChar, ItemTemplate.ITEM_ID_STONE_OF_AWEKENING, 1, true);

			activeChar.unsetVar(AwakeningManagerInstance.getAwakeningRequestVar(classId));

			activeChar.setClassId(requestAwakeningId, false);
			activeChar.broadcastUserInfo(true);
			activeChar.broadcastPacket(new SocialActionPacket(activeChar.getObjectId(), SocialActionPacket.AWAKENING));

			if(!activeChar.getVarBoolean("@awake_manual_video"))
			{
				activeChar.setVar("@awake_manual_video", "true", -1);
				activeChar.addListener(MOVE_LISTENER);
			}
		}
		else
		{
			ClassId classId = activeChar.getClassId();
			if(!_change)
			{
				activeChar.unsetVar(AwakeningManagerInstance.getAwakeningRequestVar(classId));
				activeChar.sendActionFailed();
				return;
			}

			if(activeChar.getServitors().length > 0)
			{
				activeChar.sendActionFailed();
				return;
			}

			if(classId.isOfLevel(ClassLevel.AWAKED))
			{
				if(!classId.isOutdated())
				{
					if(activeChar.isBaseClassActive() && ItemFunctions.getItemCount(activeChar, ItemTemplate.ITEM_ID_CHAOS_ESSENCE) < 1)
					{
						activeChar.sendActionFailed();
						return;
					}

					if(activeChar.isDualClassActive())
					{
						if(ItemFunctions.getItemCount(activeChar, ItemTemplate.ITEM_ID_CHAOS_ESSENCE) > 0)
						{
							activeChar.sendActionFailed();
							return;
						}

						if(ItemFunctions.getItemCount(activeChar, ItemTemplate.ITEM_ID_CHAOS_ESSENCE_DUAL_CLASS) < 1)
						{
							activeChar.sendActionFailed();
							return;
						}
					}
				}
			}
			else
			{
				activeChar.sendActionFailed();
				return;
			}

			int requestAwakeningId = activeChar.getVarInt(AwakeningManagerInstance.getAwakeningRequestVar(classId));
			if(requestAwakeningId == 0)
			{
				activeChar.sendActionFailed();
				return;
			}

			ClassId awakedClassId = ClassId.VALUES[requestAwakeningId];
			if(classId == awakedClassId)
			{
				activeChar.sendActionFailed();
				return;
			}

			if(!awakedClassId.isOfType2(classId.getType2()))
			{
				activeChar.sendActionFailed();
				return;
			}

			if(!activeChar.isQuestContinuationPossible(false))
			{
				activeChar.sendPacket(SystemMsg.YOU_CANNOT_AWAKEN_DUE_TO_WEIGHT_LIMITS_PLEASE_TRY_AWAKEN_AGAIN_AFTER_INCREASING_THE_ALLOWED_WEIGHT_BY_ORGANIZING_THE_INVENTORY);
				return;
			}

			if(activeChar.isTransformed() || activeChar.isMounted())
			{
				activeChar.sendPacket(SystemMsg.YOU_CANNOT_AWAKEN_WHILE_YOURE_TRANSFORMED_OR_RIDING);
				return;
			}

			activeChar.unsetVar(AwakeningManagerInstance.getAwakeningRequestVar(classId));

			if(!classId.isOutdated())
			{
				if(activeChar.isBaseClassActive())
				{
					if(activeChar.isNoble()) //offlike reset olympiad points when used chaos essence.
						Olympiad.manualSetNoblePoints(activeChar.getObjectId(), 10);
					ItemFunctions.removeItem(activeChar, ItemTemplate.ITEM_ID_CHAOS_ESSENCE, 1, true);
				}	
				else if(activeChar.isDualClassActive())
					ItemFunctions.removeItem(activeChar, ItemTemplate.ITEM_ID_CHAOS_ESSENCE_DUAL_CLASS, 1, true);
			}
			else
			{
				if(activeChar.isBaseClassActive())
				{
					ItemFunctions.addItem(activeChar, ItemTemplate.ITEM_ID_CHAOS_ESSENCE, 1, true);
					ItemFunctions.addItem(activeChar, ItemTemplate.ITEM_ID_CHAOS_ESSENCE, 1, true);
				}
				else if(activeChar.isDualClassActive())
				{
					ItemFunctions.addItem(activeChar, ItemTemplate.ITEM_ID_CHAOS_ESSENCE_DUAL_CLASS, 1, true);
					ItemFunctions.addItem(activeChar, ItemTemplate.ITEM_ID_CHAOS_ESSENCE_DUAL_CLASS, 1, true);
				}
			}

			activeChar.setClassId(requestAwakeningId, true);
			activeChar.broadcastUserInfo(true);
			activeChar.broadcastPacket(new SocialActionPacket(activeChar.getObjectId(), SocialActionPacket.REAWAKENING));
		}

		activeChar.sendActionFailed();
	}
}
package l2s.gameserver.network.l2.c2s;

import l2s.commons.dao.JdbcEntityState;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.ShortCut;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExVariationCancelResult;
import l2s.gameserver.network.l2.s2c.InventoryUpdatePacket;
import l2s.gameserver.network.l2.s2c.ShortCutRegisterPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.utils.VariationUtils;

public final class RequestRefineCancel extends L2GameClientPacket
{
	//format: (ch)d
	private int _targetItemObjId;

	@Override
	protected void readImpl()
	{
		_targetItemObjId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.isActionsDisabled())
		{
			activeChar.sendPacket(new ExVariationCancelResult(0));
			return;
		}

		if(activeChar.isInStoreMode())
		{
			activeChar.sendPacket(new ExVariationCancelResult(0));
			return;
		}

		if(activeChar.isInTrade())
		{
			activeChar.sendPacket(new ExVariationCancelResult(0));
			return;
		}

		ItemInstance targetItem = activeChar.getInventory().getItemByObjectId(_targetItemObjId);

		// cannot remove augmentation from a not augmented item
		if(targetItem == null || !targetItem.isAugmented())
		{
			activeChar.sendPacket(new ExVariationCancelResult(0), SystemMsg.AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM);
			return;
		}

		// get the price
		long price = VariationUtils.getRemovePrice(targetItem);

		if(price < 0)
			activeChar.sendPacket(new ExVariationCancelResult(0));

		// try to reduce the players adena
		if(!activeChar.reduceAdena(price, true))
		{
			activeChar.sendPacket(new ExVariationCancelResult(0), SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		boolean equipped = false;
		if(equipped = targetItem.isEquipped())
			activeChar.getInventory().unEquipItem(targetItem);

		// remove the augmentation
		targetItem.setVariationStoneId(0);
		targetItem.setVariation1Id(0);
		targetItem.setVariation2Id(0);
		targetItem.setJdbcState(JdbcEntityState.UPDATED);
		targetItem.update();

		if(equipped)
			activeChar.getInventory().equipItem(targetItem);

		// send inventory update
		InventoryUpdatePacket iu = new InventoryUpdatePacket().addModifiedItem(activeChar, targetItem);

		// send system message
		SystemMessagePacket sm = new SystemMessagePacket(SystemMsg.AUGMENTATION_HAS_BEEN_SUCCESSFULLY_REMOVED_FROM_YOUR_S1);
		sm.addItemName(targetItem.getItemId());
		activeChar.sendPacket(new ExVariationCancelResult(1), iu, sm);

		for(ShortCut sc : activeChar.getAllShortCuts())
		{
			if(sc.getId() == targetItem.getObjectId() && sc.getType() == ShortCut.TYPE_ITEM)
				activeChar.sendPacket(new ShortCutRegisterPacket(activeChar, sc));
		}

		activeChar.sendChanges();
	}
}
package l2s.gameserver.network.l2.c2s;

import l2s.commons.util.Rnd;
import l2s.gameserver.data.xml.holder.SynthesisDataHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.s2c.ExEnchantFail;
import l2s.gameserver.network.l2.s2c.ExEnchantSucess;
import l2s.gameserver.templates.item.support.SynthesisData;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author Bonux
**/
public class RequestNewEnchantTry extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}

	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.isActionsDisabled()) // TODO: Check.
		{
			activeChar.sendPacket(ExEnchantFail.STATIC);
			return;
		}

		if(activeChar.isInStoreMode()) // TODO: Check.
		{
			activeChar.sendPacket(ExEnchantFail.STATIC);
			return;
		}

		if(activeChar.isInTrade()) // TODO: Check.
		{
			activeChar.sendPacket(ExEnchantFail.STATIC);
			return;
		}

		if(activeChar.isFishing()) // TODO: Check.
		{
			activeChar.sendPacket(ExEnchantFail.STATIC);
			return;
		}

		final ItemInstance item1 = activeChar.getSynthesisItem1();
		if(item1 == null)
		{
			activeChar.sendPacket(ExEnchantFail.STATIC);
			return;
		}

		final ItemInstance item2 = activeChar.getSynthesisItem2();
		if(item2 == null)
		{
			activeChar.sendPacket(ExEnchantFail.STATIC);
			return;
		}

		if(item1 == item2)
		{
			activeChar.sendPacket(ExEnchantFail.STATIC);
			return;
		}

		SynthesisData data = null;
		for(SynthesisData d : SynthesisDataHolder.getInstance().getDatas())
		{
			if(item1.getItemId() == d.getItem1Id() && item2.getItemId() == d.getItem2Id())
			{
				data = d;
				break;
			}

			if(item1.getItemId() == d.getItem2Id() && item2.getItemId() == d.getItem1Id())
			{
				data = d;
				break;
			}
		}

		if(data == null)
		{
			activeChar.sendPacket(ExEnchantFail.STATIC);
			return;
		}

		final Inventory inventory = activeChar.getInventory();

		inventory.writeLock();
		try
		{
			if(inventory.getItemByObjectId(item1.getObjectId()) == null)
			{
				activeChar.sendPacket(ExEnchantFail.STATIC);
				return;
			}

			if(inventory.getItemByObjectId(item2.getObjectId()) == null)
			{
				activeChar.sendPacket(ExEnchantFail.STATIC);
				return;
			}

			ItemFunctions.removeItem(activeChar, item1, 1, true);
			ItemFunctions.removeItem(activeChar, item2, 1, true);
			if(Rnd.chance(data.getChance()))
			{
				ItemFunctions.addItem(activeChar, data.getSynthesizedItemId(), 1, true);
				activeChar.sendPacket(new ExEnchantSucess(data.getSynthesizedItemId()));
			}
			else
			{
				ItemFunctions.addItem(activeChar, Rnd.chance(50) ? data.getItem1Id() : data.getItem2Id(), 1, true);
				activeChar.sendPacket(ExEnchantFail.STATIC);
			}
		}
		finally
		{
			inventory.writeUnlock();
		}
	}
}
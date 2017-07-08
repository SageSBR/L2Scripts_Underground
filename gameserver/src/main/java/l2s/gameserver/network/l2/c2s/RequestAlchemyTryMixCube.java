package l2s.gameserver.network.l2.c2s;

import gnu.trove.iterator.TIntLongIterator;
import gnu.trove.map.TIntLongMap;
import gnu.trove.map.hash.TIntLongHashMap;

import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExTryMixCube;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author Bonux
**/
public class RequestAlchemyTryMixCube extends L2GameClientPacket
{
	private TIntLongMap _items = null;

	@Override
	protected void readImpl()
	{
		int count = readD();

		_items = new TIntLongHashMap(count);

		for(int i = 0; i < count; i++)
		{
			int itemObjectId = readD();
			long itemCount = readQ();
			_items.put(itemObjectId, itemCount);
		}
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(_items == null || _items.isEmpty())
		{
			activeChar.sendPacket(ExTryMixCube.FAIL);
			return;
		}

		if(activeChar.isActionsDisabled())
		{
			activeChar.sendPacket(ExTryMixCube.FAIL);
			return;
		}

		if(activeChar.isInCombat())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_USE_ALCHEMY_DURING_BATTLE);
			activeChar.sendPacket(ExTryMixCube.FAIL);
			return;
		}

		if(activeChar.isInStoreMode() || activeChar.isInTrade())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_USE_ALCHEMY_WHILE_TRADING_OR_USING_A_PRIVATE_STORE_OR_SHOP);
			activeChar.sendPacket(ExTryMixCube.FAIL);
			return;
		}

		if(activeChar.isDead())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_USE_ALCHEMY_WHILE_DEAD);
			activeChar.sendPacket(ExTryMixCube.FAIL);
			return;
		}

		if(activeChar.isMovementDisabled())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_USE_ALCHEMY_WHILE_IMMOBILE);
			activeChar.sendPacket(ExTryMixCube.FAIL);
			return;
		}

		long totalPrice = 0;
		long elcyumCrystalCount = 0;

		final Inventory inventory = activeChar.getInventory();

		inventory.writeLock();
		try
		{
			for(TIntLongIterator iterator = _items.iterator(); iterator.hasNext();)
			{
				iterator.advance();

				int itemObjectId = iterator.key();
				long itemCount = iterator.value();

				ItemInstance item = inventory.getItemByObjectId(itemObjectId);
				if(item == null || item.getCount() < itemCount)
					continue;

				if(!item.canBeDestroyed(activeChar)) // Правильно ли?
					continue;

				if(item.getEnchantLevel() > 0)
					continue;

				if(item.isAugmented())
					continue;

				if(item.isShadowItem())
					continue;

				if(item.isTemporalItem())
					continue;

				if(item.getItemId() == ItemTemplate.ITEM_ID_ELCYUM_CRYSTAL)
				{
					if(_items.size() <= 3) // Не используем Кристалл Эльсиума, если было вставлено менее 3х предметов (не учитывая сам кристалл).
						continue;

					elcyumCrystalCount = itemCount;
				}
				else
				{
					long price = item.getTemplate().isAdena() ? itemCount : item.getReferencePrice();
					if(price <= 0)
						continue;

					totalPrice += price;
				}

				inventory.destroyItemByObjectId(itemObjectId, itemCount);

				activeChar.sendPacket(SystemMessagePacket.removeItems(item.getItemId(), itemCount));
			}
		}
		finally
		{
			inventory.writeUnlock();
		}

		long stoneCount = 0;
		if(totalPrice > 0)
		{
			if(_items.size() >= 3)
			{
				stoneCount = totalPrice / 10000;
				stoneCount += elcyumCrystalCount * 1000;
			}
			else
			{
				if(totalPrice >= 20000 && totalPrice < 35000)
					stoneCount = 1;
				else if(totalPrice >= 35000 && totalPrice < 50000)
					stoneCount = 2;
				else if(totalPrice >= 50000)
					stoneCount = (long) Math.floor(totalPrice / (15000. + (15000. / 9.)));
			}
		}

		stoneCount *= Config.ALCHEMY_MIX_CUBE_MODIFIER;

		if(stoneCount > 0)
			ItemFunctions.addItem(activeChar, ItemTemplate.ITEM_ID_AIR_STONE, stoneCount, true);

		/*if(elcyumCrystalCount > 0) // TODO: Реализовать.
			ItemFunctions.addItem(activeChar, ItemTemplate.ITEM_ID_TEMPEST_STONE, elcyumCrystalCount, true);*/

		activeChar.sendPacket(new ExTryMixCube(ItemTemplate.ITEM_ID_AIR_STONE, stoneCount));
	}
}
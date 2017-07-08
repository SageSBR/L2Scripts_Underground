package l2s.gameserver.network.l2.c2s;

import gnu.trove.iterator.TIntLongIterator;
import gnu.trove.map.TIntLongMap;
import gnu.trove.map.hash.TIntLongHashMap;

import l2s.commons.util.Rnd;
import l2s.gameserver.data.xml.holder.AlchemyDataHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExAlchemyConversion;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.item.AlchemyDataTemplate;
import l2s.gameserver.templates.item.AlchemyDataTemplate.AlchemyItem;
import l2s.gameserver.utils.ItemFunctions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
**/
public class RequestAlchemyConversion extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(RequestAlchemyConversion.class);

	private int _count;
	private int _skillId;
	private int _skillLevel;

	@Override
	protected void readImpl()
	{
		_count = readD();

		readH(); // UNK (10)

		_skillId = readD();
		_skillLevel = readD();

		/*TODO: Зачем? Если всю инфу берем с ДП.
		int ingridientsCount = readD();
		for(int i = 0; i < ingridientsCount; i++)
		{
			int ingridientId = readD();
			int ingridientCount = readD();
			int unk = readD();
		}*/
	}

	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(_count <= 0)
		{
			activeChar.sendPacket(ExAlchemyConversion.FAIL);
			return;
		}

		if(activeChar.isActionsDisabled())
		{
			activeChar.sendPacket(ExAlchemyConversion.FAIL);
			return;
		}

		if(activeChar.isInCombat())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_USE_ALCHEMY_DURING_BATTLE);
			activeChar.sendPacket(ExAlchemyConversion.FAIL);
			return;
		}

		if(activeChar.isInStoreMode() || activeChar.isInTrade())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_USE_ALCHEMY_WHILE_TRADING_OR_USING_A_PRIVATE_STORE_OR_SHOP);
			activeChar.sendPacket(ExAlchemyConversion.FAIL);
			return;
		}

		if(activeChar.isDead())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_USE_ALCHEMY_WHILE_DEAD);
			activeChar.sendPacket(ExAlchemyConversion.FAIL);
			return;
		}

		if(activeChar.isMovementDisabled())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_USE_ALCHEMY_WHILE_IMMOBILE);
			activeChar.sendPacket(ExAlchemyConversion.FAIL);
			return;
		}

		final Skill skill = SkillHolder.getInstance().getSkill(_skillId, _skillLevel);
		if(skill == null)
		{
			_log.warn(getClass().getSimpleName() + ": Error while alchemy: Cannot find alchemy skill[" + _skillId + "-" + _skillLevel + "]!");

			activeChar.sendPacket(ExAlchemyConversion.FAIL);
			return;
		}

		final AlchemyDataTemplate data = AlchemyDataHolder.getInstance().getData(skill);
		if(data == null)
		{
			_log.warn(getClass().getSimpleName() + ": Error while alchemy: Cannot find alchemy data[" + _skillId + "-" + _skillLevel + "]!");

			activeChar.sendPacket(ExAlchemyConversion.FAIL);
			return;
		}

		final AlchemyItem[] ingridients = data.getIngridients();
		final AlchemyItem[] onSuccessProducts = data.getOnSuccessProducts();
		final AlchemyItem[] onFailProducts = data.getOnFailProducts();

		final TIntLongMap deletedItems = new TIntLongHashMap();
		final TIntLongMap addedItems = new TIntLongHashMap();

		int convensionCount = _count;

		final Inventory inventory = activeChar.getInventory();

		inventory.writeLock();
		try
		{
			for(AlchemyItem ingridient : ingridients)
			{
				ItemInstance item = inventory.getItemByItemId(ingridient.getId());
				if(item == null || item.getCount() < ingridient.getCount())
				{
					activeChar.sendPacket(ExAlchemyConversion.FAIL);
					return;
				}
				convensionCount = Math.min(convensionCount, (int) Math.floor(item.getCount() / ingridient.getCount()));
			}

			for(AlchemyItem ingridient : ingridients)
			{
				long count = ingridient.getCount() * convensionCount;
				if(!inventory.destroyItemByItemId(ingridient.getId(), count))
					continue;//TODO audit

				long deleted = deletedItems.get(ingridient.getId());
				deletedItems.put(ingridient.getId(), deleted + count);
			}

		}
		finally
		{
			inventory.writeUnlock();
		}

		int successCount = 0;
		int failCount = 0;

		for(int i = 0; i < convensionCount; i++)
		{
			if(Rnd.chance(data.getSuccessRate()))
				successCount++;
			else
				failCount++;
		}

		if(successCount > 0)
		{
			for(AlchemyItem product : onSuccessProducts)
			{
				long count = product.getCount() * successCount;
				long deleted = deletedItems.get(product.getId());
				if(deleted > 0)
				{
					deletedItems.put(product.getId(), Math.max(0, deleted - count));

					long added = count - deleted;
					if(added > 0)
						addedItems.put(product.getId(), addedItems.get(product.getId()) + added);
				}
				else
					addedItems.put(product.getId(), addedItems.get(product.getId()) + count);
			}
		}

		if(failCount > 0)
		{
			for(AlchemyItem product : onFailProducts)
			{
				long count = product.getCount() * failCount;
				long deleted = deletedItems.get(product.getId());
				if(deleted > 0)
				{
					deletedItems.put(product.getId(), Math.max(0, deleted - count));

					long added = count - deleted;
					if(added > 0)
						addedItems.put(product.getId(), addedItems.get(product.getId()) + added);
				}
				else
					addedItems.put(product.getId(), addedItems.get(product.getId()) + count);
			}
		}

		for(TIntLongIterator iterator = deletedItems.iterator(); iterator.hasNext();)
		{
			iterator.advance();

			long count = iterator.value();
			if(count > 0)
				activeChar.sendPacket(SystemMessagePacket.removeItems(iterator.key(), count));
		}

		for(TIntLongIterator iterator = addedItems.iterator(); iterator.hasNext();)
		{
			iterator.advance();

			long count = iterator.value();
			if(count > 0)
				ItemFunctions.addItem(activeChar, iterator.key(), count, true);
		}

		if(successCount == 0 && failCount == 0)
			activeChar.sendPacket(ExAlchemyConversion.FAIL);
		else
			activeChar.sendPacket(new ExAlchemyConversion(successCount, failCount));
	}
}
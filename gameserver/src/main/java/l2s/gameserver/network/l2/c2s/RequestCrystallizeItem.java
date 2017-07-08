package l2s.gameserver.network.l2.c2s;

import java.util.Collection;

import l2s.commons.util.Rnd;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.item.support.CrystallizationInfo;
import l2s.gameserver.templates.item.support.CrystallizationInfo.CrystallizationItem;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Log;

public class RequestCrystallizeItem extends L2GameClientPacket
{
	private int _objectId;
	private long _count;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_count = readQ();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();

		if(activeChar == null)
		{
			return;
		}

		ItemInstance item = activeChar.getInventory().getItemByObjectId(_objectId);
		if(item == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(!item.canBeCrystallized(activeChar))
		{
			// На всякий пожарный..
			activeChar.sendPacket(SystemMsg.THIS_ITEM_CANNOT_BE_CRYSTALLIZED);
			return;
		}

		Log.LogItem(activeChar, Log.Crystalize, item);

		if(!activeChar.getInventory().destroyItemByObjectId(_objectId, _count))
		{
			activeChar.sendActionFailed();
			return;
		}

		activeChar.sendPacket(SystemMsg.THE_ITEM_HAS_BEEN_SUCCESSFULLY_CRYSTALLIZED);

		int crystalId = item.getGrade().getCrystalId();
		int crystalCount = item.getCrystalCountOnCrystallize();

		if(crystalId > 0 && crystalCount > 0)
		{
			// чё бля за бред нахуй??? тысячи кристаллов с любых предметов? какой мудак это придумал?
			//crystalCount = ItemHolder.getInstance().getTemplate(item.getItemId()).getReferencePrice() * 2 / (ItemHolder.getInstance().getTemplate(crystalId).getReferencePrice());
			//crystalCount = ItemHolder.getInstance().getTemplate(item.getItemId()).getReferencePrice() / (ItemHolder.getInstance().getTemplate(crystalId).getReferencePrice() * 2);

			ItemFunctions.addItem(activeChar, crystalId, crystalCount, true);
		}

		CrystallizationInfo info = item.getTemplate().getCrystallizationInfo();
		if(info != null)
		{
			Collection<CrystallizationItem> items = info.getItems();
			for(CrystallizationItem i : items)
			{
				if(Rnd.chance(i.getChance()))
				{
					ItemFunctions.addItem(activeChar, i.getItemId(), i.getCount(), true);
				}
			}
		}
		activeChar.sendChanges();
	}
}
package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.templates.item.support.CrystallizationInfo;
import l2s.gameserver.templates.item.support.CrystallizationInfo.CrystallizationItem;

/**
 * @author Bonux
 **/
public class ExGetCrystalizingEstimation extends L2GameServerPacket
{
	private List<CrystallizationItem> _crysItems;

	public ExGetCrystalizingEstimation(ItemInstance item)
	{
		_crysItems = new ArrayList<>();

		int crystalId = item.getGrade().getCrystalId();
		int crystalCount = item.getCrystalCountOnCrystallize();
		if(crystalId > 0 && crystalCount > 0)
		{
			//crystalCount = ItemHolder.getInstance().getTemplate(item.getItemId()).getReferencePrice() * 2 / ItemHolder.getInstance().getTemplate(crystalId).getReferencePrice();

			_crysItems.add(new CrystallizationItem(crystalId, crystalCount, 100.));
		}

		CrystallizationInfo info = item.getTemplate().getCrystallizationInfo();
		if(info != null)
		{
			_crysItems.addAll(info.getItems());
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_crysItems.size());
		for(CrystallizationItem item : _crysItems)
		{
			writeD(item.getItemId());
			writeQ(item.getCount());
			writeF(item.getChance());
		}
	}
}
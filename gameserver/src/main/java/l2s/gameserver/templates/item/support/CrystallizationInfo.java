package l2s.gameserver.templates.item.support;

import java.util.Collection;
import java.util.TreeMap;

public class CrystallizationInfo
{
	private final TreeMap<Integer, CrystallizationItem> _items;

	public static class CrystallizationItem
	{
		private final int _itemId;
		private final int _count;
		private final double _chance;

		public CrystallizationItem(int itemId, int count, double chance)
		{
			_itemId = itemId;
			_count = count;
			_chance = chance;
		}

		public int getItemId()
		{
			return _itemId;
		}

		public int getCount()
		{
			return _count;
		}

		public double getChance()
		{
			return _chance;
		}
	}

	public CrystallizationInfo()
	{
		_items = new TreeMap<Integer, CrystallizationItem>();
	}

	public void addItem(CrystallizationItem item)
	{
		if(item.getCount() > 0)
			_items.put(_items.size() + 1, item);
	}

	public Collection<CrystallizationItem> getItems()
	{
		return _items.values();
	}
}

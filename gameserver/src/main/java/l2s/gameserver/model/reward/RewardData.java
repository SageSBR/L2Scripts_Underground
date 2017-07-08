package l2s.gameserver.model.reward;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.math.SafeMath;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.templates.item.ItemTemplate;

/**
 * @reworked by Bonux
**/
public class RewardData implements Cloneable
{
	private ItemTemplate _item;
	private boolean _notRate = false; // Рейты к вещи не применяются

	private long _mindrop;
	private long _maxdrop;
	private double _chance;

	public RewardData(int itemId)
	{
		_item = ItemHolder.getInstance().getTemplate(itemId);
		if(_item.isArrow() || _item.isBolt() // стрелы не рейтуются
				|| (Config.NO_RATE_EQUIPMENT && _item.isEquipment()) // отключаемая рейтовка эквипа
				|| (Config.NO_RATE_KEY_MATERIAL && _item.isKeyMatherial()) // отключаемая рейтовка ключевых материалов
				|| (Config.NO_RATE_RECIPES && _item.isRecipe()) // отключаемая рейтовка рецептов
				|| ArrayUtils.contains(Config.NO_RATE_ITEMS, itemId)) // индивидаульная отключаемая рейтовка для списка предметов
			_notRate = true;
	}

	public RewardData(int itemId, long min, long max, double chance)
	{
		this(itemId);
		_mindrop = min;
		_maxdrop = max;
		setChance(chance);
	}

	public boolean notRate()
	{
		return _notRate;
	}

	public void setNotRate(boolean notRate)
	{
		_notRate = notRate;
	}

	public int getItemId()
	{
		return _item.getItemId();
	}

	public ItemTemplate getItem()
	{
		return _item;
	}

	public long getMinDrop()
	{
		return _mindrop;
	}

	public long getMaxDrop()
	{
		return _maxdrop;
	}

	public double getChance()
	{
		return _chance;
	}

	public void setMinDrop(long mindrop)
	{
		_mindrop = mindrop;
	}

	public void setMaxDrop(long maxdrop)
	{
		_maxdrop = maxdrop;
	}

	public void setChance(double chance)
	{
		_chance = Math.min(chance, RewardList.MAX_CHANCE);
	}

	@Override
	public String toString()
	{
		return "ItemID: " + getItem() + " Min: " + getMinDrop() + " Max: " + getMaxDrop() + " Chance: " + getChance() / 10000.0 + "%";
	}

	@Override
	public RewardData clone()
	{
		return new RewardData(getItemId(), getMinDrop(), getMaxDrop(), getChance());
	}

	@Override
	public boolean equals(Object o)
	{
		if(o instanceof RewardData)
		{
			RewardData drop = (RewardData) o;
			return drop.getItemId() == getItemId();
		}
		return false;
	}

	@Override     
	public int hashCode()
	{
		return 18 * getItemId() + 184140;
	}

	/**
	 * Подсчет шанса выпадения этой конкретной вещи
	 * Используется в эвентах и некоторых специальных механизмах
	 * @param player игрок (его бонус влияет на шанс)
	 * @param mod (просто множитель шанса)
	 * @return информация о выпавшей вещи
	 */
	public RewardItem roll(Player player, double mod)
	{
		double rate = _item.isAdena() ? player.getRateAdena() : player.getRateItems();
		return roll(rate * mod);
	}

	/**
	 * Подсчет шанса выпадения этой конкретной вещи
	 * Используется в эвентах и некоторых специальных механизмах
	 * @param rate множитель количества
	 * @return информация о выпавшей вещи
	 */
	public RewardItem roll(double rate)
	{
		if(rate > 0)
		{
			if(notRate())
				rate = 1.;

			int rolledCount = 0;
			double mult = Math.ceil(rate);
			for(int n = 0; n < mult; n++) // TODO: Реально ли оптимизировать без цикла?
			{
				if(getChance() * Math.min(rate - n, 1.0) > Rnd.get(RewardList.MAX_CHANCE))
					rolledCount++;
			}

			if(rolledCount > 0)
			{
				RewardItem t = new RewardItem(_item.getItemId());
				if(getMinDrop() >= getMaxDrop())
					t.count = rolledCount * getMinDrop();
				else
					t.count = rolledCount * Rnd.get(getMinDrop(), getMaxDrop());
				return t;
			}
		}
		return null;
	}
}
package l2s.gameserver.model.reward;

import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.model.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @reworked by Bonux
**/
public class RewardGroup implements Cloneable
{
	private double _chance;
	private boolean _isAdena = false; // Шанс фиксирован, растет только количество
	private boolean _notRate = false; // Рейты вообще не применяются
	private List<RewardData> _items = new ArrayList<RewardData>();

	public RewardGroup(double chance)
	{
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

	public double getChance()
	{
		return _chance;
	}

	public void setChance(double chance)
	{
		_chance = Math.min(chance, RewardList.MAX_CHANCE);
	}

	public boolean isAdena()
	{
		return _isAdena;
	}

	public void setIsAdena(boolean isAdena)
	{
		_isAdena = isAdena;
	}

	public void addData(RewardData item)
	{
		if(item.getItem().isAdena())
			_isAdena = true;
		_items.add(item);
	}

	/**
	 * Возвращает список вещей
	 */
	public List<RewardData> getItems()
	{
		return _items;
	}

	/**
	 * Возвращает полностью независимую копию группы
	 */
	@Override
	public RewardGroup clone()
	{
		RewardGroup ret = new RewardGroup(_chance);
		for(RewardData i : _items)
			ret.addData(i.clone());
		return ret;
	}

	/**
	 * Функция используется в основном механизме расчета дропа, выбирает одну/несколько вещей из группы, в зависимости от рейтов
	 * 
	 */
	public RewardItem roll(RewardType type, Player player, double mod, boolean isRaid, boolean isSiegeGuard)
	{
		switch(type)
		{
			case NOT_RATED_GROUPED:
			case NOT_RATED_NOT_GROUPED:
				return rollItems(mod, 1.0);
			case SWEEP:
				return rollItems(mod, player.getRateSpoil());
			case RATED_GROUPED:
				if(_isAdena)
					return rollItems(mod, player.getRateAdena());

				if(isRaid)
					return rollItems(mod, player.getRaidRateItems());

				if(isSiegeGuard)
					return rollItems(mod, Config.RATE_DROP_SIEGE_GUARD);

				return rollItems(mod, player.getRateItems());
			default:
				return null;
		}
	}

	public RewardItem rollItems(double mod, double rate)
	{
		rate *= mod;

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
				List<RewardItem> ret = new ArrayList<RewardItem>();
				for(RewardData data : getItems())
				{
					RewardItem item = data.roll(rolledCount);
					if(item != null)
						ret.add(item);
				}
				return Rnd.get(ret);
			}
		}
		return null;
	}
}
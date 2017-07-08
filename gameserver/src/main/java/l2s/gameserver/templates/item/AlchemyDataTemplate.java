package l2s.gameserver.templates.item;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bonux
**/
public final class AlchemyDataTemplate
{
	public static class AlchemyItem
	{
		private final int _id;
		private final int _count;

		public AlchemyItem(int id, int count)
		{
			_id = id;
			_count = count;
		}

		public int getId()
		{
			return _id;
		}

		public long getCount()
		{
			return _count;
		}
	}

	private final int _skillId;
	private final int _skillLevel;
	private final int _successRate;

	private final List<AlchemyItem> _ingridients = new ArrayList<AlchemyItem>();
	private final List<AlchemyItem> _onSuccessProducts = new ArrayList<AlchemyItem>();
	private final List<AlchemyItem> _onFailProducts = new ArrayList<AlchemyItem>();

	public AlchemyDataTemplate(int skillId, int skillLevel, int successRate)
	{
		_skillId = skillId;
		_skillLevel = skillLevel;
		_successRate = successRate;
	}

	public int getSkillId()
	{
		return _skillId;
	}

	public int getSkillLevel()
	{
		return _skillLevel;
	}

	public int getSuccessRate()
	{
		return _successRate;
	}

	public void addIngridient(AlchemyItem ingridient)
	{
		_ingridients.add(ingridient);
	}

	public AlchemyItem[] getIngridients()
	{
		return _ingridients.toArray(new AlchemyItem[_ingridients.size()]);
	}

	public void addOnSuccessProduct(AlchemyItem product)
	{
		_onSuccessProducts.add(product);
	}

	public AlchemyItem[] getOnSuccessProducts()
	{
		return _onSuccessProducts.toArray(new AlchemyItem[_onSuccessProducts.size()]);
	}

	public void addOnFailProduct(AlchemyItem product)
	{
		_onFailProducts.add(product);
	}

	public AlchemyItem[] getOnFailProducts()
	{
		return _onFailProducts.toArray(new AlchemyItem[_onFailProducts.size()]);
	}
}
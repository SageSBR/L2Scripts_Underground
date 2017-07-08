package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;
import l2s.commons.data.xml.AbstractFileParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.EnchantStoneHolder;
import l2s.gameserver.templates.item.ItemGrade;
import l2s.gameserver.templates.item.support.EnchantStone;
import l2s.gameserver.templates.item.support.EnchantType;
import l2s.gameserver.templates.item.support.FailResultType;

/**
 * @author Bonux
**/
public class EnchantStoneParser extends AbstractFileParser<EnchantStoneHolder>
{
	private static EnchantStoneParser _instance = new EnchantStoneParser();

	public static EnchantStoneParser getInstance()
	{
		return _instance;
	}

	private EnchantStoneParser()
	{
		super(EnchantStoneHolder.getInstance());
	}

	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "data/enchant_stones.xml");
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		int defaultMinEnchantLevel = 0;
		int defaultMinFullbodyEnchantLevel = 0;
		int defaultMaxEnchantLevel = 0;
		FailResultType defaultResultType  = FailResultType.CRYSTALS;

		Element defaultElement = rootElement.element("default");
		if(defaultElement != null)
		{
			defaultResultType = FailResultType.valueOf(defaultElement.attributeValue("on_fail"));
			defaultMinEnchantLevel = Integer.parseInt(defaultElement.attributeValue("min_enchant_level"));
			defaultMinFullbodyEnchantLevel = Integer.parseInt(defaultElement.attributeValue("min_fullbody_enchant_level"));
			defaultMaxEnchantLevel = Integer.parseInt(defaultElement.attributeValue("max_enchant_level"));
		}

		for(Iterator<Element> iterator = rootElement.elementIterator("enchant_stone"); iterator.hasNext();)
		{
			Element enchantStoneElement = iterator.next();
			final int itemId = Integer.parseInt(enchantStoneElement.attributeValue("id"));
			final double chance = Integer.parseInt(enchantStoneElement.attributeValue("chance"));
			final ItemGrade grade = enchantStoneElement.attributeValue("grade") == null ? ItemGrade.NONE : ItemGrade.valueOf(enchantStoneElement.attributeValue("grade"));
			final EnchantType type = enchantStoneElement.attributeValue("type") == null ? EnchantType.ALL : EnchantType.valueOf(enchantStoneElement.attributeValue("type"));
			final FailResultType resultType = enchantStoneElement.attributeValue("on_fail") == null ? defaultResultType : FailResultType.valueOf(enchantStoneElement.attributeValue("on_fail"));
			final int minEnchantLevel = enchantStoneElement.attributeValue("min_enchant_level") == null ? defaultMinEnchantLevel : Integer.parseInt(enchantStoneElement.attributeValue("min_enchant_level"));
			final int minFullbodyEnchantLevel = enchantStoneElement.attributeValue("min_fullbody_enchant_level") == null ? Math.max(minEnchantLevel, defaultMinFullbodyEnchantLevel) : Integer.parseInt(enchantStoneElement.attributeValue("min_fullbody_enchant_level"));
			final int maxEnchantLevel = enchantStoneElement.attributeValue("max_enchant_level") == null ? defaultMaxEnchantLevel : Integer.parseInt(enchantStoneElement.attributeValue("max_enchant_level"));
			final int minEnchantStep = enchantStoneElement.attributeValue("min_enchant_step") == null ? 1 : Integer.parseInt(enchantStoneElement.attributeValue("min_enchant_step"));
			final int maxEnchantStep = enchantStoneElement.attributeValue("max_enchant_step") == null ? 1 : Integer.parseInt(enchantStoneElement.attributeValue("max_enchant_step"));

			getHolder().addEnchantStone(new EnchantStone(itemId, chance, type, grade, resultType, minEnchantLevel, minFullbodyEnchantLevel, maxEnchantLevel, minEnchantStep, maxEnchantStep));
		}
	}
}

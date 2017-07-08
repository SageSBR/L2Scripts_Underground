package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;
import l2s.commons.data.xml.AbstractFileParser;
import l2s.commons.string.StringArrayUtils;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.EnchantItemHolder;
import l2s.gameserver.templates.item.ItemGrade;
import l2s.gameserver.templates.item.support.EnchantScroll;
import l2s.gameserver.templates.item.support.EnchantType;
import l2s.gameserver.templates.item.support.EnchantVariation;
import l2s.gameserver.templates.item.support.EnchantVariation.EnchantLevel;
import l2s.gameserver.templates.item.support.FailResultType;

/**
 * @author VISTALL
 * @date 3:10/18.06.2011
 */
public class EnchantItemParser extends AbstractFileParser<EnchantItemHolder>
{
	private static EnchantItemParser _instance = new EnchantItemParser();

	public static EnchantItemParser getInstance()
	{
		return _instance;
	}

	private EnchantItemParser()
	{
		super(EnchantItemHolder.getInstance());
	}

	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "data/enchant_items.xml");
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		int defaultMaxEnchant = 0;
		boolean defaultFailEffect = false;

		Element defaultElement = rootElement.element("default");
		if(defaultElement != null)
		{
			defaultMaxEnchant = Integer.parseInt(defaultElement.attributeValue("max_enchant"));
			defaultFailEffect = Boolean.parseBoolean(defaultElement.attributeValue("show_fail_effect"));
		}

		for(Iterator<Element> iterator1 = rootElement.elementIterator("chance_variations"); iterator1.hasNext();)
		{
			Element element1 = iterator1.next();
			for(Iterator<Element> iterator2 = element1.elementIterator("variation"); iterator2.hasNext();)
			{
				Element element2 = iterator2.next();

				EnchantVariation variation = new EnchantVariation(Integer.parseInt(element2.attributeValue("id")));
				for(Iterator<Element> iterator3 = element2.elementIterator("enchant"); iterator3.hasNext();)
				{
					Element element3 = iterator3.next();

					final int[] enchantLvl = StringArrayUtils.stringToIntArray(element3.attributeValue("level"), "-");
					final double baseChance = Double.parseDouble(element3.attributeValue("base_chance"));
					final double magicWeaponChance = element3.attributeValue("magic_weapon_chance") == null ? baseChance : Double.parseDouble(element3.attributeValue("magic_weapon_chance"));
					final double fullBodyChance = element3.attributeValue("full_body_armor_chance") == null ? baseChance : Double.parseDouble(element3.attributeValue("full_body_armor_chance"));
					final boolean succVisualEffect = element3.attributeValue("success_visual_effect") == null ? false : Boolean.parseBoolean(element3.attributeValue("success_visual_effect"));
					if(enchantLvl.length == 2)
					{
						for(int i = enchantLvl[0]; i <= enchantLvl[1]; i++)
							variation.addLevel(new EnchantLevel(i, baseChance, magicWeaponChance, fullBodyChance, succVisualEffect));
					}
					else
						variation.addLevel(new EnchantLevel(enchantLvl[0], baseChance, magicWeaponChance, fullBodyChance, succVisualEffect));
				}
				getHolder().addEnchantVariation(variation);
			}
		}

		for(Iterator<Element> iterator = rootElement.elementIterator("enchant_scroll"); iterator.hasNext();)
		{
			Element enchantItemElement = iterator.next();
			final int itemId = Integer.parseInt(enchantItemElement.attributeValue("id"));
			final int variation = Integer.parseInt(enchantItemElement.attributeValue("variation"));
			final int maxEnchant = enchantItemElement.attributeValue("max_enchant") == null ? defaultMaxEnchant : Integer.parseInt(enchantItemElement.attributeValue("max_enchant"));
			final FailResultType resultType = FailResultType.valueOf(enchantItemElement.attributeValue("on_fail"));
			final EnchantType enchantType = enchantItemElement.attributeValue("type") == null ? EnchantType.ALL : EnchantType.valueOf(enchantItemElement.attributeValue("type"));
			final ItemGrade grade = enchantItemElement.attributeValue("grade") == null ? ItemGrade.NONE : ItemGrade.valueOf(enchantItemElement.attributeValue("grade"));
			final boolean failEffect = enchantItemElement.attributeValue("show_fail_effect") == null ? defaultFailEffect : Boolean.parseBoolean(enchantItemElement.attributeValue("show_fail_effect"));
			final int minEnchantStep = enchantItemElement.attributeValue("min_enchant_step") == null ? 1 : Integer.parseInt(enchantItemElement.attributeValue("min_enchant_step"));
			final int maxEnchantStep = enchantItemElement.attributeValue("max_enchant_step") == null ? 1 : Integer.parseInt(enchantItemElement.attributeValue("max_enchant_step"));

			EnchantScroll item = new EnchantScroll(itemId, variation, maxEnchant, enchantType, grade, resultType, failEffect, minEnchantStep, maxEnchantStep);

			for(Iterator<Element> iterator2 = enchantItemElement.elementIterator(); iterator2.hasNext();)
			{
				Element element2 = iterator2.next();
				if(element2.getName().equals("item_list"))
				{
					for(Element e : element2.elements())
						item.addItemId(Integer.parseInt(e.attributeValue("id")));
				}
			}
			getHolder().addEnchantScroll(item);
		}
	}
}

package l2s.gameserver.data.xml.parser;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;
import l2s.commons.data.xml.AbstractFileParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.SkillEnchantInfoHolder;
import l2s.gameserver.templates.SkillEnchantInfo;

/**
 * @author Bonux
**/
public final class SkillEnchantInfoParser extends AbstractFileParser<SkillEnchantInfoHolder>
{
	private static final SkillEnchantInfoParser _instance = new SkillEnchantInfoParser();

	public static SkillEnchantInfoParser getInstance()
	{
		return _instance;
	}

	protected SkillEnchantInfoParser()
	{
		super(SkillEnchantInfoHolder.getInstance());
	}

	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "data/skill_enchant_info.xml");
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element levelElement = iterator.next();

			int level = Integer.parseInt(levelElement.attributeValue("level"));
			long adena = Long.parseLong(levelElement.attributeValue("adena"));
			long sp = Long.parseLong(levelElement.attributeValue("sp"));
			int success_rate = Integer.parseInt(levelElement.attributeValue("success_rate"));
			int normal_enchant_item_id = levelElement.attributeValue("normal_enchant_item_id") == null ? 0 : Integer.parseInt(levelElement.attributeValue("normal_enchant_item_id"));
			int blessed_enchant_item_id = levelElement.attributeValue("blessed_enchant_item_id") == null ? 0 : Integer.parseInt(levelElement.attributeValue("blessed_enchant_item_id"));
			int change_enchant_item_id = levelElement.attributeValue("change_enchant_item_id") == null ? 0 : Integer.parseInt(levelElement.attributeValue("change_enchant_item_id"));
			int safe_enchant_item_id = levelElement.attributeValue("safe_enchant_item_id") == null ? 0 : Integer.parseInt(levelElement.attributeValue("safe_enchant_item_id"));

			getHolder().addInfo(new SkillEnchantInfo(level, adena, sp, success_rate, normal_enchant_item_id, blessed_enchant_item_id, change_enchant_item_id, safe_enchant_item_id));
		}
	}
}

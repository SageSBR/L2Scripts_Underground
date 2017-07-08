package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;
import l2s.commons.data.xml.AbstractFileParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.SynthesisDataHolder;
import l2s.gameserver.templates.item.support.SynthesisData;

/**
 * @author Bonux
**/
public class SynthesisDataParser extends AbstractFileParser<SynthesisDataHolder>
{
	private static SynthesisDataParser _instance = new SynthesisDataParser();

	public static SynthesisDataParser getInstance()
	{
		return _instance;
	}

	private SynthesisDataParser()
	{
		super(SynthesisDataHolder.getInstance());
	}

	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "data/synthesis_data.xml");
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator("synthesis"); iterator.hasNext();)
		{
			final Element synthesisElement = iterator.next();

			final int item_1_id = Integer.parseInt(synthesisElement.attributeValue("item_1_id"));
			final int item_2_id = Integer.parseInt(synthesisElement.attributeValue("item_2_id"));
			final double chance = Double.parseDouble(synthesisElement.attributeValue("chance"));
			final int synthesized_item_id = Integer.parseInt(synthesisElement.attributeValue("synthesized_item_id"));

			getHolder().addData(new SynthesisData(item_1_id, item_2_id, chance, synthesized_item_id));
		}
	}
}

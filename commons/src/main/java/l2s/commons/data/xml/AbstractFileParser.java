package l2s.commons.data.xml;

import java.io.File;
import java.io.FileInputStream;

/**
 * Author: VISTALL
 * Date:  20:52/30.11.2010
 */
public abstract class AbstractFileParser<H extends AbstractHolder> extends AbstractParser<H>
{
	protected AbstractFileParser(H holder)
	{
		super(holder);
	}

	public abstract File getXMLFile();

	@Override
	protected final void parse()
	{
		File file = getXMLFile();

		if(!file.exists())
		{
			warn("file " + file.getAbsolutePath() + " not exists");
			return;
		}

		try
		{
			parseDocument(new FileInputStream(file), file.getName());
		}
		catch(Exception e)
		{
			warn("Exception: " + e, e);
		}
		afterParseActions();
	}
}

package l2s.commons.data.xml;

import l2s.commons.data.xml.helpers.ErrorHandlerImpl;
import l2s.commons.logging.LoggerObject;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;

/**
 * Author: VISTALL
 * Date:  18:35/30.11.2010
 */
public abstract class AbstractParser<H extends AbstractHolder> extends LoggerObject
{
	protected final H _holder;

	protected String _currentFile;
	protected SAXReader _reader;

	protected AbstractParser(H holder)
	{
		_holder = holder;
		_reader = new SAXReader();
		_reader.setValidation(false);
		_reader.setErrorHandler(new ErrorHandlerImpl(this));
	}

	protected void parseDocument(InputStream f, String name) throws Exception
	{
		_currentFile = name;

		org.dom4j.Document document = _reader.read(f);

		readData(document.getRootElement());
	}

	protected abstract void readData(Element rootElement) throws Exception;

	protected abstract void parse();

	protected void afterParseActions()
	{}

	protected H getHolder()
	{
		return _holder;
	}

	public String getCurrentFileName()
	{
		return _currentFile;
	}

	public void load()
	{
		parse();
		_holder.process();
		_holder.log();
	}

	public void reload()
	{
		info("reload start...");
		_holder.clear();
		load();
	}
}

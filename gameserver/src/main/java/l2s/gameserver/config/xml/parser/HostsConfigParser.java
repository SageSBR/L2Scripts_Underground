package l2s.gameserver.config.xml.parser;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2s.commons.data.xml.AbstractFileParser;
import l2s.gameserver.config.templates.HostInfo;
import l2s.gameserver.config.xml.holder.HostsConfigHolder;

/**
 * @author Bonux
**/
public final class HostsConfigParser extends AbstractFileParser<HostsConfigHolder>
{
	private static final Logger _log = LoggerFactory.getLogger(HostsConfigParser.class);

	private static final HostsConfigParser _instance = new HostsConfigParser();

	public static HostsConfigParser getInstance()
	{
		return _instance;
	}

	protected HostsConfigParser()
	{
		super(HostsConfigHolder.getInstance());
	}

	@Override
	public File getXMLFile()
	{
		return new File("config/hostsconfig.xml");
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element element = iterator.next();

			if("authserver".equalsIgnoreCase(element.getName()))
			{
				final String ip = element.attributeValue("ip");
				final int port = Integer.parseInt(element.attributeValue("port"));
				getHolder().setAuthServerHost(new HostInfo(ip, port));
			}
			else if("gameserver".equalsIgnoreCase(element.getName()))
			{
				for(Iterator<Element> subIterator = element.elementIterator("host"); subIterator.hasNext();)
				{
					Element subElement = subIterator.next();

					final int id = Integer.parseInt(subElement.attributeValue("id"));
					final String ip = subElement.attributeValue("ip");
					final String inner_ip = subElement.attributeValue("inner_ip");
					final int port = Integer.parseInt(subElement.attributeValue("port"));
					final String key = subElement.attributeValue("key");
					getHolder().addGameServerHost(new HostInfo(id, ip, inner_ip, port, key));
				}
			}
		}
	}

	@Override
	protected void afterParseActions()
	{
		if(getHolder().getAuthServerHost() == null)
		{
			_log.error("Could not load authserver host config. Configure your hostsconfig.xml!");
			Runtime.getRuntime().exit(0);
		}

		if(getHolder().getGameServerHosts().length == 0)
		{
			_log.error("Could not load gameserver host config. Configure your hostsconfig.xml!");
			Runtime.getRuntime().exit(0);
		}
	}
}
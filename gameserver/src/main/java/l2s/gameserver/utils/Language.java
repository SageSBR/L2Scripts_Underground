package l2s.gameserver.utils;

import l2s.gameserver.Config;

/**
 * @author B0nux
 * @date 16:03/10.10.2011
 */
public enum Language
{
	// With offical client support
	KOREAN(9, "ko"),
	ENGLISH(1, "en"),
	CHINESE(4, "zh"),
	THAI(5, "th"),
	RUSSIAN(8, "ru"),
	// Custom
	PORTUGUESE(-1, "pt"),
	SPANISH(-2, "es"),
	ARABIC(-3, "ar"),
	GREEK(-4, "el"),
	GEORGIAN(-5, "ka"),
	HUNGARIAN(-6, "hu"),
	FINNISH(-7, "fi"),
	UKRAINIAN(-8, "uk"),
	VIETNAMESE(-9, "vi");

	public static final Language[] VALUES = values();

	public static final String LANG_VAR = "lang@";

	private final int _id;
	private final String _shortName;

	private Language(int id, String shortName)
	{
		_id = id;
		_shortName = shortName;
	}

	public int getId()
	{
		return _id;
	}

	public String getShortName()
	{
		return _shortName;
	}

	public static Language getLanguage(int langId)
	{
		for(Language lang : VALUES)
			if(lang.getId() == langId)
				return lang;
		return Config.DEFAULT_LANG;
	}

	public static Language getLanguage(String shortName)
	{
		if(shortName != null)
		{
			for(Language lang : VALUES)
			{
				if(lang.getShortName().equalsIgnoreCase(shortName))
					return lang;
			}
		}
		return Config.DEFAULT_LANG;
	}
}
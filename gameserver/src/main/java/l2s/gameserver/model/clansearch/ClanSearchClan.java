package l2s.gameserver.model.clansearch;

import l2s.gameserver.model.clansearch.base.ClanSearchListType;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class ClanSearchClan
{
	private int _clanId;
	private ClanSearchListType _searchType;
	private String _title;
	private String _desc;

	public ClanSearchClan(int clanId, ClanSearchListType searchType, String title, String desc)
	{
		_clanId = clanId;
		_searchType = searchType;
		_title = title;
		_desc = desc;
	}

	public int getClanId()
	{
		return _clanId;
	}

	public ClanSearchListType getSearchType()
	{
		return _searchType;
	}

	public void setSearchType(ClanSearchListType searchType)
	{
		_searchType = searchType;
	}

	public String getTitle()
	{
		return _title;
	}

	public void setTitle(String title)
	{
		_title = title;
	}

	public String getDesc()
	{
		return _desc;
	}

	public void setDesc(String desc)
	{
		_desc = desc;
	}
}
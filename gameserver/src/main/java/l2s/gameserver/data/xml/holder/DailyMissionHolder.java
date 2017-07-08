package l2s.gameserver.data.xml.holder;

import java.util.List;
import java.util.Map;

import l2s.commons.data.xml.AbstractHolder;

/**
 * @author Mobius
 */
public class DailyMissionHolder extends AbstractHolder
{
	private final int _id;
	private final int _clientId;
	private final String _type;
	private final int _level;
	private final List<Integer> _classes;
	private final Map<Integer, Integer> _rewards;
	
	public DailyMissionHolder(int id, int clientId, String type, int level, List<Integer> classes, Map<Integer, Integer> rewards)
	{
		_id = id;
		_clientId = clientId;
		_type = type;
		_level = level;
		_classes = classes;
		_rewards = rewards;
	}
	
	/**
	 * @return the id
	 */
	public int getId()
	{
		return _id;
	}
	
	/**
	 * @return the clientId
	 */
	public int getClientId()
	{
		return _clientId;
	}
	
	/**
	 * @return the type
	 */
	public String getType()
	{
		return _type;
	}
	
	/**
	 * @return the level
	 */
	public int getLevel()
	{
		return _level;
	}
	
	/**
	 * @return the classes
	 */
	public List<Integer> getAvailableClasses()
	{
		return _classes;
	}
	
	/**
	 * @return the rewards
	 */
	public Map<Integer, Integer> getRewards()
	{
		return _rewards;
	}

	@Override
	public int size()
	{
		return 0;
	}

	@Override
	public void clear()
	{
		_classes.clear();
		_rewards.clear();
	}
}

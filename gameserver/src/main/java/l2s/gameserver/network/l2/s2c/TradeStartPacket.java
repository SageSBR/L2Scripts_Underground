package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInfo;
import l2s.gameserver.model.items.ItemInstance;

/**
 * @reworked to Ertheia by Bonux
**/
public class TradeStartPacket extends L2GameServerPacket
{
	private static final int IS_FRIEND = 1 << 0;
	private static final int CLAN_MEMBER = 1 << 1;
	private static final int IS_MENTEE_OR_MENTOR = 1 << 2;
	private static final int ALLY_MEMBER = 1 << 3;

	private final List<ItemInfo> _tradelist = new ArrayList<ItemInfo>();
	private final int _targetId;
	private final int _targetLevel;

	private int _flags = 0;

	public TradeStartPacket(Player player, Player target)
	{
		_targetId = target.getObjectId();
		_targetLevel = target.getLevel();

		if(player.getFriendList().contains(target.getObjectId()))
			_flags |= IS_FRIEND;

		if(player.getClan() != null && player.getClan() == target.getClan())
			_flags |= CLAN_MEMBER;

		if(player.getMenteeList().getMentor() == target.getObjectId() || target.getMenteeList().getMentor() == player.getObjectId())
			_flags |= IS_MENTEE_OR_MENTOR;

		if(player.getAlliance() != null && player.getAlliance() == target.getAlliance())
			_flags |= ALLY_MEMBER;

		ItemInstance[] items = player.getInventory().getItems();
		for(ItemInstance item : items)
			if(item.canBeTraded(player))
				_tradelist.add(new ItemInfo(item, item.getTemplate().isBlocked(player, item)));
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_targetId);
		writeC(_flags); // UNK
		writeC(_targetLevel);
		writeH(_tradelist.size());
		for(ItemInfo item : _tradelist)
			writeItemInfo(item);
	}
}
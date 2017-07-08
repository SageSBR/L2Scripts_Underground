package l2s.gameserver.network.l2.s2c;

/**
 *
 * @author monithly
 */
public class ExMagicAttackInfo extends L2GameServerPacket
{
	public static final int NORMAL = 0;
	public static final int CRIT = 1;
	public static final int CRIT_ADD = 2;
	public static final int OVERHIT = 3;
	public static final int MISS = 4;
	public static final int BLOCK = 5;
	public static final int RESIST = 6;
	public static final int IMMUNE = 7;

	private final int _attackerId, _targetId, _info;

	public ExMagicAttackInfo(int attackerId, int targetId, int info)
	{
		_attackerId = attackerId;
		_targetId = targetId;
		_info = info;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_attackerId);
		writeD(_targetId);
		writeD(_info);
	}
}

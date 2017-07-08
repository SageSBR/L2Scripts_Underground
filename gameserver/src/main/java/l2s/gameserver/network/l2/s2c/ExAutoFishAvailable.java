package l2s.gameserver.network.l2.s2c;

public final class ExAutoFishAvailable extends L2GameServerPacket
{
	public static final L2GameServerPacket SHOW = new ExAutoFishAvailable(1);
	public static final L2GameServerPacket REMOVE = new ExAutoFishAvailable(0);

	private final int _type;

	public ExAutoFishAvailable(int type)
	{
		_type = type;
	}

	@Override
	protected void writeImpl()
	{
		writeC(_type);
	}
}
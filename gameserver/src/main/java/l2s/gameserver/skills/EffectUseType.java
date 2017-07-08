package l2s.gameserver.skills;

/**
 * @author Bonux
**/
public enum EffectUseType
{
	START(false),
	START_INSTANT(true),
	TICK(false),
	TICK_INSTANT(true),
	NORMAL(false),
	NORMAL_INSTANT(true),
	SELF(false),
	SELF_INSTANT(true);

	public static final EffectUseType[] VALUES = values();

	private final boolean _instant;

	private EffectUseType(boolean instant)
	{
		_instant = instant;
	}

	public boolean isInstant()
	{
		return _instant;
	}
}
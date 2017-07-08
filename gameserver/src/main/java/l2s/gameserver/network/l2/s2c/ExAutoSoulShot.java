package l2s.gameserver.network.l2.s2c;

public class ExAutoSoulShot extends L2GameServerPacket {
    public enum Type {
        Soulshot(0),
        Spiritshot(1),
        BeastSoulshot(2),
        BeastSpiritshot(3),
        MaxType(4);

        private int type;

        private Type(int type) {
            this.type = type;
        }
    }

    private final int itemId;
    private final int type;
    private final boolean enabled;

    public ExAutoSoulShot(int itemId, int type) {
        this.itemId = itemId;
        this.type = type;
        this.enabled = true;
    }

    public ExAutoSoulShot(int itemId, boolean enabled) {
        this.itemId = itemId;
        this.enabled = enabled;
        this.type = 0;
    }

    public ExAutoSoulShot(int itemId, int type, boolean enabled) {
        this.itemId = itemId;
        this.type = type;
        this.enabled = enabled;
    }

    @Override
    protected final void writeImpl() {
        writeD(itemId);
        writeD(enabled ? 1 : 0);
        writeD(type);
    }
}
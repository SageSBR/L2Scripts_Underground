package l2s.gameserver.network.l2.s2c;

public class ExOneDayRewardList extends L2GameServerPacket {
    private int playerClass;

    public ExOneDayRewardList(int playerClass) {
        this.playerClass = playerClass;
    }

    @Override
    protected void writeImpl() {
        writeD(playerClass);
        writeD(5);              // day, where 0 is sunday, 1 - monday, ...
        writeD(1);              // rewardCount
        // rewardInfo in loop
        writeH(26);             // rewardId
        writeC(1);              // Status: 1 - Available; 0 - Not available
        writeC(0x00);           // Requires multiple completion: 1 - Yes; 0 - No (must be 1 when required total > 0)
        writeD(0);              // Current Progress
        writeD(0);              // Required Total
    }
}

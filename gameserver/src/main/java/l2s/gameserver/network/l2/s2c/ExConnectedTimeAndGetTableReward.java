package l2s.gameserver.network.l2.s2c;


// this packet cause onedayreward menu displaying
public class ExConnectedTimeAndGetTableReward extends L2GameServerPacket {
    @Override
    protected void writeImpl() {
        writeD(0x00);       // unk 1
        writeD(0x00);       // unk 2
        writeD(0x00);       // unk 3
        writeD(0x00);       // unk 4
        writeD(0x00);       // unk 5
        writeD(0x00);       // unk 6
        writeD(0x00);       // unk 7
        writeD(0x00);       // unk 8
        writeD(0x00);       // unk 9
        writeD(0x00);       // unk 10
        writeD(0x00);       // unk 11
        writeD(0x00);       // unk 12
    }
}

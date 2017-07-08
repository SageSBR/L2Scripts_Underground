package l2s.gameserver.network.l2.c2s;

import java.util.ArrayList;
import java.util.Arrays;

public class ExSendClientINI extends L2GameClientPacket {
    private int partNumber;
    private int partSize;
    byte content[];

    @Override
    protected void readImpl() throws Exception {
        this.partNumber = readC();
        this.partSize = readH();
        this.content = new byte[partSize];
        readB(content);
    }

    @Override
    protected void runImpl() throws Exception {
        //getClient().setIniData(content, partNumber);
    }
}

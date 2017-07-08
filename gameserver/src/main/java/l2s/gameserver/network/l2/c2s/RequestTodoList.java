package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExOneDayRewardList;

public class RequestTodoList extends L2GameClientPacket {
    private int tab; // 9 - daily reward
    private boolean allLevels; // 0 - No, 1 - Yes

    @Override
    protected void readImpl() throws Exception {
        this.tab = readC();
        this.allLevels = readC() == 1;
    }

    @Override
    protected void runImpl() throws Exception {
        Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        int playerClass = player.getClassId().getId();
        sendPacket(new ExOneDayRewardList(playerClass));
    }
}

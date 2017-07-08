package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExBR_ProductListPacket;

public class RequestExBR_ProductList extends L2GameClientPacket {
    public enum Types {
        Store(0),
        PurchaseHistory(1),
        Favorites(2);

        private int type;

        Types(int type) {
            this.type = type;
        }

        public int getId() {
            return type;
        }
    }

    private int type;

    @Override
    protected void readImpl() {
        this.type = readD();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        // Purchase History comes last
        if (type == Types.PurchaseHistory.ordinal()) {
            activeChar.sendPacket(new ExBR_ProductListPacket(activeChar, false)); // Обычный список.
            activeChar.sendPacket(new ExBR_ProductListPacket(activeChar, true)); // История покупок.
        }
    }
}
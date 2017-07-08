package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.handler.items.IItemHandler;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.s2c.ExAutoSoulShot;
import l2s.gameserver.network.l2.s2c.SystemMessage;

public class RequestAutoSoulShot extends L2GameClientPacket {
    private int itemId;
    private int type; // look at ExAutoSoulShot.Type
    private boolean enabled;

    @Override
    protected void readImpl() {
        this.itemId = readD();
        this.enabled = readD() == 1;
        this.type = readD();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (activeChar.getPrivateStoreType() != Player.STORE_PRIVATE_NONE || activeChar.isDead()) {
            return;
        }

        ItemInstance item = activeChar.getInventory().getItemByItemId(itemId);
        if (item == null) {
            return;
        }

        if (enabled) {
            activeChar.addAutoSoulShot(itemId);
            activeChar.sendPacket(new ExAutoSoulShot(itemId, type, enabled));
            activeChar.sendPacket(new SystemMessage(SystemMessage.THE_USE_OF_S1_WILL_NOW_BE_AUTOMATED).addItemName(item.getItemId()));
            IItemHandler handler = item.getTemplate().getHandler();
            handler.useItem(activeChar, item, false);
            return;
        }

        activeChar.removeAutoSoulShot(itemId);
        activeChar.sendPacket(new ExAutoSoulShot(itemId, type, enabled));
        activeChar.sendPacket(new SystemMessage(SystemMessage.THE_AUTOMATIC_USE_OF_S1_WILL_NOW_BE_CANCELLED).addItemName(item.getItemId()));
    }
}
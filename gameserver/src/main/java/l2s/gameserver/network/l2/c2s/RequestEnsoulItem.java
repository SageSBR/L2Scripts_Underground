package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExEnsoulResult;

public class RequestEnsoulItem extends L2GameClientPacket {
    private int itemObjectId;
    private int crystalObjectId;
    private int effect;
    private byte effectCount;
    private byte changeType;
    private byte slot;

    @Override
    protected void readImpl() throws Exception {
        this.itemObjectId = readD();
        this.effectCount = (byte) readC();

        // todo: changes can be more than 1
        // so we need save all changes and handle them all
        assert (this.effectCount == 1);

        this.changeType = (byte) readC();
        this.slot = (byte) readC();
        this.crystalObjectId = readD();
        this.effect = readD();
    }

    @Override
    protected void runImpl() throws Exception {
        Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        player.isntAfk();

        if (player.isActionsDisabled()) {
            player.setEnchantScroll(null);
            player.sendActionFailed();
            return;
        }

        if (player.isInTrade()) {
            player.setEnchantScroll(null);
            player.sendActionFailed();
            return;
        }

        // 16 is Fury Stage 1 (Atk. Spd. + 11%) - just for testing
        ExEnsoulResult ensoulResult = new ExEnsoulResult(ExEnsoulResult.Result.SUCCESS.getResultCode(), 16);
        player.sendPacket(ensoulResult);
    }
}

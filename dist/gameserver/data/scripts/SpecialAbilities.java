import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExShowEnsoulWindow;
import l2s.gameserver.scripts.Functions;

public class SpecialAbilities extends Functions {
    public void ShowEnsoulWindow() {
        Player player = getSelf();
        if (player == null) {
            return;
        }

        player.sendPacket(new ExShowEnsoulWindow());
    }
}

package l2s.gameserver.model.reward;

import l2s.gameserver.model.Player;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class RewardList extends ArrayList<RewardGroup> {
    public static final int MAX_CHANCE = 1000000;
    private final RewardType _type;
    private final boolean _autoLoot;

    public RewardList(RewardType rewardType, boolean a) {
        super(5);
        _type = rewardType;
        _autoLoot = a;
    }

    public List<RewardItem> roll(Player player) {
        return roll(player, 1.0, false, false);
    }

    public List<RewardItem> roll(Player player, double mod) {
        return roll(player, mod, false, false);
    }

    public List<RewardItem> roll(Player player, double mod, boolean isRaid) {
        return roll(player, mod, isRaid, false);
    }

    public List<RewardItem> roll(Player player, double mod, boolean isRaid, boolean isSiegeGuard) {
        List<RewardItem> temp = new ArrayList<RewardItem>();
        for (RewardGroup g : this) {
            RewardItem i = g.roll(_type, player, mod, isRaid, isSiegeGuard);
            if (i != null) {
                temp.add(i);
            }
        }
        return temp;
    }

    public boolean isAutoLoot() {
        return _autoLoot;
    }

    public RewardType getType() {
        return _type;
    }
}
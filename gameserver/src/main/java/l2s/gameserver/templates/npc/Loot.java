package l2s.gameserver.templates.npc;

import l2s.gameserver.model.reward.RewardList;
import l2s.gameserver.model.reward.RewardType;

import java.util.HashMap;

public class Loot {
    private final int npcId;
    private final HashMap<RewardType, RewardList> rewards = new HashMap<>();

    public Loot(int npcId, RewardList rewards, RewardType type) {
        this.npcId = npcId;
        this.rewards.put(type, rewards);
    }

    public Loot(int npcId) {
        this.npcId = npcId;
    }

    public void addRewards(RewardList rewards) {
        this.rewards.put(rewards.getType(), rewards);
    }

    public void addRewards(RewardList rewards, RewardType type) {
        this.rewards.put(type, rewards);
    }

    public int getNpcId() {
        return npcId;
    }

    public HashMap<RewardType, RewardList> getRewards() {
        return rewards;
    }

    public RewardList getRewards(RewardType type) {
        return rewards.get(type);
    }
}

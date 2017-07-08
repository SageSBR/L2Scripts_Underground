package l2s.gameserver.data.xml.holder;

import gnu.trove.map.hash.TIntObjectHashMap;
import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.templates.npc.Loot;

public final class LootHolder extends AbstractHolder {
    private static final LootHolder instance = new LootHolder();
    private TIntObjectHashMap<Loot> rewards = new TIntObjectHashMap<>(20000);

    public static LootHolder getInstance() {
        return instance;
    }

    public void addLoot(Loot loot) {
        rewards.put(loot.getNpcId(), loot);
    }

    public Loot getLoot(int npcId) {
        return rewards.get(npcId);
    }

    @Override
    public int size() {
        return rewards.size();
    }

    @Override
    public void clear() {
        rewards.clear();
    }
}

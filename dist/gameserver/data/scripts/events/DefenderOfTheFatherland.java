package events;

import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.instancemanager.SpawnManager;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefenderOfTheFatherland extends Functions implements ScriptFile, OnDeathListener {
    private static final Logger log = LoggerFactory.getLogger(DefenderOfTheFatherland.class);
    private static final String EM_SPAWN_GROUP = "dotf_spawn_event";
    private static final int EVENT_CONFIG_SPLIT_COUNT = 6;

    private RewardCondition[] rewardConditions = null;
    private static boolean active = false;

    private static boolean isActive() { return IsActive("DefenderOfTheFatherland"); }

    @Override
    public void onLoad() {
        if (isActive()) {
            active = true;
            log.info("Loaded Event: DefenderOfTheFatherland [state: activated]");

            SpawnManager.getInstance().spawn(EM_SPAWN_GROUP);
        }
        else {
            log.info("Loaded Event: DefenderOfTheFatherland [state: deactivated]");
            return;
        }

        loadConfig();

        CharListenerList.addGlobal(this);
    }

    @Override
    public void onReload() {
        loadConfig();
    }

    @Override
    public void onShutdown() {

    }

    @Override
    public void onDeath(Creature actor, Creature killer) {
        if (!active) {
            return;
        }

        if (!SimpleCheckDrop(actor, killer)) {
            return;
        }

        Player player = killer.getPlayer();
        NpcInstance npc = (NpcInstance) actor;

        for (RewardCondition condition : rewardConditions) {
            if (condition.getMinLevel() > player.getLevel() || condition.getMaxLevel() < player.getLevel()) {
                continue;
            }

            if (!Rnd.chance(condition.getItemChance())) {
                continue;
            }

            int itemCount = Rnd.get(condition.getMinItemCount(), condition.getMaxItemCount());
            npc.dropItem(player, condition.getItemEntry(), itemCount);
            break;
        }
    }

    private boolean loadConfig() {
        if (Config.EVENT_DotF_Reward.length == 0)
            return false;

        rewardConditions = new RewardCondition[Config.EVENT_DotF_Reward.length];
        int i = 0;
        for (String condition : Config.EVENT_DotF_Reward)
        {
            String[] split = condition.split(",");
            if (split.length != EVENT_CONFIG_SPLIT_COUNT) {
                return false;
            }

            RewardCondition rewardCondition = new RewardCondition(Integer.parseInt(split[0]), Integer.parseInt(split[1]),
                    Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]), Float.parseFloat(split[5]));

            rewardConditions[i++] = rewardCondition;
        }

        return true;
    }

    public void stopEvent() {
        Player player = getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }

        if (SetActive("DefenderOfTheFatherland", false)) {
            log.info("Event: 'DefenderOfTheFatherland' stopped.");

            SpawnManager.getInstance().despawn(EM_SPAWN_GROUP);
        }
        else {
            player.sendMessage("Event: 'DefenderOfTheFatherland' not started.");
        }

        active = false;
        show("admin/events/events.htm", player);
    }

    public void startEvent() {
        Player player = getSelf();
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (SetActive("DefenderOfTheFatherland", true)) {
            log.info("Event: 'DefenderOfTheFatherland' started.");

            SpawnManager.getInstance().spawn(EM_SPAWN_GROUP);
        }
        else {
            player.sendMessage("Event 'DefenderOfTheFatherland' already started.");
        }

        if (!loadConfig()) {
            player.sendMessage("Event 'DefenderOfTheFatherland' not started (can't load config).");
        }

        active = true;
        show("admin/events/events.htm", player);
    }

    private class RewardCondition {
        private int minLevel;
        private int maxLevel;
        private int itemEntry;
        private int minItemCount;
        private int maxItemCount;
        private float itemChance;

        public RewardCondition(int minLevel, int maxLevel, int itemEntry, int minItemCount, int maxItemCount, float itemChance) {
            this.minLevel = minLevel;
            this.maxLevel = maxLevel;
            this.itemEntry = itemEntry;
            this.minItemCount = minItemCount;
            this.maxItemCount = maxItemCount;
            this.itemChance = itemChance;
        }

        public int getMinLevel() {
            return minLevel;
        }

        public int getMaxLevel() {
            return maxLevel;
        }

        public int getItemEntry() {
            return itemEntry;
        }

        public int getMinItemCount() {
            return minItemCount;
        }

        public int getMaxItemCount() {
            return maxItemCount;
        }

        public float getItemChance() {
            return itemChance;
        }
    }
}

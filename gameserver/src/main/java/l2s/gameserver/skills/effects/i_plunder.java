package l2s.gameserver.skills.effects;

import l2s.gameserver.data.xml.holder.LootHolder;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.reward.RewardList;
import l2s.gameserver.model.reward.RewardType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
 */
public class i_plunder extends i_spoil {
    public i_plunder(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    protected void doSpoil(boolean success) {
        final MonsterInstance monster = (MonsterInstance) getEffected();
        if (success) {
            if (monster.isRobbed() > 0) {
                return;
            }

            monster.setSpoiled(getEffector().getPlayer());

            final RewardList spoilReward = monster.getTemplate().getRewards().get(RewardType.SWEEP);
            if (spoilReward != null) {
                monster.rollRewards(RewardType.SWEEP, spoilReward, getEffector(), getEffector());
                if (monster.takeSweep(getEffector().getPlayer())) {
                    monster.setRobbed(2);
                    return;
                }
            }
        }
        monster.clearSweep();
        monster.setRobbed(1);
    }
}
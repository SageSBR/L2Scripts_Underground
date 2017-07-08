package l2s.gameserver.skills.effects;

import java.util.Collection;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
 */
public final class i_set_skill extends Effect
{
	private final Skill _skill;

	public i_set_skill(Env env, EffectTemplate template)
	{
		super(env, template);

		int[] skill = template.getParam().getIntegerArray("skill", "-");
		_skill = SkillHolder.getInstance().getSkill(skill[0], skill.length >= 2 ? skill[1] : 1);
	}

	@Override
	public boolean checkCondition()
	{
		if(_skill == null)
			return false;

		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		super.onStart();

		Player player = getEffected().getPlayer();
		player.addSkill(_skill, true);
		player.updateStats();
		player.sendSkillList();
		player.updateSkillShortcuts(_skill.getId(), _skill.getLevel());
	}
}
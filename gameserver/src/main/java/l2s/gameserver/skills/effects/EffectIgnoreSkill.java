package l2s.gameserver.skills.effects;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public class EffectIgnoreSkill extends Effect
{
	private final TIntSet _ignoredSkill = new TIntHashSet();

	public EffectIgnoreSkill(Env env, EffectTemplate template)
	{
		super(env, template);

		String[] skills = template.getParam().getString("skillId", "").split(";");
		for(String skill : skills)
			_ignoredSkill.add(Integer.parseInt(skill));
	}

	@Override
	public boolean isIgnoredSkill(Skill skill)
	{
		if(_ignoredSkill.isEmpty())
			return false;

		return _ignoredSkill.contains(skill.getId());
	}
}
package l2s.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.model.Skill;
import l2s.gameserver.utils.SkillUtils;

/**
 * @author Bonux
**/
public final class SkillHolder extends AbstractHolder
{
	private static final SkillHolder _instance = new SkillHolder();

	private final TIntObjectMap<Skill> _skills = new TIntObjectHashMap<Skill>();
	private final TIntObjectMap<List<Skill>> _skillsById = new TIntObjectHashMap<List<Skill>>();

	public static SkillHolder getInstance()
	{
		return _instance;
	}

	public void addSkill(Skill skill)
	{
		_skills.put(skill.hashCode(), skill);

		List<Skill> skills = _skillsById.get(skill.getId());
		if(skills == null)
		{
			skills = new ArrayList<Skill>();
			_skillsById.put(skill.getId(), skills);
		}
		skills.add(skill);
	}

	public Skill getSkill(int id, int level)
	{
		return _skills.get(SkillUtils.generateSkillHashCode(id, level));
	}

	public List<Skill> getSkills(int id)
	{
		return _skillsById.get(id);
	}

	@Override
	public int size()
	{
		return _skills.size();
	}

	@Override
	public void clear()
	{
		_skills.clear();
	}
}

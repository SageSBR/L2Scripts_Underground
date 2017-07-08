package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;

/**
 * @author Bonux
**/
public class ExAlchemySkillList extends L2GameServerPacket
{
	private final List<Skill> _skills;

	public ExAlchemySkillList(Player player)
	{
		_skills = new ArrayList<Skill>(player.getAllAlchemySkills());
	}

	@Override
	protected void writeImpl()
	{
		writeD(_skills.size());
		for(Skill skill : _skills)
		{
			writeD(skill.getId());
			writeD(skill.getLevel());
			writeD(0x00);//UNK
			writeD(0x00);//UNK
			writeC(skill.getId() == 17943 ? 0x00 : 0x01);//???VISIBLE???
		}
	}
}
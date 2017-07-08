package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.PositionUtils;

public class Replace extends Skill
{
	private final boolean _faceToFace;

	public Replace(StatsSet set)
	{
		super(set);
		_faceToFace = set.getBool("face-to-face", false);
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		if(!activeChar.isPlayer())
			return;

		final Player player = activeChar.getPlayer();
		final Location loc = player.getLoc();
		final int heading = PositionUtils.calculateHeadingFrom(player, target);

		if(_faceToFace)
			player.setHeading(PositionUtils.calculateHeadingFrom(target, player));

		player.teleToLocation(target.getLoc(), player.isMyServitor(target.getObjectId()));

		if(_faceToFace)
			target.setHeading(heading);

		target.teleToLocation(loc);
	}
}

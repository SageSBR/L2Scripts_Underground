package l2s.gameserver.model.quest.startcondition.impl;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.quest.startcondition.ICheckStartCondition;

/**
 * @author : Ragnarok
 * @date : 02.04.12  21:50
 */
public class PlayerCurrentRaceCondition implements ICheckStartCondition
{
	private final boolean _human;
	private final boolean _elf;
	private final boolean _delf;
	private final boolean _orc;
	private final boolean _dwarf;
	private final boolean _kamael;
	private final boolean _ertheia;

	public PlayerCurrentRaceCondition(boolean human, boolean elf, boolean delf, boolean orc, boolean dwarf, boolean kamael, boolean ertheia)
	{
		_human = human;
		_elf = elf;
		_delf = delf;
		_orc = orc;
		_dwarf = dwarf;
		_kamael = kamael;
		_ertheia = ertheia;
	}

	@Override
	public boolean checkCondition(Player player)
	{
		Race _race = ClassId.VALUES[player.getClassId().getId()].getRace();
		if(_human && _race == Race.HUMAN)
			return true;

		if(_elf && _race == Race.ELF)
			return true;

		if(_delf && _race == Race.DARKELF)
			return true;

		if(_orc && _race == Race.ORC)
			return true;

		if(_dwarf && _race == Race.DWARF)
			return true;

		if(_kamael && _race == Race.KAMAEL)
			return true;

		if(_ertheia && _race == Race.ERTHEIA)
			return true;

		return false;
	}
}
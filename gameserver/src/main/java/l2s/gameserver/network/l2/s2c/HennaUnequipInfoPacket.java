package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.templates.HennaTemplate;

public class HennaUnequipInfoPacket extends L2GameServerPacket
{
	private final HennaTemplate _hennaTemplate;
	private final Player _player;

	public HennaUnequipInfoPacket(HennaTemplate hennaTemplate, Player player)
	{
		_hennaTemplate = hennaTemplate;
		_player = player;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_hennaTemplate.getSymbolId()); //symbol Id
		writeD(_hennaTemplate.getDyeId()); //item id of dye
		writeQ(_hennaTemplate.getRemoveCount());
		writeQ(_hennaTemplate.getRemovePrice());
		writeD(_hennaTemplate.isForThisClass(_player)); //able to draw or not 0 is false and 1 is true
		writeQ(_player.getAdena());
		writeD(_player.getINT()); //current INT
		writeD(_player.getINT() - _hennaTemplate.getStatINT()); //equip INT
		writeD(_player.getSTR()); //current STR
		writeD(_player.getSTR() - _hennaTemplate.getStatSTR()); //equip STR
		writeD(_player.getCON()); //current CON
		writeD(_player.getCON() - _hennaTemplate.getStatCON()); //equip CON
		writeD(_player.getMEN()); //current MEM
		writeD(_player.getMEN() - _hennaTemplate.getStatMEN()); //equip MEM
		writeD(_player.getDEX()); //current DEX
		writeD(_player.getDEX() - _hennaTemplate.getStatDEX()); //equip DEX
		writeD(_player.getWIT()); //current WIT
		writeD(_player.getWIT() - _hennaTemplate.getStatWIT()); //equip WIT
		writeD(_player.getLUC()); //current LUC
		writeD(_player.getLUC() + _hennaTemplate.getStatLUC()); //equip LUC
		writeD(_player.getCHA()); //current CHA
		writeD(_player.getCHA() + _hennaTemplate.getStatCHA()); //equip CHA
		writeD(0x00); // UNK
	}
}
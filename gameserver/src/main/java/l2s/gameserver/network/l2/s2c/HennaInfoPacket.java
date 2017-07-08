package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.Henna;
import l2s.gameserver.model.actor.instances.player.HennaList;
import l2s.gameserver.templates.HennaTemplate;

public class HennaInfoPacket extends L2GameServerPacket
{
	private final Player _player;
	private final HennaList _hennaList;

	public HennaInfoPacket(Player player)
	{
		_player = player;
		_hennaList = player.getHennaList();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_hennaList.getINT()); //equip INT
		writeD(_hennaList.getSTR()); //equip STR
		writeD(_hennaList.getCON()); //equip CON
		writeD(_hennaList.getMEN()); //equip MEN
		writeD(_hennaList.getDEX()); //equip DEX
		writeD(_hennaList.getWIT()); //equip WIT
		writeD(_hennaList.getLUC()); //equip LUC
		writeD(_hennaList.getCHA()); //equip CHA
		writeD(HennaList.MAX_SIZE); //interlude, slots?
		writeD(_hennaList.size());
		for(Henna henna : _hennaList.values(false))
		{
			writeD(henna.getTemplate().getSymbolId());
			writeD(_hennaList.isActive(henna));
		}

		Henna henna = _hennaList.getPremiumHenna();
		if(henna != null)
		{
			writeD(henna.getTemplate().getSymbolId());	// Premium symbol ID
			writeD(henna.getLeftTime());	// Premium symbol left time
			writeD(_hennaList.isActive(henna));	// Premium symbol active
		}
		else
		{
			writeD(0x00);	// Premium symbol ID
			writeD(0x00);	// Premium symbol left time
			writeD(0x00);	// Premium symbol active
		}
	}
}
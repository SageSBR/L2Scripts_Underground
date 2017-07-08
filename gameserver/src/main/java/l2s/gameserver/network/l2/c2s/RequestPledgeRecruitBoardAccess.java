package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.instancemanager.clansearch.ClanSearchManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.clansearch.ClanSearchClan;
import l2s.gameserver.model.clansearch.base.ClanSearchListType;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class RequestPledgeRecruitBoardAccess extends L2GameClientPacket
{
	private int _pledgeAccess;
	private ClanSearchListType _searchType;
	private String _title;
	private String _desc;

	@Override
	protected void readImpl()
	{
		_pledgeAccess = readD();
		_searchType = ClanSearchListType.getType(readD());
		_title = readS();
		_desc = readS();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		Clan clan = activeChar.getClan();
		if(clan== null)
		{
			activeChar.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_OR_SOMEONE_WITH_RANK_MANAGEMENT_AUTHORITY_MAY_REGISTER_THE_CLAN);
			return;
		}

		if(_title.isEmpty())
		{
			activeChar.sendActionFailed();
			return;
		}

		if((activeChar.getClanPrivileges() & Clan.CP_CL_MANAGE_RANKS) != Clan.CP_CL_MANAGE_RANKS)
		{
			activeChar.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_OR_SOMEONE_WITH_RANK_MANAGEMENT_AUTHORITY_MAY_REGISTER_THE_CLAN);
			return;
		}

		if(_title.length() > 64)
			_title = _title.substring(0, 63);

		if(_desc.length() > 256)
			_desc = _desc.substring(0, 255);

		if(ClanSearchManager.getInstance().addClan(new ClanSearchClan(clan.getClanId(), _searchType, _title, _desc)))
			activeChar.sendPacket(SystemMsg.ENTRY_APPLICATION_COMPLETE_USE_ENTRY_APPLICATION_INFO_TO_CHECK_OR_CANCEL_YOUR_APPLICATION);
		else
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.YOU_MAY_APPLY_FOR_ENTRY_AFTER_S1_MINUTES_DUE_TO_CANCELLING_YOUR_APPLICATION).addInteger(5)); // TODO[Bonux]: Fix me.
	}
}
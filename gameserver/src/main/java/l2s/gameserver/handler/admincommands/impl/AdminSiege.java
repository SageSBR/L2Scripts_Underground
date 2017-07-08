package l2s.gameserver.handler.admincommands.impl;

import java.util.StringTokenizer;

import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.ResidenceHolder;
import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.events.impl.SiegeEvent;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.instances.VillageMasterInstance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.pledge.SubUnit;
import l2s.gameserver.model.pledge.UnitMember;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.PledgeShowInfoUpdatePacket;
import l2s.gameserver.network.l2.s2c.PledgeStatusChangedPacket;
import l2s.gameserver.tables.ClanTable;
import l2s.gameserver.utils.Util;

/**
 * Pledge Manipulation //siege id <start|stop>
 */
public class AdminSiege implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_siege
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		@SuppressWarnings("unused")
		Commands command = (Commands) comm;

		if(activeChar.getPlayerAccess() == null || !activeChar.getPlayerAccess().CanSiege)
			return false;


		if(fullString.startsWith("admin_siege"))
		{
			try
			{
				StringTokenizer st = new StringTokenizer(fullString);
				st.nextToken();

				int castle_id = Integer.parseInt(st.nextToken()); // id
				Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, castle_id);
				SiegeEvent siege = castle.getSiegeEvent();
				String action = st.nextToken(); // <start|stop>

				if(action.equals("start"))
				{
					if(siege.isInProgress())
					{
						activeChar.sendMessage("осада замка "+castle.getName()+" уже идёт");
						return false;
					}
					siege.startEvent();
					activeChar.sendMessage("осада замка " + castle.getName() + " НАЧАЛАСЬ");
				}
				else if(action.equals("stop"))
				{
					if(!siege.isInProgress())
					{
						activeChar.sendMessage("осада замка "+castle.getName()+" не идёт");
						return false;
					}
					siege.stopEvent();
					activeChar.sendMessage("осада замка "+castle.getName()+" ЗАКОНЧИЛАСЬ");
				}

			}
			catch(Exception e)
			{
				e.printStackTrace();
				activeChar.sendMessage("неверный формат команды. //siege castle_id <start|stop>");
				return false;
			}
		}

		return true;
	}

	@Override
	public Enum<?>[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}
package l2s.gameserver.taskmanager.tasks;

import l2s.commons.dbutils.DbUtils;
import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.Config;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.instances.NpcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RestoreOfflineTraders extends RunnableImpl
{
	private static final Logger _log = LoggerFactory.getLogger(RestoreOfflineTraders.class);

	@Override
	public void runImpl() throws Exception
	{
		int count = 0;

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();

			//Убираем просроченных
			if(Config.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK > 0)
			{
				int expireTimeSecs = (int) (System.currentTimeMillis() / 1000L - Config.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK);

				statement = con.prepareStatement("DELETE FROM character_variables WHERE name = 'offline' AND value < ?");
				statement.setLong(1, expireTimeSecs);
				statement.executeUpdate();

				DbUtils.close(statement);
			}

			//Убираем забаненных
			statement = con.prepareStatement("DELETE FROM character_variables WHERE name = 'offline' AND obj_id IN (SELECT obj_id FROM characters WHERE accessLevel < 0)");
			statement.executeUpdate();

			DbUtils.close(statement);

			statement = con.prepareStatement("SELECT obj_id, value FROM character_variables WHERE name = 'offline'");
			rset = statement.executeQuery();

			int objectId;
			int expireTimeSecs;
			Player p;

			while(rset.next())
			{
				objectId = rset.getInt("obj_id");
				expireTimeSecs = rset.getInt("value");

				p = Player.restore(objectId, false);
				if(p == null)
					continue;

				if(p.isDead())
				{
					p.kick();
					continue;
				}

				p.setNameColor(Config.SERVICES_OFFLINE_TRADE_NAME_COLOR);
				p.startAbnormalEffect(Config.SERVICES_OFFLINE_TRADE_ABNORMAL_EFFECT);
				p.setOfflineMode(true);
				p.setOnlineStatus(true);

				p.spawnMe();

				if(p.getClan() != null && p.getClan().getAnyMember(p.getObjectId()) != null)
					p.getClan().getAnyMember(p.getObjectId()).setPlayerInstance(p, false);

				if(Config.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK > 0)
					p.startKickTask((Config.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK + expireTimeSecs - System.currentTimeMillis() / 1000L) * 1000L);

				// Если кто-то успел сесть рядом с оффлайн торговцем до его прогрузки - снимаем его с торга.
				if(Config.SERVICES_TRADE_ONLY_FAR)
				{
					for(Player player : World.getAroundPlayers(p, Config.SERVICES_TRADE_RADIUS, 200))
						if(player.isInStoreMode())
							if(player.isInOfflineMode())
							{
								player.setOfflineMode(false);
								player.kick();
								_log.warn("Offline trader: " + player + " kicked.");
							}
							else
							{
								player.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
								player.standUp();
								player.broadcastCharInfo();
							}
					for(NpcInstance npc : World.getAroundNpc(p, Config.SERVICES_TRADE_RADIUS, 200))
					{
						p.setOfflineMode(false);
						p.kick();
						_log.warn("Offline trader: " + p + " kicked.");
						break;
					}
				}

				count++;
			}
		}
		catch(Exception e)
		{
			_log.error("Error while restoring offline traders!", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}

		_log.info("Restored " + count + " offline traders");
	}
}

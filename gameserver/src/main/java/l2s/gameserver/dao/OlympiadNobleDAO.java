package l2s.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.base.SubClassType;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VISTALL
 * @date 20:00/02.05.2011
 */
public class OlympiadNobleDAO
{
	private static final Logger _log = LoggerFactory.getLogger(OlympiadNobleDAO.class);
	private static final OlympiadNobleDAO _instance = new OlympiadNobleDAO();

	private static final String SELECT_SQL_QUERY = "SELECT char_id, characters.char_name as char_name, character_subclasses.class_id, olympiad_points, olympiad_points_past, olympiad_points_past_static, competitions_done, competitions_loose, competitions_win, game_classes_count, game_noclasses_count FROM olympiad_nobles LEFT JOIN characters ON characters.obj_Id = olympiad_nobles.char_id LEFT JOIN character_subclasses ON character_subclasses.char_obj_id = olympiad_nobles.char_id AND character_subclasses.type=? WHERE characters.obj_Id > 0 AND characters.is_noble > 0";
	private static final String REPLACE_SQL_QUERY = "REPLACE INTO `olympiad_nobles` (`char_id`, `olympiad_points`, `olympiad_points_past`, `olympiad_points_past_static`, `competitions_done`, `competitions_win`, `competitions_loose`, game_classes_count, game_noclasses_count) VALUES (?,?,?,?,?,?,?,?,?)";

	public static final String OLYMPIAD_GET_HEROS = "SELECT `char_id`, characters.char_name AS char_name FROM `olympiad_nobles` LEFT JOIN characters ON char_id=characters.obj_Id LEFT JOIN character_subclasses ON char_id=character_subclasses.char_obj_id AND character_subclasses.type=? WHERE characters.obj_Id > 0 AND characters.is_noble > 0 AND character_subclasses.class_id = ? AND `competitions_done` >= ? AND `competitions_win` > 0 ORDER BY `olympiad_points` DESC, `competitions_win` DESC, `competitions_done` DESC";
	public static final String GET_EACH_CLASS_LEADER = "SELECT characters.char_name AS char_name FROM `olympiad_nobles` LEFT JOIN characters ON char_id=characters.obj_Id LEFT JOIN character_subclasses ON char_id=character_subclasses.char_obj_id AND character_subclasses.type=? WHERE characters.obj_Id > 0 AND characters.is_noble > 0 AND character_subclasses.class_id = ? AND `olympiad_points_past_static` != 0 ORDER BY `olympiad_points_past_static` DESC LIMIT 10";
	public static final String GET_ALL_CLASSIFIED_NOBLESS = "SELECT `char_id` FROM `olympiad_nobles` ORDER BY olympiad_points_past_static DESC";
	public static final String OLYMPIAD_CALCULATE_LAST_PERIOD = "UPDATE `olympiad_nobles` SET `olympiad_points_past` = `olympiad_points`, `olympiad_points_past_static` = `olympiad_points` WHERE `competitions_done` >= ?";
	public static final String OLYMPIAD_CLEANUP_NOBLES = "UPDATE `olympiad_nobles` SET `olympiad_points` = ?, `competitions_done` = 0, `competitions_win` = 0, `competitions_loose` = 0, game_classes_count=0, game_noclasses_count=0";

	public static OlympiadNobleDAO getInstance()
	{
		return _instance;
	}

	public void select()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(SELECT_SQL_QUERY);
			statement.setInt(1, SubClassType.BASE_CLASS.ordinal());
			rset = statement.executeQuery();
			while(rset.next())
			{
				StatsSet statDat = new StatsSet();
				int charId = rset.getInt(Olympiad.CHAR_ID);
				statDat.set(Olympiad.CLASS_ID, rset.getInt(Olympiad.CLASS_ID));
				statDat.set(Olympiad.CHAR_NAME, rset.getString(Olympiad.CHAR_NAME));
				statDat.set(Olympiad.POINTS, rset.getInt(Olympiad.POINTS));
				statDat.set(Olympiad.POINTS_PAST, rset.getInt(Olympiad.POINTS_PAST));
				statDat.set(Olympiad.POINTS_PAST_STATIC, rset.getInt(Olympiad.POINTS_PAST_STATIC));
				statDat.set(Olympiad.COMP_DONE, rset.getInt(Olympiad.COMP_DONE));
				statDat.set(Olympiad.COMP_WIN, rset.getInt(Olympiad.COMP_WIN));
				statDat.set(Olympiad.COMP_LOOSE, rset.getInt(Olympiad.COMP_LOOSE));
				statDat.set(Olympiad.GAME_CLASSES_COUNT, rset.getInt(Olympiad.GAME_CLASSES_COUNT));
				statDat.set(Olympiad.GAME_NOCLASSES_COUNT, rset.getInt(Olympiad.GAME_NOCLASSES_COUNT));

				Olympiad.getNoblesMap().put(charId, statDat);
			}
		}
		catch(Exception e)
		{
			_log.error("OlympiadNobleDAO: select():", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	public void replace(int nobleId)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			StatsSet nobleInfo = Olympiad.getNoblesInfo(nobleId);

			statement = con.prepareStatement(REPLACE_SQL_QUERY);
			statement.setInt(1, nobleId);
			statement.setInt(2, nobleInfo.getInteger(Olympiad.POINTS));
			statement.setInt(3, nobleInfo.getInteger(Olympiad.POINTS_PAST));
			statement.setInt(4, nobleInfo.getInteger(Olympiad.POINTS_PAST_STATIC));
			statement.setInt(5, nobleInfo.getInteger(Olympiad.COMP_DONE));
			statement.setInt(6, nobleInfo.getInteger(Olympiad.COMP_WIN));
			statement.setInt(7, nobleInfo.getInteger(Olympiad.COMP_LOOSE));
			statement.setInt(8, nobleInfo.getInteger(Olympiad.GAME_CLASSES_COUNT));
			statement.setInt(9, nobleInfo.getInteger(Olympiad.GAME_NOCLASSES_COUNT));
			statement.execute();
		}
		catch(Exception e)
		{
			_log.error("OlympiadNobleDAO: replace(int): " + nobleId, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}
}

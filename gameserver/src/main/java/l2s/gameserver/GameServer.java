package l2s.gameserver;

import java.io.File;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import l2s.commons.lang.StatsUtils;
import l2s.commons.listener.Listener;
import l2s.commons.listener.ListenerList;
import l2s.commons.net.nio.impl.SelectorStats;
import l2s.commons.net.nio.impl.SelectorThread;
import l2s.gameserver.cache.CrestCache;
import l2s.gameserver.cache.ImagesCache;
import l2s.gameserver.config.templates.HostInfo;
import l2s.gameserver.config.xml.ConfigParsers;
import l2s.gameserver.config.xml.holder.HostsConfigHolder;
import l2s.gameserver.dao.CharacterDAO;
import l2s.gameserver.dao.CustomHeroDAO;
import l2s.gameserver.dao.CustomStatsDAO;
import l2s.gameserver.dao.FreePremiumAccountsDao;
import l2s.gameserver.dao.HidenItemsDAO;
import l2s.gameserver.dao.ItemsDAO;
import l2s.gameserver.dao.LfcDAO;
import l2s.gameserver.dao.LfcStatisticDAO;
import l2s.gameserver.dao.PremiumAccountRatesHolder;
import l2s.gameserver.data.BoatHolder;
import l2s.gameserver.data.xml.Parsers;
import l2s.gameserver.data.xml.holder.EventHolder;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.data.xml.holder.ResidenceHolder;
import l2s.gameserver.data.xml.holder.StaticObjectHolder;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.handler.admincommands.AdminCommandHandler;
import l2s.gameserver.handler.items.ItemHandler;
import l2s.gameserver.handler.usercommands.UserCommandHandler;
import l2s.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2s.gameserver.idfactory.IdFactory;
import l2s.gameserver.instancemanager.ArcanManager;
import l2s.gameserver.instancemanager.BaltusManager;
import l2s.gameserver.instancemanager.BotCheckManager;
import l2s.gameserver.instancemanager.CastleManorManager;
import l2s.gameserver.instancemanager.CommissionManager;
import l2s.gameserver.instancemanager.CoupleManager;
import l2s.gameserver.instancemanager.CursedWeaponsManager;
import l2s.gameserver.instancemanager.DailyQuestsManager;
import l2s.gameserver.instancemanager.DimensionalRiftManager;
import l2s.gameserver.instancemanager.ParnassusManager;
import l2s.gameserver.instancemanager.PetitionManager;
import l2s.gameserver.instancemanager.PlayerMessageStack;
import l2s.gameserver.instancemanager.RaidBossSpawnManager;
import l2s.gameserver.instancemanager.SoDManager;
import l2s.gameserver.instancemanager.SoHManager;
import l2s.gameserver.instancemanager.SoIManager;
import l2s.gameserver.instancemanager.SpawnManager;
import l2s.gameserver.instancemanager.ToIManager;
import l2s.gameserver.instancemanager.WorldStatisticsManager;
import l2s.gameserver.instancemanager.clansearch.ClanSearchManager;
import l2s.gameserver.instancemanager.games.FishingChampionShipManager;
import l2s.gameserver.instancemanager.games.LotteryManager;
import l2s.gameserver.instancemanager.games.MiniGameScoreManager;
import l2s.gameserver.instancemanager.itemauction.ItemAuctionManager;
import l2s.gameserver.instancemanager.naia.NaiaCoreManager;
import l2s.gameserver.instancemanager.naia.NaiaTowerManager;
import l2s.gameserver.listener.GameListener;
import l2s.gameserver.listener.game.OnShutdownListener;
import l2s.gameserver.listener.game.OnStartListener;
import l2s.gameserver.model.World;
import l2s.gameserver.model.entity.Hero;
import l2s.gameserver.model.entity.MonsterRace;
import l2s.gameserver.model.entity.events.fightclubmanager.FightClubEventManager;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.network.authcomm.AuthServerCommunication;
import l2s.gameserver.network.l2.GameClient;
import l2s.gameserver.network.l2.GamePacketHandler;
import l2s.gameserver.network.telnet.TelnetServer;
import l2s.gameserver.scripts.Scripts;
import l2s.gameserver.security.HWIDBan;
import l2s.gameserver.tables.ClanTable;
import l2s.gameserver.tables.EnchantHPBonusTable;
import l2s.gameserver.tables.PetSkillsTable;
import l2s.gameserver.tables.SubClassTable;
import l2s.gameserver.taskmanager.ItemsAutoDestroy;
import l2s.gameserver.taskmanager.TaskManager;
import l2s.gameserver.taskmanager.tasks.RestoreOfflineTraders;
import l2s.gameserver.utils.OnlineTxtGenerator;
import l2s.gameserver.utils.Strings;
import net.sf.ehcache.CacheManager;
import ru.akumu.smartguard.GuardConfig;
import ru.akumu.smartguard.manager.LicenseManager;
import ru.akumu.smartguard.manager.UpdateManager;
import ru.akumu.smartguard.manager.bans.BanManager;
import ru.akumu.smartguard.manager.screen.ScreenTextManager;
import ru.akumu.smartguard.menu.SmartGuardMenu;
import ru.akumu.smartguard.utils.log.GuardLog;

public class GameServer
{
	public static boolean DEVELOP = false;

	public static final String PROJECT_REVISION = "L2s [18003]";
	public static final String UPDATE_NAME = "Epic Tale of Aden: Infinite of Odyssey";

	public static final int AUTH_SERVER_PROTOCOL = 2;

	private static final Logger _log = LoggerFactory.getLogger(GameServer.class);

	public class GameServerListenerList extends ListenerList<GameServer>
	{
		public void onStart()
		{
			for(Listener<GameServer> listener : getListeners())
			{
				if(OnStartListener.class.isInstance(listener))
				{
					((OnStartListener) listener).onStart();
				}
			}
		}

		public void onShutdown()
		{
			for(Listener<GameServer> listener : getListeners())
			{
				if(OnShutdownListener.class.isInstance(listener))
				{
					((OnShutdownListener) listener).onShutdown();
				}
			}
		}
	}

	public static GameServer _instance;

	private final List<SelectorThread<GameClient>> _selectorThreads = new ArrayList<SelectorThread<GameClient>>();
	private final SelectorStats _selectorStats = new SelectorStats();
	private TelnetServer statusServer;
	private final GameServerListenerList _listeners;

	private long _serverStartTimeMillis;

	private final int _onlineLimit;

	public List<SelectorThread<GameClient>> getSelectorThreads()
	{
		return _selectorThreads;
	}

	public SelectorStats getSelectorStats()
	{
		return _selectorStats;
	}

	public long getServerStartTime()
	{
		return _serverStartTimeMillis;
	}

	public int getOnlineLimit()
	{
		return _onlineLimit;
	}

	public GameServer() throws Exception
	{
		_instance = this;
		_serverStartTimeMillis = System.currentTimeMillis();
		_listeners = new GameServerListenerList();

		new File("./log/").mkdir();

		// Initialize config
		ConfigParsers.parseAll();
		Config.load();

		final TIntSet ports = new TIntHashSet();
		for(HostInfo host : HostsConfigHolder.getInstance().getGameServerHosts())
		{
			if(host.getIP() != null || host.getInnerIP() != null)
			{
				ports.add(host.getPort());
			}
		}

		// Check binding address
		checkFreePorts(ports);

		_onlineLimit = Config.MAXIMUM_ONLINE_USERS;

		try
		{
			GuardConfig.load();
			if(GuardConfig.ProtectionEnabled)
			{
				LicenseManager.getInstance();
				UpdateManager.getInstance();
				ScreenTextManager.getInstance();
				BanManager.store();
			}
		}
		catch(Exception e)
		{
			GuardLog.getLogger().severe("Error initializing SmartGuard");
			GuardLog.logException(e);
			return;
		}

		try
		{
			AdminCommandHandler.getInstance().registerAdminCommandHandler(new SmartGuardMenu());
		}
		catch(Exception e)
		{
			GuardLog.getLogger().severe("Error initializing SmartGuard AdminCommandHandler!");
			GuardLog.logException(e);
		}

		// Initialize database
		Class.forName(Config.DATABASE_DRIVER).newInstance();
		DatabaseFactory.getInstance().getConnection().close();

		IdFactory _idFactory = IdFactory.getInstance();
		if(!_idFactory.isInitialized())
		{
			_log.error("Could not read object IDs from DB. Please Check Your Data.");
			throw new Exception("Could not initialize the ID factory");
		}

		CacheManager.getInstance();

		ThreadPoolManager.getInstance();

		LfcDAO.LoadArenas();
		LfcStatisticDAO.LoadGlobalStatistics();
		LfcStatisticDAO.LoadLocalStatistics();

		CustomStatsDAO.LoadCustomValues();

		PremiumAccountRatesHolder.loadLists();

		BotCheckManager.loadBotQuestions();

		FreePremiumAccountsDao.LoadTable();

		HidenItemsDAO.LoadAllHiddenItems();

		CustomHeroDAO.getInstance();

		HWIDBan.getInstance().load();

		Scripts.getInstance();

		GeoEngine.load();

		Strings.reload();

		GameTimeController.getInstance();

		World.init();

		Parsers.parseAll();

		ItemsDAO.getInstance();

		CrestCache.getInstance();

		ImagesCache.getInstance();

		CharacterDAO.getInstance();

		ClanTable.getInstance();

		DailyQuestsManager.EngageSystem();

		//FakePlayersTable.getInstance();

		SubClassTable.getInstance();

		EnchantHPBonusTable.getInstance();

		PetSkillsTable.getInstance();

		ItemAuctionManager.getInstance();

		SpawnManager.getInstance().spawnAll();

		//FakePlayersSpawnManager.getInstance().spawnAll();

		StaticObjectHolder.getInstance().spawnAll();

		RaidBossSpawnManager.getInstance();

		Scripts.getInstance().init();

		ItemHolder.getInstance().initItems();

		DimensionalRiftManager.getInstance();

		Announcements.getInstance();

		LotteryManager.getInstance();

		PlayerMessageStack.getInstance();

		if(Config.AUTODESTROY_ITEM_AFTER > 0)
		{
			ItemsAutoDestroy.getInstance();
		}

		MonsterRace.getInstance();

		if(Config.ENABLE_OLYMPIAD)
		{
			Olympiad.load();
			Hero.getInstance();
		}

		PetitionManager.getInstance();

		CursedWeaponsManager.getInstance();

		if(Config.ALLOW_WEDDING)
		{
			CoupleManager.getInstance();
		}

		ItemHandler.getInstance();

		AdminCommandHandler.getInstance().log();
		UserCommandHandler.getInstance().log();
		VoicedCommandHandler.getInstance().log();

		TaskManager.getInstance();

		ClanTable.getInstance().checkClans();

		_log.info("=[Events]=========================================");
		ResidenceHolder.getInstance().callInit();
		EventHolder.getInstance().callInit();
		_log.info("==================================================");

		BoatHolder.getInstance().spawnAll();
		CastleManorManager.getInstance();

		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());

		_log.info("IdFactory: Free ObjectID's remaining: " + IdFactory.getInstance().size());

		if(Config.ALT_FISH_CHAMPIONSHIP_ENABLED)
		{
			FishingChampionShipManager.getInstance();
		}

		NaiaTowerManager.getInstance();
		NaiaCoreManager.getInstance();

		SoDManager.getInstance();
		SoIManager.getInstance();
		SoHManager.getInstance();

		MiniGameScoreManager.getInstance();

		CommissionManager.getInstance().init();

		ClanSearchManager.getInstance().load();

		WorldStatisticsManager.getInstance();
		ArcanManager.getInstance();
		ToIManager.getInstance();
		ParnassusManager.getInstance();
		BaltusManager.getInstance();
		Shutdown.getInstance().schedule(Config.RESTART_AT_TIME, Shutdown.RESTART);
		if(Config.ALLOW_FIGHT_CLUB)
		{
			FightClubEventManager.getInstance();
		}



		/* CCP Guard START
		ccpGuard.Protection.Init();
		** CCP Guard END*/

		_log.info("GameServer Started");
		_log.info("Maximum Numbers of Connected Players: " + getOnlineLimit());

		registerSelectorThreads(ports);

		getListeners().onStart();

		if(Config.SERVICES_OFFLINE_TRADE_RESTORE_AFTER_RESTART)
		{
			ThreadPoolManager.getInstance().schedule(new RestoreOfflineTraders(), 30000L);
		}

		if(Config.ONLINE_GENERATOR_ENABLED)
		{
			ThreadPoolManager.getInstance().scheduleAtFixedRate(new OnlineTxtGenerator(), 5000L, Config.ONLINE_GENERATOR_DELAY * 60 * 1000L);
		}

		AuthServerCommunication.getInstance().start();

		if(Config.IS_TELNET_ENABLED)
		{
			statusServer = new TelnetServer();
		}
		else
		{
			_log.info("Telnet server is currently disabled.");
		}

		_log.info("=================================================");
		String memUsage = String.valueOf(StatsUtils.getMemUsage());
		for(String line : memUsage.split("\n"))
		{
			_log.info(line);
		}
		_log.info("=================================================");
	}

	public GameServerListenerList getListeners()
	{
		return _listeners;
	}

	public static GameServer getInstance()
	{
		return _instance;
	}

	public <T extends GameListener> boolean addListener(T listener)
	{
		return _listeners.add(listener);
	}

	public <T extends GameListener> boolean removeListener(T listener)
	{
		return _listeners.remove(listener);
	}

	private void checkFreePorts(TIntSet ports)
	{
		for(int port : ports.toArray())
		{
			while(!checkFreePort(null, port))
			{
				;
			}
		}
	}

	private boolean checkFreePort(String ip, int port)
	{
		try
		{
			ServerSocket ss;
			if(ip == null)
			{
				ss = new ServerSocket(port);
			}
			else
			{
				ss = new ServerSocket(port, 50, InetAddress.getByName(ip));
			}
			ss.close();
		}
		catch(Exception e)
		{
			_log.warn("Port " + port + " is allready binded. Please free it and restart server.");
			try
			{
				Thread.sleep(1000L);
			}
			catch(InterruptedException e2)
			{
				//
			}
			return false;
		}
		return true;
	}

	private void registerSelectorThreads(TIntSet ports)
	{
		final GamePacketHandler gph = new GamePacketHandler();

		for(int port : ports.toArray())
		{
			registerSelectorThread(gph, null, port);
		}
	}

	private void registerSelectorThread(GamePacketHandler gph, String ip, int port)
	{
		try
		{
			SelectorThread<GameClient> selectorThread = new SelectorThread<GameClient>(Config.SELECTOR_CONFIG, _selectorStats, gph, gph, gph, null);
			selectorThread.openServerSocket(ip == null ? null : InetAddress.getByName(ip), port);
			selectorThread.start();
			_selectorThreads.add(selectorThread);
		}
		catch(Exception e)
		{
			//
		}
	}

	public static void main(String[] args) throws Exception
	{
		for (String arg : args) {
			if (arg.equalsIgnoreCase("-dev")) {
				DEVELOP = true;
			}
		}

		new GameServer();
	}

	public TelnetServer getStatusServer()
	{
		return statusServer;
	}
}
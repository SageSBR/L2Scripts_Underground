package instances;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.ai.NpcAI;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.AggroList;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Spawner;
import l2s.gameserver.model.World;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.base.InvisibleType;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.DoorInstance;
import l2s.gameserver.model.instances.GuardInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.skills.AbnormalEffect;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.PositionUtils;
import l2s.gameserver.utils.ReflectionUtils;

/**
 * @author Iqman + GW
 */

//Edited by Evil_dnk
public class Kartia extends Reflection
{
	private static final int SSQ_CAMERA = 18830;
	private static final int SOLO_ROOM_DOOR = 16170002;
	private static final int SOLO_RAID_DOOR = 16170003;
	private static final int PARTY_ROOM_DOOR = 16170012;
	private static final int PARTY_RAID_DOOR = 16170013;
	private static final Location SOLO_ENTRANCE = new Location(-108983, -10446, -11920);
	private static final Location SOLO_ZONE_TELEPORTER = new Location(-110264, -10456, -11949);
	private static final Location PARTY_ZONE_TELEPORTER = new Location(-119832, -10424, -11949);
	private static final Map<String, Integer> SOLO85_MONSTERS = new TreeMap<String, Integer>();
	private static final Map<String, Integer> PARTY85_MONSTERS = new TreeMap<String, Integer>();
	private static final Map<String, Integer> SOLO90_MONSTERS = new TreeMap<String, Integer>();
	private static final Map<String, Integer> PARTY90_MONSTERS = new TreeMap<String, Integer>();
	private static final Map<String, Integer> SOLO95_MONSTERS = new TreeMap<String, Integer>();
	private static final Map<String, Integer> PARTY95_MONSTERS = new TreeMap<String, Integer>();
	private static final List<Location> SOLO_LEFT_KILLER_ROUTES = new ArrayList<Location>();
	private static final List<Location> SOLO_RIGHT_KILLER_ROUTES = new ArrayList<Location>();
	private static final List<Location> PARTY_LEFT_KILLER_ROUTES = new ArrayList<Location>();
	private static final List<Location> PARTY_RIGHT_KILLER_ROUTES = new ArrayList<Location>();

	static
	{
		SOLO85_MONSTERS.put("npc_adolph", 33608);
		SOLO85_MONSTERS.put("support_adolph", 33609);
		SOLO85_MONSTERS.put("npc_barton", 33610);
		SOLO85_MONSTERS.put("support_barton", 33611);
		SOLO85_MONSTERS.put("npc_hayuk", 33612);
		SOLO85_MONSTERS.put("support_hayuk", 33613);
		SOLO85_MONSTERS.put("npc_eliyah", 33614);
		SOLO85_MONSTERS.put("support_eliyah", 33615);
		SOLO85_MONSTERS.put("npc_elise", 33616);
		SOLO85_MONSTERS.put("support_elise", 33617);
		SOLO85_MONSTERS.put("support_eliyah_spirit", 33618);
		SOLO85_MONSTERS.put("support_troop", 33642);
		SOLO85_MONSTERS.put("captivated", 33641);
		SOLO85_MONSTERS.put("altar", 19247);

		SOLO85_MONSTERS.put("keeper", 19220);
		SOLO85_MONSTERS.put("watcher", 19221);
		SOLO85_MONSTERS.put("overseer", 19222);
		SOLO85_MONSTERS.put("ruler", 19253);

		PARTY85_MONSTERS.put("support_troop", 33642);
		PARTY85_MONSTERS.put("altar", 19248);
		PARTY85_MONSTERS.put("captivated", 33641);
		PARTY85_MONSTERS.put("altar", 19248);

		PARTY85_MONSTERS.put("keeper", 19229);
		PARTY85_MONSTERS.put("watcher", 19230);
		PARTY85_MONSTERS.put("overseer", 19231);
		PARTY85_MONSTERS.put("ruler", 25882);

		SOLO90_MONSTERS.put("npc_adolph", 33619);
		SOLO90_MONSTERS.put("support_adolph", 33620);
		SOLO90_MONSTERS.put("npc_barton", 33621);
		SOLO90_MONSTERS.put("support_barton", 33622);
		SOLO90_MONSTERS.put("npc_hayuk", 33623);
		SOLO90_MONSTERS.put("support_hayuk", 33624);
		SOLO90_MONSTERS.put("npc_eliyah", 33625);
		SOLO90_MONSTERS.put("support_eliyah", 33626);
		SOLO90_MONSTERS.put("npc_elise", 33627);
		SOLO90_MONSTERS.put("support_elise", 33628);
		SOLO90_MONSTERS.put("support_eliyah_spirit", 33629);
		SOLO90_MONSTERS.put("support_troop", 33644);
		SOLO90_MONSTERS.put("altar", 19249);
		SOLO90_MONSTERS.put("captivated", 33643);
		SOLO90_MONSTERS.put("altar", 19249);

		SOLO90_MONSTERS.put("keeper", 19223);
		SOLO90_MONSTERS.put("watcher", 19224);
		SOLO90_MONSTERS.put("overseer", 19225);
		SOLO90_MONSTERS.put("ruler", 19254);

		PARTY90_MONSTERS.put("support_troop", 33644);
		PARTY85_MONSTERS.put("altar", 19250);
		PARTY90_MONSTERS.put("captivated", 33643);
		PARTY90_MONSTERS.put("altar", 19250);

		PARTY90_MONSTERS.put("keeper", 19232);
		PARTY90_MONSTERS.put("watcher", 19233);
		PARTY90_MONSTERS.put("overseer", 19234);
		PARTY90_MONSTERS.put("ruler", 25883);

		SOLO95_MONSTERS.put("npc_adolph", 33630);
		SOLO95_MONSTERS.put("support_adolph", 33631);
		SOLO95_MONSTERS.put("npc_barton", 33632);
		SOLO95_MONSTERS.put("support_barton", 33633);
		SOLO95_MONSTERS.put("npc_hayuk", 33634);
		SOLO95_MONSTERS.put("support_hayuk", 33635);
		SOLO95_MONSTERS.put("npc_eliyah", 33636);
		SOLO95_MONSTERS.put("support_eliyah", 33637);
		SOLO95_MONSTERS.put("npc_elise", 33638);
		SOLO95_MONSTERS.put("support_elise", 33639);
		SOLO95_MONSTERS.put("support_eliyah_spirit", 33640);
		SOLO95_MONSTERS.put("support_troop", 33646);
		SOLO95_MONSTERS.put("altar", 19251);
		SOLO95_MONSTERS.put("captivated", 33645);
		SOLO95_MONSTERS.put("altar", 19251);

		SOLO95_MONSTERS.put("keeper", 19226);
		SOLO95_MONSTERS.put("watcher", 19227);
		SOLO95_MONSTERS.put("overseer", 19228);
		SOLO95_MONSTERS.put("ruler", 19255);

		PARTY95_MONSTERS.put("support_troop", 33646);
		PARTY95_MONSTERS.put("altar", 19252);
		PARTY95_MONSTERS.put("captivated", 33645);
		PARTY95_MONSTERS.put("altar", 19252);

		PARTY95_MONSTERS.put("keeper", 19235);
		PARTY95_MONSTERS.put("watcher", 19236);
		PARTY95_MONSTERS.put("overseer", 19237);
		PARTY95_MONSTERS.put("ruler", 25884);

		SOLO_LEFT_KILLER_ROUTES.add(new Location(-110440, -10472, -11926));
		SOLO_LEFT_KILLER_ROUTES.add(new Location(-110085, -10876, -11920));
		SOLO_LEFT_KILLER_ROUTES.add(new Location(-109182, -10791, -11920));
		SOLO_LEFT_KILLER_ROUTES.add(new Location(-109162, -10453, -11926));
		SOLO_LEFT_KILLER_ROUTES.add(new Location(-109933, -10451, -11688));

		SOLO_RIGHT_KILLER_ROUTES.add(new Location(-110440, -10472, -11926));
		SOLO_RIGHT_KILLER_ROUTES.add(new Location(-110020, -9980, -11920));
		SOLO_RIGHT_KILLER_ROUTES.add(new Location(-109157, -10009, -11920));
		SOLO_RIGHT_KILLER_ROUTES.add(new Location(-109162, -10453, -11926));
		SOLO_RIGHT_KILLER_ROUTES.add(new Location(-109933, -10451, -11688));

		PARTY_LEFT_KILLER_ROUTES.add(new Location(-120008, -10472, -11926));
		PARTY_LEFT_KILLER_ROUTES.add(new Location(-119653, -10876, -11920));
		PARTY_LEFT_KILLER_ROUTES.add(new Location(-118750, -10791, -11920));
		PARTY_LEFT_KILLER_ROUTES.add(new Location(-118730, -10453, -11926));
		PARTY_LEFT_KILLER_ROUTES.add(new Location(-119501, -10451, -11688));

		PARTY_RIGHT_KILLER_ROUTES.add(new Location(-120008, -10472, -11926));
		PARTY_RIGHT_KILLER_ROUTES.add(new Location(-119588, -9980, -11920));
		PARTY_RIGHT_KILLER_ROUTES.add(new Location(-118725, -10009, -11920));
		PARTY_RIGHT_KILLER_ROUTES.add(new Location(-118730, -10453, -11926));
		PARTY_RIGHT_KILLER_ROUTES.add(new Location(-119501, -10451, -11688));
	}

	private List<NpcInstance> _captivateds = new ArrayList<NpcInstance>();
	private List<NpcInstance> _wave = new CopyOnWriteArrayList<NpcInstance>();
	private List<NpcInstance> _supports = new ArrayList<NpcInstance>();
	private List<NpcInstance> _followers = new ArrayList<NpcInstance>();

	private boolean _isPartyInstance;

	private DeathListener _deathListener = new DeathListener();

	private NpcInstance _kartiaAlthar = null;
	private NpcInstance _ssqCameraLight = null;
	private NpcInstance _ssqCameraZone = null;
	private NpcInstance _ruler = null;
	private NpcInstance _warrior = null;
	private NpcInstance _archer = null;
	private NpcInstance _summoner = null;
	private NpcInstance _healer = null;
	private NpcInstance _knight = null;

	private int _currentWave = 0;
	private int _currentSubwave = 0;
	private long _waveSpawnTime;

	private int _killedSubwaves;
	private int _killedWaves = 0;

	private int _status = 0;

	private ZoneListener _startZoneListener = new ZoneListener();

	private Map<String, Integer> _monsterSet;
	private Map<String, List<String>> _firstRoomWaveNames = null;
	private Map<String, List<String>> _secondRoomWaveNames = null;
	private Map<String, List<String>> _raidRoomWaveNames = null;

	private TIntIntMap _monstersToKill = new TIntIntHashMap();

	private int _savedCaptivateds = 0;
	private String _excludedSupport;
	private boolean _poisonZoneEnabled = false;
	private int _firstRoomSubwavesSize;

	private ScheduledFuture<?> _healTask;
	private ScheduledFuture<?> _supportTask;
	private ScheduledFuture<?> _aggroCheckTask;
	private ScheduledFuture<?> _waveMovementTask;
	private ScheduledFuture<?> _altharCheckTask;
	private ScheduledFuture<?> __poisonZone;

	@Override
	protected void onCreate()
	{
		super.onCreate();

		getZone("[400061]").addListener(_startZoneListener);
		getZone("[400062]").addListener(_startZoneListener);
		getZone("[4600071]").addListener(_startZoneListener);
		getZone("[4600072]").addListener(_startZoneListener);

		_firstRoomSubwavesSize = 0;
		_firstRoomWaveNames = new TreeMap<String, List<String>>();
		String pf = getPrefix();
		List<String> waves = new ArrayList<String>();
		waves.add(pf + "wave1");
		_firstRoomWaveNames.put(pf + "wave1", waves);
		waves = new ArrayList<String>();
		waves.add(pf + "wave2");
		_firstRoomWaveNames.put(pf + "wave2", waves);
		waves = new ArrayList<String>();
		waves.add(pf + "wave3_part1");
		waves.add(pf + "wave3_part2");
		_firstRoomWaveNames.put(pf + "wave3", waves);
		waves = new ArrayList<String>();
		waves.add(pf + "wave4_part1");
		waves.add(pf + "wave4_part2");
		_firstRoomWaveNames.put(pf + "wave4", waves);
		waves = new ArrayList<String>();
		waves.add(pf + "wave5_part1");
		waves.add(pf + "wave5_part2");
		waves.add(pf + "wave5_part3");
		_firstRoomWaveNames.put(pf + "wave5", waves);
		waves = new ArrayList<String>();
		waves.add(pf + "wave6_part1");
		waves.add(pf + "wave6_part2");
		waves.add(pf + "wave6_part3");
		_firstRoomWaveNames.put(pf + "wave6", waves);
		waves = new ArrayList<String>();
		waves.add(pf + "wave7_part1");
		waves.add(pf + "wave7_part2");
		waves.add(pf + "wave7_part3");
		_firstRoomWaveNames.put(pf + "wave7", waves);

		for(List<String> spawns : _firstRoomWaveNames.values()) 
			_firstRoomSubwavesSize += spawns.size();

		_secondRoomWaveNames = new TreeMap<String, List<String>>();
		waves = new ArrayList<String>();
		waves.add(pf + "wave_room");
		_secondRoomWaveNames.put(pf + "wave_room", waves);
		waves = new ArrayList<String>();
		waves.add(pf + "wave_room");
		_secondRoomWaveNames.put(pf + "wave_room", waves);

		_raidRoomWaveNames = new TreeMap<String, List<String>>();
		waves = new ArrayList<String>();
		waves.add(pf + "rb1");
		_raidRoomWaveNames.put(pf + "wave1", waves);
		waves = new ArrayList<String>();
		waves.add(pf + "rb2");
		_raidRoomWaveNames.put(pf + "wave2", waves);
		waves = new ArrayList<String>();
		waves.add(pf + "rb3");
		_raidRoomWaveNames.put(pf + "wave3", waves);
		waves = new ArrayList<String>();
		waves.add(pf + "rb4");
		_raidRoomWaveNames.put(pf + "wave4", waves);
		waves = new ArrayList<String>();
		waves.add(pf + "rb5");
		_raidRoomWaveNames.put(pf + "wave5", waves);
		waves = new ArrayList<String>();
		waves.add(pf + "rb6");
		_raidRoomWaveNames.put(pf + "wave6", waves);
		waves = new ArrayList<String>();
		waves.add(pf + "rb7");
		_raidRoomWaveNames.put(pf + "wave7", waves);
		switch(getInstancedZoneId())
		{
			case 205:
				_isPartyInstance = false;
				_monsterSet = SOLO85_MONSTERS;
				break;
			case 206:
				_isPartyInstance = false;
				_monsterSet = SOLO90_MONSTERS;
				break;
			case 207:
				_isPartyInstance = false;
				_monsterSet = SOLO95_MONSTERS;
				break;
			case 208:
				_isPartyInstance = true;
				_monsterSet = PARTY85_MONSTERS;
				break;
			case 209:
				_isPartyInstance = true;
				_monsterSet = PARTY90_MONSTERS;
				break;
			case 210:
				_isPartyInstance = true;
				_monsterSet = PARTY95_MONSTERS;
				break;
		}


		if(!_isPartyInstance)
		{
			spawnByGroup(pf + "support");
			getZone("[kartia_teleport_solo]").addListener(_startZoneListener);

		}
		else
		{
			startChallenge();
			getZone("[kartia_teleport_party]").addListener(_startZoneListener);
		}
		for(NpcInstance npc : getNpcs())
			npc.setRandomWalk(false);
	}

	private String getPrefix()
	{
		switch (getInstancedZoneId())
		{
			case 205:
				return "K85S_";
			case 206:
				return "K90S_";
			case 207:
				return "K95S_";
			case 208:
				return "K85P_";
			case 209:
				return "K90P_";
			case 210:
				return "K95P_";
		}
		return "";
	}

	public void deselectSupport(String support)
	{
		if(_excludedSupport == null || _excludedSupport.equals(""))
		{
			_excludedSupport = support;
			for(Player player : getPlayers())
				player.teleToLocation(SOLO_ENTRANCE);
		}
		startChallenge();
		_excludedSupport = "";
	}

	private void cleanup()
	{
		_ssqCameraZone.setNpcState(3);
		_ssqCameraZone.setNpcState(0);
		_ssqCameraZone.deleteMe();
		if(_aggroCheckTask != null)
			_aggroCheckTask.cancel(true);
		if(_waveMovementTask != null)
			_waveMovementTask.cancel(true);
		if(_altharCheckTask != null)
			_altharCheckTask.cancel(true);
		if(_healTask != null)
			_healTask.cancel(true);
		if(_supportTask != null)
			_supportTask.cancel(true);
		if(__poisonZone != null)
			__poisonZone.cancel(true);
	}
  
	private long getReuseTime()
	{
		Calendar _instanceTime = Calendar.getInstance();

		Calendar currentTime = Calendar.getInstance();
		_instanceTime.set(Calendar.HOUR_OF_DAY, 6);
		_instanceTime.set(Calendar.MINUTE, 30);
		_instanceTime.set(Calendar.SECOND, 0);

		if(_instanceTime.compareTo(currentTime) < 0)
			_instanceTime.add(Calendar.DATE, 1);

		return _instanceTime.getTimeInMillis();
	}

	public class ZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			if(cha.isPlayer() && (_isPartyInstance && zone.getName().equalsIgnoreCase("[kartia_teleport_party]")))
			{
				if(_status == 0)
					cha.teleToLocation(PARTY_ZONE_TELEPORTER, cha.getReflection());
			}
			if(cha.isPlayer() &&  (!_isPartyInstance && zone.getName().equalsIgnoreCase("[kartia_teleport_solo]")))
			{
				if(_status == 0)
					cha.teleToLocation(SOLO_ZONE_TELEPORTER, cha.getReflection());
			}
			if(cha.isPlayer() && (_isPartyInstance && zone.getName().equalsIgnoreCase("[4600071]") || (!_isPartyInstance && zone.getName().equalsIgnoreCase("[4600072]"))))
				cha.getPlayer().addListener(_deathListener);
			if(_ssqCameraZone != null)
				_ssqCameraZone.setNpcState(0);
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
			if(cha.isPlayer() && ((_isPartyInstance && zone.getName().equalsIgnoreCase("[4600071]")) || (!_isPartyInstance && zone.getName().equalsIgnoreCase("[4600072]"))))
				cha.getPlayer().removeListener(_deathListener);
		}
	}

	private void invokeDeathListener()
	{
		for(NpcInstance npc : getNpcs())
			npc.addListener(_deathListener);
	}

	private class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature self, Creature killer)
		{
			if(self.isNpc())
			{
				NpcInstance npc = (NpcInstance) self;
				onNpcDie(npc, killer);
			}
			if(!self.isPlayer())
				return;
			boolean exit = true;
			if(_isPartyInstance)
			{
				for(Player member : getPlayers())
				{
					if(!member.isDead())
					{
						exit = false;
						break;
					}
				}
			}

			if(exit)
			{
				ThreadPoolManager.getInstance().schedule(() ->
				{
					clearReflection(5, true);
				}, 15000L);
			}
		}
	}

	private void startChallenge()
	{
		if(_isPartyInstance)
		{
			_kartiaAlthar = addSpawnWithoutRespawn(_monsterSet.get("altar").intValue(), new Location(-119684, -10453, -11307, 0), 0);
			_ssqCameraLight = addSpawnWithoutRespawn(SSQ_CAMERA, new Location(-119684, -10453, -11307, 0), 0);
			_ssqCameraZone = addSpawnWithoutRespawn(SSQ_CAMERA, new Location(-119907, -10443, -11924, 0), 0);

		}
		else
		{
			_kartiaAlthar = addSpawnWithoutRespawn(_monsterSet.get("altar").intValue(), new Location(-110116, -10453, -11307, 0), 0);
			_ssqCameraLight = addSpawnWithoutRespawn(SSQ_CAMERA, new Location(-110116, -10453, -11307, 0), 0);
			_ssqCameraZone = addSpawnWithoutRespawn(SSQ_CAMERA, new Location(-110339, -10443, -11924, 0), 0);
		}

		_ssqCameraZone.setNpcState(3);
		_ssqCameraZone.setNpcState(0);

		_ssqCameraLight.setNpcState(3);
		_ssqCameraLight.setNpcState(0);

		_kartiaAlthar.setRandomWalk(false);
		_kartiaAlthar.setIsInvul(true);
		_ssqCameraLight.setRandomWalk(false);
		_ssqCameraZone.setRandomWalk(false);

		if(!_isPartyInstance)
		{
			_knight = addSpawnWithoutRespawn(_monsterSet.get("support_adolph").intValue(), new Location(SOLO_ENTRANCE.getX(), SOLO_ENTRANCE.getY(), SOLO_ENTRANCE.getZ(), 0), 0);
			_followers.add(_knight);

			if(!_excludedSupport.equals("WARRIOR"))
			{
				_warrior = addSpawnWithoutRespawn(_monsterSet.get("support_barton").intValue(), new Location(SOLO_ENTRANCE.getX(), SOLO_ENTRANCE.getY(), SOLO_ENTRANCE.getZ(), 0), 0);
				_followers.add(_warrior);
			}

			if(!_excludedSupport.equals("ARCHER"))
			{
				_archer = addSpawnWithoutRespawn(_monsterSet.get("support_hayuk").intValue(), new Location(SOLO_ENTRANCE.getX(), SOLO_ENTRANCE.getY(), SOLO_ENTRANCE.getZ(), 0), 0);
				_followers.add(_archer);
			}

			if(!_excludedSupport.equals("SUMMONER"))
			{
				_summoner = addSpawnWithoutRespawn(_monsterSet.get("support_eliyah").intValue(), new Location(SOLO_ENTRANCE.getX(), SOLO_ENTRANCE.getY(), SOLO_ENTRANCE.getZ(), 0), 0);
				_followers.add(_summoner);

				for(byte i = 0; i < 3; i = (byte)(i + 1))
				{
					NpcInstance light = addSpawnWithoutRespawn(_monsterSet.get("support_eliyah_spirit").intValue(), new Location(_summoner.getX(), _summoner.getY(), _summoner.getZ(), 0), 0);
					_followers.add(light);
				}
			}

			if(!_excludedSupport.equals("HEALER"))
			{
				_healer = addSpawnWithoutRespawn(_monsterSet.get("support_elise").intValue(), new Location(SOLO_ENTRANCE.getX(), SOLO_ENTRANCE.getY(), SOLO_ENTRANCE.getZ(), 0), 0);
				_followers.add(_healer);

				_healTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new HealTask(), 2000L, 7000L);
			}

			_supportTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new KartiaSupportTask(), 2000L, 2000L);
		}

		for(DoorInstance door : getDoors())
			door.closeMe();

		String pf = getPrefix();
		spawnByGroup(pf + "captivated");

		for(NpcInstance npc : getNpcs())
		{
			if(npc.getNpcId() == _monsterSet.get("captivated") )
			{
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
				npc.doCast(SkillHolder.getInstance().getSkill(14988, 1), npc, true);
				_captivateds.add(npc);
			}
			npc.setBusy(true);
		}

		ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl()
			{
				nextWave();
			}
		}
		, 10000L);

		_aggroCheckTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new MonsterAggroTask(), 5000L, 3000L);
		_waveMovementTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new MonsterMovementTask(), 5000L, 3000L);
		_altharCheckTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new AltharTask(), 5000L, 3000L);
		__poisonZone = ThreadPoolManager.getInstance().scheduleAtFixedRate(new PoisenTask(), 2000L, 10000L);
	}

	private boolean nextWave()
	{
		if(_status > 2)
			return false;

		Map<String, List<String>> waves = null;
		if(_status == 0)
		{
			waves = _firstRoomWaveNames;
		}
		else if(_status == 1)
			waves = _secondRoomWaveNames;
		else if(_status == 2)
			waves = _raidRoomWaveNames;

		if(waves == null || _currentWave >= waves.size())
		{
			return false;
		}

		List<String> subwaves = null;
		int i = 0;
		for(List<String> wave : waves.values())
		{
			if(i == _currentWave)
			{
				if(_currentSubwave >= wave.size())
				{
					_currentSubwave = 0;
					_currentWave++;
					i++;
					continue;
				}

				subwaves = wave;
				_currentWave = i;
			}
			i++;
		}
		if(subwaves == null || (_currentSubwave >= subwaves.size()))
			return false;

		String waveName = subwaves.get(_currentSubwave++);

		List<Spawner> spawnList = spawnByGroup(waveName);

		invokeDeathListener();
		for(NpcInstance npc : getNpcs())
		{
			if(npc.getAI() instanceof NpcAI)
			{
				System.out.println("NpcID "+npc.getNpcId()+" isn't DefaultAI");
				continue;
			}
			DefaultAI ai = (DefaultAI) npc.getAI();
			if(ai != null)
				ai.setMaxPursueRange(Integer.MAX_VALUE);
			npc.setRandomWalk(false);
		}

		//getCurrentNpcId
		if((_currentSubwave - 1) == 0 && (_status == 0 || _status == 2))
		{
			for(Player player : getPlayers())
				player.sendPacket(new ExShowScreenMessage(NpcString.STAGE_S1, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, String.valueOf(_currentWave + 1)));
		}

		_waveSpawnTime = 0L;
		_wave.clear();
		_monstersToKill.clear();
		for(Spawner spawn : spawnList)
		{
			int npcId = spawn.getCurrentNpcId();
			if(!_monstersToKill.containsKey(npcId))
				_monstersToKill.put(npcId, 0);
			_monstersToKill.put(spawn.getCurrentNpcId(), _monstersToKill.get(npcId) + 1);
			_wave.add(spawn.getLastSpawn());
		}

		_waveSpawnTime = System.currentTimeMillis();

		return true;
	}


	public void onNpcDie(NpcInstance npc, Creature killer)
	{
		if((_status <= 2 && _monstersToKill != null && !_monstersToKill.isEmpty()))
		{
			boolean neededToProgress = false;
			for(int npcId : _monstersToKill.keys()) 
			{ 
				if(npc.getNpcId() == npcId)
				{
					int newValue = _monstersToKill.get(npcId) - 1;
					if(newValue <= 0)
						_monstersToKill.remove(npcId);
					else
						_monstersToKill.put(npcId, newValue);
					neededToProgress = true;
					break;
				}
			}

			if(neededToProgress)
			{
				boolean needNextWave = true;
				for(int killCount : _monstersToKill.values()) 
				{ 
					if(killCount > 0)
					{
						needNextWave = false;
						break;
					}
				}

				if(needNextWave)
				{
					if((_currentSubwave - 1) == 0)
						_killedWaves += 1;
					_killedSubwaves += 1;

					if((_status == 0) && (_killedWaves == _firstRoomWaveNames.size()) && (_killedSubwaves == _firstRoomSubwavesSize - 1))
					{
						_ssqCameraLight.setNpcState(1);
						_ssqCameraZone.setNpcState(2);
						_poisonZoneEnabled = true;
					}
					else if((_status == 0) && (_killedSubwaves >= _firstRoomSubwavesSize))
					{
						_poisonZoneEnabled = false;
						_status = 1;
						_killedWaves = 0;
						_currentWave = 0;
						_currentSubwave = 0;
						if(_isPartyInstance)
							getDoor(PARTY_ROOM_DOOR).openMe();
						else
							getDoor(SOLO_ROOM_DOOR).openMe();
						_ssqCameraZone.setNpcState(3);
						_ssqCameraZone.setNpcState(0);

						saveCaptivateds();
					}
					else if((_status == 2) && (_killedWaves >= _raidRoomWaveNames.size() - 1))
					{
						_status = 3;
						freeRuler();
					}

					if(_status < 3)
					{
						ThreadPoolManager.getInstance().schedule(new RunnableImpl()
						{
							@Override
							public void runImpl()
							{
								nextWave();
							}
						}
						, 10000L);
					}
				}
			}

		}
		else if(_status == 3 && npc.getNpcId() == _monsterSet.get("ruler").intValue()){
			cleanup();
			clearReflection(5, true);

			if(!_isPartyInstance){
				for (Player player : getPlayers()){
					switch (getInstancedZoneId()){
						case 205:
							player.addExpAndSp(483347222L, 118102);
							break;
						case 206:
							player.addExpAndSp(670413210L, 161608);
							break;
						case 207:
							player.addExpAndSp(970749459L, 235848);
					}
				}
			}
			setReenterTime(getReuseTime());
		}
	}

	public void openRaidDoor()
	{
		_status = 2;
		_killedWaves = 0;
		_currentWave = 0;
		_currentSubwave = 0;
		_wave.clear();
		_monstersToKill.clear();

		ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl()
			{
				nextWave();
			}
		}
		, 10000L);

		if(_isPartyInstance)
			getDoor(PARTY_RAID_DOOR).openMe();
		else
			getDoor(SOLO_RAID_DOOR).openMe();
		if(_isPartyInstance)
			_ruler = addSpawnWithoutRespawn(_monsterSet.get("ruler").intValue(), new Location(-120864, -15872, -11400, 15596), 0);
		else
			_ruler = addSpawnWithoutRespawn(_monsterSet.get("ruler").intValue(), new Location(-111296, -15872, -11400, 15596), 0);

		_ruler.setIsInvul(true);
		_ruler.startAbnormalEffect(AbnormalEffect.FLESH_STONE);
		_ruler.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, _ruler);
		_ruler.startParalyzed();

		if(_savedCaptivateds > 0)
		{
			for(int i = 0; i < _savedCaptivateds; i++)
			{
				NpcInstance support;

				if(_isPartyInstance)
					support = addSpawnWithoutRespawn(_monsterSet.get("support_troop").intValue(), new Location(-120901 + Rnd.get(100, 250), -14562 + Rnd.get(100, 250), -11424, 47595), 0);
				else
					support = addSpawnWithoutRespawn(_monsterSet.get("support_troop").intValue(), new Location(-111333 + Rnd.get(100, 250), -14562 + Rnd.get(100, 250), -11424, 47595), 0);
				_supports.add(support);
			}
			ThreadPoolManager.getInstance().scheduleAtFixedRate(new RaidSupportTask(), 1000L, 5000L);
		}

		ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl()
			{
				if(_isPartyInstance)
					getDoor(PARTY_RAID_DOOR).closeMe();
				else
					getDoor(SOLO_RAID_DOOR).closeMe();
			}
		}
		, 15000L);
	}

	public void spawnHealingTree()
	{

		Skill buff = SkillHolder.getInstance().getSkill(15003, 1);
		Skill heal = SkillHolder.getInstance().getSkill(15002, 1);

		if(getPlayers().isEmpty())
			return;

		final Player player = getPlayers().get(0);

		Location loc = player.getLoc();

		final Creature tree = addSpawnWithoutRespawn(19256, new Location(loc.getX(), loc.getY(), loc.getZ(), loc.h), 0);
		tree.setTarget(player);
		tree.doCast(buff, player, true);
		tree.doCast(heal, player, true);

		ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl()
			{
				if((tree != null) && (!player.isDead()))
				{
					tree.setTarget(player);

					tree.doCast(SkillHolder.getInstance().getSkill(15002, 1), player, true);

					ThreadPoolManager.getInstance().schedule(this, 10000L);
				}
			}
		}
		, 10000L);

		ThreadPoolManager.getInstance().schedule(new RunnableImpl(){
			@Override
			public void runImpl(){
				if((tree != null) && (!player.isDead())){
					tree.setTarget(player);

					tree.doCast(SkillHolder.getInstance().getSkill(15003, 1), player, true);

					ThreadPoolManager.getInstance().schedule(this, 20000L);
				}
			}
		}
				, 20000L);
	}

	public synchronized void saveCaptivateds()
	{
		int delay = 0;
		for(final NpcInstance captivated : _captivateds)
		{
			_savedCaptivateds += 1;

			ThreadPoolManager.getInstance().schedule(new RunnableImpl()
			{
				@Override
				public void runImpl()
				{
					if(captivated != null)
					{
						DefaultAI ai = (DefaultAI) captivated.getAI();

						if(_isPartyInstance)
							ai.addTaskMove(Location.findPointToStay(new Location(-118391, -10454, -11924), 250, 250, captivated.getGeoIndex()), true);
						else
							ai.addTaskMove(Location.findPointToStay(new Location(-108823, -10454, -11924), 250, 250, captivated.getGeoIndex()), true);
					}
				}
			}
			, delay);

			ThreadPoolManager.getInstance().schedule(new RunnableImpl()
			{
				@Override
				public void runImpl()
				{
					captivated.deleteMe();
				}
			}
			, 10000 + delay);

			delay += 1000;
		}
		_captivateds.clear();
	}

	public void freeRuler()
	{
		if(_ruler != null)
		{
			_ruler.stopAbnormalEffect(AbnormalEffect.FLESH_STONE);
			_ruler.setIsInvul(false);
			_ruler.stopParalyzed();
			_wave.add(_ruler);
			_ruler.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK);
		}
	}


	public class KartiaSupportTask extends RunnableImpl
	{
		private int _lastPlyaerHeading;

		public KartiaSupportTask()
		{
			_lastPlyaerHeading = -1;
		}

		@Override
		public void runImpl()
		{
			if(getPlayers().isEmpty())
				return;

			boolean refollowAll = false;
			for(NpcInstance follower : _followers)
			{
				boolean needFollow = true;

				if(((follower.getAI().getIntention() != CtrlIntention.AI_INTENTION_ATTACK) || (!follower.isAttackingNow())) && ((follower.getAI().getIntention() != CtrlIntention.AI_INTENTION_CAST) || (!follower.isCastingNow())) && (!follower.isMoving))
				{
					for(AggroList.HateInfo aggro : follower.getAggroList().getPlayableMap().values())
						if((aggro.attacker.isNpc()) || (aggro.attacker.isPlayer()))
							follower.getAggroList().remove(aggro.attacker, true);

					if((getPlayers().size() > 0) && (PositionUtils.calculateDistance(getPlayers().get(0), follower, true) > 600.0D))
						needFollow = true;
					else
					{
						for(NpcInstance npc : follower.getAroundNpc(600, 50))
						{
							if(npc.isMonster() && npc.getNpcId() != _monsterSet.get("captivated").intValue() && npc.getNpcId() != _monsterSet.get("altar").intValue() && !npc.isInvul())
							{
								follower.setRunning();
								follower.getAggroList().addDamageHate(npc, 999, 999);
								follower.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, npc, 1000);
								needFollow = false;
								break;
							}

						}

						if(needFollow)
						{
							if(_lastPlyaerHeading < 0 || (getPlayers().size() > 0 && _lastPlyaerHeading != getPlayers().get(0).getLoc().h) || refollowAll)
							{
								needFollow = true;
								refollowAll = true;
							}
							else
								needFollow = false;
							_lastPlyaerHeading = getPlayers().size() > 0 ? getPlayers().get(0).getLoc().h : 0;
						}
					}
				}
				else
					needFollow = false;

				if(needFollow)
				{
					double angle = PositionUtils.convertHeadingToDegree(getPlayers().get(0).getLoc().h);
					double radians = Math.toRadians(angle);
					double radius = 100.0D;
					double course = 160.0D;

					int x = (int)(Math.cos(3.141592653589793D + radians + course) * radius);
					int y = (int)(Math.sin(3.141592653589793D + radians + course) * radius);

					follower.setRunning();
					Location loc = getPlayers().get(0).getLoc();
					loc.setX(loc.getX() + x + Rnd.get(-60, 60));
					loc.setY(loc.getY() + y + Rnd.get(-60, 60));
					DefaultAI ai = (DefaultAI) follower.getAI();
					ai.addTaskMove(Location.findPointToStay(loc, 100, 100, follower.getGeoIndex()), true);
				}
			}
		}
	}

	public class HealTask extends RunnableImpl
	{
		public HealTask()
		{
			//
		}

		@Override
		public void runImpl()
		{
			if(getPlayers().isEmpty())
				return;

			final Player player = getPlayers().get(0);
			if(_healer != null)
			{
				double percentHp = player.getCurrentHp() / player.getMaxHp();
				if(percentHp <= 0.5D)
				{
					_healer.setTarget(player);

					switch (getInstancedZoneId())
					{
						case 205:
							_healer.doCast(SkillHolder.getInstance().getSkill(14899, 1), player, true);
							break;
						case 206:
							_healer.doCast(SkillHolder.getInstance().getSkill(14900, 1), player, true);
							break;
						case 207:
							_healer.doCast(SkillHolder.getInstance().getSkill(14901, 1), player, true);
					}

					boolean needTree = true;
					for(NpcInstance npc : getNpcs())
					{
						if(npc.getNpcId() == 19256)
						{
							needTree = false;
							break;
						}
					}
					if(needTree)
					{
						ThreadPoolManager.getInstance().schedule(new RunnableImpl()
						{
							@Override
							public void runImpl()
							{
								switch(getInstancedZoneId())
								{
									case 205:
										_healer.doCast(SkillHolder.getInstance().getSkill(14903, 1), player, true);
										break;
									case 206:
										_healer.doCast(SkillHolder.getInstance().getSkill(14904, 1), player, true);
										break;
									case 207:
										_healer.doCast(SkillHolder.getInstance().getSkill(14905, 1), player, true);
								}

								ThreadPoolManager.getInstance().schedule(new RunnableImpl()
								{
									@Override
									public void runImpl()
									{
										spawnHealingTree();
									}
								}
								, 2000L);
							}
						}
						, 2000L);
					}

				}
				else if(percentHp <= 0.85D)
				{
					_healer.setTarget(player);
					_healer.doCast(SkillHolder.getInstance().getSkill(14899, 1), player, true);
				}
			}
		}
	}


	public class RaidSupportTask extends RunnableImpl
	{
		public RaidSupportTask()
		{
			//
		}

		@Override
		public void runImpl()
		{
			for(NpcInstance support : _supports) 
			{
				if((!support.isAttackingNow()) && (!support.isCastingNow()))
				{
					for(NpcInstance monster : support.getAroundNpc(1200, 1200))
					{
						if(monster.isMonster() && !monster.isInvul())
						{
							support.setRunning();
							support.getAggroList().addDamageHate(monster, 999, 999);
							support.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, monster, 1000);
							break;
						}
					}
				}
			}
		}
	}

	public class AltharTask extends RunnableImpl
	{
		private final Skill HP_ABSORBTION85 = SkillHolder.getInstance().getSkill(14982, 1);
		private final Skill HP_ABSORBTION90 = SkillHolder.getInstance().getSkill(14983, 1);
		private final Skill HP_ABSORBTION95 = SkillHolder.getInstance().getSkill(14984, 1);

		public AltharTask()
		{
			//
		}

		@Override
		public void runImpl()
		{
			Skill castSkill = null;
			switch(getInstancedZoneId())
			{
				case 205:
				case 208:
					castSkill = HP_ABSORBTION85;
					break;
				case 206:
				case 209:
					castSkill = HP_ABSORBTION90;
					break;
				case 207:
				case 210:
					castSkill = HP_ABSORBTION95;
			}

			if(castSkill == null)
				return;

			if(_captivateds != null && !_captivateds.isEmpty())
			{
				for(NpcInstance npc : _wave)
				{
					if(!npc.isDead())
					{
						double distance = PositionUtils.calculateDistance(npc, _kartiaAlthar, true);
						if(distance < 500.0D && npc.getZ() - _kartiaAlthar.getZ() < 150)
						{
							onNpcDie(npc, _kartiaAlthar);
							if(npc != null)
								npc.deleteMe();
							_kartiaAlthar.setNpcState(1);
							if(_captivateds.size() > 0)
							{
								final NpcInstance captivated = _captivateds.get(Rnd.get(_captivateds.size()));
								if(captivated != null)
								{
									_kartiaAlthar.setTarget(captivated);
									_kartiaAlthar.doCast(castSkill, captivated, true);
									_kartiaAlthar.setHeading(0);

									ThreadPoolManager.getInstance().schedule(() ->
									{
										captivated.deleteMe();
											if(_captivateds != null)
												_captivateds.remove(captivated);
									}, 11000L);
								}
							}
						}
					}
				}
			}
		}
	}

	public class PoisenTask extends RunnableImpl
	{

		private final Skill ZONE_POISEN85 = SkillHolder.getInstance().getSkill(14989, 1);
		private final Skill ZONE_POISEN90 = SkillHolder.getInstance().getSkill(14990, 1);
		private final Skill ZONE_POISEN95 = SkillHolder.getInstance().getSkill(14991, 1);

		public PoisenTask()
		{
			//
		}

		private void chekZone()
		{
			Skill castSkillp = null;

			switch(getInstancedZoneId())
			{
				case 205:
				case 208:
					castSkillp = ZONE_POISEN85;
					break;
				case 206:
				case 209:
					castSkillp = ZONE_POISEN90;
					break;
				case 207:
				case 210:
					castSkillp = ZONE_POISEN95;
			}

			if (_poisonZoneEnabled)
			{
				for(Player player : getPlayers())
					_ssqCameraZone.doCast(castSkillp, player, true);
			}
		}

		@Override
		public void runImpl()
		{
			if (_status == 0)
			{
				chekZone();
			}
		}

	}
		public class MonsterAggroTask extends RunnableImpl
	{

		public MonsterAggroTask()
		{
			//
		}

		private void aggroCheck(NpcInstance npc)
		{
			if((npc.getAI().getIntention() != CtrlIntention.AI_INTENTION_CAST || npc.getAI().getIntention() != CtrlIntention.AI_INTENTION_ATTACK) && npc.getTarget() == null)
			{
				if(!World.getAroundPlayers(npc).isEmpty())
				{
					List<GameObject> objects = World.getAroundObjects(npc, 450, 50);
					for(GameObject object : objects)
					{
						if(object instanceof Player || object instanceof GuardInstance)
						{
							if (object.getInvisibleType() == InvisibleType.NONE)
							{
								npc.getAggroList().addDamageHate((Creature) object, 999, 999);
								npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, object, 1000);
								break;
							}
						}
						npc.getAggroList().clear();
					}
				}
				else
					npc.getAggroList().clear();
			}
		}

		@Override
		public void runImpl()
		{
			if(_status <= 2)
			{
				for(NpcInstance npc : _wave)
				{
					aggroCheck(npc);
					if (npc.getNpcId() == _monsterSet.get("overseer").intValue())
					{
						Location loc = _isPartyInstance ? new Location(-120840, -13944, -11456) : new Location(-111297, -13904, -11440);
						double distance = PositionUtils.calculateDistance(npc.getX(), npc.getY(), npc.getZ(), loc.getX(), loc.getY(), loc.getZ(), true);
						if (distance < 80)
						{
							npc.deleteMe();
							if (_status == 1)
								openRaidDoor();
						}
					}
				}
				if(_status == 3 && _ruler != null)
					aggroCheck(_ruler);
			}
		}
	}

	public class MonsterMovementTask extends RunnableImpl
	{
		public MonsterMovementTask()
		{
			//
		}

		@Override
		public void runImpl()
		{
			if(_waveSpawnTime == 0L || (System.currentTimeMillis() - _waveSpawnTime) / 1000L < 15L || _status != 0)
				return;

			int waveSize = _wave.size();
			int counter = 0;
			for(NpcInstance npc : _wave)
			{
				counter++;
				if(npc.getAggroList().isEmpty() && !npc.isMoving)
				{
					List<Location> routes;
					if(_isPartyInstance)
					{
						if(counter <= waveSize / 2)
							routes = PARTY_LEFT_KILLER_ROUTES;
						else
							routes = PARTY_RIGHT_KILLER_ROUTES;
					}
					else
					{
						if(counter <= waveSize / 2)
							routes = SOLO_LEFT_KILLER_ROUTES;
						else
							routes = SOLO_RIGHT_KILLER_ROUTES;
					}
					boolean takeNextRoute = false;
					Location nearestLoc = null;
					Double nearestLocDistance = null;
					Location npcLoc = npc.getLoc();
					for(Location loc : routes)
					{
						if(takeNextRoute)
						{
							nearestLoc = loc;
							break;
						}
						double distance = PositionUtils.calculateDistance(npcLoc.getX(), npcLoc.getY(), npcLoc.getZ(), loc.getX(), loc.getY(), loc.getZ(), true);
						if(distance < 150.0D)
							takeNextRoute = true;
						else if(nearestLoc == null)
						{
							nearestLoc = loc;
							nearestLocDistance = Double.valueOf(distance);
						}
						else
						{
							double currentLocDistance = PositionUtils.calculateDistance(npcLoc.getX(), npcLoc.getY(), npcLoc.getZ(), loc.getX(), loc.getY(), loc.getZ(), true);
							if(currentLocDistance <= nearestLocDistance.doubleValue())
							{
								nearestLoc = loc;
								nearestLocDistance = Double.valueOf(currentLocDistance);
							}
						}
					}
					if(nearestLoc != null)
					{
						nearestLoc.setX(nearestLoc.getX());
						nearestLoc.setY(nearestLoc.getY());
						npc.setRunning();
						DefaultAI ai = (DefaultAI) npc.getAI();
						ai.addTaskMove(Location.findPointToStay(nearestLoc, 100, 100, npc.getGeoIndex()), true);
					}
				}
			}
		}
	}
	public int getStatus()
	{
		return _status;
	}

	public Map<String, Integer> getMonsterSet()
	{
		return _monsterSet;
	}

	public boolean isPartyInstance()
	{
		return _isPartyInstance;
	}
}
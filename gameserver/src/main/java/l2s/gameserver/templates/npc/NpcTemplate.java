package l2s.gameserver.templates.npc;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import l2s.gameserver.ai.CharacterAI;
import l2s.gameserver.data.xml.holder.LootHolder;
import l2s.gameserver.idfactory.IdFactory;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.instances.RaidBossInstance;
import l2s.gameserver.model.instances.ReflectionBossInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestEventType;
import l2s.gameserver.model.reward.RewardList;
import l2s.gameserver.model.reward.RewardType;
import l2s.gameserver.scripts.Scripts;
import l2s.gameserver.skills.EffectUseType;
import l2s.gameserver.templates.CreatureTemplate;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.TeleportLocation;
import l2s.gameserver.templates.skill.EffectTemplate;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.*;

public class NpcTemplate extends CreatureTemplate {
    public enum ShotsType {
        NONE,
        SOUL,
        SPIRIT,
        BSPIRIT,
        SOUL_SPIRIT,
        SOUL_BSPIRIT
    }

    @SuppressWarnings("unchecked")
    public static final Constructor<NpcInstance> DEFAULT_TYPE_CONSTRUCTOR = (Constructor<NpcInstance>) NpcInstance.class.getConstructors()[0];
    @SuppressWarnings("unchecked")
    public static final Constructor<CharacterAI> DEFAULT_AI_CONSTRUCTOR = (Constructor<CharacterAI>) CharacterAI.class.getConstructors()[0];
    private static final Logger _log = LoggerFactory.getLogger(NpcTemplate.class);
    public final String name;
    public final String forceName;      // if set will be used over client name
    public final String title;
    public final String forceTitle;     // if set will be used over client title
    public final int rewardRp;
    public final double rateHp;
    public final String jClass;
    public final int displayId;
    public final ShotsType shots;
    public final int _enchantEffect;
    private final int _npcId;
    private final StatsSet _AIParams;
    private final int _castleId;
    private final int _baseRandDam;
    private final int _baseReuseDelay;
    @SuppressWarnings("unused")
    private final double _basePHitModify;
    @SuppressWarnings("unused")
    private final double _basePAvoidModify;
    @SuppressWarnings("unused")
    private final double _baseHitTimeFactor;
    @SuppressWarnings("unused")
    private final int _baseSafeHeight;
    // не используется - public final String sex;
    public int level;
    public long rewardExp;
    public long rewardSp;
    public int aggroRange;
    public int rhand;
    public int lhand;
    public boolean isRaid = false;
    private Faction faction = Faction.NONE;
    /**
     * fixed skills
     */
    private int race = 0;
    private HashMap<RewardType, RewardList> rewards;
    private TIntObjectMap<List<TeleportLocation>> _teleportList = new TIntObjectHashMap<List<TeleportLocation>>(1);
    private List<MinionData> _minions = Collections.emptyList();
    private List<AbsorbInfo> _absorbInfo = Collections.emptyList();
    private Map<QuestEventType, Quest[]> _questEvents = Collections.emptyMap();
    private TIntObjectMap<Skill> _skills = new TIntObjectHashMap<Skill>();
    private Skill[] _damageSkills = Skill.EMPTY_ARRAY;
    private Skill[] _dotSkills = Skill.EMPTY_ARRAY;
    private Skill[] _debuffSkills = Skill.EMPTY_ARRAY;
    private Skill[] _buffSkills = Skill.EMPTY_ARRAY;
    private Skill[] _stunSkills = Skill.EMPTY_ARRAY;
    private Skill[] _healSkills = Skill.EMPTY_ARRAY;
    private Class<NpcInstance> _classType = NpcInstance.class;
    private Constructor<NpcInstance> _constructorType = DEFAULT_TYPE_CONSTRUCTOR;
    private Class<CharacterAI> _classAI = CharacterAI.class;
    private Constructor<CharacterAI> _constructorAI = DEFAULT_AI_CONSTRUCTOR;
    private String _htmRoot;
    private TIntObjectMap<WalkerRoute> _walkerRoute = new TIntObjectHashMap<WalkerRoute>();
    private RandomActions _randomActions = null;
    /**
     * Constructor<?> of L2Character.<BR><BR>
     *
     * @param set The StatsSet object to transfer data to the method
     */
    public NpcTemplate(StatsSet set) {
        super(set);
        _npcId = set.getInteger("npcId");
        displayId = set.getInteger("displayId");

        name = set.getString("name");
        forceName = set.getString("forceName");
        title = set.getString("title");
        forceTitle = set.getString("forceTitle");
        // sex = set.getString("sex");
        level = set.getInteger("level");
        rewardExp = set.getLong("rewardExp");
        rewardSp = set.getLong("rewardSp");
        rewardRp = set.getInteger("rewardRp");
        aggroRange = set.getInteger("aggroRange");
        rhand = set.getInteger("rhand", 0);
        lhand = set.getInteger("lhand", 0);
        rateHp = set.getDouble("baseHpRate");
        jClass = set.getString("texture", null);
        _htmRoot = set.getString("htm_root", null);
        shots = set.getEnum("shots", ShotsType.class, ShotsType.NONE);
        _castleId = set.getInteger("castle_id", 0);
        _AIParams = (StatsSet) set.getObject("aiParams", StatsSet.EMPTY);
        _enchantEffect = set.getInteger("enchant_effect", 0);

        _baseRandDam = set.getInteger("baseRandDam", 5 + (int) Math.sqrt(level));

        _baseReuseDelay = set.getInteger("baseReuseDelay", 0);
        _basePHitModify = set.getDouble("basePHitModify", 0);
        _basePAvoidModify = set.getDouble("basePAvoidModify", 0);
        _baseHitTimeFactor = set.getDouble("baseHitTimeFactor", 0);
        _baseSafeHeight = set.getInteger("baseSafeHeight", 100);

        setType(set.getString("type", null));
        setAI(set.getString("ai_type", null));
    }

    @Override
    public void update(StatsSet set) {
        super.update(set);
        level = set.getInteger("level", level);
        rewardExp = set.getLong("rewardExp", rewardExp);
        rewardSp = set.getLong("rewardSp", rewardSp);
        rhand = set.getInteger("rhand", rhand);
        lhand = set.getInteger("lhand", lhand);
        aggroRange = set.getInteger("aggroRange", aggroRange);
    }

    public Class<? extends NpcInstance> getInstanceClass() {
        return _classType;
    }

    public Constructor<? extends NpcInstance> getInstanceConstructor() {
        return _constructorType;
    }

    public boolean isInstanceOf(Class<?> _class) {
        return _class.isAssignableFrom(getInstanceClass());
    }

    /**
     * Создает новый инстанс NPC. Для него следует вызывать (именно в этом порядке):
     * <br> setSpawnedLoc (обязательно)
     * <br> setReflection (если reflection не базовый)
     * <br> setChampion (опционально)
     * <br> setCurrentHpMp (если вызывался setChampion)
     * <br> spawnMe (в качестве параметра брать getSpawnedLoc)
     */
    public NpcInstance getNewInstance() {
        try {
            return _constructorType.newInstance(IdFactory.getInstance().getNextId(), this);
        }
        catch (Exception e) {
            _log.error("Unable to create instance of NPC " + _npcId, e);
        }

        return null;
    }

    public CharacterAI getNewAI(NpcInstance npc) {
        try {
            return _constructorAI.newInstance(npc);
        }
        catch (Exception e) {
            _log.error("Unable to create ai of NPC " + _npcId, e);
        }

        return new CharacterAI(npc);
    }

    @SuppressWarnings("unchecked")
    protected void setType(String type) {
        Class<NpcInstance> classType = null;
        try {
            classType = (Class<NpcInstance>) Class.forName("l2s.gameserver.model.instances." + type + "Instance");
        }
        catch (ClassNotFoundException e) {
            classType = (Class<NpcInstance>) Scripts.getInstance().getClasses().get("npc.model." + type + "Instance");
        }

        if (classType == null) {
            _log.error("Not found type class for type: " + type + ". NpcId: " + _npcId);
        }

        if (_npcId == 0) //temp
        {
            try {
                classType = (Class<NpcInstance>) Class.forName("l2s.gameserver.model.instances.NpcInstance");
            }
            catch (ClassNotFoundException e) {
            }

            _classType = classType;
            _constructorType = (Constructor<NpcInstance>) _classType.getConstructors()[0];
        }
        else {
            _classType = classType;
            _constructorType = (Constructor<NpcInstance>) _classType.getConstructors()[0];
        }

        if (_classType.isAnnotationPresent(Deprecated.class)) {
            _log.error("Npc type: " + type + ", is deprecated. NpcId: " + _npcId);
        }

        //TODO [G1ta0] сделать поле в соотвествующих классах
        isRaid = isInstanceOf(RaidBossInstance.class) && !isInstanceOf(ReflectionBossInstance.class);
    }

    @SuppressWarnings("unchecked")
    protected void setAI(String ai) {
        Class<CharacterAI> classAI = null;
        try {
            classAI = (Class<CharacterAI>) Class.forName("l2s.gameserver.ai." + ai);
        }
        catch (ClassNotFoundException e) {
            classAI = (Class<CharacterAI>) Scripts.getInstance().getClasses().get("ai." + ai);
        }

        if (classAI == null) {
            _log.error("Not found ai class for ai: " + ai + ". NpcId: " + _npcId);
        }

        if (_npcId == 0) //temp
        {
            try {
                classAI = (Class<CharacterAI>) Class.forName("l2s.gameserver.ai.NpcAI");
            }
            catch (ClassNotFoundException e) {
            }

            _classAI = classAI;
            _constructorAI = (Constructor<CharacterAI>) _classAI.getConstructors()[0];
        }
        else {
            _classAI = classAI;
            _constructorAI = (Constructor<CharacterAI>) _classAI.getConstructors()[0];
        }

        if (_classAI.isAnnotationPresent(Deprecated.class)) {
            _log.error("Ai type: " + ai + ", is deprecated. NpcId: " + _npcId);
        }
    }

    public void addTeleportList(int id, List<TeleportLocation> list) {
        _teleportList.put(id, list);
    }

    public List<TeleportLocation> getTeleportList(int id) {
        return _teleportList.get(id);
    }

    public TIntObjectMap<List<TeleportLocation>> getTeleportList() {
        return _teleportList;
    }

    public void addAbsorbInfo(AbsorbInfo absorbInfo) {
        if (_absorbInfo.isEmpty()) {
            _absorbInfo = new ArrayList<AbsorbInfo>(1);
        }

        _absorbInfo.add(absorbInfo);
    }

    public void addMinion(MinionData minion) {
        if (_minions.isEmpty()) {
            _minions = new ArrayList<MinionData>(1);
        }

        _minions.add(minion);
    }

    public Faction getFaction() {
        return faction;
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }

    public void addSkill(Skill skill) {
        _skills.put(skill.getId(), skill);

        //TODO [G1ta0] перенести в AI
        if (skill.isNotUsedByAI() || skill.getTargetType() == Skill.SkillTargetType.TARGET_NONE || skill.getSkillType() == Skill.SkillType.NOTDONE || !skill.isActive()) {
            return;
        }

        switch (skill.getSkillType()) {
            case PDAM:
            case MANADAM:
            case MDAM:
            case DRAIN:
            case DRAIN_SOUL: {
                boolean added = false;

                for (EffectTemplate eff : skill.getEffectTemplates(EffectUseType.NORMAL)) {
                    switch (eff.getEffectType()) {
                        case Stun:
                            _stunSkills = ArrayUtils.add(_stunSkills, skill);
                            added = true;
                            break;
                        case DamOverTime:
                        case DamOverTimeLethal:
                        case ManaDamOverTime:
                        case LDManaDamOverTime:
                            _dotSkills = ArrayUtils.add(_dotSkills, skill);
                            added = true;
                            break;
                    }
                }

                if (!added) {
                    _damageSkills = ArrayUtils.add(_damageSkills, skill);
                }

                break;
            }
            case DOT:
            case MDOT:
            case POISON:
                _dotSkills = ArrayUtils.add(_dotSkills, skill);
                break;
            case DEBUFF:
            case SLEEP:
            case ROOT:
            case PARALYZE:
            case MUTE:
            case AGGRESSION:
                _debuffSkills = ArrayUtils.add(_debuffSkills, skill);
                break;
            case BUFF:
                _buffSkills = ArrayUtils.add(_buffSkills, skill);
                break;
            case STUN:
                _stunSkills = ArrayUtils.add(_stunSkills, skill);
                break;
            case HEAL:
            case HEAL_PERCENT:
            case HOT:
                _healSkills = ArrayUtils.add(_healSkills, skill);
                break;
            default:

                break;
        }
    }

    public Skill[] getDamageSkills() {
        return _damageSkills;
    }

    public Skill[] getDotSkills() {
        return _dotSkills;
    }

    public Skill[] getDebuffSkills() {
        return _debuffSkills;
    }

    public Skill[] getBuffSkills() {
        return _buffSkills;
    }

    public Skill[] getStunSkills() {
        return _stunSkills;
    }

    public Skill[] getHealSkills() {
        return _healSkills;
    }

    public List<MinionData> getMinionData() {
        return _minions;
    }

    public TIntObjectMap<Skill> getSkills() {
        return _skills;
    }

    public void addQuestEvent(QuestEventType EventType, Quest q) {
        if (_questEvents.isEmpty()) {
            _questEvents = new HashMap<QuestEventType, Quest[]>();
        }

        if (_questEvents.get(EventType) == null) {
            _questEvents.put(EventType, new Quest[]{q});
        }
        else {
            Quest[] _quests = _questEvents.get(EventType);
            int len = _quests.length;

            Quest[] tmp = new Quest[len + 1];
            for (int i = 0; i < len; i++) {
                if (_quests[i].getName().equals(q.getName())) {
                    _quests[i] = q;
                    return;
                }
                tmp[i] = _quests[i];
            }
            tmp[len] = q;

            _questEvents.put(EventType, tmp);
        }
    }

    public Quest[] getEventQuests(QuestEventType EventType) {
        return _questEvents.get(EventType);
    }

    public int getRace() {
        return race;
    }

    public void setRace(int newrace) {
        race = newrace;
    }

    public boolean isUndead() {
        return race == 1;
    }

    @Override
    public String toString() {
        return "Npc template " + name + "[" + _npcId + "]";
    }

    @Override
    public int getId() {
        return _npcId;
    }

    public String getName() {
        return name;
    }

    public final String getJClass() {
        return jClass;
    }

    public final StatsSet getAIParams() {
        return _AIParams;
    }

    public List<AbsorbInfo> getAbsorbInfo() {
        return _absorbInfo;
    }

    public int getCastleId() {
        return _castleId;
    }

    public Map<QuestEventType, Quest[]> getQuestEvents() {
        return _questEvents;
    }

    public String getHtmRoot() {
        return _htmRoot;
    }

    public void addWalkerRoute(WalkerRoute walkerRoute) {
        if (!walkerRoute.isValid()) {
            return;
        }

        _walkerRoute.put(walkerRoute.getId(), walkerRoute);
    }

    public WalkerRoute getWalkerRoute(int id) {
        return _walkerRoute.get(id);
    }

    public void setRandomActions(RandomActions randomActions) {
        _randomActions = randomActions;
    }

    public RandomActions getRandomActions() {
        return _randomActions;
    }

    public HashMap<RewardType, RewardList> getRewards() {
        return rewards;
    }

    public void setRewards(HashMap<RewardType, RewardList> rewards) {
        this.rewards = rewards;
    }

    public int getEnchantEffect() {
        return _enchantEffect;
    }

    @Override
    public int getBaseRandDam() {
        return _baseRandDam;
    }

    public int getBaseReuseDelay() {
        return _baseReuseDelay;
    }
}
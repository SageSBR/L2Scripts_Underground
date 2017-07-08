package l2s.gameserver.network.l2.s2c;

import org.apache.commons.lang3.StringUtils;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.instances.PetInstance;
import l2s.gameserver.model.instances.SummonInstance;
import l2s.gameserver.model.pledge.Alliance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.updatetype.NpcInfoType;
import l2s.gameserver.skills.AbnormalEffect;
import l2s.gameserver.utils.Location;

/**
 * @author UnAfraid
 * reworked by Bonux
 */
public class NpcInfoPacket extends AbstractMaskPacket<NpcInfoType>
{
    public static class SummonInfoPacket extends NpcInfoPacket
    {
        public SummonInfoPacket(SummonInstance summon, Creature attacker)
        {
            super(summon, attacker);
        }
    }

    public static class ExPetInfo extends NpcInfoPacket
    {
        public ExPetInfo(PetInstance summon, Creature attacker)
        {
            super(summon, attacker);
        }
    }

    // Flags
    private static final int IS_IN_COMBAT = 1 << 0;
    private static final int IS_ALIKE_DEAD = 1 << 1;
    private static final int IS_TARGETABLE = 1 << 2;
    private static final int IS_SHOW_NAME = 1 << 3;

    private final byte[] _masks = new byte[]
    {
        (byte) 0x00,
        (byte) 0x0C,
        (byte) 0x0C,
        (byte) 0x00,
        (byte) 0x00
    };

    private final int _npcId;
    private final boolean _isAttackable;
    private final int _rHand, _lHand;
    private final String _name, _title;
    private final int _state;
    private final NpcString _nameNpcString, _titleNpcString;

    private int _initSize = 0;
    private int _blockSize = 0;

    private int _statusMask = 0;

    private int _showSpawnAnimation;
    private int _npcObjId;
    private Location _loc;
    private int _pAtkSpd, _mAtkSpd;
    private double _atkSpdMul, _runSpdMul;
    private int _pvpFlag;
    private boolean _alive, _running, _flying, _inWater;
    private TeamType _team;
    private int _currentHP, _currentMP;
    private int _maxHP, _maxMP;
    private int _enchantEffect;
    private int _transformId;
    private AbnormalEffect[] _abnormalEffects;
    private int _clanId, _clanCrestId, _largeClanCrestId;
    private int _allyId, _allyCrestId;

    public NpcInfoPacket(NpcInstance npc, Creature attacker)
    {
        _npcId = npc.getDisplayId() != 0 ? npc.getDisplayId() : npc.getNpcId();
        _isAttackable = npc.isAutoAttackable(attacker);
        _rHand = npc.getRightHandItem();
        _lHand = npc.getLeftHandItem();

        if (!npc.getTemplate().forceName.isEmpty()) {
            _name = npc.getTemplate().forceName;
            addComponentType(NpcInfoType.NAME);
        }
        else if (!npc.getName().equals(npc.getTemplate().name)) {
            _name = npc.getName();
            //_masks[2] |= 0x10;
            addComponentType(NpcInfoType.NAME);
        }
        else {
            _name = StringUtils.EMPTY;
        }

        if (!npc.getTemplate().forceTitle.isEmpty()) {
            _title = npc.getTemplate().forceTitle;
        }
        else if (!npc.getTitle().equals(npc.getTemplate().title)) {
            _title = npc.getTitle();
        }
        else {
            _title = StringUtils.EMPTY;
        }

        _showSpawnAnimation = npc.getSpawnAnimation();
        _state = npc.getNpcState();
        _nameNpcString = npc.getNameNpcString();
        _titleNpcString = npc.getTitleNpcString();

        if(npc.isTargetable(attacker))
            _statusMask |= IS_TARGETABLE;

        if(npc.isShowName())
            _statusMask |= IS_SHOW_NAME;

        common(npc);
    }

    public NpcInfoPacket(Servitor servitor, Creature attacker)
    {
        _npcId = servitor.getDisplayId() != 0 ? servitor.getDisplayId() : servitor.getNpcId();
        _isAttackable = servitor.isAutoAttackable(attacker);
        _rHand = servitor.getTemplate().rhand;
        _lHand = servitor.getTemplate().lhand;

        if (!servitor.getTemplate().forceName.isEmpty()) {
            _name = servitor.getTemplate().forceName;
            addComponentType(NpcInfoType.NAME);
        }
        else if (!servitor.getName().equals(servitor.getTemplate().name)) {
            _name = servitor.getName();
            //_masks[2] |= 0x10;
            addComponentType(NpcInfoType.NAME);
        }
        else {
            _name = StringUtils.EMPTY;
        }

        if (!servitor.getTemplate().forceTitle.isEmpty()) {
            _title = servitor.getTemplate().forceTitle;
        }
        else if (!servitor.getTitle().equals(servitor.getTemplate().title)) {
            _title = servitor.getTitle();
        }
        else {
            _title = StringUtils.EMPTY;
        }

        _showSpawnAnimation = servitor.getSpawnAnimation();
        _state = servitor.getNpcState();
        _nameNpcString = NpcString.NONE;
        _titleNpcString = NpcString.NONE;

        if(servitor.isTargetable(attacker))
            _statusMask |= IS_TARGETABLE;

        if(servitor.isShowName())
            _statusMask |= IS_SHOW_NAME;

        common(servitor);
    }

    private void common(Creature character)
    {
        _npcObjId = character.getObjectId();
        _loc = character.getLoc();
        _pAtkSpd = character.getPAtkSpd();
        _mAtkSpd = character.getMAtkSpd();
        _atkSpdMul = character.getAttackSpeedMultiplier();
        _runSpdMul = character.getMovementSpeedMultiplier();
        _pvpFlag = character.getPvpFlag();
        _alive = !character.isAlikeDead();
        _running = character.isRunning();
        _flying = character.isFlying();
        _inWater = character.isInWater();
        _team = character.getTeam();
        _currentHP = (int) character.getCurrentHp();
        _currentMP = (int) character.getCurrentMp();
        _maxHP = character.getMaxHp();
        _maxMP = character.getMaxMp();
        _enchantEffect = character.getEnchantEffect();
        _transformId = character.getVisualTransformId();
        _abnormalEffects = character.getAbnormalEffectsArray();

        Clan clan = character.getClan();
        Alliance alliance = clan == null ? null : clan.getAlliance();

        _clanId = clan == null ? 0 : clan.getClanId();
        _clanCrestId = clan == null ? 0 : clan.getCrestId();
        _largeClanCrestId = clan == null ? 0 : clan.getCrestLargeId();
        _allyId = alliance == null ? 0 : alliance.getAllyId();
        _allyCrestId = alliance == null ? 0 : alliance.getAllyCrestId();

        addComponentType(NpcInfoType.ATTACKABLE, NpcInfoType.UNKNOWN1, NpcInfoType.TITLE, NpcInfoType.ID, NpcInfoType.POSITION, NpcInfoType.ALIVE, NpcInfoType.RUNNING);

        if(_loc.h > 0)
            addComponentType(NpcInfoType.HEADING);

        if(_pAtkSpd > 0 || _mAtkSpd > 0)
            addComponentType(NpcInfoType.ATK_CAST_SPEED);

        if(_running && character.getRunSpeed() > 0 || !_running && character.getWalkSpeed() > 0)
            addComponentType(NpcInfoType.SPEED_MULTIPLIER);

        if(_rHand > 0 || _lHand > 0)
            addComponentType(NpcInfoType.EQUIPPED);

        if(_team != TeamType.NONE)
            addComponentType(NpcInfoType.TEAM);

        if(_state > 0)
            addComponentType(NpcInfoType.DISPLAY_EFFECT);

        if(_inWater || _flying)
            addComponentType(NpcInfoType.SWIM_OR_FLY);

        if(_flying)
            addComponentType(NpcInfoType.FLYING);

        if(_maxHP > 0)
            addComponentType(NpcInfoType.MAX_HP);

        if(_maxMP > 0)
            addComponentType(NpcInfoType.MAX_MP);

        if(_currentHP <= _maxHP)
            addComponentType(NpcInfoType.CURRENT_HP);

        if(_currentMP <= _maxMP)
            addComponentType(NpcInfoType.CURRENT_MP);

        if(_abnormalEffects.length > 0)
            addComponentType(NpcInfoType.ABNORMALS);

        if(_enchantEffect > 0)
            addComponentType(NpcInfoType.ENCHANT);

        if(_transformId > 0)
            addComponentType(NpcInfoType.TRANSFORMATION);

        if(_clanId > 0)
            addComponentType(NpcInfoType.CLAN);

        addComponentType(NpcInfoType.UNKNOWN8);

        if(character.isInCombat())
            _statusMask |= IS_IN_COMBAT;

        if(character.isDead())
            _statusMask |= IS_ALIKE_DEAD;

        if(_statusMask != 0)
            addComponentType(NpcInfoType.VISUAL_STATE);
    }

    public NpcInfoPacket update()
    {
        _showSpawnAnimation = 1;
        return this;
    }

    @Override
    protected byte[] getMasks()
    {
        return _masks;
    }

    @Override
    protected void onNewMaskAdded(NpcInfoType component)
    {
        switch(component)
        {
            case ATTACKABLE:
            case UNKNOWN1:
            {
                _initSize += component.getBlockLength();
                break;
            }
            case TITLE:
            {
                _initSize += component.getBlockLength() + (_title.length() * 2);
                break;
            }
            case NAME:
            {
                _blockSize += component.getBlockLength() + (_name.length() * 2);
                break;
            }
            default:
            {
                _blockSize += component.getBlockLength();
                break;
            }
        }
    }

    @Override
    protected void writeImpl()
    {
        writeD(_npcObjId);
        writeC(_showSpawnAnimation); // // 0=teleported 1=default 2=summoned
        writeH(37); // mask_bits_37
        writeB(_masks);
        
        // Block 1
        writeC(_initSize);
        
        if(containsMask(NpcInfoType.ATTACKABLE))
            writeC(_isAttackable);

        if(containsMask(NpcInfoType.UNKNOWN1))
            writeD(0x00); // unknown

        if(containsMask(NpcInfoType.TITLE))
            writeS(_title);
        
        // Block 2
        writeH(_blockSize);

        if(containsMask(NpcInfoType.ID))
            writeD(_npcId + 1000000);

        if(containsMask(NpcInfoType.POSITION))
        {
            writeD(_loc.x);
            writeD(_loc.y);
            writeD(_loc.z);
        }

        if(containsMask(NpcInfoType.HEADING))
            writeD(_loc.h);

        if(containsMask(NpcInfoType.UNKNOWN2))
            writeD(0x00); // Unknown

        if(containsMask(NpcInfoType.ATK_CAST_SPEED))
        {
            writeD(_pAtkSpd);
            writeD(_mAtkSpd);
        }

        if(containsMask(NpcInfoType.SPEED_MULTIPLIER))
        {
            writeCutF(_runSpdMul);
            writeCutF(_atkSpdMul);
        }

        if(containsMask(NpcInfoType.EQUIPPED))
        {
            writeD(_rHand);
            writeD(0x00); // Armor id?
            writeD(_lHand);
        }

        if(containsMask(NpcInfoType.ALIVE))
            writeC(_alive);

        if(containsMask(NpcInfoType.RUNNING))
            writeC(_running);

        if(containsMask(NpcInfoType.SWIM_OR_FLY))
            writeC(_inWater ? 1 : _flying ? 2 : 0);

        if(containsMask(NpcInfoType.TEAM))
            writeC(_team.ordinal());

        if(containsMask(NpcInfoType.ENCHANT))
            writeD(_enchantEffect);

        if(containsMask(NpcInfoType.FLYING))
            writeD(_flying);

        if(containsMask(NpcInfoType.CLONE))
            writeD(0x00); // Player ObjectId with Decoy

        if(containsMask(NpcInfoType.UNKNOWN8))
        {
            // No visual effect
            writeD(0x00); // Unknown
        }

        if(containsMask(NpcInfoType.DISPLAY_EFFECT))
            writeD(_state);

        if(containsMask(NpcInfoType.TRANSFORMATION))
            writeD(_transformId);

        if(containsMask(NpcInfoType.CURRENT_HP))
            writeD(_currentHP);

        if(containsMask(NpcInfoType.CURRENT_MP))
            writeD(_currentMP);

        if(containsMask(NpcInfoType.MAX_HP))
            writeD(_maxHP);

        if(containsMask(NpcInfoType.MAX_MP))
            writeD(_maxMP);

        if(containsMask(NpcInfoType.UNKNOWN11))
            writeC(0x00); // 2 - do some animation on spawn

        if(containsMask(NpcInfoType.UNKNOWN12))
        {
            writeD(0x00);
            writeD(0x00);
        }

        if(containsMask(NpcInfoType.NAME))
            writeS(_name);

        if(containsMask(NpcInfoType.NAME_NPCSTRINGID))
            writeD(_nameNpcString.getId()); // NPCStringId for name

        if(containsMask(NpcInfoType.TITLE_NPCSTRINGID))
            writeD(_titleNpcString.getId()); // NPCStringId for title

        if(containsMask(NpcInfoType.PVP_FLAG))
            writeC(_pvpFlag); // PVP flag

        if(containsMask(NpcInfoType.NAME_COLOR))
            writeD(0x00); // Name color

        if(containsMask(NpcInfoType.CLAN))
        {
            writeD(_clanId);
            writeD(_clanCrestId);
            writeD(_largeClanCrestId);
            writeD(_allyId);
            writeD(_allyCrestId);
        }
        
        if(containsMask(NpcInfoType.VISUAL_STATE))
            writeC(_statusMask);
        
        if(containsMask(NpcInfoType.ABNORMALS))
        {
            writeH(_abnormalEffects.length);
            for(AbnormalEffect abnormal : _abnormalEffects)
                writeH(abnormal.ordinal());
        }
    }
}
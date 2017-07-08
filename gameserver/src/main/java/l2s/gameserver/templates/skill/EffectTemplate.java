package l2s.gameserver.templates.skill;

import java.util.Collection;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.model.actor.instances.creature.EffectList;
import l2s.gameserver.model.Skill;
import l2s.gameserver.skills.AbnormalEffect;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.skills.EffectUseType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.StatTemplate;
import l2s.gameserver.stats.conditions.Condition;
import l2s.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EffectTemplate extends StatTemplate
{
	private static final Logger _log = LoggerFactory.getLogger(EffectTemplate.class);

	public static final EffectTemplate[] EMPTY_ARRAY = new EffectTemplate[0];

	private final int _index;

	private Condition _attachCond;
	private final double _value;

	public final EffectType _effectType;

	private final AbnormalType _abnormalType;
	private final int _abnormalLvl;
	private final AbnormalEffect[] _abnormalEffects;

	public final boolean _cancelOnAction;
	private final boolean _hideTime;

	private final StatsSet _paramSet;
	private final int _chance;
	private final boolean _hasCombo;

	private final int _interval;

	private final EffectUseType _useType;

	public EffectTemplate(final StatsSet set, final Skill skill, EffectUseType useType)
	{
		final boolean instant = set.getBool("instant", false);
		if(instant)
		{
			switch(useType)
			{
				case START:
					useType = EffectUseType.START_INSTANT;
					break;
				case TICK:
					useType = EffectUseType.TICK_INSTANT;
					break;
				case SELF:
					useType = EffectUseType.SELF_INSTANT;
					break;
				case NORMAL:
					useType = EffectUseType.NORMAL_INSTANT;
					break;
			}
		}

		_useType = useType;

		_index = (_useType.ordinal() + 1) * 100 + skill.getEffectsCount(_useType) + 1;
		_value = set.getDouble("value", 0D);

		if(_useType == EffectUseType.NORMAL)
		{
			_abnormalType = skill.getAbnormalType();
			_abnormalLvl = skill.getAbnormalLvl();
			_abnormalEffects = skill.getAbnormalEffects();
		}
		else
		{
			_abnormalType = AbnormalType.none;
			_abnormalLvl = 0;
			_abnormalEffects = new AbnormalEffect[0];
		}

		_interval = set.getInteger("interval", Integer.MAX_VALUE);
		_cancelOnAction = set.getBool("cancel_on_action", false);
		_effectType = set.getEnum("name", EffectType.class, EffectType.Buff);
		_chance = set.getInteger("chance", -1);
		_hideTime = set.getBool("hide_time", false);
		_hasCombo = set.getBool("has_combo", false);
		_paramSet = set;
	}

	public int getIndex()
	{
		return _index;
	}

	public Effect getEffect(Env env)
	{
		try
		{
			return _effectType.makeEffect(env, this);
		}
		catch(Exception e)
		{
			_log.error("", e);
		}

		return null;
	}

	public void attachCond(Condition c)
	{
		_attachCond = c;
	}

	public boolean checkCondition(Effect effect)
	{
		if(_attachCond == null)
			return true;

		Env env = new Env();
		env.character = effect.getEffector();
		env.skill = effect.getSkill();
		env.target = effect.getEffected();

		return _attachCond.test(env);
	}

	public EffectType getEffectType()
	{
		return _effectType;
	}

	public Effect getSameByAbnormalType(Collection<Effect> list)
	{
		for(Effect ef : list)
			if(ef != null && EffectList.checkAbnormalType(ef.getTemplate(), this))
				return ef;
		return null;
	}

	public Effect getSameByAbnormalType(EffectList list)
	{
		return getSameByAbnormalType(list.getEffects());
	}

	public Effect getSameByAbnormalType(Creature actor)
	{
		return getSameByAbnormalType(actor.getEffectList().getEffects());
	}

	public StatsSet getParam()
	{
		return _paramSet;
	}

	public int getChance()
	{
		return _chance;
	}

	public boolean isHideTime()
	{
		return _hideTime;
	}

	public AbnormalType getAbnormalType()
	{
		return _abnormalType;
	}

	public int getAbnormalLvl()
	{
		return _abnormalLvl;
	}

	public AbnormalEffect[] getAbnormalEffects()
	{
		return _abnormalEffects;
	}

	public boolean hasCombo()
	{
		return _hasCombo;
	}

	public int getInterval()
	{
		return _interval;
	}

	public EffectUseType getUseType()
	{
		return _useType;
	}

	public boolean isInstant()
	{
		return _useType.isInstant();
	}

	public final double getValue()
	{
		return _value;
	}
}
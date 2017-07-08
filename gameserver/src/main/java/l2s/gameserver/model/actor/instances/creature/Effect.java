package l2s.gameserver.model.actor.instances.creature;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.Config;
import l2s.gameserver.listener.actor.OnAttackListener;
import l2s.gameserver.listener.actor.OnMagicUseListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.network.l2.s2c.AbnormalStatusUpdatePacket;
import l2s.gameserver.network.l2.s2c.ExAbnormalStatusUpdateFromTargetPacket;
import l2s.gameserver.network.l2.s2c.ExOlympiadSpelledInfoPacket;
import l2s.gameserver.network.l2.s2c.PartySpelledPacket;
import l2s.gameserver.network.l2.s2c.ShortBuffStatusUpdatePacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.skills.AbnormalEffect;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.skills.EffectUseType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.funcs.Func;
import l2s.gameserver.stats.funcs.FuncOwner;
import l2s.gameserver.stats.triggers.TriggerType;
import l2s.gameserver.taskmanager.EffectTaskManager;
import l2s.gameserver.templates.skill.EffectTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Effect extends RunnableImpl implements Comparable<Effect>, FuncOwner
{
	protected static final Logger _log = LoggerFactory.getLogger(Effect.class);

	public final static Effect[] EMPTY_L2EFFECT_ARRAY = new Effect[0];

	//Состояние, при котором работает задача запланированного эффекта
	public static int SUSPENDED = -1;

	public static int STARTING = 0;
	public static int ACTING = 1;
	public static int FINISHED = 2;

	/** Накладывающий эффект */
	protected final Creature _effector;
	/** Тот, на кого накладывают эффект */
	protected final Creature _effected;

	protected final Skill _skill;

	// the current state
	private final AtomicInteger _state;

	// period, milliseconds
	private long _startTimeMillis = Long.MAX_VALUE;

	private int _duration;
	private int _timeLeft;
	private final int _interval;

	private Effect _next = null;
	private boolean _active = false;

	protected final EffectTemplate _template;

	private Future<?> _effectTask;

	private final boolean _isOffensive;

	private final boolean _reflected;

	protected Effect(Env env, EffectTemplate template)
	{
		_skill = env.skill;
		_effector = env.character;
		_effected = env.target;
		_reflected = env.reflected;

		_template = template;

		_duration = Math.min(Integer.MAX_VALUE, Math.max(0, _skill.getAbnormalTime() < 0 ? Integer.MAX_VALUE : _skill.getAbnormalTime()));
		_timeLeft = _duration;
		_interval = Math.max(1, template.getInterval());

		_state = new AtomicInteger(STARTING);

		boolean isSelf = template.getUseType() == EffectUseType.SELF;
		_isOffensive = template.getParam().getBool("offensive", isSelf && _skill.isSelfOffensive() || !isSelf && _skill.isOffensive());
	}

	/**
	 * Возвращает время старта эффекта, если время не установлено, возвращается текущее
	 */
	public final long getStartTime()
	{
		return _startTimeMillis;
	}

	/** Возвращает оставшееся время в секундах. */
	public final int getTimeLeft()
	{
		return _timeLeft;
	}

	public final void setTimeLeft(int value)
	{
		_timeLeft = Math.max(0, Math.min(value, _duration));
	}

	/** Возвращает true, если осталось время для действия эффекта */
	public final boolean isTimeLeft()
	{
		return getTimeLeft() > 0;
	}

	public final boolean isActive()
	{
		return _active;
	}

	/**
	 * Для неактивных эфектов не вызывается onActionTime.
	 */
	public void setActive(boolean set)
	{
		_active = set;
	}

	public EffectTemplate getTemplate()
	{
		return _template;
	}

	public AbnormalType getAbnormalType()
	{
		return getTemplate().getAbnormalType();
	}

	public int getAbnormalLvl()
	{
		return getTemplate().getAbnormalLvl();
	}

	public boolean checkAbnormalType(AbnormalType abnormal)
	{
		AbnormalType abnormalType = getAbnormalType();
		if(abnormalType == AbnormalType.none)
			return false;

		return abnormal == abnormalType;
	}

	public boolean checkAbnormalType(Effect effect)
	{
		return checkAbnormalType(effect.getAbnormalType());
	}

	public Skill getSkill()
	{
		return _skill;
	}

	public Creature getEffector()
	{
		return _effector;
	}

	public Creature getEffected()
	{
		return _effected;
	}

	public double calc()
	{
		return getTemplate().getValue();
	}

	public boolean isFinished()
	{
		return getState() == FINISHED;
	}

	private int getState()
	{
		return _state.get();
	}

	private boolean setState(int oldState, int newState)
	{
		return _state.compareAndSet(oldState, newState);
	}

	private ActionDispelListener _listener;

	private class ActionDispelListener implements OnAttackListener, OnMagicUseListener
	{
		@Override
		public void onMagicUse(Creature actor, Skill skill, Creature target, boolean alt)
		{
			if(getSkill().isDoNotDispelOnSelfBuff() /*&& !skill.isOffensive()*/)
				return;
			exit();
		}

		@Override
		public void onAttack(Creature actor, Creature target)
		{
			exit();
		}
	}

	public boolean checkCondition()
	{
		return getTemplate().checkCondition(this);
	}

	protected boolean checkActingCondition()
	{
		if(isOfUseType(EffectUseType.START) || isOfUseType(EffectUseType.TICK))
		{
			if(getEffector().getCastingSkill() == getSkill())
				return true;

			if(getEffector().getDualCastingSkill() == getSkill())
				return true;

			return false;
		}
		return getTemplate().checkCondition(this);
	}

	public void instantUse()
	{
		onStart();
		onActionTime();
		onExit();
	}

	/** Notify started */
	protected void onStart()
	{
		// Остальные операции для одноразовых эффектов не нужны.
		if(getTemplate().isInstant())
			return;

		getEffected().addStatFuncs(getStatFuncs());
		getEffected().addTriggers(getTemplate());

		AbnormalEffect[] abnormals = _template.getAbnormalEffects();
		for(AbnormalEffect abnormal : abnormals)
		{
			if(abnormal != AbnormalEffect.NONE)
				getEffected().startAbnormalEffect(abnormal);
		}

		if(_template._cancelOnAction && getEffected().isPlayable())
			getEffected().addListener(_listener = new ActionDispelListener());
		if(getEffected().isPlayer() && !getSkill().canUseTeleport())
			getEffected().getPlayer().getPlayerAccess().UseTeleport = false;

		if(getSkill().getChainIndex() != -1 && getSkill().getChainSkillId() > 0 && getEffector() != null && getEffector().isPlayer() && !getEffector().getPlayer().getSkillChainDetails().containsKey(getSkill().getChainIndex()))
		{
			final Skill known = getEffector().getKnownSkill(getSkill().getChainSkillId());
			if(known != null && !getEffector().isSkillDisabled(known) && !getEffector().isUnActiveSkill(known.getId()))
				getEffector().getPlayer().addChainDetail(getEffected(), getSkill(), getTimeLeft());
		}

		//tigger on start
		getEffected().useTriggers(getEffected(), TriggerType.ON_START_EFFECT, null, _skill, getTemplate(), 0);
	}

	/** Return true for continuation of this effect */
	protected boolean onActionTime()
	{
		return true;
	}

	/**
	 * Cancel the effect in the the abnormal effect map of the effected L2Character.<BR><BR>
	 */
	protected void onExit()
	{
		// Остальные операции для одноразовых эффектов не нужны.
		if(getTemplate().isInstant())
			return;

		getEffected().removeStatsOwner(this);

		getEffected().removeTriggers(getTemplate());

		AbnormalEffect[] abnormals = _template.getAbnormalEffects();
		for(AbnormalEffect abnormal : abnormals)
		{
			if(abnormal != AbnormalEffect.NONE)
				getEffected().stopAbnormalEffect(abnormal);
		}

		if(_template._cancelOnAction)
			getEffected().removeListener(_listener);
		if(getEffected().isPlayer() && checkAbnormalType(AbnormalType.hp_recover))
			getEffected().sendPacket(new ShortBuffStatusUpdatePacket());
		if(getEffected().isPlayer() && !getSkill().canUseTeleport() && !getEffected().getPlayer().getPlayerAccess().UseTeleport)
			getEffected().getPlayer().getPlayerAccess().UseTeleport = true;

		if(getSkill().getChainSkillId() > 0 && getSkill().getChainIndex() != -1 && getEffector() != null && getEffector().isPlayer())
			getEffector().getPlayer().removeChainDetail(getSkill().getChainIndex());

		if(isOfUseType(EffectUseType.START) || isOfUseType(EffectUseType.TICK))
		{
			if(getEffected() == getEffector().getCastingTarget() && getSkill() == getEffector().getCastingSkill())
				getEffector().abortCast(true, false, true, false);

			if(getEffected() == getEffector().getDualCastingTarget() && getSkill() == getEffector().getDualCastingSkill())
				getEffector().abortCast(true, false, false, true);
		}

		//tigger on exit
		getEffected().useTriggers(getEffected(), TriggerType.ON_EXIT_EFFECT, null, _skill, getTemplate(), 0);
	}

	private void stopEffectTask()
	{
		if(_effectTask != null)
		{
			_effectTask.cancel(false);
			_effectTask = null;
		}
	}

	private void startEffectTask()
	{
		if(_effectTask == null)
		{
			_startTimeMillis = System.currentTimeMillis();
			_effectTask = EffectTaskManager.getInstance().scheduleAtFixedRate(this, 1000L, 1000L);
		}
	}

	public void restart()
	{
		_timeLeft = getDuration();

		stopEffectTask();
		startEffectTask();
	}

	/**
	 * Добавляет эффект в список эффектов, в случае успешности вызывается метод start
	 */
	public final boolean schedule()
	{
		Creature effected = getEffected();
		if(effected == null)
			return false;

		if(!checkCondition())
			return false;

		return getEffected().getEffectList().addEffect(this);
	}

	/**
	 * Переводит эффект в "фоновый" режим, эффект может быть запущен методом schedule
	 */
	/*private final void suspend()
	{
		// Эффект создан, запускаем задачу в фоне
		if(setState(STARTING, SUSPENDED))
			startEffectTask();
		else if(setState(ACTING, SUSPENDED))
		{
			synchronized (this)
			{
				if(isInUse())
				{
					setInUse(false);
					setActive(false);
					onExit();
				}
			}
			getEffected().getEffectList().removeEffect(this);
		}
	}*/

	/**
	 * Запускает задачу эффекта, в случае если эффект успешно добавлен в список
	 */
	public final void start()
	{
		if(setState(STARTING, ACTING))
		{
			synchronized (this)
			{
				setActive(true);
				onStart();
				startEffectTask();
			}
		}
	}

	@Override
	public final void runImpl() throws Exception
	{
		_timeLeft--;

		if(getState() == SUSPENDED)
		{
			if(isTimeLeft())
				return;

			exit();
			return;
		}

		if(getState() == ACTING)
		{
			if(isTimeLeft())
			{
				if(checkActingCondition())
				{
					if(!isActive())
						return;

					if((getTimeLeft() % getInterval()) == 0)
					{
						if(onActionTime())
							return;
					}
					else
						return;
				}
			}
		}

		if(getDuration() == Integer.MAX_VALUE) // Если вдруг закончится время у безконечного эффекта.
		{
			if(checkActingCondition())
			{
				if((getDuration() % getInterval()) == 0)
					onActionTime();

				_timeLeft = getDuration();
				return;
			}
		}

		if(setState(ACTING, FINISHED))
		{
			if(checkActingCondition() && (getDuration() % getInterval()) == 0)
				onActionTime();

			synchronized(this)
			{
				setActive(false);
				stopEffectTask();
				onExit();
			}

			boolean msg = !isHidden() && getEffected().getEffectList().getEffectsCount(getSkill()) == 1;

			getEffected().getEffectList().removeEffect(this);

			// Отображать сообщение только для последнего оставшегося эффекта скилла
			if(msg)
				getEffected().sendPacket(new SystemMessage(SystemMessage.S1_HAS_WORN_OFF).addSkillName(getDisplayId(), getDisplayLevel()));

			//tigger on finish
			getEffected().useTriggers(getEffected(), TriggerType.ON_FINISH_EFFECT, null, _skill, getTemplate(), 0);

			// Добавляем следующий запланированный эффект
			Effect next = getNext();
			if(next != null && next.setState(SUSPENDED, STARTING))
				next.schedule();
		}
	}

	/**
	 * Завершает эффект и все связанные, удаляет эффект из списка эффектов
	 */
	public final void exit()
	{
		Effect next = getNext();
		if(next != null)
			next.exit();
		removeNext();

		//Эффект запланирован на запуск, удаляем
		if(setState(STARTING, FINISHED))
			getEffected().getEffectList().removeEffect(this);
		//Эффект работает в "фоне", останавливаем задачу в планировщике
		else if(setState(SUSPENDED, FINISHED))
			stopEffectTask();
		else if(setState(ACTING, FINISHED))
		{
			synchronized (this)
			{
				setActive(false);
				stopEffectTask();
				onExit();
			}
			getEffected().getEffectList().removeEffect(this);
		}
	}

	/**
	 * Поставить в очередь эффект
	 * @param e
	 * @return true, если эффект поставлен в очередь
	 */
	private boolean scheduleNext(Effect e)
	{
		if(e == null || e.isFinished())
			return false;

		Effect next = getNext();
		if(next != null && !next.maybeScheduleNext(e))
			return false;

		_next = e;

		return true;
	}

	public Effect getNext()
	{
		return _next;
	}

	private void removeNext()
	{
		_next = null;
	}

	/**
	 * @return false - игнорировать новый эффект, true - использовать новый эффект
	 */
	public boolean maybeScheduleNext(Effect newEffect)
	{
		/*TODO: [Bonux] Починить от овербаффа и сделать, чтобы распостранялось только на эффекты хербов.
		if(newEffect.getAbnormalLvl() < getAbnormalLvl()) // новый эффект слабее
		{
			if(newEffect.getTimeLeft() > getTimeLeft()) // новый эффект длинее
			{
				newEffect.suspend();
				scheduleNext(newEffect); // пробуем пристроить новый эффект в очередь
			}

			return false; // более слабый эффект всегда игнорируется, даже если не попал в очередь
		}
		else // если старый не дольше, то просто остановить его
		if(newEffect.getTimeLeft() >= getTimeLeft())
		{
			// наследуем зашедуленый старому, если есть смысл
			if(getNext() != null && getNext().getTimeLeft() > newEffect.getTimeLeft())
			{
				newEffect.scheduleNext(getNext());
				// отсоединяем зашедуленные от текущего
				removeNext();
			}
			exit();
		}
		else
		// если новый короче то зашедулить старый
		{
			suspend();
			newEffect.scheduleNext(this);
		}*/
		if(newEffect.getAbnormalLvl() < getAbnormalLvl())
			return false;

		exit();
		return true;
	}

	public Func[] getStatFuncs()
	{
		return getTemplate().getStatFuncs(this);
	}

	public void addIcon(AbnormalStatusUpdatePacket abnormalStatus)
	{
		if(!isActive() || isHidden())
			return;
		int duration = isHideTime() ? AbnormalStatusUpdatePacket.INFINITIVE_EFFECT : getTimeLeft();
		abnormalStatus.addEffect(getDisplayId(), getDisplayLevel(), duration);
	}

	public void addIcon(ExAbnormalStatusUpdateFromTargetPacket abnormalStatus)
	{
		if(!isActive() || isHidden())
			return;
		int duration = isHideTime() ? AbnormalStatusUpdatePacket.INFINITIVE_EFFECT : getTimeLeft();
		abnormalStatus.addEffect(_effector.getObjectId(), getDisplayId(), getDisplayLevel(), duration, getSkill().getComboTypeFromCharStatus(getEffector(), getEffected()).getId());
	}

	public void addPartySpelledIcon(PartySpelledPacket ps)
	{
		if(!isActive() || isHidden())
			return;
		int duration = isHideTime() ? AbnormalStatusUpdatePacket.INFINITIVE_EFFECT : getTimeLeft();
		ps.addPartySpelledEffect(getDisplayId(), getDisplayLevel(), duration);
	}

	public void addOlympiadSpelledIcon(Player player, ExOlympiadSpelledInfoPacket os)
	{
		if(!isActive() || isHidden())
			return;
		int duration = isHideTime() ? AbnormalStatusUpdatePacket.INFINITIVE_EFFECT : getTimeLeft();
		os.addSpellRecivedPlayer(player);
		os.addEffect(getDisplayId(), getDisplayLevel(), duration);
	}

	protected int getLevel()
	{
		return _skill.getLevel();
	}

	public EffectType getEffectType()
	{
		return getTemplate()._effectType;
	}

	public boolean isHidden()
	{
		return getDisplayId() < 0 || isOfUseType(EffectUseType.START) || isOfUseType(EffectUseType.TICK);
	}

	@Override
	public int compareTo(Effect obj)
	{
		if(obj.equals(this))
			return 0;
		return 1;
	}

	public boolean isSaveable()
	{
		return getSkill().isSaveable() && getTimeLeft() >= Config.ALT_SAVE_EFFECTS_REMAINING_TIME && !isHidden();
	}

	public boolean isCancelable()
	{
		return getSkill().isCancelable() && !isHidden();
	}

	public boolean isSelfDispellable()
	{
		return getSkill().isSelfDispellable() && !isHidden();
	}

	public int getDisplayId()
	{
		return getSkill().getDisplayId();
	}

	public int getDisplayLevel()
	{
		return getSkill().getDisplayLevel();
	}

	@Override
	public String toString()
	{
		return "Skill: " + _skill + ", state: " + getState() + ", active : " + _active;
	}

	@Override
	public boolean isFuncEnabled()
	{
		return true;
	}

	@Override
	public boolean overrideLimits()
	{
		return false;
	}
	public int getIndex()
	{
		return _template.getIndex();
	}

	public boolean checkBlockedAbnormalType(AbnormalType abnormal)
	{
		return false;
	}

	public boolean checkDebuffImmunity()
	{
		return false;
	}

	public boolean isIgnoredSkill(Skill skill)
	{
		return false;
	}

	public final boolean isOfUseType(EffectUseType useType)
	{
		return getTemplate().getUseType() == useType;
	}

	public final boolean isOffensive()
	{
		return _isOffensive;
	}

	public int getDuration()
	{
		return _duration;
	}

	public final void setDuration(int value)
	{
		_duration = Math.min(Integer.MAX_VALUE, Math.max(0, value));
		_timeLeft = _duration;
	}

	public int getInterval()
	{
		return _interval;
	}

	public boolean isHideTime()
	{
		return getTemplate().isHideTime() || getDuration() == Integer.MAX_VALUE;
	}

	public boolean isReflected()
	{
		return _reflected;
	}
}
package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.skill.EffectTemplate;

public class EffectDamOverTime extends Effect
{
	// TODO уточнить уровни 1, 2, 9, 10, 11, 12
	private static int[] poison = new int[] { 11, 16, 24, 32, 41, 50, 58, 63, 68, 72, 77, 82 };

	private boolean _percent;
	private boolean _byMatk;

	public EffectDamOverTime(Env env, EffectTemplate template)
	{
		super(env, template);
		_percent = getTemplate().getParam().getBool("percent", false);
		_byMatk = getTemplate().getParam().getBool("byMatk", false);
	}

	@Override
	public boolean onActionTime()
	{
		if(_effected.isDead())
			return false;

		double damage = calc();
		if(_percent)
			damage = _effected.getMaxHp() * calc() * 0.01;

		if(damage < 2 && getAbnormalLvl() != -1)
		{
			int modIndex = getAbnormalLvl() - 1;
			switch(getEffectType())
			{
				case Poison:
					if(modIndex > (poison.length - 1))
					{
						modIndex = poison.length - 1;
						_log.warn("EffectDamOverTime: missing poison modifier for skill id[" + getSkill().getId() + "], level[" + getSkill().getLevel() + "]");
					}
					damage = poison[modIndex] * getInterval() / 1000;
					break;
			}
		}

		if(_byMatk)
		{
			Formulas.AttackInfo info = Formulas.calcMagicDam(getEffector(), getEffected(), getSkill(), calc(), false);
			damage = info.damage;
			if(getEffected().getPlayer() != null)
				damage /= Math.sqrt(2);
		}
		else
			damage = _effector.calcStat(getSkill().isMagic() ? Stats.INFLICTS_M_DAMAGE_POWER : Stats.INFLICTS_P_DAMAGE_POWER, damage, _effected, getSkill());

		if(damage > _effected.getCurrentHp() - 1 && !_effected.isNpc())
		{
			if(!getSkill().isOffensive())
				_effected.sendPacket(SystemMsg.NOT_ENOUGH_HP);
			return false;
		}

		if(getSkill().getAbsorbPart() > 0)
			_effector.setCurrentHp(getSkill().getAbsorbPart() * Math.min(_effected.getCurrentHp(), damage) + _effector.getCurrentHp(), false);

		if(getSkill().getId() == 11273 || getSkill().getId() == 11296)
			_effected.reduceCurrentHp(damage, _effected, getSkill(), !_effected.isNpc() && _effected != _effector, _effected != _effector, _effector.isNpc() || _effected == _effector, false, false, true, /*_byMatk*/true);
		else
			_effected.reduceCurrentHp(damage, _effector, getSkill(), !_effected.isNpc() && _effected != _effector, _effected != _effector, _effector.isNpc() || _effected == _effector, false, false, true, /*_byMatk*/true);

		return true;
	}
}
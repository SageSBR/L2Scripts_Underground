package l2s.gameserver.skills.effects;

import l2s.gameserver.Config;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.network.l2.s2c.FlyToLocationPacket.FlyType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;
import l2s.gameserver.utils.Location;

/**
 * @author Bonux
**/
public abstract class EffectFlyAbstract extends Effect
{
	private final FlyType _flyType;
	private final double _flyCourse;
	private final int _flySpeed;
	private final int _flyDelay;
	private final int _flyAnimationSpeed;
	private final int _flyRadius;

	public EffectFlyAbstract(Env env, EffectTemplate template)
	{
		super(env, template);

		_flyType = template.getParam().getEnum("fly_type", FlyType.class, getSkill().getFlyType());
		_flyCourse = template.getParam().getDouble("fly_course", 0D);
		_flySpeed = template.getParam().getInteger("fly_speed", getSkill().getFlySpeed());
		_flyDelay = template.getParam().getInteger("fly_delay", getSkill().getFlyDelay());
		_flyAnimationSpeed = template.getParam().getInteger("fly_animation_speed", getSkill().getFlyAnimationSpeed());
		_flyRadius = template.getParam().getInteger("fly_radius", getSkill().getFlyRadius());
	}

	public FlyType getFlyType()
	{
		return _flyType;
	}

	public double getFlyCourse()
	{
		return _flyCourse;
	}

	public int getFlySpeed()
	{
		return _flySpeed;
	}

	public int getFlyDelay()
	{
		return _flyDelay;
	}

	public int getFlyAnimationSpeed()
	{
		return _flyAnimationSpeed;
	}

	public int getFlyRadius()
	{
		return _flyRadius;
	}
}

package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public class p_block_chat extends Effect
{
	public p_block_chat(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(!getEffected().isPlayer())
			return false;

		return getTemplate().checkCondition(this);
	}

	@Override
	public void onStart()
	{
		super.onStart();

		getEffected().getPlayer().startChatBlock();
	}

	@Override
	public void onExit()
	{
		super.onExit();

		getEffected().getPlayer().stopChatBlock();
	}
}
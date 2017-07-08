package npc.model;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

public class SquireInstance extends NpcInstance
{
	public static final Skill CUCURU_SKILL = SkillHolder.getInstance().getSkill(9204, 1);

	public SquireInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.startsWith("try_kukura"))
		{
			//TODO: [Bonux] Проверить, наверно тут есть какие-то условия.
			CUCURU_SKILL.getEffects(player, player);
		}
		else
			super.onBypassFeedback(player, command);
	}
}
package npc.model;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
 */
public class LeonaInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public LeonaInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.startsWith("listen"))
			player.startScenePlayer(SceneMovie.SCENE_HELLBOUND);
		else
			super.onBypassFeedback(player, command);
	}
}
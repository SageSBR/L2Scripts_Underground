package npc.model;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
 */
public class WarpgateInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public WarpgateInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... replace)
	{
		if(val == 0)
		{
			if(player.getLevel() < 99)
			{
				showChatWindow(player, "default/" + getNpcId() + "-no_level.htm");
				return;
			}
		}
		super.showChatWindow(player, val, replace);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.startsWith("enter"))
		{
			if(player.getLevel() < 99)
				return;

			player.teleToLocation(-28575, 255984, -2200);
		}
		else if(command.startsWith("exit"))
			player.teleToLocation(111382, 219202, -3536);
		else
			super.onBypassFeedback(player, command);
	}
}
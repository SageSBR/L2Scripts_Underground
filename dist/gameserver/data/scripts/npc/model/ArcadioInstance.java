package npc.model;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.templates.npc.NpcTemplate;

//By Evil_dnk

public final class ArcadioInstance extends NpcInstance
{

	public ArcadioInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		if (player.getPledgeType() != Clan.SUBUNIT_ACADEMY)
		{
			showChatWindow(player, "default/33905-3.htm");
			return;
		}
		else
		{
			showChatWindow(player, "default/33905.htm");
			return;
		}
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.startsWith("getCiclet"))
		{
			if(player.getInventory().getCountOf(8181) > 0)
			{
				showChatWindow(player, "default/33905-2.htm");
				return;
			}
			else
			{
				player.getInventory().addItem(8181, 1);
				showChatWindow(player, "default/33905-1.htm");
				return;
			}
		}

		else
			super.onBypassFeedback(player, command);
	}
}
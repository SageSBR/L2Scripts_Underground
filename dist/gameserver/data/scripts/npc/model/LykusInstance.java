package npc.model;

import java.util.StringTokenizer;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author Bonux
 */
public class LykusInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final int ORBIS_ANCIENT_HEROS_SHIELD = 17724;	// Orbis Ancient Hero's Shield
	private static final int POLISHED_ANCIENT_HEROS_SHIELD = 17723;	// Polished Ancient Hero's Shield

	private static final long EXCHANGE_COST = 5000L;

	public LykusInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equals("exchangeshield"))
		{
			final long shieldsCount = ItemFunctions.getItemCount(player, ORBIS_ANCIENT_HEROS_SHIELD);
			if(shieldsCount <= 0)
			{
				showChatWindow(player, "default/" + getNpcId() + "-no_shield.htm");
				return;
			}

			boolean all = false;
			if(st.hasMoreTokens())
			{
				String cmd2 = st.nextToken();
				if(cmd2.equals("all"))
					all = true;
			}

			final long exchangeCost = (all ? shieldsCount : 1) * EXCHANGE_COST;
			if(!player.reduceAdena(exchangeCost))
			{
				showChatWindow(player, "default/" + getNpcId() + "-no_adena.htm");
				return;
			}

			ItemFunctions.addItem(player, POLISHED_ANCIENT_HEROS_SHIELD, (all ? shieldsCount : 1), true);

			if(all)
				showChatWindow(player, "default/" + getNpcId() + "-success_all.htm");
			else
				showChatWindow(player, "default/" + getNpcId() + "-success.htm");
		}
		else
			super.onBypassFeedback(player, command);
	}
}

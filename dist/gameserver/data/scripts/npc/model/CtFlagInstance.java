package npc.model;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
**/
public class CtFlagInstance extends NpcInstance
{
	private final TeamType _team;

	public CtFlagInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);

		_team = TeamType.valueOf(getParameter("team", "NONE").toUpperCase());
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		String pom;
		if(val == 0)
			pom = String.valueOf(getNpcId());
		else
			pom = getNpcId() + "-" + val;

		return "events/ctf/" + pom + ".htm";
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		if(val == 0)
		{
			if(_team == TeamType.NONE)
				return;

			if(_team == player.getTeam())
				return;

			super.showChatWindow(player, 0, "<?n1?>", String.valueOf(Rnd.get(100, 999)), "<?n2?>", String.valueOf(Rnd.get(100, 999)));
		}
		else
			super.showChatWindow(player, val, arg);
	}
}

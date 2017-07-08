package npc.model.residences.castle;

import java.util.StringTokenizer;

import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
**/
public class GeroldInstance extends NpcInstance
{
	private static final int GIFT_SKILL_ID = 19036;

	public GeroldInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		String pom;
		if(val == 0)
		{
			Castle castle = getCastle(player);
			if(castle != null && castle.getOwnerId() > 0)
				pom = String.valueOf(getNpcId());
			else
				pom = "no_clan";
		}
		else
			pom = getNpcId() + "-" + val;

		return "castle/gerold/" + pom + ".htm";
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		StringTokenizer st = new StringTokenizer(command);
		String cmd = st.nextToken();
		if(cmd.equals("receive_gift"))
		{
			Skill skill = SkillHolder.getInstance().getSkill(GIFT_SKILL_ID, 1);
			skill.getEffects(player, player);
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		Castle castle = getCastle(player);
		Clan clan = (castle == null ? null : castle.getOwner());
		if(clan != null)
		{
			if(val == 0)
				Functions.npcSayToPlayer(this, player, NpcString.WHEN_THE_WORLD_PLUNGES_INTO_CHAOS_WE_WILL_NEED_YOUR_HELP_AT_THAT_TIME_PLEASE_JOIN_IN_WITH_US_I_HOPE_THAT_YOU_WILL_BECOME_STRONGER);

			if(arg == null)
				arg = new Object[0];

			arg = ArrayUtils.add(arg, "<?clan_name?>");
			arg = ArrayUtils.add(arg, clan.getName());
			arg = ArrayUtils.add(arg, "<?leader_name?>");
			arg = ArrayUtils.add(arg, clan.getLeaderName());
		}

		super.showChatWindow(player, val, arg);
	}
}
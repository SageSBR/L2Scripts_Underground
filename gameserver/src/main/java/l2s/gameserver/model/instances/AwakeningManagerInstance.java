package l2s.gameserver.model.instances;

import java.util.Collection;
import java.util.StringTokenizer;

import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.ClassType2;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.network.l2.s2c.ExChangeToAwakenedClass;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.SkillUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
**/
public final class AwakeningManagerInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final Logger _log = LoggerFactory.getLogger(AwakeningManagerInstance.class);

	//Item's
	private static final int ESSENCE_OF_THE_LESSER_GIANTS = 30306; // Эссенция Гигантов

	//Other
	private static final int[] SECOND_CLASS_ESSENCE_COMPENSATION = new int[] {
		//	+0	+1	+2	+3	+4	+5	+6	+7	+8	+9	+10
			0,	0,	0,	0,	1,	1,	2,	3,	4,	5,	6,
		//	+11	+12	+13	+14	+15	+16	+17	+18	+19	+20	+21
			7,	9,	10,	12,	13,	15,	17,	19,	22,	24,	27,
		//	+22	+23	+24	+25	+26	+27	+28	+29	+30
			29,	32,	35,	42,	45,	48,	63,	70,	83
	};
	private static final int[] THIRD_CLASS_ESSENCE_COMPENSATION = new int[] {
		//	+0	+1	+2	+3	+4	+5	+6	+7	+8	+9	+10
			0,	0,	0,	0,	1,	1,	2,	3,	4,	5,	7,
		//	+11	+12	+13	+14	+15
			9,	10,	19,	24,	35
	};
	private static final String AWEKENING_REQUEST_VAR = "@awakening_request";
	public static final Location TELEPORT_LOC = new Location(-114962, 226564, -2864);

	public AwakeningManagerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		if(val == 0)
		{
			if(player == null)
				return;

			if(player.getClassId().isOfRace(Race.ERTHEIA) || !player.getClassId().isOfLevel(ClassLevel.THIRD) || player.getLevel() < 85 || ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_SCROLL_OF_AFTERLIFE) < 1 || player.getClassId() == ClassId.JUDICATOR)
			{
				showChatWindow(player, "default/" + getNpcId() + "-no.htm");
				return;
			}

			if(!player.isBaseClassActive() && !ClassId.VALUES[player.getBaseClassId()].isOfLevel(ClassLevel.AWAKED))
			{
				showChatWindow(player, "default/" + getNpcId() + "-no_baseclass.htm");
				return;
			}

			if(!Config.ALT_ALLOW_AWAKE_ON_SUB_CLASS && !player.isBaseClassActive() && !player.isDualClassActive())
			{
				showChatWindow(player, "default/" + getNpcId() + "-no_subclass.htm");
				return;
			}

			if(!checkClassIdForNpc(player))
			{
				showChatWindow(player, "default/" + getNpcId() + "-no_class.htm");
				return;
			}

			if(ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_STONE_OF_AWEKENING) > 0)
			{
				int awakeClassId = player.getVarInt(getAwakeningRequestVar(player.getClassId()));
				if(awakeClassId > 0)
				{
					player.sendPacket(new ExChangeToAwakenedClass(player, this, awakeClassId));
					return;
				}
			}
		}
		super.showChatWindow(player, val, arg);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equalsIgnoreCase("awakening"))
		{
			if(!st.hasMoreTokens())
				return;

			if(player.getClassId().isOfRace(Race.ERTHEIA) || !player.getClassId().isOfLevel(ClassLevel.THIRD) || player.getLevel() < 85 || ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_SCROLL_OF_AFTERLIFE) < 1)
				return;

			if(!player.isBaseClassActive() && !ClassId.VALUES[player.getBaseClassId()].isOfLevel(ClassLevel.AWAKED))
				return;

			if(!Config.ALT_ALLOW_AWAKE_ON_SUB_CLASS && !player.isBaseClassActive() && !player.isDualClassActive())
				return;

			if(!checkClassIdForNpc(player))
				return;

			if(player.getServitors().length > 0)
			{
				showChatWindow(player, "default/" + getNpcId() + "-no_summon.htm");
				return;
			}

			int steep = Integer.parseInt(st.nextToken());
			if(steep == 0)
			{
				showChatWindow(player, "default/awakening_compensation.htm", "<?SP?>", 0, "<?ESSENCE?>", 0);
				return;
			}
			else if(steep == 1)
			{
				ClassId classId = player.getClassId();
				ClassId awakedClassId = classId.getAwakedClass();
				if(awakedClassId == null)
				{
					_log.warn(getClass().getSimpleName() + ": Cannot find awaked class for class: " + classId.toString());
					return;
				}

				Collection<SkillLearn> availableSkills = SkillAcquireHolder.getInstance().getAwakeParentSkillTree(awakedClassId.getBaseAwakedClassId(), classId);
				StringBuilder skillList = new StringBuilder();
				for(SkillLearn sl : availableSkills)
				{
					if(sl == null)
						continue;

					Skill skill = SkillHolder.getInstance().getSkill(sl.getId(), sl.getLevel());
					if(skill == null)
						continue;

					skillList.append("<table><tr><td width=40 height=40><img src=\"" + skill.getIcon() + "\" width=32 height=32></td><td width=200>" + skill.getName(player) + "</td></tr></table>");
				}
				showChatWindow(player, "default/awakening_skill_training.htm", "<?SKILL_LIST?>", skillList.toString());
				return;
			}
			else if(steep == 2)
			{
				// Чтобы из-за лагов не получили компенсацию дважды.
				if(player.getVarInt(getAwakeningRequestVar(player.getClassId())) > 0)
					return;

				ClassId classId = player.getClassId();
				ClassId awakedClassId = classId.getAwakedClass();
				if(awakedClassId == null)
				{
					_log.warn(getClass().getSimpleName() + ": Cannot find awaked class for class: " + classId.toString());
					return;
				}

				ItemFunctions.addItem(player, ItemTemplate.ITEM_ID_STONE_OF_AWEKENING, 1, true);
				player.setVar(getAwakeningRequestVar(classId), String.valueOf(awakedClassId.getId()), -1);
				player.sendPacket(new ExChangeToAwakenedClass(player, this, awakedClassId.getId()));
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	private boolean checkClassIdForNpc(Player player)
	{
		return player.getClassId().isOfType2(getClassTypeByNpc());
	}

	public ClassType2 getClassTypeByNpc()
	{
		switch(getNpcId())
		{
			case 33404:
				return ClassType2.HEALER;
			case 33397:
				return ClassType2.KNIGHT;
			case 33400:
				return ClassType2.ARCHER;
			case 33402:
				return ClassType2.ENCHANTER;
			case 33399:
				return ClassType2.ROGUE;
			case 33398:
				return ClassType2.WARRIOR;
			case 33401:
				return ClassType2.WIZARD;
			case 33403:
				return ClassType2.SUMMONER;
		}
		return null;
	}

	public static String getAwakeningRequestVar(ClassId classId)
	{
		return AWEKENING_REQUEST_VAR + "_" + classId.getId();
	}
}
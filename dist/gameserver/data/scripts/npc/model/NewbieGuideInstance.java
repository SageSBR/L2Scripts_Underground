package npc.model;

import java.util.StringTokenizer;

import l2s.commons.time.cron.SchedulingPattern;
import l2s.commons.util.Rnd;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Bonux
 */
public class NewbieGuideInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final int MAX_SUPPORT_LEVEL = 91;

	private static final int[][] BUFF_SETS = new int[][]{
		{ 15642, 1 }, // Путешественник - Поэма Рога
		{ 15643, 1 }, // Путешественник - Поэма Барабана
		{ 15644, 1 }, // Путешественник - Поэма Органа
		{ 15645, 1 }, // Путешественник - Поэма Гитары
		{ 15646, 1 }, // Путешественник - Поэма Арфы
		{ 15647, 1 },  // Путешественник - Поэма Лютни
		{ 15651, 1 },  // Путешественник - Соната Битвы
		{ 15652, 1 },  // Путешественник - Соната Движения
		{ 15653, 1 }  // Путешественник - Соната Расслабления
	};

	private static final int[] KNIGHTS_HARMONY = { 15648, 1 }; // Путешественник - Гармония Стража
	private static final int[] WARRIORS_HARMONY = { 15649, 1 }; // Путешественник - Гармония Берсерка
	private static final int[] WIZARDS_HARMONY = { 15650, 1 }; // Путешественник - Гармония Мага

	private static final int[] BLESSING_OF_PROTECTION = { 5182, 1 }; // Благословение Защиты

	private static int TIPS = -1;

	private static final int ADVENTURER_SUPPORT_GOODS = 32241; // Вещи Поддержки Путешественника
	private static final String ADVENTURER_SUPPORT_VAR = "@received_advent_support";

	private static final SchedulingPattern RESTART_DATE_PATTERN = new SchedulingPattern("30 6 * * *");

	public NewbieGuideInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		String pom;
		if(val != 0)
			pom = npcId + "-" + val;
		else
			pom = String.valueOf(npcId);

		return "newbie_guide/" + pom + ".htm";
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equalsIgnoreCase("buffs"))
		{
			if(player.getLevel() > MAX_SUPPORT_LEVEL || player.getClassId().isAwaked())
			{
				showChatWindow(player, "newbie_guide/blessing_list002.htm");
				return;
			}

			for(int[] skill : BUFF_SETS)
				getBuff(skill[0], skill[1], player);

			int setId = Integer.parseInt(st.nextToken());
			switch(setId)
			{
				case 1:
					getBuff(KNIGHTS_HARMONY[0], KNIGHTS_HARMONY[1], player);
					break;
				case 2:
					getBuff(WARRIORS_HARMONY[0], WARRIORS_HARMONY[1], player);
					break;
				case 3:
					getBuff(WIZARDS_HARMONY[0], WIZARDS_HARMONY[1], player);
					break;
			}

			if(!player.isPK() && player.getLevel() <= 39 && player.getClassLevel().ordinal() < ClassLevel.SECOND.ordinal())
				getBuff(BLESSING_OF_PROTECTION[0], BLESSING_OF_PROTECTION[1], player);
		}
		else if(cmd.equalsIgnoreCase("receivebless"))
		{
			if(player.isPK() || player.getLevel() > 39 || player.getClassLevel().ordinal() >= ClassLevel.SECOND.ordinal())
			{
				showChatWindow(player, "newbie_guide/pk_protect002.htm");
				return;
			}

			getBuff(BLESSING_OF_PROTECTION[0], BLESSING_OF_PROTECTION[1], player);
		}
		else if(cmd.equalsIgnoreCase("easeshilien"))
		{
			if(player.getDeathPenalty().getLevel() < 3)
			{
				showChatWindow(player, "newbie_guide/ease_shilien001.htm");
				return;
			}

			player.getDeathPenalty().reduceLevel();
		}
		else if(cmd.equalsIgnoreCase("bid1"))
		{
			/*int[][] list = new int[][]{{36551, 1}, {36552, 1}, {36546, 1}, {36547, 1}, {36556, 1}, {36557, 1}, {36526, 1}, {36516, 1}, {36517, 1}, {36518, 1}, {36519, 1}, {36520, 1}, {36521, 1}, {36522, 1}, {36523, 1}, {36524, 1}, {36525, 1}, {36527, 1}, {36528, 1},
			{36529, 1}, {36530, 1}, {36531, 1}, {36532, 1}, {36533, 1}, {36534, 1}, {36535, 1}, {36536, 1}, {36537, 1}, {36538, 1}, {36538, 1}, {36539, 1}, {36540, 1}, {36541, 1}, {36542, 1}, {36543, 1}, {36544, 1}, {36545, 1}, {36546, 1}, {36547, 1}, {36548, 1}, {36549, 1}, {36550, 1},
			{36551, 1}, {36552, 1}, {36553, 1}, {36554, 1}, {36555, 1}, {36556, 1}, {36557, 1}, {36558, 1}, {36559, 1}, {36560, 1}, {36561, 1}, {36562, 1}, {36563, 1}, {36564, 1}, {36565,1}};*/

			/*double[] chances = new double[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};*/
			int[][] list = new int[][]{{36556, 1}, {36551, 1}, {36516, 1}, {36526, 1}, {36521, 1}, {36561, 1}, {36517, 1}, {36557, 1}, {36546, 1}, {36528, 1}, {36562, 1}, {36527, 1}, {36522, 1}, {36518, 1}, {36552, 1}, {36547, 1}, {36523, 1}, {6550, 1}, {22921, 1}};
			double[] chances = new double[]{21.1, 5, 23.9, 24.7, 10.5, 4.6, 1.1, 0.8, 6.6, 0.4, 0.3, 0.3, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1};
			if (player.getInventory().getCountOf(37045) < 1)
			{
				showChatWindow(player, "newbie_guide/iron_gate_coin_nogamble.htm");
				return;
			}
			else
			{
				ItemFunctions.removeItem(player, 37045, 1, true);
				RandomOneItem(player, list, chances);
			}
		}
		else if(cmd.equalsIgnoreCase("bid10"))
		{
			int[][] list = new int[][]{{36551, 1}, {36552, 1}, {36546, 1}, {36547, 1}, {36556, 1}, {36557, 1}, {36526, 1}, {36516, 1}, {36517, 1}, {36518, 1}, {36519, 1}, {36520, 1}, {36521, 1}, {36522, 1}, {36523, 1}, {36524, 1}, {36525, 1}, {36527, 1}, {36528, 1},
					{36529, 1}, {36530, 1}, {36531, 1}, {36532, 1}, {36533, 1}, {36534, 1}, {36535, 1}, {36536, 1}, {36537, 1}, {36538, 1}, {36538, 1}, {36539, 1}, {36540, 1}, {36541, 1}, {36542, 1}, {36543, 1}, {36544, 1}, {36545, 1}, {36546, 1}, {36547, 1}, {36548, 1}, {36549, 1}, {36550, 1},
					{36551, 1}, {36552, 1}, {36553, 1}, {36554, 1}, {36555, 1}, {36556, 1}, {36557, 1}, {36558, 1}, {36559, 1}, {36560, 1}, {36561, 1}, {36562, 1}, {36563, 1}, {36564, 1}, {36565,1},
					{9546, 1}, {9547, 1}, {9548, 1}, {9549, 1}, {9550, 1}, {9551, 1}};
			double[] chances = new double[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			8, 8, 8, 8, 8, 8};

			if (player.getInventory().getCountOf(37045) < 10 || player.getAdena() < 50000)
			{
				showChatWindow(player, "newbie_guide/iron_gate_coin_nogamble.htm");
				return;
			}
			else
			{
				ItemFunctions.removeItem(player, 37045, 10, true);
				ItemFunctions.removeItem(player, 57, 50000, true);
				RandomOneItem(player, list, chances);
			}
		}
		else if(cmd.equalsIgnoreCase("bid50"))
		{
			int[][] list = new int[][]{{9546, 1}, {9547, 1}, {9548, 1}, {9549, 1}, {9550, 1}, {9551, 1}, {9552, 1}, {9553, 1}, {9554, 1}, {9555, 1}, {9556, 1}, {9557, 1}};
			double[] chances = new double[]{10, 10, 10, 10, 10, 10, 3, 3, 3, 3, 3, 3};
			if (player.getInventory().getCountOf(37045) < 50 || player.getAdena() < 500000)
			{
				showChatWindow(player, "newbie_guide/iron_gate_coin_nogamble.htm");
				return;
			}
			else
			{
				ItemFunctions.removeItem(player, 37045, 50, true);
				ItemFunctions.removeItem(player, 57, 500000, true);
				RandomOneItem(player, list, chances);
			}
		}
		else if(cmd.equalsIgnoreCase("bid2000"))
		{
			if(player.getLevel() < 86)
			{
				showChatWindow(player, "newbie_guide/iron_gate_coin_level.htm");
				return;
			}
			else if (player.getInventory().getCountOf(37045) < 2000)
			{
				showChatWindow(player, "newbie_guide/iron_gate_coin_nogamble.htm");
				return;
			}
			else
			{
				ItemFunctions.removeItem(player, 37045, 2000, true);
				ItemFunctions.addItem(player, 38600, 1, true);
				ItemFunctions.addItem(player, 38601, 2, true);
			}

		}
		else if(cmd.equalsIgnoreCase("tips"))
		{
			if(!player.getVarBoolean(ADVENTURER_SUPPORT_VAR))
			{
				long restartTime = RESTART_DATE_PATTERN.next(System.currentTimeMillis());
				if(restartTime < System.currentTimeMillis()) // Заглушка, крон не умеет работать с секундами.
					restartTime += 86400000L; // Добавляем сутки.

				player.setVar(ADVENTURER_SUPPORT_VAR, "true", restartTime);
				ItemFunctions.addItem(player, ADVENTURER_SUPPORT_GOODS, 1L, true);
			}

			if(TIPS < 0)
			{
				int i = 0;
				while(true)
				{
					i++;
					String html = HtmCache.getInstance().getNullable("newbie_guide/tips/tip-" + i + ".htm", player);
					if(html == null)
					{
						TIPS = i - 1;
						break;
					}
				}
			}
			showChatWindow(player, "newbie_guide/tips/tip-" + Rnd.get(1, TIPS) + ".htm");
		}
		else
			super.onBypassFeedback(player, command);
	}

	private static boolean RandomOneItem(Player player, int[][] items, double[] chances)
	{
		if(items.length != chances.length)
			return false;

		double extractChance = 0;
		for(double c : chances)
			extractChance += c;

		int[] successfulItems = new int[0];
		//while(successfulItems.length == 0)
		for(int i = 0; i < items.length; i++)
		{
			if(Rnd.chance(chances[i]))
			{
				successfulItems = ArrayUtils.add(successfulItems, i);
				break;
			}
		}
		if(successfulItems.length > 0)
		{
			int[] item = items[successfulItems[Rnd.get(successfulItems.length)]];
			if(item.length < 2)
				return false;

			Functions.addItem(player, item[0], item[1]);
		}
		else
			player.sendMessage("Увы, вам не повезло.");
		return true;
	}


	private void getBuff(int skillId, int skillLevel, Player player)
	{
		Skill skill = SkillHolder.getInstance().getSkill(skillId, skillLevel);
		if(skill == null)
			return;

		int removed = player.getEffectList().stopEffects(skill);
		if(removed > 0)
			player.sendPacket(new SystemMessagePacket(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(skill.getId(), skill.getLevel()));

		forceUseSkill(skill, player);
	}
}
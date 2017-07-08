package handler.bbs.custom;

import l2s.commons.map.hash.TIntStringHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import l2s.gameserver.Config;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.handler.bbs.CommunityBoardManager;
import l2s.gameserver.handler.bbs.ICommunityBoardHandler;
import l2s.gameserver.instancemanager.QuestManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.SubClass;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.NobleType;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.base.SubClassType;
import l2s.gameserver.model.instances.AwakeningManagerInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExSubjobInfo;
import l2s.gameserver.network.l2.s2c.ShowBoardPacket;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.utils.HtmlUtils;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import handler.bbs.ScriptsCommunityHandler;

/**
 * @author Bonux
**/
public class CommunityCareer extends ScriptsCommunityHandler
{
	private static final Logger _log = LoggerFactory.getLogger(CommunityCareer.class);

	@Override
	public String[] getBypassCommands()
	{
		return new String[]
		{
			"_cbbscareer"
		};
	}

	@Override
	protected void doBypassCommand(Player player, String bypass)
	{
		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();
		String html = "";

		if("cbbscareer".equals(cmd))
		{
			String cmd2 = st.nextToken();
			if("profession".equals(cmd2))
			{
				if(BBSConfig.OCCUPATION_SERVICE_COST_ITEM_ID_1 == 0 && BBSConfig.OCCUPATION_SERVICE_COST_ITEM_ID_2 == 0 && BBSConfig.OCCUPATION_SERVICE_COST_ITEM_ID_3 == 0 && BBSConfig.OCCUPATION_SERVICE_COST_ITEM_ID_4 == 0)
				{
					player.sendMessage(player.isLangRus() ? "Данный сервис отключен." : "This service disallowed.");
					player.sendPacket(ShowBoardPacket.CLOSE);
					return;
				}

				TIntStringHashMap tpls = HtmCache.getInstance().getTemplates("scripts/handler/bbs/pages/professions.htm", player);
				html = tpls.get(0);

				StringBuilder content = new StringBuilder();

				final int feeItemId = getFeeItemIdForChangeClass(player);
				final long feeItemCount = getFeeItemCountForChangeClass(player);
				final int nextClassMinLevel = getNextClassMinLevel(player);
				if(!st.hasMoreTokens())
				{
					if(nextClassMinLevel == -1)
						content.append(tpls.get(1));
					else if(feeItemId == 0)
						content.append(tpls.get(8));
					else
					{
						if(nextClassMinLevel > player.getLevel())
							content.append(tpls.get(5).replace("<?level?>", String.valueOf(nextClassMinLevel)));
						else
						{
							List<ClassId> availClasses = getAvailClasses(player.getClassId());
							if(availClasses.isEmpty())
								content.append(tpls.get(6));
							else
							{
								ClassId classId = availClasses.get(0);
								if(classId.isOfLevel(ClassLevel.AWAKED))
									content.append(tpls.get(9));
								else
									content.append(tpls.get(2));

								if(!classId.isOfLevel(ClassLevel.AWAKED) || ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_SCROLL_OF_AFTERLIFE) == 0)
								{
									if(feeItemId > 0 && feeItemCount > 0)
									{
										content.append("<br1>");
										String priceMsg = classId.isOfLevel(ClassLevel.AWAKED) ? tpls.get(10) : tpls.get(3);
										content.append(priceMsg.replace("<?fee_item_count?>", String.valueOf(feeItemCount)).replace("<?fee_item_name?>", HtmlUtils.htmlItemName(feeItemId)));
									}
								}

								if(classId.isOfLevel(ClassLevel.AWAKED))
								{
									content.append("<br>");

									String classHtm = tpls.get(11);
									classHtm = classHtm.replace("<?class_id?>", String.valueOf(classId.getId()));

									content.append(classHtm);
								}
								else
								{
									for(ClassId cls : availClasses)
									{
										content.append("<br>");

										String classHtm = tpls.get(4);
										classHtm = classHtm.replace("<?class_name?>", cls.getName(player));
										classHtm = classHtm.replace("<?class_id?>", String.valueOf(cls.getId()));

										content.append(classHtm);
									}
								}
							}
						}
					}
				}
				else
				{
					if(!BBSConfig.GLOBAL_USE_FUNCTIONS_CONFIGS && !checkUseCondition(player))
					{
						onWrongCondition(player);
						return;
					}

					if(nextClassMinLevel == -1 || feeItemId == 0 || nextClassMinLevel > player.getLevel())
					{
						ICommunityBoardHandler handler = CommunityBoardManager.getInstance().getCommunityHandler("_cbbscareer_profession");
						if(handler != null)
							onBypassCommand(player, "_cbbscareer_profession");
						return;
					}

					List<ClassId> availClasses = getAvailClasses(player.getClassId());
					if(availClasses.isEmpty())
					{
						ICommunityBoardHandler handler = CommunityBoardManager.getInstance().getCommunityHandler("_cbbscareer_profession");
						if(handler != null)
							onBypassCommand(player, "_cbbscareer_profession");
						return;
					}

					boolean avail = false;
					ClassId classId = ClassId.VALUES[Integer.parseInt(st.nextToken())];
					for(ClassId cls : availClasses)
					{
						if(cls == classId)
						{
							avail = true;
							break;
						}
					}

					if(!avail)
					{
						ICommunityBoardHandler handler = CommunityBoardManager.getInstance().getCommunityHandler("_cbbscareer_profession");
						if(handler != null)
							onBypassCommand(player, "_cbbscareer_profession");
						return;
					}

					if(!classId.isOfLevel(ClassLevel.AWAKED) || ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_SCROLL_OF_AFTERLIFE) == 0)
					{
						if(feeItemId > 0 && feeItemCount > 0 && ItemFunctions.removeItem(player, feeItemId, feeItemCount, true) != feeItemCount)
						{
							String errorMsg = tpls.get(7).replace("<?fee_item_count?>", String.valueOf(feeItemCount)).replace("<?fee_item_name?>", HtmlUtils.htmlItemName(feeItemId));
							html = html.replace("<?content?>", errorMsg);
							ShowBoardPacket.separateAndSend(html, player);
							return;
						}

						if(classId.isOfLevel(ClassLevel.AWAKED))
							ItemFunctions.addItem(player, ItemTemplate.ITEM_ID_SCROLL_OF_AFTERLIFE, 1, true);
					}

					if(classId.isOfLevel(ClassLevel.AWAKED))
					{
						player.teleToLocation(AwakeningManagerInstance.TELEPORT_LOC);
						player.sendPacket(ShowBoardPacket.CLOSE);
						return;
					}
					else
					{
						player.sendPacket(SystemMsg.CONGRATULATIONS__YOUVE_COMPLETED_A_CLASS_TRANSFER);
						player.setClassId(classId.getId(), false);
						player.broadcastUserInfo(true);
					}

					ICommunityBoardHandler handler = CommunityBoardManager.getInstance().getCommunityHandler("_cbbscareer_profession");
					if(handler != null)
						onBypassCommand(player, "_cbbscareer_profession");
					return;
				}

				html = html.replace("<?content?>", content.toString());
			}
			else if("subclass".equals(cmd2))
			{
				if(BBSConfig.SUBCLASS_SERVICE_COST_ITEM_ID == 0)
				{
					player.sendMessage(player.isLangRus() ? "Данный сервис отключен." : "This service disallowed.");
					player.sendPacket(ShowBoardPacket.CLOSE);
					return;
				}

				TIntStringHashMap tpls = HtmCache.getInstance().getTemplates("scripts/handler/bbs/pages/subclass.htm", player);
				html = tpls.get(0);

				final long price = BBSConfig.SUBCLASS_SERVICE_COST_ITEM_COUNT;

				StringBuilder content = new StringBuilder();

				final QuestState qs = player.getQuestState("_10385_RedThreadofFate");
				if(player.getRace() == Race.ERTHEIA)
					content.append(tpls.get(8));
				else if(qs != null && qs.isCompleted())
					content.append(tpls.get(5));
				else if(player.getLevel() < Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS)
					content.append(tpls.get(4).replace("<?min_level?>", String.valueOf(Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS)));
				else
				{
					if(!st.hasMoreTokens())
					{
						if(price > 0)
						{
							String priceMsg = tpls.get(1).replace("<?fee_item_count?>", Util.formatAdena(price));
							priceMsg = priceMsg.replace("<?fee_item_name?>", HtmlUtils.htmlItemName(BBSConfig.SUBCLASS_SERVICE_COST_ITEM_ID));
							content.append(priceMsg);
						}
						else
							content.append(tpls.get(2));

						content.append(tpls.get(3));
					}
					else
					{
						String cmd3 = st.nextToken();
						if("buy".equals(cmd3))
						{
							if(!BBSConfig.GLOBAL_USE_FUNCTIONS_CONFIGS && !checkUseCondition(player))
							{
								onWrongCondition(player);
								return;
							}

							if(price == 0 || ItemFunctions.removeItem(player, BBSConfig.SUBCLASS_SERVICE_COST_ITEM_ID, price, true) == price)
							{
								сompleteQuest("_10385_RedThreadofFate", player);

								content.append(tpls.get(7));
							}
							else
							{
								String errorMsg = tpls.get(6).replace("<?fee_item_count?>", Util.formatAdena(price));
								errorMsg = errorMsg.replace("<?fee_item_name?>", HtmlUtils.htmlItemName(BBSConfig.SUBCLASS_SERVICE_COST_ITEM_ID));
								content.append(errorMsg);
							}
						}
					}
				}
				html = html.replace("<?content?>", content.toString());
			}
			else if("noble".equals(cmd2))
			{
				if(BBSConfig.NOBLE_SERVICE_COST_ITEM_ID == 0)
				{
					player.sendMessage(player.isLangRus() ? "Данный сервис отключен." : "This service disallowed.");
					player.sendPacket(ShowBoardPacket.CLOSE);
					return;
				}

				TIntStringHashMap tpls = HtmCache.getInstance().getTemplates("scripts/handler/bbs/pages/noble.htm", player);
				html = tpls.get(0);

				final long price = BBSConfig.NOBLE_SERVICE_COST_ITEM_COUNT;

				StringBuilder content = new StringBuilder();
				if(player.isNoble())
					content.append(tpls.get(5));
				else if(player.getSubLevel() < 75)
					content.append(tpls.get(4));
				else
				{
					if(!st.hasMoreTokens())
					{
						if(price > 0)
						{
							String priceMsg = tpls.get(1).replace("<?fee_item_count?>", Util.formatAdena(price));
							priceMsg = priceMsg.replace("<?fee_item_name?>", HtmlUtils.htmlItemName(BBSConfig.NOBLE_SERVICE_COST_ITEM_ID));
							content.append(priceMsg);
						}
						else
							content.append(tpls.get(2));

						content.append(tpls.get(3));
					}
					else
					{
						String cmd3 = st.nextToken();
						if("buy".equals(cmd3))
						{
							if(!BBSConfig.GLOBAL_USE_FUNCTIONS_CONFIGS && !checkUseCondition(player))
							{
								onWrongCondition(player);
								return;
							}

							if(price == 0 || ItemFunctions.removeItem(player, BBSConfig.NOBLE_SERVICE_COST_ITEM_ID, price, true) == price)
							{
								player.setNobleType(NobleType.NORMAL);
								content.append(tpls.get(7));
							}
							else
							{
								String errorMsg = tpls.get(6).replace("<?fee_item_count?>", Util.formatAdena(price));
								errorMsg = errorMsg.replace("<?fee_item_name?>", HtmlUtils.htmlItemName(BBSConfig.NOBLE_SERVICE_COST_ITEM_ID));
								content.append(errorMsg);
							}
						}
					}
				}
				html = html.replace("<?content?>", content.toString());
			}
			else if("dualclass".equals(cmd2))
			{
				final boolean isErtheia = player.getRace() == Race.ERTHEIA;
				final int itemId = isErtheia ? BBSConfig.ERTHEIA_DUALCLASS_SERVICE_COST_ITEM_ID : BBSConfig.DUALCLASS_SERVICE_COST_ITEM_ID;
				if(itemId == 0)
				{
					player.sendMessage(player.isLangRus() ? "Данный сервис отключен." : "This service disallowed.");
					player.sendPacket(ShowBoardPacket.CLOSE);
					return;
				}

				TIntStringHashMap tpls = HtmCache.getInstance().getTemplates("scripts/handler/bbs/pages/dualclass.htm", player);
				html = tpls.get(0);

				final long price = isErtheia ? BBSConfig.ERTHEIA_DUALCLASS_SERVICE_COST_ITEM_COUNT : BBSConfig.DUALCLASS_SERVICE_COST_ITEM_COUNT;

				StringBuilder content = new StringBuilder();

				boolean haveDualClass = false;
				for(SubClass sub : player.getSubClassList().values())
				{
					if(sub.isDual())
					{
						haveDualClass = true;
						break;
					}
				}

				final QuestState qs = player.getQuestState("_10472_WindsOfFateEncroachingShadows");
				if(haveDualClass)
					content.append(tpls.get(5));
				else if(qs != null && qs.isCompleted())
					content.append(tpls.get(10));
				else if(!isErtheia && (player.isBaseClassActive() || !ClassId.VALUES[player.getBaseClassId()].isOfLevel(ClassLevel.AWAKED) || player.getLevel() < 80))
					content.append(tpls.get(4));
				else if(isErtheia && (!player.isBaseClassActive() || !player.getClassId().isOfLevel(ClassLevel.THIRD) || player.getLevel() < 85))
					content.append(tpls.get(8));
				else if(player.getClassId() == ClassId.INSPECTOR || player.getClassId() == ClassId.JUDICATOR)
					content.append(tpls.get(11));
				else
				{
					if(!st.hasMoreTokens())
					{
						if(price > 0)
						{
							String priceMsg = tpls.get(1).replace("<?fee_item_count?>", Util.formatAdena(price));
							priceMsg = priceMsg.replace("<?fee_item_name?>", HtmlUtils.htmlItemName(itemId));
							content.append(priceMsg);
						}
						else
							content.append(tpls.get(2));

						content.append(tpls.get(3));
					}
					else
					{
						String cmd3 = st.nextToken();
						if("buy".equals(cmd3))
						{
							final SubClass sub = player.getActiveSubClass();
							if(sub == null)
								return;

							if(sub.isDual())
								return;

							if(price == 0 || ItemFunctions.removeItem(player, itemId, price, true) == price)
							{
								if(isErtheia)
								{
									сompleteQuest("_10472_WindsOfFateEncroachingShadows", player);

									content.append(tpls.get(9));
								}
								else
								{
									sub.setType(SubClassType.DUAL_SUBCLASS);

									// Для добавления дуал-класс скиллов.
									player.restoreSkills(true);
									player.sendSkillList();

									int classId = sub.getClassId();
									player.sendPacket(new SystemMessagePacket(SystemMsg.SUBCLASS_S1_HAS_BEEN_UPGRADED_TO_DUEL_CLASS_S2_CONGRATULATIONS).addClassName(classId).addClassName(classId));
									player.sendPacket(new ExSubjobInfo(player, true));
									player.broadcastPacket(new SocialActionPacket(player.getObjectId(), SocialActionPacket.LEVEL_UP));

									content.append(tpls.get(7));
								}
							}
							else
							{
								String errorMsg = tpls.get(6).replace("<?fee_item_count?>", Util.formatAdena(price));
								errorMsg = errorMsg.replace("<?fee_item_name?>", HtmlUtils.htmlItemName(itemId));
								content.append(errorMsg);
							}
						}
					}
				}
				html = html.replace("<?content?>", content.toString());
			}
			else if("honornoble".equals(cmd2))
			{
				if(BBSConfig.HONOR_NOBLE_SERVICE_COST_ITEM_ID == 0)
				{
					player.sendMessage(player.isLangRus() ? "Данный сервис отключен." : "This service disallowed.");
					player.sendPacket(ShowBoardPacket.CLOSE);
					return;
				}

				TIntStringHashMap tpls = HtmCache.getInstance().getTemplates("scripts/handler/bbs/pages/honornoble.htm", player);
				html = tpls.get(0);

				final long price = BBSConfig.HONOR_NOBLE_SERVICE_COST_ITEM_COUNT;

				StringBuilder content = new StringBuilder();
				if(player.getNobleType() == NobleType.HONORABLE)
					content.append(tpls.get(5));
				else if(player.getSubLevel() < 99 || player.getNobleType() != NobleType.NORMAL)
					content.append(tpls.get(4));
				else
				{
					if(!st.hasMoreTokens())
					{
						if(price > 0)
						{
							String priceMsg = tpls.get(1).replace("<?fee_item_count?>", Util.formatAdena(price));
							priceMsg = priceMsg.replace("<?fee_item_name?>", HtmlUtils.htmlItemName(BBSConfig.HONOR_NOBLE_SERVICE_COST_ITEM_ID));
							content.append(priceMsg);
						}
						else
							content.append(tpls.get(2));

						content.append(tpls.get(3));
					}
					else
					{
						String cmd3 = st.nextToken();
						if("buy".equals(cmd3))
						{
							if(!BBSConfig.GLOBAL_USE_FUNCTIONS_CONFIGS && !checkUseCondition(player))
							{
								onWrongCondition(player);
								return;
							}

							if(price == 0 || ItemFunctions.removeItem(player, BBSConfig.HONOR_NOBLE_SERVICE_COST_ITEM_ID, price, true) == price)
							{
								ItemFunctions.addItem(player, 45644, 1, true); // Почетная Тиара 
								ItemFunctions.addItem(player, 37763, 1, true);	// Почетный Плащ
								player.setNobleType(NobleType.HONORABLE);
								content.append(tpls.get(7));
							}
							else
							{
								String errorMsg = tpls.get(6).replace("<?fee_item_count?>", Util.formatAdena(price));
								errorMsg = errorMsg.replace("<?fee_item_name?>", HtmlUtils.htmlItemName(BBSConfig.HONOR_NOBLE_SERVICE_COST_ITEM_ID));
								content.append(errorMsg);
							}
						}
					}
				}
				html = html.replace("<?content?>", content.toString());
			}
		}
		ShowBoardPacket.separateAndSend(html, player);
	}

	@Override
	protected void doWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		//
	}

	private static int getNextClassMinLevel(Player player)
	{
		final ClassId classId = player.getClassId();
		if(classId.isLast())
			return -1;

		return classId.getClassMinLevel(true);
	}

	private static int getFeeItemIdForChangeClass(Player player)
	{
		if(player.getRace() != Race.ERTHEIA)
		{
			switch(player.getClassId().getClassLevel())
			{
				case NONE:
					return BBSConfig.OCCUPATION_SERVICE_COST_ITEM_ID_1;
				case FIRST:
					return BBSConfig.OCCUPATION_SERVICE_COST_ITEM_ID_2;
				case SECOND:
					return BBSConfig.OCCUPATION_SERVICE_COST_ITEM_ID_3;
				case THIRD:
					return BBSConfig.OCCUPATION_SERVICE_COST_ITEM_ID_4;
			}
		}
		else
		{
			switch(player.getClassId().getClassLevel())
			{
				case NONE:
					return BBSConfig.OCCUPATION_SERVICE_COST_ITEM_ID_2;
				case FIRST:
					return BBSConfig.OCCUPATION_SERVICE_COST_ITEM_ID_3;
				case SECOND:
					return BBSConfig.OCCUPATION_SERVICE_COST_ITEM_ID_4;
			}
		}
		return 0;
	}

	private static long getFeeItemCountForChangeClass(Player player)
	{
		if(player.getRace() != Race.ERTHEIA)
		{
			switch(player.getClassId().getClassLevel())
			{
				case NONE:
					return BBSConfig.OCCUPATION_SERVICE_COST_ITEM_COUNT_1;
				case FIRST:
					return BBSConfig.OCCUPATION_SERVICE_COST_ITEM_COUNT_2;
				case SECOND:
					return BBSConfig.OCCUPATION_SERVICE_COST_ITEM_COUNT_3;
				case THIRD:
					return BBSConfig.OCCUPATION_SERVICE_COST_ITEM_COUNT_4;
			}
		}
		else
		{
			switch(player.getClassId().getClassLevel())
			{
				case NONE:
					return BBSConfig.OCCUPATION_SERVICE_COST_ITEM_COUNT_2;
				case FIRST:
					return BBSConfig.OCCUPATION_SERVICE_COST_ITEM_COUNT_3;
				case SECOND:
					return BBSConfig.OCCUPATION_SERVICE_COST_ITEM_COUNT_4;
			}
		}
		return 0L;
	}

	private static List<ClassId> getAvailClasses(ClassId playerClass)
	{
		List<ClassId> result = new ArrayList<ClassId>();
		for(ClassId _class : ClassId.values())
		{
			if(!_class.isDummy() && !_class.isOutdated() && _class.getClassLevel().ordinal() == playerClass.getClassLevel().ordinal() + 1 && _class.childOf(playerClass) && _class != ClassId.INSPECTOR)
				result.add(_class);
		}		
		return result;
	}

	private static void сompleteQuest(String name, Player player)
	{
		Quest quest = QuestManager.getQuest2(name);
		QuestState qs = player.getQuestState(quest.getName());
		if(qs == null)
			qs = quest.newQuestState(player, Quest.COMPLETED);
		qs.setState(Quest.COMPLETED);
	}
}
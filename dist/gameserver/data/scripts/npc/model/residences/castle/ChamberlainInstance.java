package npc.model.residences.castle;

import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.napile.primitive.maps.IntObjectMap.Entry;

import l2s.gameserver.dao.CastleDamageZoneDAO;
import l2s.gameserver.dao.CastleDoorUpgradeDAO;
import l2s.gameserver.data.xml.holder.ResidenceHolder;
import l2s.gameserver.instancemanager.CastleManorManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.PledgeRank;
import l2s.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2s.gameserver.model.entity.events.impl.SiegeEvent;
import l2s.gameserver.model.entity.events.objects.CastleDamageZoneObject;
import l2s.gameserver.model.entity.events.objects.DoorObject;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.entity.residence.Fortress;
import l2s.gameserver.model.entity.residence.Residence;
import l2s.gameserver.model.entity.residence.ResidenceSide;
import l2s.gameserver.model.instances.DoorInstance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.pledge.Privilege;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.CastleSiegeInfoPacket;
import l2s.gameserver.network.l2.s2c.ExShowCropInfo;
import l2s.gameserver.network.l2.s2c.ExShowCropSettingPacket;
import l2s.gameserver.network.l2.s2c.ExShowManorDefaultInfoPacket;
import l2s.gameserver.network.l2.s2c.ExShowSeedInfoPacket;
import l2s.gameserver.network.l2.s2c.ExShowSeedSetting;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import l2s.gameserver.network.l2.s2c.NpcHtmlMessagePacket;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.manor.CropProcure;
import l2s.gameserver.templates.manor.SeedProduction;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.HtmlUtils;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Log;
import l2s.gameserver.utils.ReflectionUtils;
import npc.model.residences.ResidenceManager;

public class ChamberlainInstance extends ResidenceManager
{
	private static final long serialVersionUID = 1L;

	// Плащи
	private static final int CloakLight = 34925;
	private static final int CloakDark = 34926;
	private static final int CloakLightLeader = 34996;
	private static final int CloakDarkLeader = 34997;

	public ChamberlainInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	protected void setDialogs()
	{
		_mainDialog = "castle/chamberlain/chamberlain.htm";
		_failDialog = "castle/chamberlain/chamberlain-notlord.htm";
		_siegeDialog = _mainDialog;
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		int condition = getCond(player);
		if(condition != COND_OWNER)
			return;

		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken();
		String val = "";
		if(st.countTokens() >= 1)
			val = st.nextToken();

		Castle castle = getCastle();
		if(actualCommand.equalsIgnoreCase("viewSiegeInfo"))
		{
			if(!isHaveRigths(player, Clan.CP_CS_MANAGE_SIEGE))
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			player.sendPacket(new CastleSiegeInfoPacket(castle,  player));
		}
		else if(actualCommand.equalsIgnoreCase("ManageTreasure"))
		{
			if(!player.isClanLeader())
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(player, this);
			html.setFile("castle/chamberlain/chamberlain-castlevault.htm");
			html.replace("%Treasure%", String.valueOf(castle.getTreasury()));
			html.replace("%CollectedShops%", String.valueOf(castle.getCollectedShops()));
			html.replace("%CollectedSeed%", String.valueOf(castle.getCollectedSeed()));
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("TakeTreasure"))
		{
			if(!player.isClanLeader())
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			if(!val.equals(""))
			{
				long treasure = Long.parseLong(val);
				if(castle.getTreasury() < treasure)
				{
					NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(player, this);
					html.setFile("castle/chamberlain/chamberlain-havenottreasure.htm");
					html.replace("%Treasure%", String.valueOf(castle.getTreasury()));
					html.replace("%Requested%", String.valueOf(treasure));
					player.sendPacket(html);
					return;
				}
				if(treasure > 0)
				{
					castle.addToTreasuryNoTax(-treasure, false, false);
					Log.add(castle.getName() + "|" + -treasure + "|CastleChamberlain", "treasury");
					player.addAdena(treasure);
				}
			}

			NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(player, this);
			html.setFile("castle/chamberlain/chamberlain-castlevault.htm");
			html.replace("%Treasure%", String.valueOf(castle.getTreasury()));
			html.replace("%CollectedShops%", String.valueOf(castle.getCollectedShops()));
			html.replace("%CollectedSeed%", String.valueOf(castle.getCollectedSeed()));
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("PutTreasure"))
		{
			if(!val.equals(""))
			{
				long treasure = Long.parseLong(val);
				if(treasure > player.getAdena())
				{
					player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
					return;
				}
				if(treasure > 0)
				{
					castle.addToTreasuryNoTax(treasure, false, false);
					Log.add(castle.getName() + "|" + treasure + "|CastleChamberlain", "treasury");
					player.reduceAdena(treasure, true);
				}
			}

			NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(player, this);
			html.setFile("castle/chamberlain/chamberlain-castlevault.htm");
			html.replace("%Treasure%", String.valueOf(castle.getTreasury()));
			html.replace("%CollectedShops%", String.valueOf(castle.getCollectedShops()));
			html.replace("%CollectedSeed%", String.valueOf(castle.getCollectedSeed()));
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("manor"))
		{
			if(!isHaveRigths(player, Clan.CP_CS_MANOR_ADMIN))
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			String filename = "";
			if(CastleManorManager.getInstance().isDisabled())
				filename = "npcdefault.htm";
			else
			{
				int cmd = Integer.parseInt(val);
				switch(cmd)
				{
					case 0:
						filename = "castle/chamberlain/manor/manor.htm";
						break;
						// TODO: correct in html's to 1
					case 4:
						filename = "castle/chamberlain/manor/manor_help00" + st.nextToken() + ".htm";
						break;
					default:
						filename = "castle/chamberlain/chamberlain-no.htm";
						break;
				}
			}

			if(filename.length() > 0)
			{
				NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(player, this);
				html.setFile(filename);
				player.sendPacket(html);
			}
		}
		else if(actualCommand.startsWith("manor_menu_select"))
		{
			if(!isHaveRigths(player, Clan.CP_CS_MANOR_ADMIN))
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			// input string format:
			// manor_menu_select?ask=X&state=Y&time=X
			if(CastleManorManager.getInstance().isUnderMaintenance())
			{
				player.sendPacket(SystemMsg.THE_MANOR_SYSTEM_IS_CURRENTLY_UNDER_MAINTENANCE);
				player.sendActionFailed();
				return;
			}

			String params = actualCommand.substring(actualCommand.indexOf("?") + 1);
			StringTokenizer str = new StringTokenizer(params, "&");
			int ask = Integer.parseInt(str.nextToken().split("=")[1]);
			int state = Integer.parseInt(str.nextToken().split("=")[1]);
			int time = Integer.parseInt(str.nextToken().split("=")[1]);

			int castleId;
			if(state == -1) // info for current manor
				castleId = castle.getId();
			else
				// info for requested manor
				castleId = state;

			switch(ask)
			{ // Main action
				case 3: // Current seeds (Manor info)
					if(time == 1 && !ResidenceHolder.getInstance().getResidence(Castle.class, castleId).isNextPeriodApproved())
						player.sendPacket(new ExShowSeedInfoPacket(castleId, Collections.<SeedProduction>emptyList()));
					else
						player.sendPacket(new ExShowSeedInfoPacket(castleId, ResidenceHolder.getInstance().getResidence(Castle.class, castleId).getSeedProduction(time)));
					break;
				case 4: // Current crops (Manor info)
					if(time == 1 && !ResidenceHolder.getInstance().getResidence(Castle.class, castleId).isNextPeriodApproved())
						player.sendPacket(new ExShowCropInfo(castleId, Collections.<CropProcure>emptyList()));
					else
						player.sendPacket(new ExShowCropInfo(castleId, ResidenceHolder.getInstance().getResidence(Castle.class, castleId).getCropProcure(time)));
					break;
				case 5: // Basic info (Manor info)
					player.sendPacket(new ExShowManorDefaultInfoPacket());
					break;
				case 7: // Edit seed setup
					if(castle.isNextPeriodApproved())
						player.sendPacket(SystemMsg.A_MANOR_CANNOT_BE_SET_UP_BETWEEN_430_AM_AND_8_PM);
					else
						player.sendPacket(new ExShowSeedSetting(castle.getId()));
					break;
				case 8: // Edit crop setup
					if(castle.isNextPeriodApproved())
						player.sendPacket(SystemMsg.A_MANOR_CANNOT_BE_SET_UP_BETWEEN_430_AM_AND_8_PM);
					else
						player.sendPacket(new ExShowCropSettingPacket(castle.getId()));
					break;
			}
		}
		else if(actualCommand.equalsIgnoreCase("operate_door")) // door control
		{
			if(!isHaveRigths(player, Clan.CP_CS_ENTRY_EXIT))
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			if(castle.getSiegeEvent().isInProgress())
			{
				showChatWindow(player, "residence2/castle/chamberlain_saius021.htm");
				return;
			}
			if(!val.equals(""))
			{
				boolean open = Integer.parseInt(val) == 1;
				while(st.hasMoreTokens())
				{
					DoorInstance door = ReflectionUtils.getDoor(Integer.parseInt(st.nextToken()));
					if(open)
						door.openMe(player, true);
					else
						door.closeMe(player, true);
				}
			}

			NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(player, this);
			html.setFile("castle/chamberlain/" + getNpcId() + "-d.htm");
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("tax_set")) // tax rates control
		{
			if(!isHaveRigths(player, Clan.CP_CS_TAXES))
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			if(!val.equals(""))
			{
				// По умолчанию налог не более 30%
				int maxTax = 30;

				int tax = Integer.parseInt(val);
				if(tax < 0 || tax > maxTax)
				{
					NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(player, this);
					html.setFile("castle/chamberlain/chamberlain-hightax.htm");
					html.replace("%CurrentTax%", String.valueOf(castle.getTaxPercent()));
					player.sendPacket(html);
					return;
				}
				castle.setTaxPercent(player, tax);
			}

			NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(player, this);
			html.setFile("castle/chamberlain/chamberlain-settax.htm");
			html.replace("%CurrentTax%", String.valueOf(castle.getTaxPercent()));
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("upgrade_castle"))
		{
			if(!checkSiegeFunctions(player))
				return;

			showChatWindow(player, "castle/chamberlain/chamberlain-upgrades.htm");
		}
		else if(actualCommand.equalsIgnoreCase("reinforce"))
		{
			if(!checkSiegeFunctions(player))
				return;

			NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(player, this);
			html.setFile("castle/chamberlain/doorStrengthen-" + castle.getName() + ".htm");
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("trap_select"))
		{
			if(!checkSiegeFunctions(player))
				return;

			NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(player, this);
			html.setFile("castle/chamberlain/trap_select-" + castle.getName() + ".htm");
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("buy_trap"))
		{
			if(!checkSiegeFunctions(player))
				return;

			if(castle.getSiegeEvent().getObjects(CastleSiegeEvent.BOUGHT_ZONES).contains(val))
			{
				NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(player, this);
				html.setFile("castle/chamberlain/trapAlready.htm");
				player.sendPacket(html);
				return;
			}

			List<CastleDamageZoneObject> objects = castle.getSiegeEvent().getObjects(val);
			long price = 0;
			for(CastleDamageZoneObject o : objects)
				price += o.getPrice();

			if(player.getClan().getAdenaCount() < price)
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}

			player.getClan().getWarehouse().destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, price);
			castle.getSiegeEvent().addObject(CastleSiegeEvent.BOUGHT_ZONES, val);
			CastleDamageZoneDAO.getInstance().insert(castle, val);

			NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(player, this);
			html.setFile("castle/chamberlain/trapSuccess.htm");
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("door_manage"))
		{
			if(!isHaveRigths(player, Clan.CP_CS_ENTRY_EXIT))
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			if(castle.getSiegeEvent().isInProgress())
			{
				showChatWindow(player, "residence2/castle/chamberlain_saius021.htm");
				return;
			}

			NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(player, this);
			html.setFile("castle/chamberlain/doorManage.htm");
			html.replace("%id%", val);
			html.replace("%type%", st.nextToken());
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("upgrade_door_confirm"))
		{
			if(!isHaveRigths(player, Clan.CP_CS_MANAGE_SIEGE))
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			int id = Integer.parseInt(val);
			int type = Integer.parseInt(st.nextToken());
			int level = Integer.parseInt(st.nextToken());
			long price = getDoorCost(type, level);

			NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(player, this);
			html.setFile("castle/chamberlain/doorConfirm.htm");
			html.replace("%id%", String.valueOf(id));
			html.replace("%level%", String.valueOf(level));
			html.replace("%type%", String.valueOf(type));
			html.replace("%price%", String.valueOf(price));
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("upgrade_door"))
		{
			if(checkSiegeFunctions(player))
				return;

			int id = Integer.parseInt(val);
			int type = Integer.parseInt(st.nextToken());
			int level = Integer.parseInt(st.nextToken());
			long price = getDoorCost(type, level);

			List<DoorObject> doorObjects = castle.getSiegeEvent().getObjects(SiegeEvent.DOORS);
			DoorObject targetDoorObject = null;
			for(DoorObject o : doorObjects)
				if(o.getUId() == id)
				{
					targetDoorObject = o;
					break;
				}

			DoorInstance door = targetDoorObject.getDoor();
			int upgradeHp = (door.getMaxHp() - door.getUpgradeHp()) * level - door.getMaxHp();

			if(price == 0 || upgradeHp < 0)
			{
				player.sendMessage(new CustomMessage("common.Error", player));
				return;
			}

			if(door.getUpgradeHp() >= upgradeHp)
			{
				int oldLevel = door.getUpgradeHp() / (door.getMaxHp() - door.getUpgradeHp()) + 1;
				NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(player, this);
				html.setFile("castle/chamberlain/doorAlready.htm");
				html.replace("%level%", String.valueOf(oldLevel));
				player.sendPacket(html);
				return;
			}

			if(player.getClan().getAdenaCount() < price)
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}

			player.getClan().getWarehouse().destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, price);

			targetDoorObject.setUpgradeValue(castle.<SiegeEvent>getSiegeEvent(), upgradeHp);
			CastleDoorUpgradeDAO.getInstance().insert(door.getDoorId(), upgradeHp);
		}
		else if(actualCommand.equalsIgnoreCase("report")) // Report page
		{
			if(!isHaveRigths(player, Clan.CP_CS_USE_FUNCTIONS))
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			NpcString ssq_period = NpcString.PREPARATION;

			NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(player, this);
			html.setFile("castle/chamberlain/chamberlain-report.htm");
			html.replaceNpcString("%FeudName%", castle.getNpcStringName());
			html.replace("%CharClan%", player.getClan().getName());
			html.replace("%CharName%", player.getName());
			html.replaceNpcString("%SSPeriod%", ssq_period);
			html.replaceNpcString("%Avarice%", getSealOwner(1));
			html.replaceNpcString("%Revelation%", getSealOwner(2));
			html.replaceNpcString("%Strife%", getSealOwner(3));
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("fortressStatus"))
		{
			NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(player, this);
			html.setFile("castle/chamberlain/chamberlain-fortress-status.htm");
			StringBuilder b = new StringBuilder(100);

			for(Entry<List<Fortress>> entry : castle.getRelatedFortresses().entrySet())
			{
				NpcString type;
				switch(entry.getKey())
				{
					case Fortress.DOMAIN:
						type = NpcString.DOMAIN_FORTRESS;
						break;
					case Fortress.BOUNDARY:
						type = NpcString.BOUNDARY_FORTRESS;
						break;
					default:
						continue;
				}
				List<Fortress> fortresses = entry.getValue();
				for(Fortress fort : fortresses)
				{
					b.append(HtmlUtils.htmlResidenceName(fort.getId())).append(" (").append(HtmlUtils.htmlNpcString(type)).append(") : <font color=\"00FFFF\">");

					NpcString contractType;
					switch(fort.getContractState())
					{
						case Fortress.NOT_DECIDED:
							contractType = NpcString.NONPARTISAN;
							break;
						case Fortress.INDEPENDENT:
							contractType = NpcString.INDEPENDENT_STATE;
							break;
						case Fortress.CONTRACT_WITH_CASTLE:
							contractType = NpcString.CONTRACT_STATE;
							break;
						default:
							continue;
					}
					b.append(HtmlUtils.htmlNpcString(contractType)).append("</font> <br>");
				}
			}
			html.replace("%list%", b.toString());
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("Crown")) // Give Crown to Castle Owner
		{
			if(!player.isClanLeader())
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			if(player.getInventory().getItemByItemId(6841) == null)
			{
				ItemFunctions.addItem(player, 6841, 1, true);

				NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(player, this);
				html.setFile("castle/chamberlain/chamberlain-givecrown.htm");
				html.replace("%CharName%", player.getName());
				html.replaceNpcString("%FeudName%", castle.getNpcStringName());
				player.sendPacket(html);
			}
			else
			{
				NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(player, this);
				html.setFile("castle/chamberlain/alreadyhavecrown.htm");
				player.sendPacket(html);
			}
		}
		else if(actualCommand.equalsIgnoreCase("Cloak"))
		{
			if(player.isClanLeader())
			{
				int itemId = getCastle().getResidenceSide() == ResidenceSide.LIGHT ? CloakLightLeader : CloakDarkLeader;
				if(player.getInventory().getItemByItemId(itemId) != null)
				{
					showChatWindow(player, "castle/chamberlain/alreadyhavecloak.htm");
					return;
				}
				else
				{
					ItemFunctions.addItem(player, itemId, 1, true);
					showChatWindow(player, "castle/chamberlain/chamberlain-givecloak.htm");
					return;
				}
			}
			else
			{
				int itemId = getCastle().getResidenceSide() == ResidenceSide.LIGHT ? CloakLight : CloakDark;

				// Плащ может получить только маркиз и выше.
				if(player.getInventory().getItemByItemId(itemId) != null || player.getPledgeRank().ordinal() < PledgeRank.MARQUIS.ordinal())
				{
					showChatWindow(player, "castle/chamberlain/alreadyhavecloak.htm");
					return;
				}
				else
				{
					ItemFunctions.addItem(player, itemId, 1, true);
					showChatWindow(player, "castle/chamberlain/chamberlain-givecloak.htm");
					return;
				}
			}
		}
		else if(actualCommand.equalsIgnoreCase("manageFunctions"))
		{
			if(!player.hasPrivilege(Privilege.CS_FS_SET_FUNCTIONS))
				showChatWindow(player, "residence2/castle/chamberlain_saius063.htm");
			else
				showChatWindow(player, "residence2/castle/chamberlain_saius065.htm");
		}
		else if(actualCommand.equalsIgnoreCase("manageSiegeFunctions"))
		{
			if(!player.hasPrivilege(Privilege.CS_FS_SET_FUNCTIONS))
				showChatWindow(player, "residence2/castle/chamberlain_saius063.htm");
			else if(castle.getDomainFortressContract() == 0)
				showChatWindow(player, "residence2/castle/chamberlain_saius069.htm");
			else
				showChatWindow(player, "residence2/castle/chamberlain_saius052.htm");
		}
		else if(actualCommand.equalsIgnoreCase("items"))
		{
			NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(player, this);
			html.setFile("residence2/castle/chamberlain_saius064.htm");
			html.replace("%npcId%", String.valueOf(getNpcId()));
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("default"))
		{
			NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(player, this);
			html.setFile("castle/chamberlain/chamberlain.htm");
			player.sendPacket(html);
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	protected int getCond(Player player)
	{
		if(player.isGM())
			return COND_OWNER;
		Residence castle = getCastle();
		if(castle != null && castle.getId() > 0)
			if(player.getClan() != null)
				if(castle.getSiegeEvent().isInProgress())
					return COND_SIEGE; // Busy because of siege
				else if(castle.getOwnerId() == player.getClanId())
				{
					if(player.isClanLeader()) // Leader of clan
						return COND_OWNER;
					if(isHaveRigths(player, Clan.CP_CS_ENTRY_EXIT) || // doors
							isHaveRigths(player, Clan.CP_CS_MANOR_ADMIN) || // manor
							isHaveRigths(player, Clan.CP_CS_MANAGE_SIEGE) || // siege
							isHaveRigths(player, Clan.CP_CS_USE_FUNCTIONS) || // funcs
							isHaveRigths(player, Clan.CP_CS_DISMISS) || // banish
							isHaveRigths(player, Clan.CP_CS_TAXES) || // tax
							isHaveRigths(player, Clan.CP_CS_MERCENARIES) || // merc
							isHaveRigths(player, Clan.CP_CS_SET_FUNCTIONS) //funcs
					)
						return COND_OWNER; // Есть какие либо замковые привилегии
				}

		return COND_FAIL;
	}

	private NpcString getSealOwner(int seal)
	{
		//TODO: [Bonux] пересмотреть
		return NpcString.NO_OWNER;
	}

	private long getDoorCost(int type, int level)
	{
		int price = 0;

		switch(type)
		{
			case 1: // Главные ворота
				switch(level)
				{
					case 2:
						price = 3000000;
						break;
					case 3:
						price = 4000000;
						break;
					case 5:
						price = 5000000;
						break;
				}
				break;
			case 2: // Внутренние ворота
				switch(level)
				{
					case 2:
						price = 750000;
						break;
					case 3:
						price = 900000;
						break;
					case 5:
						price = 1000000;
						break;
				}
				break;
			case 3: // Стены
				switch(level)
				{
					case 2:
						price = 1600000;
						break;
					case 3:
						price = 1800000;
						break;
					case 5:
						price = 2000000;
						break;
				}
				break;
		}

		return price;
	}

	@Override
	protected Residence getResidence()
	{
		return getCastle();
	}

	@Override
	public L2GameServerPacket decoPacket()
	{
		return null;
	}

	@Override
	protected int getPrivUseFunctions()
	{
		return Clan.CP_CS_USE_FUNCTIONS;
	}

	@Override
	protected int getPrivSetFunctions()
	{
		return Clan.CP_CS_SET_FUNCTIONS;
	}

	@Override
	protected int getPrivDismiss()
	{
		return Clan.CP_CS_DISMISS;
	}

	@Override
	protected int getPrivDoors()
	{
		return Clan.CP_CS_ENTRY_EXIT;
	}

	private boolean checkSiegeFunctions(Player player)
	{
		Castle castle = getCastle();
		if(!player.hasPrivilege(Privilege.CS_FS_SIEGE_WAR))
		{
			player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return false;
		}

		if(castle.getSiegeEvent().isInProgress())
		{
			showChatWindow(player, "residence2/castle/chamberlain_saius021.htm");
			return false;
		}
		return true;
	}
}
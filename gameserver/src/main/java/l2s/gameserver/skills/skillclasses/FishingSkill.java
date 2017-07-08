package l2s.gameserver.skills.skillclasses;

import l2s.commons.collections.LazyArrayList;
import l2s.commons.util.Rnd;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.World;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.Zone.ZoneType;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.tables.FishTable;
import l2s.gameserver.templates.FishTemplate;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.item.WeaponTemplate;
import l2s.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.PositionUtils;

import java.util.List;

public class FishingSkill extends Skill
{
	public FishingSkill(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		Player player = (Player) activeChar;

		if(player.getSkillLevel(SKILL_FISHING_MASTERY) == -1)
		{
			player.sendMessage("Ваш навык Эксперт Рыболовства слишком низок.");
			return false;
		}

		if(player.isFishing())
		{
			player.stopFishing();
			player.sendPacket(SystemMsg.YOUR_ATTEMPT_AT_FISHING_HAS_BEEN_CANCELLED);
			return false;
		}

		if(player.isInBoat())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_FISH_WHILE_RIDING_AS_A_PASSENGER_OF_A_BOAT__ITS_AGAINST_THE_RULES);
			return false;
		}

		if(player.getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_FISH_WHILE_USING_A_RECIPE_BOOK_PRIVATE_MANUFACTURE_OR_PRIVATE_STORE);
			return false;
		}

		if(player.isInWater())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_FISH_WHILE_UNDER_WATER);
			return false;
		}

		if(player.isInPeaceZone())
		{
			player.sendPacket(SystemMsg.YOU_CANT_FISH_HERE);
			return false;
		}

		if(!player.isInZone(ZoneType.FISHING))
		{
			player.sendPacket(SystemMsg.YOU_CANT_FISH_HERE);
			return false;
		}

		WeaponTemplate weaponItem = player.getActiveWeaponTemplate();
		if(weaponItem == null || weaponItem.getItemType() != WeaponType.ROD)
		{
			//Fishing poles are not installed
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_A_FISHING_POLE_EQUIPPED);
			return false;
		}

		ItemInstance lure = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
		if(lure == null || lure.getCount() < 1)
		{
			player.sendPacket(SystemMsg.YOU_MUST_PUT_BAIT_ON_YOUR_HOOK_BEFORE_YOU_CAN_FISH);
			return false;
		}

		//Вычисляем координаты поплавка
		int rnd = Rnd.get(50) + 150;
		double angle = PositionUtils.convertHeadingToDegree(player.getHeading());
		//double radian = Math.toRadians(angle - 90);
		double radian = Math.toRadians(angle);
		double sin = Math.sin(radian);
		double cos = Math.cos(radian);
		//int x1 = -(int) (sin * rnd);
		int x1 = (int) (sin * rnd);
		int y1 = (int) (cos * rnd);
		int x = player.getX() + x1;
		int y = player.getY() + y1;
		//z - уровень карты
		int z = GeoEngine.getHeight(x, y, player.getZ(), player.getGeoIndex()) + 50;

		//Проверяем, что поплавок оказался в воде
		LazyArrayList<Zone> zones = LazyArrayList.newInstance();
		World.getZones(zones, new Location(x, y, z), player.getReflection());
		for(Zone zone : zones)
		{
			if(zone.getType() == ZoneType.water)
			{
				//z - уровень воды
				z = zone.getTerritory().getZmax() + 10;
				break;
			}
		}

		LazyArrayList.recycle(zones);

		player.getFishing().setFishLoc(new Location(x, y, z));

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void onEndCast(Creature activeChar, List<Creature> targets)
	{
		super.onEndCast(activeChar, targets);

		if(activeChar == null || !activeChar.isPlayer())
			return;

		Player player = activeChar.getPlayer();

		ItemInstance lure = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
		if(lure == null || lure.getCount() < 1)
		{
			player.sendPacket(SystemMsg.YOU_MUST_PUT_BAIT_ON_YOUR_HOOK_BEFORE_YOU_CAN_FISH);
			return;
		}
		Zone zone = player.getZone(ZoneType.FISHING);
		if(zone == null)
		{
			player.sendMessage("Здесь рыбы нет!");
			return;
		}

		int distributionId = zone.getParams().getInteger("distribution_id");

		int lureId = lure.getItemId();

		int group = l2s.gameserver.model.Fishing.getFishGroup(lure.getItemId());
		int type = l2s.gameserver.model.Fishing.getRandomFishType(lureId);
		int lvl = l2s.gameserver.model.Fishing.getRandomFishLvl(player);

		List<FishTemplate> fishs = FishTable.getInstance().getFish(group, type, lvl);
		if(fishs == null || fishs.size() == 0)
		{
			player.sendPacket(SystemMsg.SYSTEM_ERROR);
			return;
		}

		if(!player.getInventory().destroyItemByObjectId(player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LHAND), 1L))
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_BAIT);
			return;
		}

		int check = Rnd.get(fishs.size());
		FishTemplate fish = fishs.get(check);

		player.startFishing(fish, lureId);
	}
}
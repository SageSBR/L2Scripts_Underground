package l2s.gameserver.model;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.model.items.ItemInstance;

public final class ArmorSet
{
	private final TIntHashSet _chests = new TIntHashSet();
	private final TIntHashSet _legs = new TIntHashSet();
	private final TIntHashSet _head = new TIntHashSet();
	private final TIntHashSet _gloves = new TIntHashSet();
	private final TIntHashSet _feet = new TIntHashSet();
	private final TIntHashSet _shield = new TIntHashSet();
	private final TIntObjectHashMap<List<Skill>> _skills = new TIntObjectHashMap<List<Skill>>();
	private final List<Skill> _shieldSkills = new ArrayList<Skill>();
	private final List<Skill> _enchant6skills = new ArrayList<Skill>();
	private final List<Skill> _enchant7skills = new ArrayList<Skill>();
	private final List<Skill> _enchant8skills = new ArrayList<Skill>();

	public ArmorSet(String[] chests, String[] legs, String[] head, String[] gloves, String[] feet, String[] shield, String[] shield_skills, String[] enchant6skills, String[] enchant7skills, String[] enchant8skills)
	{
		_chests.addAll(parseItemIDs(chests));
		_legs.addAll(parseItemIDs(legs));
		_head.addAll(parseItemIDs(head));
		_gloves.addAll(parseItemIDs(gloves));
		_feet.addAll(parseItemIDs(feet));
		_shield.addAll(parseItemIDs(shield));

		if(shield_skills != null)
		{
			for(String skill : shield_skills)
			{
				StringTokenizer st = new StringTokenizer(skill, "-");
				if(st.hasMoreTokens())
				{
					int skillId = Integer.parseInt(st.nextToken());
					int skillLvl = Integer.parseInt(st.nextToken());
					_shieldSkills.add(SkillHolder.getInstance().getSkill(skillId, skillLvl));
				}
			}
		}

		if(enchant6skills != null)
		{
			for(String skill : enchant6skills)
			{
				StringTokenizer st = new StringTokenizer(skill, "-");
				if(st.hasMoreTokens())
				{
					int skillId = Integer.parseInt(st.nextToken());
					int skillLvl = Integer.parseInt(st.nextToken());
					_enchant6skills.add(SkillHolder.getInstance().getSkill(skillId, skillLvl));
				}
			}
		}

		if(enchant7skills != null)
		{
			for(String skill : enchant7skills)
			{
				StringTokenizer st = new StringTokenizer(skill, "-");
				if(st.hasMoreTokens())
				{
					int skillId = Integer.parseInt(st.nextToken());
					int skillLvl = Integer.parseInt(st.nextToken());
					_enchant7skills.add(SkillHolder.getInstance().getSkill(skillId, skillLvl));
				}
			}
		}

		if(enchant8skills != null)
		{
			for(String skill : enchant8skills)
			{
				StringTokenizer st = new StringTokenizer(skill, "-");
				if(st.hasMoreTokens())
				{
					int skillId = Integer.parseInt(st.nextToken());
					int skillLvl = Integer.parseInt(st.nextToken());
					_enchant8skills.add(SkillHolder.getInstance().getSkill(skillId, skillLvl));
				}
			}
		}
	}

	private static int[] parseItemIDs(String[] items)
	{
		TIntHashSet result = new TIntHashSet();
		if(items != null)
		{
			for(String s_id : items)
			{
				int id = Integer.parseInt(s_id);
				if(id > 0)
				{
					result.add(id);
				}
			}
		}
		return result.toArray();
	}

	public void addSkills(int partsCount, String[] skills)
	{
		List<Skill> skillList = new ArrayList<Skill>();
		if(skills != null)
		{
			for(String skill : skills)
			{
				StringTokenizer st = new StringTokenizer(skill, "-");
				if(st.hasMoreTokens())
				{
					int skillId = Integer.parseInt(st.nextToken());
					int skillLvl = Integer.parseInt(st.nextToken());
					skillList.add(SkillHolder.getInstance().getSkill(skillId, skillLvl));
				}
			}
		}
		_skills.put(partsCount, skillList);
	}

	/**
	 * Checks if player have equipped all items from set (not checking shield)
	 * @param player whose inventory is being checked
	 * @return True if player equips whole set
	 */
	public boolean containAll(Player player)
	{
		Inventory inv = player.getInventory();

		ItemInstance chestItem = inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
		ItemInstance legsItem = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);
		ItemInstance headItem = inv.getPaperdollItem(Inventory.PAPERDOLL_HEAD);
		ItemInstance glovesItem = inv.getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
		ItemInstance feetItem = inv.getPaperdollItem(Inventory.PAPERDOLL_FEET);

		int chest = 0;
		int legs = 0;
		int head = 0;
		int gloves = 0;
		int feet = 0;

		if(chestItem != null)
			chest = chestItem.getItemId();
		if(legsItem != null)
			legs = legsItem.getItemId();
		if(headItem != null)
			head = headItem.getItemId();
		if(glovesItem != null)
			gloves = glovesItem.getItemId();
		if(feetItem != null)
			feet = feetItem.getItemId();

		return containAll(chest, legs, head, gloves, feet);

	}

	public boolean containAll(int chest, int legs, int head, int gloves, int feet)
	{
		if(!_chests.isEmpty() && !_chests.contains(chest))
			return false;
		if(!_legs.isEmpty() && !_legs.contains(legs))
			return false;
		if(!_head.isEmpty() && !_head.contains(head))
			return false;
		if(!_gloves.isEmpty() && !_gloves.contains(gloves))
			return false;
		if(!_feet.isEmpty() && !_feet.contains(feet))
			return false;

		return true;
	}

	public boolean containItem(int slot, int itemId)
	{
		switch(slot)
		{
			case Inventory.PAPERDOLL_CHEST:
				return _chests.contains(itemId);
			case Inventory.PAPERDOLL_LEGS:
				return _legs.contains(itemId);
			case Inventory.PAPERDOLL_HEAD:
				return _head.contains(itemId);
			case Inventory.PAPERDOLL_GLOVES:
				return _gloves.contains(itemId);
			case Inventory.PAPERDOLL_FEET:
				return _feet.contains(itemId);
			default:
				return false;
		}
	}

	public int getEquipedSetPartsCount(Player player)
	{
		Inventory inv = player.getInventory();

		ItemInstance chestItem = inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
		ItemInstance legsItem = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);
		ItemInstance headItem = inv.getPaperdollItem(Inventory.PAPERDOLL_HEAD);
		ItemInstance glovesItem = inv.getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
		ItemInstance feetItem = inv.getPaperdollItem(Inventory.PAPERDOLL_FEET);

		int chest = 0;
		int legs = 0;
		int head = 0;
		int gloves = 0;
		int feet = 0;

		if(chestItem != null)
			chest = chestItem.getItemId();
		if(legsItem != null)
			legs = legsItem.getItemId();
		if(headItem != null)
			head = headItem.getItemId();
		if(glovesItem != null)
			gloves = glovesItem.getItemId();
		if(feetItem != null)
			feet = feetItem.getItemId();

		int result = 0;
		if(!_chests.isEmpty() && _chests.contains(chest))
			result++;
		if(!_legs.isEmpty() && _legs.contains(legs))
			result++;
		if(!_head.isEmpty() && _head.contains(head))
			result++;
		if(!_gloves.isEmpty() && _gloves.contains(gloves))
			result++;
		if(!_feet.isEmpty() && _feet.contains(feet))
			result++;

		return result;
	}

	public List<Skill> getSkills(int partsCount)
	{
		if(_skills.get(partsCount) == null)
			return new ArrayList<Skill>();

		return _skills.get(partsCount);
	}

	public List<Skill> getSkillsToRemove()
	{
		List<Skill> result = new ArrayList<Skill>();
		for(int i : _skills.keys())
		{
			List<Skill> skills = _skills.get(i);
			if(skills != null)
			{
				for(Skill skill : skills)
					result.add(skill);
			}
		}
		return result;
	}

	public List<Skill> getShieldSkills()
	{
		return _shieldSkills;
	}

	public List<Skill> getEnchant6skills()
	{
		return _enchant6skills;
	}

	public List<Skill> getEnchant7skills()
	{
		return _enchant7skills;
	}

	public List<Skill> getEnchant8skills()
	{
		return _enchant8skills;
	}

	public boolean containShield(Player player)
	{
		Inventory inv = player.getInventory();

		ItemInstance shieldItem = inv.getPaperdollItem(Inventory.PAPERDOLL_LHAND);
		if(shieldItem != null && _shield.contains(shieldItem.getItemId()))
			return true;

		return false;
	}

	public boolean containShield(int shield_id)
	{
		if(_shield.isEmpty())
			return false;

		return _shield.contains(shield_id);
	}

	/**
	 * Checks if all parts of set are enchanted to +6 or more
	 * @param player
	 * @return
	 */
	public int getEnchantLevel(Player player)
	{
		// Player don't have full set
		if(!containAll(player))
			return 0;

		Inventory inv = player.getInventory();

		ItemInstance chestItem = inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
		ItemInstance legsItem = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);
		ItemInstance headItem = inv.getPaperdollItem(Inventory.PAPERDOLL_HEAD);
		ItemInstance glovesItem = inv.getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
		ItemInstance feetItem = inv.getPaperdollItem(Inventory.PAPERDOLL_FEET);

		int value = -1;
		if(!_chests.isEmpty())
			value = value > -1 ? Math.min(value, chestItem.getEnchantLevel()) : chestItem.getEnchantLevel();

		if(!_legs.isEmpty())
			value = value > -1 ? Math.min(value, legsItem.getEnchantLevel()) : legsItem.getEnchantLevel();

		if(!_gloves.isEmpty())
			value = value > -1 ? Math.min(value, glovesItem.getEnchantLevel()) : glovesItem.getEnchantLevel();

		if(!_head.isEmpty())
			value = value > -1 ? Math.min(value, headItem.getEnchantLevel()) : headItem.getEnchantLevel();

		if(!_feet.isEmpty())
			value = value > -1 ? Math.min(value, feetItem.getEnchantLevel()) : feetItem.getEnchantLevel();

		return value;
	}

	public int[] getChestIds()
	{
		return _chests.toArray();
	}

	public int[] getLegIds()
	{
		return _legs.toArray();
	}

	public int[] getHeadIds()
	{
		return _head.toArray();
	}

	public int[] getGlovesIds()
	{
		return _gloves.toArray();
	}

	public int[] getFeetIds()
	{
		return _feet.toArray();
	}

	public int[] getShieldIds()
	{
		return _shield.toArray();
	}
}
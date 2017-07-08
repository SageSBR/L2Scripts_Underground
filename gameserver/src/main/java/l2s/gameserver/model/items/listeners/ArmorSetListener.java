package l2s.gameserver.model.items.listeners;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.data.xml.holder.ArmorSetsHolder;
import l2s.gameserver.listener.inventory.OnEquipListener;
import l2s.gameserver.model.ArmorSet;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;

public final class ArmorSetListener implements OnEquipListener
{
	private static final ArmorSetListener _instance = new ArmorSetListener();

	public static ArmorSetListener getInstance()
	{
		return _instance;
	}

	@Override
	public void onEquip(int slot, ItemInstance item, Playable actor)
	{
		if(!item.isEquipable())
			return;

		// checks if there is armorset for chest item that player worns
		List<ArmorSet> armorSets = ArmorSetsHolder.getInstance().getArmorSets(item.getItemId());
		if(armorSets == null || armorSets.isEmpty())
			return;

		Player player = (Player) actor;

		boolean update = false;
		for(ArmorSet armorSet : armorSets)
		{
			// checks if equipped item is part of set
			if(armorSet.containItem(slot, item.getItemId()))
			{
				List<Skill> skills = armorSet.getSkills(armorSet.getEquipedSetPartsCount(player));
				for(Skill skill : skills)
				{
					player.addSkill(skill, false);
					update = true;
				}

				if(armorSet.containAll(player))
				{
					if(armorSet.containShield(player)) // has shield from set
					{
						skills = armorSet.getShieldSkills();
						for(Skill skill : skills)
						{
							player.addSkill(skill, false);
							update = true;
						}
					}

					int enchantLevel = armorSet.getEnchantLevel(player);
					if(enchantLevel >= 6) // has all parts of set enchanted to 6 or more
					{
						skills = armorSet.getEnchant6skills();
						for(Skill skill : skills)
						{
							player.addSkill(skill, false);
							update = true;
						}
					}
					if(enchantLevel >= 7) // has all parts of set enchanted to 7 or more
					{
						skills = armorSet.getEnchant7skills();
						for(Skill skill : skills)
						{
							player.addSkill(skill, false);
							update = true;
						}
					}
					if(enchantLevel >= 8) // has all parts of set enchanted to 8 or more
					{
						skills = armorSet.getEnchant8skills();
						for(Skill skill : skills)
						{
							player.addSkill(skill, false);
							update = true;
						}
					}
					player.setArmorSetEnchant(enchantLevel);
				}
			}
			else if(armorSet.containShield(item.getItemId()) && armorSet.containAll(player))
			{
				List<Skill> skills = armorSet.getShieldSkills();
				for(Skill skill : skills)
				{
					player.addSkill(skill, false);
					update = true;
				}
			}
		}

		if(update)
		{
			player.sendSkillList();
			player.updateStats();
		}
	}

	@Override
	public void onUnequip(int slot, ItemInstance item, Playable actor)
	{
		if(!item.isEquipable())
			return;

		List<ArmorSet> armorSets = ArmorSetsHolder.getInstance().getArmorSets(item.getItemId());
		if(armorSets == null || armorSets.isEmpty())
			return;

		Player player = (Player) actor;

		boolean update = false;
		for(ArmorSet armorSet : armorSets)
		{
			boolean remove = false;
			boolean setPartUneqip = false;
			List<Skill> removeSkillId1 = new ArrayList<Skill>(); // set skill
			List<Skill> removeSkillId2 = new ArrayList<Skill>(); // shield skill
			List<Skill> removeSkillId3 = new ArrayList<Skill>(); // enchant +6 skill
			List<Skill> removeSkillId4 = new ArrayList<Skill>(); // enchant +7 skill
			List<Skill> removeSkillId5 = new ArrayList<Skill>(); // enchant +8 skill

			if(armorSet.containItem(slot, item.getItemId())) // removed part of set
			{
				remove = true;
				setPartUneqip = true;
				removeSkillId1 = armorSet.getSkillsToRemove();
				removeSkillId2 = armorSet.getShieldSkills();
				removeSkillId3 = armorSet.getEnchant6skills();
				removeSkillId4 = armorSet.getEnchant7skills();
				removeSkillId5 = armorSet.getEnchant8skills();
			}
			else if(armorSet.containShield(item.getItemId())) // removed shield
			{
				remove = true;
				removeSkillId2 = armorSet.getShieldSkills();
			}

			if(remove)
			{
				for(Skill skill : removeSkillId1)
				{
					player.removeSkill(skill, false);
					update = true;
				}
				for(Skill skill : removeSkillId2)
				{
					player.removeSkill(skill);
					update = true;
				}
				for(Skill skill : removeSkillId3)
				{
					player.removeSkill(skill);
					update = true;
				}
				for(Skill skill : removeSkillId4)
				{
					player.removeSkill(skill);
					update = true;
				}
				for(Skill skill : removeSkillId5)
				{
					player.removeSkill(skill);
					update = true;
				}
				player.setArmorSetEnchant(0);
			}

			List<Skill> skills = armorSet.getSkills(armorSet.getEquipedSetPartsCount(player));
			for(Skill skill : skills)
			{
				player.addSkill(skill, false);
				update = true;
			}
		}

		if(update)
		{
			player.sendSkillList();
			player.updateStats();
		}
	}
}
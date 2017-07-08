package handler.items;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;

public class BeastShot extends ScriptItemHandler
{
	private static final Skill SOULSHOT_SKILL = SkillHolder.getInstance().getSkill(2033, 1);
	private static final Skill SPIRITSHOT_SKILL = SkillHolder.getInstance().getSkill(2008, 1);
	private static final Skill BLESSED_SPIRITSHOT_SKILL = SkillHolder.getInstance().getSkill(2009, 1);

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;

		Player player = (Player) playable;

		boolean isAutoSoulShot = false;
		if(player.getAutoSoulShot().contains(item.getItemId()))
			isAutoSoulShot = true;

		int deadServitors = 0;
		Servitor[] servitors = player.getServitors();
		if(servitors.length > 0)
		{
			loop: for(int i = 0; i < servitors.length; i++)
			{
				Servitor s = servitors[i];
				if(s.isDead())
				{
					deadServitors++;
					continue;
				}

				int consumption = 0;

				Skill skill = null;
				switch(item.getItemId())
				{
					case 6645:
					case 20332:
					{
						if(s.getChargedSoulshotPower() > 0)
							continue loop;

						consumption = s.getSoulshotConsumeCount();
						if(!player.getInventory().destroyItem(item, consumption))
						{
							player.sendPacket(SystemMsg.YOU_DONT_HAVE_ENOUGH_SOULSHOTS_NEEDED_FOR_A_PETSERVITOR);
							return false;
						}
						skill = s.getAdditionalSSEffect(false, false);
						if(skill == null)
							skill = item.getTemplate().getFirstSkill();
						if(skill == null)
							skill = SOULSHOT_SKILL;
						break;
					}
					case 6646:
					case 20333:
					{
						if(s.getChargedSpiritshotPower() > 0)
							continue loop;

						consumption = s.getSpiritshotConsumeCount();
						if(!player.getInventory().destroyItem(item, consumption))
						{
							player.sendPacket(SystemMsg.YOU_DONT_HAVE_ENOUGH_SPIRITSHOTS_NEEDED_FOR_A_PETSERVITOR);
							return false;
						}
						skill = s.getAdditionalSSEffect(true, false);
						if(skill == null)
							skill = item.getTemplate().getFirstSkill();
						if(skill == null)
							skill = SPIRITSHOT_SKILL;
						break;
					}
					case 6647:
					case 20334:
					{
						if(s.getChargedSpiritshotPower() > 0)
							continue loop;

						consumption = s.getSpiritshotConsumeCount();
						if(!player.getInventory().destroyItem(item, consumption))
						{
							player.sendPacket(SystemMsg.YOU_DONT_HAVE_ENOUGH_SPIRITSHOTS_NEEDED_FOR_A_PETSERVITOR);
							return false;
						}
						skill = s.getAdditionalSSEffect(true, true);
						if(skill == null)
							skill = item.getTemplate().getFirstSkill();
						if(skill == null)
							skill = BLESSED_SPIRITSHOT_SKILL;
						break;
					}
				}

				s.forceUseSkill(skill, s);
			}

			if(deadServitors == servitors.length && !isAutoSoulShot)
			{
				player.sendPacket(SystemMsg.SOULSHOTS_AND_SPIRITSHOTS_ARE_NOT_AVAILABLE_FOR_A_DEAD_PET_OR_SERVITOR);
				return false;
			}
		}
		else if(!isAutoSoulShot)
		{
			player.sendPacket(SystemMsg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
			return false;
		}

		return true;
	}
}
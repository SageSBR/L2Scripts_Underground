package l2s.gameserver.skills.effects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.data.xml.holder.CubicHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.MagicSkillLaunchedPacket;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.CubicTemplate;
import l2s.gameserver.templates.skill.EffectTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VISTALL && pchayka
 * @date  13:17/22.12.2010
 * Каждую секунду задача кубика вызывает выбор скила из списка доступных. Если умение на откате (delay), выбирает следующее и отправляет в исполнение.
 * В обработке умения считается шанс запуска умения (info.getChance()), если скилл не удался - отмена обработки.
 * При успешной активации проходит обработка умения и оно запускается на откат (delay).
 */
public class EffectCubic extends Effect
{
	private static final Logger _log = LoggerFactory.getLogger(EffectCubic.class);

	private final CubicTemplate _template;

	public EffectCubic(Env env, EffectTemplate template)
	{
		super(env, template);

		int cubicId = getTemplate().getParam().getInteger("id", 0);
		int cubicLevel = getTemplate().getParam().getInteger("level", 0);
		if(cubicId > 0 && cubicLevel > 0)
		{
			_template = CubicHolder.getInstance().getTemplate(cubicId, cubicLevel);
			setTimeLeft(getDuration());
		}
		else
		{
			_template = null;
			_log.warn(getClass().getSimpleName() + ": Cannot find cubic template for skill: ID[" + getSkill().getId() + "], LEVEL[" + getSkill().getLevel() + "]!");
		}
	}

	@Override
	public boolean checkCondition()
	{
		if(!getEffected().isPlayer())
			return false;

		if(_template == null)
			return false;

		Player player = getEffected().getPlayer();
		if(player.getCubic(_template.getId()) != null)
			return true;

		int size = (int) player.calcStat(Stats.CUBICS_LIMIT, 1);
		if(player.getCubics().size() >= size)
		{
			if(getEffector() == player)
				player.sendPacket(SystemMsg.CUBIC_SUMMONING_FAILED); //todo un hard code it

			return false;
		}

		return true;
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		Player player = _effected.getPlayer();
		if(player == null)
			return;

		player.addCubic(this);
	}

	@Override
	protected void onExit()
	{
		super.onExit();
		Player player = _effected.getPlayer();
		if(player == null)
			return;

		player.removeCubic(getId());
	}

	@Override
	protected boolean onActionTime()
	{
		if(!getEffected().isPlayer())
			return true;

		Player player = getEffected().getPlayer();
		if(player == null)
			return true;

		for(Map.Entry<Integer, List<CubicTemplate.SkillInfo>> entry : _template.getSkills())
		{
			if(Rnd.chance(entry.getKey())) //TODO: Должен выбирать один из списка, а не перебирать список шансов
			{
				for(CubicTemplate.SkillInfo skillInfo : entry.getValue())
				{
					if(player.isSkillDisabled(skillInfo.getSkill()))
						continue;
					switch(skillInfo.getActionType())
					{
						case ATTACK:
							doAttack(player, skillInfo, _template.getDelay());
							break;
						case BUFF:
							doBuff(player, skillInfo, _template.getDelay());
							break;
						case DEBUFF:
							doDebuff(player, skillInfo, _template.getDelay());
							break;
						case HEAL:
							doHeal(player, skillInfo, _template.getDelay());
							break;
						case MANA:
							doMana(player, skillInfo, _template.getDelay());
							break;
						case CANCEL:
							doCancel(player, skillInfo, _template.getDelay());
							break;
					}
				}
				break;
			}
		}
		return true;
	}

	@Override
	public boolean isHidden()
	{
		return true;
	}

	@Override
	public int getDuration()
	{
		if(super.getTimeLeft() > 0 && super.getTimeLeft() < Integer.MAX_VALUE)
			return super.getTimeLeft();
		return _template.getDuration();
	}

	@Override
	public int getInterval()
	{
		return 1;
	}

	public int getId()
	{
		return _template.getId();
	}

	private static void doHeal(final Player player, CubicTemplate.SkillInfo info, final int delay)
	{
		final Skill skill = info.getSkill();
		Creature target = null;
		if(player.getParty() == null)
		{
			if(!player.isCurrentHpFull() && !player.isDead())
				target = player;
		}
		else
		{
			double currentHp = Integer.MAX_VALUE;
			for(Player member : player.getParty().getPartyMembers())
			{
				if(member == null)
					continue;

				if((info.getSkill().getCastRange() == -1 || player.isInRange(member, info.getSkill().getCastRange())) && !member.isCurrentHpFull() && !member.isDead() && member.getCurrentHp() < currentHp)
				{
					currentHp = member.getCurrentHp();
					target = member;
				}
			}
		}

		if(target == null)
			return;

		int chance = info.getChance((int) target.getCurrentHpPercents());

		if(!Rnd.chance(chance))
			return;

		final Creature aimTarget = target;
		player.broadcastPacket(new MagicSkillUse(player, aimTarget, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), 0));
		player.disableSkill(skill, delay * 1000L);
		ThreadPoolManager.getInstance().schedule(() ->
		{
			final List<Creature> targets = new ArrayList<Creature>(1);
			targets.add(aimTarget);
			player.broadcastPacket(new MagicSkillLaunchedPacket(player.getObjectId(), skill.getDisplayId(), skill.getDisplayLevel(), targets));
			player.callSkill(skill, targets, false);
		}, skill.getHitTime());
	}

	private static void doMana(final Player player, CubicTemplate.SkillInfo info, final int delay)
	{
		final Skill skill = info.getSkill();
		Creature target = null;
		if(player.getParty() == null)
		{
			if(!player.isCurrentMpFull() && !player.isDead())
				target = player;
		}
		else
		{
			double currentMp = Integer.MAX_VALUE;
			for(Player member : player.getParty().getPartyMembers())
			{
				if(member == null)
					continue;

				if((info.getSkill().getCastRange() == -1 || player.isInRange(member, info.getSkill().getCastRange())) && !member.isCurrentMpFull() && !member.isDead() && member.getCurrentMp() < currentMp)
				{
					currentMp = member.getCurrentMp();
					target = member;
				}
			}
		}

		if(target == null)
			return;

		int chance = info.getChance((int) target.getCurrentMpPercents());

		if(!Rnd.chance(chance))
			return;

		final Creature aimTarget = target;
		player.broadcastPacket(new MagicSkillUse(player, aimTarget, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), 0));
		player.disableSkill(skill, delay * 1000L);
		ThreadPoolManager.getInstance().schedule(() ->
		{
			final List<Creature> targets = new ArrayList<Creature>(1);
			targets.add(aimTarget);
			player.broadcastPacket(new MagicSkillLaunchedPacket(player.getObjectId(), skill.getDisplayId(), skill.getDisplayLevel(), targets));
			player.callSkill(skill, targets, false);
		}, skill.getHitTime());
	}

	private static void doAttack(final Player player, final CubicTemplate.SkillInfo info, final int delay)
	{
		if(!Rnd.chance(info.getChance()))
			return;

		final Skill skill = info.getSkill();
		Creature target = null;
		if(player.isInCombat())
		{
			GameObject object = player.getTarget();
			target = object != null && object.isCreature() ? (Creature) object : null;
		}
		if(target == null || target.isDead() || (target.isDoor() && !info.isCanAttackDoor()) || skill.getCastRange() != -1 && !player.isInRangeZ(target, skill.getCastRange()) || !target.isAutoAttackable(player))
			return;
		final Creature aimTarget = target;
		player.broadcastPacket(new MagicSkillUse(player, target, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), 0));
		player.disableSkill(skill, delay * 1000L);
		ThreadPoolManager.getInstance().schedule(() ->
		{
			final List<Creature> targets = new ArrayList<Creature>(1);
			targets.add(aimTarget);

			player.broadcastPacket(new MagicSkillLaunchedPacket(player.getObjectId(), skill.getDisplayId(), skill.getDisplayLevel(), targets));
			player.callSkill(skill, targets, false);

			if(aimTarget.isNpc())
			{
				if(aimTarget.paralizeOnAttack(player))
				{
					if(Config.PARALIZE_ON_RAID_DIFF)
						player.paralizeMe(aimTarget);
				}
				else
				{
					int damage = skill.getEffectPoint() != 0 ? skill.getEffectPoint() : (int) skill.getPower();
					aimTarget.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, player, damage);
				}
			}
		}, skill.getHitTime());
	}

	private static void doBuff(final Player player, final CubicTemplate.SkillInfo info, final int delay)
	{
		if(!Rnd.chance(info.getChance()))
			return;

		final Skill skill = info.getSkill();
		player.broadcastPacket(new MagicSkillUse(player, player, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), 0));
		player.disableSkill(skill, delay * 1000L);
		ThreadPoolManager.getInstance().schedule(() ->
		{
			final List<Creature> targets = new ArrayList<Creature>(1);
			targets.add(player);
			player.broadcastPacket(new MagicSkillLaunchedPacket(player.getObjectId(), skill.getDisplayId(), skill.getDisplayLevel(), targets));
			player.callSkill(skill, targets, false);
		}, skill.getHitTime());
	}

	private static void doDebuff(final Player player, final CubicTemplate.SkillInfo info, final int delay)
	{
		if(!Rnd.chance(info.getChance()))
			return;

		final Skill skill = info.getSkill();
		Creature target = null;
		if(player.isInCombat())
		{
			GameObject object = player.getTarget();
			target = object != null && object.isCreature() ? (Creature) object : null;
		}
		if(target == null || target.isDead() || (target.isDoor() && !info.isCanAttackDoor()) || skill.getCastRange() != -1 && !player.isInRangeZ(target, skill.getCastRange()) || !target.isAutoAttackable(player))
			return;
		final Creature aimTarget = target;
		player.broadcastPacket(new MagicSkillUse(player, target, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), 0));
		player.disableSkill(skill, delay * 1000L);
		ThreadPoolManager.getInstance().schedule(() ->
		{
			final List<Creature> targets = new ArrayList<Creature>(1);
			targets.add(aimTarget);
			player.broadcastPacket(new MagicSkillLaunchedPacket(player.getObjectId(), skill.getDisplayId(), skill.getDisplayLevel(), targets));
			final boolean succ = Formulas.calcSkillSuccess(player, aimTarget, skill, info.getChance());
			if(succ)
				player.callSkill(skill, targets, false);

			if(aimTarget.isNpc())
			{
				if(aimTarget.paralizeOnAttack(player))
				{
					if(Config.PARALIZE_ON_RAID_DIFF)
						player.paralizeMe(aimTarget);
				}
				else
				{
					int damage = skill.getEffectPoint() != 0 ? skill.getEffectPoint() : (int) skill.getPower();
					aimTarget.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, player, damage);
				}
			}
		}, skill.getHitTime());
	}

	private static void doCancel(final Player player, final CubicTemplate.SkillInfo info, final int delay)
	{
		if(!Rnd.chance(info.getChance()))
			return;

		final Skill skill = info.getSkill();
		/*boolean hasDebuff = false;
		for(Effect e : player.getEffectList().getAllEffects())
		{
			if(e != null && e.isOffensive() && e.isCancelable() && !e.getTemplate()._applyOnCaster)
			{
				hasDebuff = true;
				break;
			}	
		}		

		if(!hasDebuff)
			return; wtf is this shit?! let the skill deside if canceled completed or not
*/
		player.broadcastPacket(new MagicSkillUse(player, player, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), 0));
		player.disableSkill(skill, delay * 1000L);
		ThreadPoolManager.getInstance().schedule(() ->
		{
			final List<Creature> targets = new ArrayList<Creature>(1);
			targets.add(player);
			player.broadcastPacket(new MagicSkillLaunchedPacket(player.getObjectId(), skill.getDisplayId(), skill.getDisplayLevel(), targets));
			player.callSkill(skill, targets, false);
		}, skill.getHitTime());
	}
}
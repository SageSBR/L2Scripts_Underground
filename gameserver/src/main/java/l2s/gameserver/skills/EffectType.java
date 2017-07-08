package l2s.gameserver.skills;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.skills.effects.*;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum EffectType
{
	// Основные эффекты
	AddSkills(EffectAddSkills.class, false),
	AgathionResurrect(EffectAgathionRes.class, true),
	AwakenForce(EffectAwakenForce.class, true),
	Betray(EffectBetray.class, true),
	BlessNoblesse(EffectBlessNoblesse.class, true),
	Buff(EffectBuff.class, false),
	Bluff(EffectBluff.class, true),
	DebuffImmunity(EffectDebuffImmunity.class, true),
	DamageBlock(EffectDamageBlock.class, true),
	DistortedSpace(EffectDistortedSpace.class, true),
	CallSkills(EffectCallSkills.class, false),
	ConsumeSoulsOverTime(EffectConsumeSoulsOverTime.class, true),
	Charge(EffectCharge.class, false),
	CharmOfCourage(EffectCharmOfCourage.class, true),
	CPDamPercent(EffectCPDamPercent.class, true),
	Cubic(EffectCubic.class, true),
	DamageHealToEffector(EffectDamageHealToEffector.class, false),
	DamOverTime(EffectDamOverTime.class, false),
	DamOverTimeLethal(EffectDamOverTimeLethal.class, false),
	DestroySummon(EffectDestroySummon.class, true),
	DeathImmunity(EffectDeathImmunity.class, false),
	DeathPenalty(EffectDeathPenalty.class, false),
	Disarm(EffectDisarm.class, true),
	Discord(EffectDiscord.class, true),
	DispelOnHit(EffectDispelOnHit.class, true),
	EffectImmunity(EffectEffectImmunity.class, true),
	Enervation(EffectEnervation.class, false),
	FakeDeath(EffectFakeDeath.class, true),
	Fear(EffectFear.class, true),
	MoveToEffector(EffectMoveToEffector.class, true),
	Grow(EffectGrow.class, false),
	Hate(EffectHate.class, false),
	HealBlock(EffectHealBlock.class, true),
	HPDamPercent(EffectHPDamPercent.class, true),
	HpToOne(EffectHpToOne.class, true),
	IgnoreSkill(EffectIgnoreSkill.class, false),
	Immobilize(EffectImmobilize.class, true),
	Interrupt(EffectInterrupt.class, true),
	Invulnerable(EffectInvulnerable.class, false),
	Invisible(EffectInvisible.class, false),
	LockInventory(EffectLockInventory.class, false),
	CurseOfLifeFlow(EffectCurseOfLifeFlow.class, true),
	Laksis(EffectLaksis.class, true),
	LDManaDamOverTime(EffectLDManaDamOverTime.class, true),
	ManaDamOverTime(EffectManaDamOverTime.class, true),
	Meditation(EffectMeditation.class, false),
	MPDamPercent(EffectMPDamPercent.class, true),
	Mute(EffectMute.class, true),
	MuteAll(EffectMuteAll.class, true),
	Mutation(EffectMutation.class, true),
	MuteAttack(EffectMuteAttack.class, true),
	MutePhisycal(EffectMutePhisycal.class, true),
	NegateMark(EffectNegateMark.class, false),
	Paralyze(EffectParalyze.class, true),
	Petrification(EffectPetrification.class, true),
	RandomHate(EffectRandomHate.class, true),
	Relax(EffectRelax.class, true),
	RemoveTarget(EffectRemoveTarget.class, true),
	Root(EffectRoot.class, true),
	Salvation(EffectSalvation.class, true),
	ServitorShare(EffectServitorShare.class, true),
	ServitorShare2(EffectServitorShare2.class, true),
	SilentMove(EffectSilentMove.class, true),
	Sleep(EffectSleep.class, true),
	Stun(EffectStun.class, true),
	TeleportBlock(EffectBuff.class, true),
	ResurrectBlock(EffectBuff.class, true),
	KnockDown(EffectKnockDown.class, true),
	KnockBack(EffectKnockBack.class, true),
	FlyUp(EffectFlyUp.class, true),
	GetEffects(EffectGetEffects.class, true),
	ThrowHorizontal(EffectThrowHorizontal.class, true),
	ThrowUp(EffectThrowUp.class, true),
	Transformation(EffectTransformation.class, true),
	VisualTransformation(EffectVisualTransformation.class, true),
	TargetableDisable(EffectTargetableDisable.class, true),
	TargetLock(EffectTargetLock.class, true),
	Vitality(EffectBuff.class, true),
	ShadowStep(EffectShadowStep.class, false),

	RestoreCP(EffectRestoreCP.class, false),
	RestoreHP(EffectRestoreHP.class, false),
	RestoreMP(EffectRestoreMP.class, false),

	CPDrain(EffectCPDrain.class, true),
	HPDrain(EffectHPDrain.class, true),
	MPDrain(EffectMPDrain.class, true),

	// Производные от основных эффектов
	Poison(EffectDamOverTime.class, false),
	PoisonLethal(EffectDamOverTimeLethal.class, false),

	AbsorbDamageToEffector(EffectBuff.class, false), // абсорбирует часть дамага к еффектора еффекта
	AbsorbDamageToMp(EffectBuff.class, false), // абсорбирует часть дамага в мп
	AbsorbDamageToSummon(EffectLDManaDamOverTime.class, true), // абсорбирует часть дамага к сумону

	// Offlike Effects
	i_dispel_all(i_dispel_all.class, false),
	i_dispel_by_category(i_dispel_by_category.class, false),
	i_dispel_by_slot(i_dispel_by_slot.class, false),
	i_dispel_by_slot_myself(i_dispel_by_slot.class, false),
	i_dispel_by_slot_probability(i_dispel_by_slot_probability.class, false),
	i_delete_hate(i_delete_hate_of_me.class, true),
	i_delete_hate_of_me(i_delete_hate_of_me.class, true),
	i_elemental_type(i_elemental_type.class, false),
	i_fishing_shot(i_fishing_shot.class, false),
	i_m_attack(i_m_attack.class, false),
	i_my_summon_kill(i_my_summon_kill.class, false),
	i_p_attack(i_p_attack.class, false),
	i_plunder(i_plunder.class, false),
	i_set_skill(i_set_skill.class, false),
	i_soul_shot(i_soul_shot.class, false),
	i_spirit_shot(i_spirit_shot.class, false),
	i_spoil(i_spoil.class, false),
	i_summon_soul_shot(i_soul_shot.class, false),
	i_summon_spirit_shot(i_spirit_shot.class, false),
	p_block_buff_slot(p_block_buff_slot.class, false),
	p_block_chat(p_block_chat.class, true),
	p_dual_cast(p_dual_cast.class, false),
	p_passive(p_passive.class, true),
	t_hp(t_hp.class, false);

	private final Constructor<? extends Effect> _constructor;
	private final boolean _isRaidImmune;

	private EffectType(Class<? extends Effect> clazz, boolean isRaidImmune)
	{
		try
		{
			_constructor = clazz.getConstructor(Env.class, EffectTemplate.class);
		}
		catch(NoSuchMethodException e)
		{
			throw new Error(e);
		}
		_isRaidImmune = isRaidImmune;
	}

	public boolean isRaidImmune()
	{
		return _isRaidImmune;
	}

	public Effect makeEffect(Env env, EffectTemplate template) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		return _constructor.newInstance(env, template);
	}
}
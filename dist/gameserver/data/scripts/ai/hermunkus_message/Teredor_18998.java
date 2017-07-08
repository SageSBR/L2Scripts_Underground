package ai.hermunkus_message;

import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;

import instances.MemoryOfDisaster;

/**
 * @author : Ragnarok
 * @date : 01.04.12  14:28
 */
public class Teredor_18998 extends DefaultAI
{
	private static final int SKILL_ID = 16021;

	public Teredor_18998(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		addTimer(1, 2500);
	}

	@Override
	protected void onEvtTimer(int timer_id, Object arg1, Object arg2)
	{
		super.onEvtTimer(timer_id, arg1, arg2);

		switch(timer_id)
		{
			case 1:
				Skill sk = SkillHolder.getInstance().getSkill(SKILL_ID, 1);
				addTaskBuff(getActor(), sk);
				doTask();
				break;
			case 2:
				Reflection r = getActor().getReflection();
				if(r instanceof MemoryOfDisaster)
					((MemoryOfDisaster) r).spawnWyrm();
				getActor().deleteMe();
				break;
		}
	}

	@Override
	protected void onEvtFinishCasting(Skill skill, Creature target, boolean success)
	{
		if(skill.getId() == SKILL_ID)
		{
			addTimer(2, 2000);
		}
	}
}
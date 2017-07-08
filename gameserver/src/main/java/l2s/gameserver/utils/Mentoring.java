package l2s.gameserver.utils;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import l2s.gameserver.dao.MentoringDAO;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.database.mysql;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.World;
import l2s.gameserver.model.actor.instances.player.Mentee;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.mail.Mail;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Cain
 * Date: 12.04.12
 * Time: 18:22
 */
//TODO: [Bonux] Переписать полностью это говно.
public class Mentoring
{
	public static final TIntIntMap SIGN_OF_TUTOR = new TIntIntHashMap();
	static
	{
		SIGN_OF_TUTOR.put(10, 2);
		SIGN_OF_TUTOR.put(20, 25);
		SIGN_OF_TUTOR.put(30, 45);
		SIGN_OF_TUTOR.put(40, 109);
		SIGN_OF_TUTOR.put(50, 175);
		SIGN_OF_TUTOR.put(51, 179);
		SIGN_OF_TUTOR.put(52, 199);
		SIGN_OF_TUTOR.put(53, 221);
		SIGN_OF_TUTOR.put(54, 243);
		SIGN_OF_TUTOR.put(55, 266);
		SIGN_OF_TUTOR.put(56, 290);
		SIGN_OF_TUTOR.put(57, 315);
		SIGN_OF_TUTOR.put(58, 341);
		SIGN_OF_TUTOR.put(59, 367);
		SIGN_OF_TUTOR.put(60, 395);
		SIGN_OF_TUTOR.put(61, 424);
		SIGN_OF_TUTOR.put(62, 432);
		SIGN_OF_TUTOR.put(63, 461);
		SIGN_OF_TUTOR.put(64, 445);
		SIGN_OF_TUTOR.put(65, 473);
		SIGN_OF_TUTOR.put(66, 488);
		SIGN_OF_TUTOR.put(67, 516);
		SIGN_OF_TUTOR.put(68, 544);
		SIGN_OF_TUTOR.put(69, 573);
		SIGN_OF_TUTOR.put(70, 602);
		SIGN_OF_TUTOR.put(71, 561);
		SIGN_OF_TUTOR.put(72, 589);
		SIGN_OF_TUTOR.put(73, 618);
		SIGN_OF_TUTOR.put(74, 647);
		SIGN_OF_TUTOR.put(75, 676);
		SIGN_OF_TUTOR.put(76, 689);
		SIGN_OF_TUTOR.put(77, 488);
		SIGN_OF_TUTOR.put(78, 514);
		SIGN_OF_TUTOR.put(79, 542);
		SIGN_OF_TUTOR.put(80, 576);
		SIGN_OF_TUTOR.put(81, 726);
		SIGN_OF_TUTOR.put(82, 759);
		SIGN_OF_TUTOR.put(83, 793);
		SIGN_OF_TUTOR.put(84, 829);
		SIGN_OF_TUTOR.put(85, 863);
	}

	private static final int[] EFFECTS_FOR_MENTEE = { 9233, 9227, 9228, 9229, 9230, 9231, 9232, 17082, 17083, 17084 };
	private static final int SKILL_FOR_MENTEE = 9379;
	private static final int[] SKILLS_FOR_MENTOR = { 9376, 9377, 9378 };
	private static final int EFFECT_FOR_MENTOR = 9256;
	private static final int[] EFFECTS_FOR_DEBUFF = { 9233, 9227, 9228, 9229, 9230, 9231, 9232, 9376, 9377, 9378, 9256, 17082, 17083, 17084 };
    private static final int[] EFFECTS_FOR_MENTEE_REPLACEMENT = {11517,11518,11519,11520,11521,11522,11529,11530,11532};
    private static final Logger LOGGER = LoggerFactory.getLogger(Mentoring.class);

    public static void removeMentoring(Player mentor, Player mentee, boolean isNotify)
    {
        if(mentor != null && mentee != null)
        {
            mentor.getMenteeList().remove(mentee.getName(),true,isNotify);
            if(mentor.isOnline())
            {
                mentor.sendPacket(new ExMentorList(mentor));
            }
            mentee.getMenteeList().remove(mentor.getName(),false,isNotify);
            if(mentee.isOnline())
            {
                mentee.sendPacket(new ExMentorList(mentee));
            }
            MentoringDAO.getInstance().delete(mentor.getObjectId(),mentee.getObjectId());
            applyMentoringCond(mentor,true);
            addMentoringSkills(mentor);
            applyMentoringCond(mentee,true);
            addMentoringSkills(mentee);
        }
    }

	public static void applyMentoringCond(Player dependPlayer, boolean login)
	{
		if(dependPlayer == null)
			return;

        // remove alls mentoring buffs
        for(int buff : EFFECTS_FOR_DEBUFF) {
            dependPlayer.getEffectList().stopEffects(buff);
        }
        dependPlayer.getEffectList().stopEffects(EFFECT_FOR_MENTOR);

		if(login) // Чар вошел уже находится в игре.
		{
			if(hasMenteeOnline(dependPlayer))
			{

				if(!dependPlayer.getEffectList().containsEffects(EFFECT_FOR_MENTOR))
				{
					Skill skill = SkillHolder.getInstance().getSkill(EFFECT_FOR_MENTOR, 1);
					if(skill != null)
						skill.getEffects(dependPlayer, dependPlayer); // Баф себе.
				}

				for(Mentee mentee : dependPlayer.getMenteeList().values()) // Баф ученикам.
				{
					Player menteePlayer = World.getPlayer(mentee.getObjectId());
					if(menteePlayer != null)
					{
                        if(!mentee.isMentor()) {
                            if (dependPlayer.getObjectId() != menteePlayer.getObjectId()) {
                                // KIET: Apply mentee effect for base class only
                                if (menteePlayer.getActiveClassId() == menteePlayer.getBaseClassId()) {
                                    // KIET: if mentee has base class at level 85+, remove mentee/mentor relationship
                                    if (menteePlayer.getSubClassList().getBaseSubClass().getLevel() > 84) {
                                        dependPlayer.getMenteeList().remove(menteePlayer.getName(), true, false);
                                        removeEffFromGraduatedMentee(menteePlayer);
                                        addMentoringSkills(menteePlayer);
                                        MentoringDAO.getInstance().delete(dependPlayer.getObjectId(),menteePlayer.getObjectId());
                                    }
                                    else {
                                        for (int effect : EFFECTS_FOR_MENTEE) {
                                            if (!menteePlayer.getEffectList().containsEffects(effect)) {
								Skill skill = SkillHolder.getInstance().getSkill(effect, 1);
								if(skill != null)
									skill.getEffects(menteePlayer, menteePlayer);
							}
						}
					}
				}
                            } else {
                                // KIET: remove self mentoring relationship
                                dependPlayer.getMenteeList().remove(menteePlayer.getName(), true, false);
                                removeEffFromGraduatedMentee(menteePlayer);
                                addMentoringSkills(menteePlayer);
                                MentoringDAO.getInstance().delete(dependPlayer.getObjectId(),menteePlayer.getObjectId());
			}
                        }
                        else {
                            // KIET: remove cross mentoring relationship
                            dependPlayer.getMenteeList().remove(menteePlayer.getName(),false,false);
                            removeEffFromGraduatedMentee(menteePlayer);
                            addMentoringSkills(menteePlayer);
                            MentoringDAO.getInstance().delete(menteePlayer.getObjectId(),dependPlayer.getObjectId());
                        }
					}
				}
			}
			else if(hasMentorOnline(dependPlayer))
				{
                Player mentorPlayer = World.getPlayer(dependPlayer.getMenteeList().getMentor());
                if(dependPlayer.getActiveClassId() == dependPlayer.getBaseClassId()) {
                    // KIET: if mentee has base class at level 85+, remove mentee/mentor relationship
                    if(dependPlayer.getSubClassList().getBaseSubClass().getLevel() > 84) {
                        if(mentorPlayer != null)
					{
                            dependPlayer.getMenteeList().remove(mentorPlayer.getName(), false, false);
                            removeEffFromGraduatedMentee(dependPlayer);
                            MentoringDAO.getInstance().delete(mentorPlayer.getObjectId(),dependPlayer.getObjectId());
                        }
                    }
                    else {
                        for (int effect : EFFECTS_FOR_MENTEE) {
                            if (!dependPlayer.getEffectList().containsEffects(effect)) {
						Skill skill = SkillHolder.getInstance().getSkill(effect, 1);
						if(skill != null)
							skill.getEffects(dependPlayer, dependPlayer);
					}
				}

                    }
                }
				if(mentorPlayer != null)
				{
					if(!mentorPlayer.getEffectList().containsEffects(EFFECT_FOR_MENTOR))
					{
						Skill skill = SkillHolder.getInstance().getSkill(EFFECT_FOR_MENTOR, 1);
						if(skill != null)
							skill.getEffects(mentorPlayer, mentorPlayer);
					}
				}
			}
		}
		else
		// Чар выходит игры или из системы наставничества.
		{
			for(Mentee mentee : dependPlayer.getMenteeList().values())
			{
				Player menteePlayer = World.getPlayer(mentee.getObjectId());
				if(menteePlayer != null)
				{
					if(!menteePlayer.getMenteeList().someOneOnline(false))
					{
						for(int buff : EFFECTS_FOR_DEBUFF)
							menteePlayer.getEffectList().stopEffects(buff);

                        if(!mentee.isMentor())
						{
                            for(int buff : EFFECTS_FOR_MENTEE_REPLACEMENT)
							{
                                if(!menteePlayer.getEffectList().containsEffects(buff))
								{
                                    Skill skill = SkillHolder.getInstance().getSkill(buff, 1);
                                    if(skill != null)
                                        skill.getEffects(menteePlayer, menteePlayer);
                                }
                            }
                        }
                    }
				}
			}
            if(!hasMenteeOnline(dependPlayer))
            {
                dependPlayer.getEffectList().stopEffects(EFFECT_FOR_MENTOR);
            }
		}
        addMentoringSkills(dependPlayer);
	}

    private static boolean hasMentorOnly(Player player){
        if(player != null && player.getMenteeList().size() > 0)
        {
            for(Mentee mentee : player.getMenteeList().values())
            {
                if(!mentee.isMentor())
                    return false;
            }
        }
        return true;
    }

    private static boolean hasMenteeOnly(Player player)
    {
        if(player != null && player.getMenteeList().size() > 0)
        {
            for(Mentee mentee : player.getMenteeList().values())
            {
                if(mentee.isMentor())
                    return false;
            }
        }
        return true;
    }

    private static boolean hasMentorOnline(Player player)
    {
        if(player != null && player.getMenteeList().size() > 0)
        {
            for(Mentee mentee : player.getMenteeList().values())
            {
                if(mentee.isOnline() && mentee.isMentor())
                    return true;
            }
        }
        return false;
    }

    private static boolean hasMenteeOnline(Player player)
    {
        if(player != null && player.getMenteeList().size() > 0 ){
            for(Mentee mentee : player.getMenteeList().values())
            {
                if(mentee.isOnline() && !mentee.isMentor())
                    return true;
            }
        }
        return false;
    }

	public static void removeEffFromGraduatedMentee(Player graduated)
	{
		if(graduated == null)
			return;

		graduated.removeSkillById(SKILL_FOR_MENTEE);
		for(int buff : EFFECTS_FOR_DEBUFF)
			graduated.getEffectList().stopEffects(buff);
	}

	public static void addMentoringSkills(Player mentoringPlayer)
	{
		if(hasMenteeOnline(mentoringPlayer))
		{
			for(int skillId : SKILLS_FOR_MENTOR) // Скиллы для наставника.
			{
				Skill skill = SkillHolder.getInstance().getSkill(skillId, 1);
				if(skill != null) {

				mentoringPlayer.addSkill(skill, true);
				mentoringPlayer.sendSkillList();
			}
		}
		}
		else if(hasMentorOnline(mentoringPlayer))
		{
			Skill skill = SkillHolder.getInstance().getSkill(SKILL_FOR_MENTEE, 1); // Скилл для ученика.
			if(skill == null)
				return;

			mentoringPlayer.addSkill(skill, true);
			mentoringPlayer.sendSkillList();
		}
        else
        {
            for(int skillId : SKILLS_FOR_MENTOR) // Скиллы для наставника.
            {
                Skill skill = SkillHolder.getInstance().getSkill(skillId, 1);
                if(skill != null) {
                    mentoringPlayer.removeSkill(skill, true);
                    mentoringPlayer.sendSkillList();
                }
            }

            Skill skill = SkillHolder.getInstance().getSkill(SKILL_FOR_MENTEE, 1); // Скилл для ученика.
            if(skill != null) {
                mentoringPlayer.removeSkill(skill, true);
                mentoringPlayer.sendSkillList();
            }
        }
	}

	public static void setTimePenalty(int mentorId, long timeTo, long expirationTime)
	{
		Player mentor = World.getPlayer(mentorId);
		if(mentor != null && mentor.isOnline())
			mentor.setVar("mentorPenalty", timeTo, -1);
		else
			mysql.set("REPLACE INTO character_variables (obj_id, name, value, expire_time) VALUES (?,'mentorPenalty',?,?)", mentorId, timeTo, expirationTime);
	}

	public static int getGraduatedMenteesCount(int mentorId)
	{
		Player mentor = World.getPlayer(mentorId);
		if(mentor != null && mentor.isOnline())
			return mentor.getVar("mentees_count") == null ? -1 : Integer.parseInt(mentor.getVar("mentees_count"));
		int value = mysql.simple_get_int_alt("value", "character_variables", "`obj_id`='" + mentorId + "'", "`name`='mentees_count'");
		
		if(value == 0)
			return -1;
		return value;	
	}

	public static void setNewMenteesCount(int mentorId, int count)
	{
		Player mentor = World.getPlayer(mentorId);
		if(mentor != null && mentor.isOnline())
			mentor.setVar("mentees_count", count, -1);
		else
			mysql.set("REPLACE INTO character_variables (obj_id, name, value, expire_time) VALUES (?,'mentees_count',?,?)", mentorId, count, -1);
	}

	public static void unsetMenteesCount(int mentorId)
	{
		Player mentor = World.getPlayer(mentorId);
		if(mentor != null && mentor.isOnline())
			mentor.unsetVar("mentees_count");
		else
			mysql.set("DELETE FROM character_variables WHERE obj_id=? AND name='mentees_count'", mentorId);
	}
	
	public static void sendMentorMail(Player receiver, int itemId, long itemCount)
	{
		if(receiver == null || !receiver.isOnline())
			return;

		Mail mail = new Mail();
		mail.setSenderId(1);
		mail.setSenderName("Mentoring System");
		mail.setReceiverId(receiver.getObjectId());
		mail.setReceiverName(receiver.getName());
		mail.setTopic("Mentoring");
		mail.setBody("Sign of Tutor for Mentor");

		ItemInstance item = ItemFunctions.createItem(itemId);
		item.setLocation(ItemInstance.ItemLocation.MAIL);
		item.setCount(itemCount);
		item.save();

		mail.addAttachment(item);
		mail.setType(Mail.SenderType.MENTOR);
		mail.setUnread(true);
		mail.setExpireTime(720 * 3600 + (int) (System.currentTimeMillis() / 1000L));
		mail.save();

		receiver.sendPacket(ExNoticePostArrived.STATIC_TRUE);
		receiver.sendPacket(new ExUnReadMailCount(receiver));
		receiver.sendPacket(SystemMsg.THE_MAIL_HAS_ARRIVED);
	}
}

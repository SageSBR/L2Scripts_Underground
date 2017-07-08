package quests;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.skills.AbnormalType;

public class _194_SevenSignsMammonsContract extends Quest implements ScriptFile
{
	// NPCs
	private static int Colin = 32571;
	private static int SirGustavAthebaldt = 30760;
	private static int Frog = 32572;
	private static int Tess = 32573;
	private static int Kuta = 32574;
	private static int ClaudiaAthebaldt = 31001;

	// ITEMS
	private static int AthebaldtsIntroduction = 13818;
	private static int FrogKingsBead = 13820;
	private static int GrandmaTessCandyPouch = 13821;
	private static int NativesGlove = 13819;

	public _194_SevenSignsMammonsContract()
	{
		super(false);
		addStartNpc(SirGustavAthebaldt);
		addTalkId(Colin, SirGustavAthebaldt, Frog, Tess, Kuta, ClaudiaAthebaldt);
		addQuestItem(AthebaldtsIntroduction, FrogKingsBead, GrandmaTessCandyPouch, NativesGlove);
		addLevelCheck(79);	
		addQuestCompletedCheck(_193_SevenSignDyingMessage.class);		
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("sirgustavathebaldt_q194_2.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("sirgustavathebaldt_q194_2c.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
			player.startScenePlayer(SceneMovie.SSQ_MAMMONS_CONTRACT);
			return null;
		}
		else if(event.equalsIgnoreCase("sirgustavathebaldt_q194_3.htm"))
		{
			st.setCond(3);
			st.playSound(SOUND_MIDDLE);
			st.giveItems(AthebaldtsIntroduction, 1);
		}
		else if(event.equalsIgnoreCase("colin_q194_3.htm"))
		{
			st.takeItems(AthebaldtsIntroduction, -1);
			st.setCond(4);
		}
		else if(event.equalsIgnoreCase("colin_q194_3a.htm"))
		{
			if(player.isTransformed() || player.isMounted())
			{
				player.sendPacket(SystemMsg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
				return null;
			}
			negateSpeedBuffs(player);
			SkillHolder.getInstance().getSkill(6201, 1).getEffects(npc, player);
		}
		else if(event.equalsIgnoreCase("frog_q194_2.htm"))
		{
			st.setCond(5);
			st.playSound(SOUND_MIDDLE);
			st.giveItems(FrogKingsBead, 1);
		}
		else if(event.equalsIgnoreCase("colin_q194_5.htm"))
		{
			st.setCond(6);
			st.takeItems(FrogKingsBead, -1);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("colin_q194_6.htm"))
		{
			if(player.isTransformed() || player.isMounted())
			{
				player.sendPacket(SystemMsg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
				return null;
			}
			st.setCond(7);
			st.playSound(SOUND_MIDDLE);
			negateSpeedBuffs(player);
			SkillHolder.getInstance().getSkill(6202, 1).getEffects(player, player);
		}
		else if(event.equalsIgnoreCase("tess_q194_2.htm"))
		{
			st.setCond(8);
			st.giveItems(GrandmaTessCandyPouch, 1);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("colin_q194_8.htm"))
		{
			st.setCond(9);
			st.takeItems(GrandmaTessCandyPouch, -1);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("colin_q194_9.htm"))
		{
			if(player.isTransformed() || player.isMounted())
			{
				player.sendPacket(SystemMsg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
				return null;
			}
			st.setCond(10);
			st.playSound(SOUND_MIDDLE);
			negateSpeedBuffs(player);
			SkillHolder.getInstance().getSkill(6203, 1).getEffects(player, player);
		}
		else if(event.equalsIgnoreCase("kuta_q194_2.htm"))
		{
			st.setCond(11);
			st.giveItems(NativesGlove, 1);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("colin_q194_10a.htm"))
		{
			st.setCond(12);
			st.takeItems(NativesGlove, -1);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("claudiaathebaldt_q194_2.htm"))
		{
			if(player.isBaseClassActive())
			{
				st.addExpAndSp(10000000, 2500000);
				st.setState(COMPLETED);
				st.exitCurrentQuest(false);
				st.playSound(SOUND_FINISH);
			}
			else
				return "subclass_forbidden.htm";
		}
		else if(event.equalsIgnoreCase("colin_q194_11a.htm"))
		{
			if(player.isTransformed() || player.isMounted())
			{
				player.sendPacket(SystemMsg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
				return null;
			}
			negateSpeedBuffs(player);
			SkillHolder.getInstance().getSkill(6201, 1).getEffects(player, player);
		}
		else if(event.equalsIgnoreCase("colin_q194_12a.htm"))
		{
			if(player.isTransformed() || player.isMounted())
			{
				player.sendPacket(SystemMsg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
				return null;
			}
			negateSpeedBuffs(player);
			SkillHolder.getInstance().getSkill(6202, 1).getEffects(player, player);
		}
		else if(event.equalsIgnoreCase("colin_q194_13a.htm"))
		{
			if(player.isTransformed() || player.isMounted())
			{
				player.sendPacket(SystemMsg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
				return null;
			}
			negateSpeedBuffs(player);
			SkillHolder.getInstance().getSkill(6203, 1).getEffects(player, player);
		}
		else if(event.equalsIgnoreCase("colin_q194_0c.htm"))
			negateTransformations(player);
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		Player player = st.getPlayer();
		String htmltext = "noquest";
		if(!player.isBaseClassActive())
			return "subclass_forbidden.htm";
		if(npcId == SirGustavAthebaldt)
		{
			QuestState qs = player.getQuestState(_193_SevenSignDyingMessage.class);
			if(cond == 0 && player.getLevel() >= 79 && qs != null && qs.isCompleted())
				htmltext = "sirgustavathebaldt_q194_1.htm";
			else if(cond == 1)
				htmltext = "sirgustavathebaldt_q194_2b.htm";
			else if(cond == 2)
				htmltext = "sirgustavathebaldt_q194_2c.htm";
			else if(cond == 3)
			{
				if(st.getQuestItemsCount(AthebaldtsIntroduction) < 1)
					st.giveItems(AthebaldtsIntroduction, 1);
				htmltext = "sirgustavathebaldt_q194_4.htm";
			}
			else
			{
				htmltext = "sirgustavathebaldt_q194_0.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if(npcId == Colin)
		{
			if(cond == 3)
			{
				if(st.getQuestItemsCount(AthebaldtsIntroduction) > 0)
					htmltext = "colin_q194_1.htm";
				else
					htmltext = "colin_q194_0b.htm";
			}
			else if(cond == 5)
				htmltext = "colin_q194_4.htm";
			else if(cond == 6)
				htmltext = "colin_q194_5.htm";
			else if(cond == 8)
				htmltext = "colin_q194_7.htm";
			else if(cond == 9)
				htmltext = "colin_q194_8.htm";
			else if(cond == 11)
				htmltext = "colin_q194_10.htm";
			else if(cond == 12)
				htmltext = "colin_q194_14.htm";

				//if player has lost transformation - sarcasticly giving him it again
			else if(cond == 4 && !player.isTransformed())
				htmltext = "colin_q194_11.htm";
			else if(cond == 7 && !player.isTransformed())
				htmltext = "colin_q194_12.htm";
			else if(cond == 10 && !player.isTransformed())
				htmltext = "colin_q194_13.htm";
			else if((cond == 4 || cond == 7 || cond == 10) && player.isTransformed())
				htmltext = "colin_q194_0a.htm";
		}
		else if(npcId == Frog)
		{
			if(cond == 4 && player.getTransformId() == 111)
				htmltext = "frog_q194_1.htm";
			else if(cond == 5 && player.getTransformId() == 111)
				htmltext = "frog_q194_4.htm";
			else
				htmltext = "frog_q194_3.htm";
		}
		else if(npcId == Tess)
		{
			if(cond == 7 && player.getTransformId() == 112)
				htmltext = "tess_q194_1.htm";
			else if(cond == 8 && player.getTransformId() == 112)
				htmltext = "tess_q194_3.htm";
			else
				htmltext = "tess_q194_0.htm";
		}
		else if(npcId == Kuta)
		{
			if(cond == 10 && player.getTransformId() == 101)
				htmltext = "kuta_q194_1.htm";
			else if(cond == 11 && player.getTransformId() == 101)
				htmltext = "kuta_q194_3.htm";
			else
				htmltext = "kuta_q194_0.htm";
		}
		else if(npcId == ClaudiaAthebaldt)
			if(cond == 12)
				htmltext = "claudiaathebaldt_q194_1.htm";
			else
				htmltext = "claudiaathebaldt_q194_0.htm";
		return htmltext;
	}

	private void negateSpeedBuffs(Player p)
	{
		for(Effect e : p.getEffectList().getEffects())
			if(e.checkAbnormalType(AbnormalType.speed_up) && !e.isOffensive())
				e.exit();
	}

	private void negateTransformations(Player p)
	{
		p.setTransform(null);
	}

	@Override
	public void onLoad()
	{
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}
}
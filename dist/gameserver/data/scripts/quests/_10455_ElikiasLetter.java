package quests;

import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.listener.actor.player.OnTeleportedListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.templates.item.ItemTemplate;

/**
 * @author Bonux
**/
public class _10455_ElikiasLetter extends Quest implements ScriptFile, OnPlayerEnterListener
{
	private static class TeleportedListener implements OnTeleportedListener
	{
		@Override
		public void onTeleported(Player player)
		{
			QuestState st = player.getQuestState(_10455_ElikiasLetter.class);
			if(st == null)
				return;

			if(st.getCond() == 1 && st.getInt(VIDEO_VAR) == 0 && player.isInZone(VIDEO_ZONE))
			{
				st.set(VIDEO_VAR, 1);
				player.startScenePlayer(SceneMovie.SCENE_HELLBOUND);
				player.removeListener(TELEPORT_LISTENER);
			}
		}
	}

	// NPC's
	private static final int ELRIKIA_VERDURE_ELDER = 31620;	// Элрика Старейшина Леса
	private static final int DEVIANNE_TRUTH_SEEKER = 31590;	// Девиан Искатель Правды
	private static final int LEONA_BLACKBIRD_FIRE_DRAGON_BRIDE = 31595;	// Леона Блэкберд Невеста Дракона Огня

	// Item's
	private static final int ELRIKIAS_LETTER = 37765;	// Письмо Элрики

	// Rewards
	private static final long REWARD_ADENA = 32962;	// Награда аден.
	private static final long REWARD_EXP = 3859143;	// Награда EXP.
	private static final int REWARD_SP = 14816;	// Награда SP.

	// Other
	private static final String VIDEO_ZONE = "[village_refugees]";
	private static final String VIDEO_VAR = "@video_showed";
	private static final TeleportedListener TELEPORT_LISTENER = new TeleportedListener();

	public _10455_ElikiasLetter()
	{
		super(false);

		addStartNpc(ELRIKIA_VERDURE_ELDER);
		addTalkId(ELRIKIA_VERDURE_ELDER, DEVIANNE_TRUTH_SEEKER, LEONA_BLACKBIRD_FIRE_DRAGON_BRIDE);

		addQuestItem(ELRIKIAS_LETTER);

		addLevelCheck(99);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		final int cond = st.getCond();

		String htmltext = event;

		if(htmltext.equalsIgnoreCase("elrikia_q10455_04.htm"))
		{
			if(cond == 0)
			{
				st.giveItems(ELRIKIAS_LETTER, 1);
				st.setCond(1);
				st.setState(STARTED);
				st.playSound(SOUND_ACCEPT);
				st.getPlayer().addListener(TELEPORT_LISTENER);
				Functions.npcSay(npc, st.getPlayer(), NpcString.YOU_MUST_ACTIVATE_THE_WARP_GATE_BEHIND_ME_IN_ORDER_TO_TELEPORT_TO_HELLBOUND);
			}
		}
		else if(htmltext.equalsIgnoreCase("devianne_q10455_02.htm"))
		{
			if(cond == 1)
			{
				st.setCond(2);
				st.playSound(SOUND_MIDDLE);
				st.getPlayer().removeListener(TELEPORT_LISTENER);
			}
		}
		else if(htmltext.equalsIgnoreCase("leona_q10455_03.htm"))
		{
			if(cond == 2)
			{
				st.giveItems(ItemTemplate.ITEM_ID_ADENA, REWARD_ADENA);
				st.takeItems(ELRIKIAS_LETTER, -1);
				st.addExpAndSp(REWARD_EXP, REWARD_SP);
				st.setState(COMPLETED);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
				st.getPlayer().removeListener(TELEPORT_LISTENER);
				Functions.npcSay(npc, st.getPlayer(), NpcString.HAVE_YOU_MADE_PREPARATIONS_FOR_THE_MISSION_THERE_ISNT_MUCH_TIME);
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		final int npcId = npc.getNpcId();
		final int cond = st.getCond();

		String htmltext = "noquest";

		if(npcId == ELRIKIA_VERDURE_ELDER)
		{
			if(cond == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					htmltext = "elrikia_q10455_01.htm";
				else
					htmltext = "elrikia_q10455_00.htm";
			}
			else if(cond == 1)
				htmltext = "elrikia_q10455_05.htm";
		}
		else if(npcId == DEVIANNE_TRUTH_SEEKER)
		{
			if(cond == 1)
				htmltext = "devianne_q10455_01.htm";
			else if(cond == 2)
				htmltext = "devianne_q10455_03.htm";
		}
		else if(npcId == LEONA_BLACKBIRD_FIRE_DRAGON_BRIDE)
		{
			if(cond == 2)
				htmltext = "leona_q10455_01.htm";
		}
		return htmltext;
	}

	@Override
	public void onPlayerEnter(Player player)
	{
		QuestState st = player.getQuestState(getClass());
		if(st == null)
			return;

		if(st.getCond() == 1 && st.getInt(VIDEO_VAR) == 0)
			player.addListener(TELEPORT_LISTENER);
	}

	@Override
	public void onLoad()
	{
		//
	}

	@Override
	public void onReload()
	{
		//
	}

	@Override
	public void onShutdown()
	{
		//
	}
}
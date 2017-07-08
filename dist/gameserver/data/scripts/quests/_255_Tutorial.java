package quests;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import l2s.gameserver.instancemanager.QuestManager;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.UsmVideo;
import l2s.gameserver.network.l2.s2c.ExCallToChangeClass;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.TutorialCloseHtmlPacket;
import l2s.gameserver.network.l2.s2c.TutorialShowHtmlPacket;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @reworked to Lindvior by Bonux
**/
public class _255_Tutorial extends Quest implements ScriptFile
{
	// table for Question Mark Clicked (4) 1st class transfer [raceId, html]
	public final TIntObjectMap<String> QMCc1 = new TIntObjectHashMap<String>();
	// table for Question Mark Clicked (5) 2nd class transfer [raceId, html]
	public final TIntObjectMap<String> QMCc2 = new TIntObjectHashMap<String>();
	// table for Question Mark Clicked (6) 3rd class transfer [raceId, html]
	public final TIntObjectMap<String> QMCc3 = new TIntObjectHashMap<String>();

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

	public _255_Tutorial()
	{
		super(false);

		QMCc1.put(0, "tutorial_1st_ct_human.htm");
		QMCc1.put(1, "tutorial_1st_ct_elf.htm");
		QMCc1.put(2, "tutorial_1st_ct_dark_elf.htm");
		QMCc1.put(3, "tutorial_1st_ct_orc.htm");
		QMCc1.put(4, "tutorial_1st_ct_dwarf.htm");
		QMCc1.put(5, "tutorial_1st_ct_kamael.htm");

		QMCc2.put(0, "tutorial_2nd_ct_human.htm");
		QMCc2.put(1, "tutorial_2nd_ct_elf.htm");
		QMCc2.put(2, "tutorial_2nd_ct_dark_elf.htm");
		QMCc2.put(3, "tutorial_2nd_ct_orc.htm");
		QMCc2.put(4, "tutorial_2nd_ct_dwarf.htm");
		QMCc2.put(5, "tutorial_2nd_ct_kamael.htm");

		QMCc3.put(0, "tutorial_3rd_ct_human.htm");
		QMCc3.put(1, "tutorial_3rd_ct_elf.htm");
		QMCc3.put(2, "tutorial_3rd_ct_dark_elf.htm");
		QMCc3.put(3, "tutorial_3rd_ct_orc.htm");
		QMCc3.put(4, "tutorial_3rd_ct_dwarf.htm");
		QMCc3.put(5, "tutorial_3rd_ct_kamael.htm");
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		if(player == null)
			return null;

		String html = "";

		// Вход в мир
		if(event.startsWith("UC"))
		{
			if(st.getState() == CREATED)
				st.setState(STARTED);

			if(st.getInt("intro_ert_video") == 0)
			{
				st.set("intro_ert_video", 1);
				if(player.getRace() == Race.ERTHEIA)
					player.sendPacket(UsmVideo.ERTHEIA.packet(player));
				else
					player.sendPacket(UsmVideo.HEROES.packet(player));
			}

			int level = player.getLevel();
			if(level < 6)
			{
				int uc = st.getInt("uc_memo");
				if(uc == 0)
				{
					st.startQuestTimer("QT", 10000);
					st.set("ex_state", "1");
				}
				else if(uc == 1)
				{
					st.showQuestionMark(1);
					st.playTutorialVoice("tutorial_voice_006");
					st.playSound(SOUND_TUTORIAL);
				}
			}
			else if(level >= 18 && checkQuestionMarkCondition(st, 4))
			{
				st.showQuestionMark(4);
				st.playSound(SOUND_TUTORIAL);
			}
			else if(level == 28 && checkQuestionMarkCondition(st, 8))
			{
				st.showQuestionMark(8);
				st.playSound(SOUND_TUTORIAL);
			}
			else if(level >= 38 && checkQuestionMarkCondition(st, 5))
			{
				st.showQuestionMark(5);
				st.playSound(SOUND_TUTORIAL);
			}
			else if(level == 43 && checkQuestionMarkCondition(st, 8))
			{
				st.showQuestionMark(8);
				st.playSound(SOUND_TUTORIAL);
			}
			else if(level == 49 && checkQuestionMarkCondition(st, 8))
			{
				st.showQuestionMark(8);
				st.playSound(SOUND_TUTORIAL);
			}
			else if(level == 58 && checkQuestionMarkCondition(st, 8))
			{
				st.showQuestionMark(8);
				st.playSound(SOUND_TUTORIAL);
			}
			else if(level == 61 && checkQuestionMarkCondition(st, 8))
			{
				st.showQuestionMark(8);
				st.playSound(SOUND_TUTORIAL);
			}
			else if(level == 68 && checkQuestionMarkCondition(st, 8))
			{
				st.showQuestionMark(8);
				st.playSound(SOUND_TUTORIAL);
			}
			else if(level == 73 && checkQuestionMarkCondition(st, 8))
			{
				st.showQuestionMark(8);
				st.playSound(SOUND_TUTORIAL);
			}
			else if(level >= 76 && checkQuestionMarkCondition(st, 6))
			{
				st.showQuestionMark(6);
				st.playSound(SOUND_TUTORIAL);
			}
			else if(level >= 76 && checkQuestionMarkCondition(st, 6))
			{
				st.showQuestionMark(6);
				st.playSound(SOUND_TUTORIAL);
				if(player.getClassId().isMage())
					player.sendPacket(new ExShowScreenMessage(NpcString.MAGISTER_AYANTHE_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				else
					player.sendPacket(new ExShowScreenMessage(NpcString.MASTER_KATALIN_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
			}
			else if(level >= 76 && checkQuestionMarkCondition(st, 22) && !checkQuestionMarkCondition(st, 6))
			{
				player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ2, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				Quest quest = QuestManager.getQuest2("_10798_LettersfromtheQueenDragonValley");
				QuestState qs10798 = player.getQuestState(quest.getName());
				if(qs10798 == null)
					qs10798 = quest.newQuestState(player, Quest.STARTED);
				qs10798.setState(Quest.STARTED);
				qs10798.setCond(1);
				st.playSound(SOUND_TUTORIAL);
				st.showQuestionMark(22);
				st.giveItems(39586, 1, false);
			}
			else if(level == 79 && checkQuestionMarkCondition(st, 8))
			{
				st.showQuestionMark(8);
				st.playSound(SOUND_TUTORIAL);
			}
  			else if(level >= 81 && checkQuestionMarkCondition(st, 24)) //Kerkopus
			{
				st.showQuestionMark(24);
				st.playSound(SOUND_TUTORIAL);
				player.sendPacket(new ExShowScreenMessage(NpcString.KEKROPUS_LETTER_HAS_ARRIVEDNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
			}
			else if(level >= 85 && checkQuestionMarkCondition(st, 10))
			{
				st.showQuestionMark(10);
				st.playSound(SOUND_TUTORIAL);
			}
			else if(level >= 85 && checkQuestionMarkCondition(st, 11))
			{
				st.showQuestionMark(11);
				st.playSound(SOUND_TUTORIAL);
				if(player.getClassId().isMage())
					player.sendPacket(new ExShowScreenMessage(NpcString.MAGISTER_AYANTHE_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				else
					player.sendPacket(new ExShowScreenMessage(NpcString.MASTER_KATALIN_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
			}
			else if(level >= 85 && checkQuestionMarkCondition(st, 12))
			{
				st.showQuestionMark(12);
				st.playSound(SOUND_TUTORIAL);
				player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
			}

			checkHermenkusMsg(st);
		}

		// Обработка таймера QT
		else if(event.startsWith("QT"))
		{
			int exState = st.getInt("ex_state");
			if(exState == 1)
			{
				if(player.getRace() == Race.ERTHEIA)
				{
					if(st.getInt("@queen_called") == 0)
					{
						for(NpcInstance tempNpc : player.getAroundNpc(2000, 500))
						{
							if(tempNpc.getNpcId() == 33931)
							{
								st.set("@queen_called", 1);
								player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_SERENITY_IS_CAUSING_YOU, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
							}
						}
					}
					html = "tutorial_start_e.htm";
				}
				else
					html = "tutorial_start.htm";

				st.set("ex_state", "2");
				st.cancelQuestTimer("QT");
				st.startQuestTimer("QT", 30000);
			}
			else if(exState == 2)
			{
				st.playTutorialVoice("tutorial_voice_002");
				st.set("ex_state", "0");
			}
			else if(exState == 3)
			{
				st.playTutorialVoice("tutorial_voice_008");
				st.set("ex_state", "-1");
			}
		}

		// Tutorial close
		else if(event.startsWith("TE"))
		{
			st.cancelQuestTimer("TE");
			int event_id = 0;
			if(!event.equalsIgnoreCase("TE"))
				event_id = Integer.valueOf(event.substring(2));
			if(event_id == 0) // Закрыть окно.
				player.sendPacket(TutorialCloseHtmlPacket.STATIC);
			else if(event_id == 1) // Способ перемещения.
			{
				player.sendPacket(TutorialCloseHtmlPacket.STATIC);
				st.playTutorialVoice("tutorial_voice_006");
				st.showQuestionMark(1);
				st.playSound(SOUND_TUTORIAL);
				st.startQuestTimer("QT", 30000);
				st.set("ex_state", "3");
			}
			else if(event_id == 2) // Передвижение.
			{
				st.playTutorialVoice("tutorial_voice_003");
				html = "tutorial_move.htm";
				st.onTutorialClientEvent(1);
				st.set("ex_state", "-1");
			}
			else if(event_id == 3) // Выйти из режима обучения (Перемещение).
			{
				html = "tutorial_move_exit.htm";
				st.onTutorialClientEvent(0);
			}
			else if(event_id == 4)	// Телепорт к Королеве Артеас Сирении (Бенон)
			{
				player.sendPacket(TutorialCloseHtmlPacket.STATIC);
				player.teleToLocation(-80565, 251763, -3080, ReflectionManager.DEFAULT);
			}
			else if(event_id == 5)	// Телепорт к Магистру Арис (Бенон)
			{
				player.sendPacket(TutorialCloseHtmlPacket.STATIC);
				player.teleToLocation(-82139, 249852, -3360, ReflectionManager.DEFAULT);
			}
			else if(event_id == 6)	// Телепорт в Глудин, старт квеста 10755/10760
			{
				player.sendPacket(TutorialCloseHtmlPacket.STATIC);
				player.teleToLocation(-80712, 149992, -3069, ReflectionManager.DEFAULT);
				st.takeItems(39486, 1);
			}
			else if(event_id == 7)	// Телепорт в Дион, старт квеста 10769/10774
			{
				player.sendPacket(TutorialCloseHtmlPacket.STATIC);
				player.teleToLocation(15784, 142965, -2731, ReflectionManager.DEFAULT);
				st.takeItems(39595, 1);
			}
			else if(event_id == 8)	// Телепорт в Орен, старт квеста 10779/10393
			{
				player.sendPacket(TutorialCloseHtmlPacket.STATIC);
				if (player.getClassId().isOfRace(Race.ERTHEIA))
				{
					player.teleToLocation(83633, 53064, -1456, ReflectionManager.DEFAULT);
					st.takeItems(39574, 1);
				}
				else
				{
					player.teleToLocation(83656, 55528, -1537, ReflectionManager.DEFAULT);
					st.takeItems(37113, 1);
				}
			}
			else if(event_id == 9)	// Телепорт в Аден, старт квеста 10782/10785/10401
			{
				if (player.getClassId().isOfRace(Race.ERTHEIA))
				{
					player.sendPacket(TutorialCloseHtmlPacket.STATIC);
					player.teleToLocation(147448, 22760, -2017, ReflectionManager.DEFAULT);
					st.takeItems(39576, 1);
				}
				else
				{
					player.sendPacket(TutorialCloseHtmlPacket.STATIC);
					player.teleToLocation(147632, 24664, -1991, ReflectionManager.DEFAULT);
					st.takeItems(37115, 1);
					st.takeItems(37116, 1);
				}
			}
			else if(event_id == 10)	// Телепорт в Руну, старт квеста 10789/10792/10408/10411
			{
				if (player.getClassId().isOfRace(Race.ERTHEIA))
				{
					player.sendPacket(TutorialCloseHtmlPacket.STATIC);
					player.teleToLocation(36563, -49178, -1128, ReflectionManager.DEFAULT);
					st.takeItems(39582, 1);
				}
				else
				{
					player.sendPacket(TutorialCloseHtmlPacket.STATIC);
					player.teleToLocation(42808, -48024, -822, ReflectionManager.DEFAULT);
					st.takeItems(37117, 1);
				}
			}
			else if(event_id == 11)	// Телепорт в Годдард, старт квеста 10795/10414/10415/10419/10424
			{
				if (player.getClassId().isOfRace(Race.ERTHEIA))
				{
					player.sendPacket(TutorialCloseHtmlPacket.STATIC);
					player.teleToLocation(147705, -53066, -2731, ReflectionManager.DEFAULT);
					st.takeItems(39582, 1);
				}
				else
				{
					player.sendPacket(TutorialCloseHtmlPacket.STATIC);
					player.teleToLocation(147560, -56424, -2806, ReflectionManager.DEFAULT);
					st.takeItems(37117, 1);
					st.takeItems(37119, 1);
				}
			}
			else if(event_id == 12)	// Телепорт в Гиран, старт квеста 10798
			{
				player.sendPacket(TutorialCloseHtmlPacket.STATIC);
				player.teleToLocation(86733, 148616, -3400, ReflectionManager.DEFAULT);
				st.takeItems(39586, 1);
			}
			else if(event_id == 13)	// Телепорт в Хейн, старт квеста 10439
			{
				player.sendPacket(TutorialCloseHtmlPacket.STATIC);
				player.teleToLocation(108520, 221704, -3623, ReflectionManager.DEFAULT);
				st.takeItems(37127, 1);
			}
			else if(event_id == 14)	// Телепорт в Орен, старт квеста 10397/10430/10433/10436
			{
				player.sendPacket(TutorialCloseHtmlPacket.STATIC);
				player.teleToLocation(80872, 56040, -1585, ReflectionManager.DEFAULT);
				st.takeItems(37113, 1);
				st.takeItems(37114, 1);
			}
			else if(event_id == 15)	// Телепорт в Годдард, старт квеста 10424/10419
			{
				st.showQuestionMark(23);
				player.sendPacket(TutorialCloseHtmlPacket.STATIC);
			}
			else if(event_id == 16)	// Телепорт в Шутгард, старт квеста 10430
			{
				player.sendPacket(TutorialCloseHtmlPacket.STATIC);
				player.teleToLocation(85976, -142328, -1365, ReflectionManager.DEFAULT);
				st.takeItems(37123, 1);
			}

		}

		// Client Event
		else if(event.startsWith("CE"))
		{
			int event_id = Integer.valueOf(event.substring(2));
			if(event_id == 1 && player.getLevel() < 6) // Способ перемещения.
			{
				if(player.getRace() == Race.ERTHEIA)
					html = "tutorial_way_to_move_e.htm";
				else
					html = "tutorial_way_to_move.htm";

				st.playSound(SOUND_TUTORIAL);
				st.playTutorialVoice("ItemSound.quest_tutorial");
				st.set("uc_memo", "1");
				st.set("ex_state", "-1");
			}
			else if(event_id == 100) // Смена класса.
				checkHermenkusMsg(st);
			else if(event_id == 200 && player.getLevel() < 10 && st.getInt("die") == 0) // Смерть.
			{
				st.playTutorialVoice("tutorial_voice_016");
				st.playSound(SOUND_TUTORIAL);
				st.set("die", "1");
				st.showQuestionMark(7);
				st.onTutorialClientEvent(0);
			}
			else if(event_id == 300) // Повышение уровня.
			{
				final int level = player.getLevel();
				if(level >= 40 && st.getInt("advent_book") == 0) // Выдаем книгу путишественника.
				{
					if(!st.haveQuestItem(32777))
						st.giveItems(32777, 1);
					st.set("advent_book", 1);
				}

				if(level >= 85 && st.getInt("awake_book") == 0) // Выдаем книгу перерождения.
				{
					if(!st.haveQuestItem(32778))
						st.giveItems(32778, 1);
					st.set("awake_book", 1);
				}

				if(level == 10 && checkQuestionMarkCondition(st, 2)) // О штрафе при смерте.
				{
					if(st.getInt("lvl") < 10)
					{
						// st.playTutorialVoice("tutorial_voice_???");
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "10");
						st.showQuestionMark(2);
					}
				}
				else if(level == 15 && checkQuestionMarkCondition(st, 3)) // О квесте на волка.
				{
					if(st.getInt("lvl") < 15)
					{
						// st.playTutorialVoice("tutorial_voice_???");
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "15");
						st.showQuestionMark(3);
					}
				}
				else if(level >= 18 && checkQuestionMarkCondition(st, 4)) // О Квесте на 1ю професиию.
				{
					if(st.getInt("lvl") < 18)
					{
						// st.playTutorialVoice("tutorial_voice_???");
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "18");
						st.showQuestionMark(4);
					}
				}
				else if(level >= 20 && player.getClassId().isOfRace(Race.ERTHEIA) && checkQuestionMarkCondition(st, 13)) // Квест 10755.
				{
					if(st.getInt("lvl") < 20)
					{
						player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ2, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
						Quest quest = QuestManager.getQuest2("_10755_LettersfromtheQueen");
						QuestState qs10755 = player.getQuestState(quest.getName());
						if(qs10755 == null)
							qs10755 = quest.newQuestState(player, Quest.STARTED);
						qs10755.setState(Quest.STARTED);
						qs10755.setCond(1);
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "20");
						st.showQuestionMark(13);
						st.giveItems(39486, 1, false);
					}
				}
				else if(level == 28 && checkQuestionMarkCondition(st, 8)) // О камалоке 28-го уровня.
				{
					if(st.getInt("lvl") < 28)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "28");
						st.showQuestionMark(8);
					}
				}
				else if(level >= 30 && player.getClassId().isOfRace(Race.ERTHEIA) && checkQuestionMarkCondition(st, 14)) // Квест 10760.
				{
					if(st.getInt("lvl") < 30)
					{
						player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ2, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
						st.playSound(SOUND_TUTORIAL);
						Quest quest = QuestManager.getQuest2("_10760_LettersfromtheQueenOrcBarracks");
						QuestState qs10760 = player.getQuestState(quest.getName());
						if(qs10760 == null)
							qs10760 = quest.newQuestState(player, Quest.STARTED);
						qs10760.setState(Quest.STARTED);
						qs10760.setCond(1);
						st.set("lvl", "30");
						st.giveItems(39486, 1, false);
						st.showQuestionMark(14);
					}
				}
				else if(level >= 38 && checkQuestionMarkCondition(st, 5)) // О Квесте на 2ю професиию.
				{
					if(st.getInt("lvl") < 38)
					{
						// st.playTutorialVoice("tutorial_voice_???");
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "38");
						st.showQuestionMark(5);
					}
				}
				else if(level >= 38 && checkQuestionMarkCondition(st, 5)) // О 1-м Освобождении Артей.
				{
					if(st.getInt("lvl") < 38)
					{
						// st.playTutorialVoice("tutorial_voice_???");
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "38");
						st.showQuestionMark(5);
						player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
					}
				}
				else if(level >= 40 && checkQuestionMarkCondition(st, 15)) // Квест 10769/10390
				{
					if(st.getInt("lvl") < 40)
					{
						if(player.getClassId().isOfRace(Race.ERTHEIA))
						{
							player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ2, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
							st.playSound(SOUND_TUTORIAL);
							Quest quest = QuestManager.getQuest2("_10769_Lettersfromth_QueenCrumaTowerPart1");
							QuestState qs10769 = player.getQuestState(quest.getName());
							if(qs10769 == null)
								qs10769 = quest.newQuestState(player, Quest.STARTED);
							qs10769.setState(Quest.STARTED);
							qs10769.setCond(1);
							st.set("lvl", "40");
							st.giveItems(39595, 1, false);
							st.showQuestionMark(15);
						}
						else
						{
							player.sendPacket(new ExShowScreenMessage(NpcString.KEKROPUS_LETTER_HAS_ARRIVEDNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
							st.playSound(SOUND_TUTORIAL);
							Quest quest = QuestManager.getQuest2("_10390_KekropusLetter");
							QuestState qs10390 = player.getQuestState(quest.getName());
							if(qs10390 == null)
								qs10390 = quest.newQuestState(player, Quest.STARTED);
							qs10390.setState(Quest.STARTED);
							qs10390.setCond(1);
							st.set("lvl", "40");
							st.showQuestionMark(15);
						}
					}

				}
				else if(level == 43 && checkQuestionMarkCondition(st, 8)) // О камалоке 43-го уровня.
				{
					if(st.getInt("lvl") < 43)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "43");
						st.showQuestionMark(8);
					}
				}
				else if(level >= 46 && checkQuestionMarkCondition(st, 16)) // Квест 10774.
				{
					if(st.getInt("lvl") < 46)
					{
						if(player.getClassId().isOfRace(Race.ERTHEIA))
						{
							player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ2, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
							st.playSound(SOUND_TUTORIAL);
							Quest quest = QuestManager.getQuest2("_10774_LettersfromtheQueenCrumaTowerPart2");
							QuestState qs10774 = player.getQuestState(quest.getName());
							if(qs10774 == null)
								qs10774 = quest.newQuestState(player, Quest.STARTED);
							qs10774.setState(Quest.STARTED);
							qs10774.setCond(1);
							st.set("lvl", "46");
							st.giveItems(39595, 1, false);
							st.showQuestionMark(16);
						}
						else
						{
							player.sendPacket(new ExShowScreenMessage(NpcString.KEKROPUS_LETTER_HAS_ARRIVEDNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
							st.playSound(SOUND_TUTORIAL);
							Quest quest = QuestManager.getQuest2("_10393_ACluesComplete");
							QuestState qs10393 = player.getQuestState(quest.getName());
							if(qs10393 == null)
								qs10393 = quest.newQuestState(player, Quest.STARTED);
							qs10393.setState(Quest.STARTED);
							qs10393.setCond(1);
							st.set("lvl", "46");
							st.giveItems(37113, 1, false);
							st.showQuestionMark(16);
						}
					}
				}
				else if(level == 49 && checkQuestionMarkCondition(st, 8)) // О пайлаке 49-го уровня.
				{
					if(st.getInt("lvl") < 49)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "49");
						st.showQuestionMark(8);
					}
				}
				else if(level == 50 && checkQuestionMarkCondition(st, 9)) // О локации: Лес Разбойников.
				{
					if(st.getInt("lvl") < 50)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "50");
						st.showQuestionMark(9);
					}
				}
				else if(level >= 52 && checkQuestionMarkCondition(st, 17)) // Квест 10779/10397
				{
					if(st.getInt("lvl") < 52)
					{
						if(player.getClassId().isOfRace(Race.ERTHEIA))
						{
							player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ2, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
							st.playSound(SOUND_TUTORIAL);
							Quest quest = QuestManager.getQuest2("_10779_LettersFromtheQueenSeaofSpores");
							QuestState qs10779 = player.getQuestState(quest.getName());
							if(qs10779 == null)
								qs10779 = quest.newQuestState(player, Quest.STARTED);
							qs10779.setState(Quest.STARTED);
							qs10779.setCond(1);
							st.set("lvl", "52");
							st.giveItems(39574, 1, false);
							st.showQuestionMark(17);
						}
						else
						{
							player.sendPacket(new ExShowScreenMessage(NpcString.KEKROPUS_LETTER_HAS_ARRIVEDNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
							st.playSound(SOUND_TUTORIAL);
							Quest quest = QuestManager.getQuest2("_10397_KekropusLetter");
							QuestState qs10397 = player.getQuestState(quest.getName());
							if(qs10397 == null)
								qs10397 = quest.newQuestState(player, Quest.STARTED);
							qs10397.setState(Quest.STARTED);
							qs10397.setCond(1);
							st.set("lvl", "52");
							st.giveItems(37114, 1, false);
							st.showQuestionMark(17);
						}
					}
				}
				else if(level == 55 && checkQuestionMarkCondition(st, 9)) // О локации: Забытые Равнины.
				{
					if(st.getInt("lvl") < 55)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "55");
						st.showQuestionMark(9);
					}
				}
				else if(level >= 58 && checkQuestionMarkCondition(st, 18)) // Квест 10782/10401
				{
					if(st.getInt("lvl") < 58)
					{
						if(player.getClassId().isOfRace(Race.ERTHEIA))
						{
							player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ2, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
							st.playSound(SOUND_TUTORIAL);
							Quest quest = QuestManager.getQuest2("_10782_LettersfromtheQueenForsakenPlains");
							QuestState qs10782 = player.getQuestState(quest.getName());
							if(qs10782 == null)
								qs10782 = quest.newQuestState(player, Quest.STARTED);
							qs10782.setState(Quest.STARTED);
							qs10782.setCond(1);
							st.set("lvl", "58");
							st.giveItems(39576, 1, false);
							st.showQuestionMark(18);
						}
						else
						{
							player.sendPacket(new ExShowScreenMessage(NpcString.KEKROPUS_LETTER_HAS_ARRIVEDNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
							st.playSound(SOUND_TUTORIAL);
							Quest quest = QuestManager.getQuest2("_10401_KekropusLetterDecodingTheBadge");
							QuestState qs10401 = player.getQuestState(quest.getName());
							if(qs10401 == null)
								qs10401 = quest.newQuestState(player, Quest.STARTED);
							qs10401.setState(Quest.STARTED);
							qs10401.setCond(1);
							st.set("lvl", "58");
							st.giveItems(37115, 1, false);
							st.showQuestionMark(18);
						}
					}
				}
				else if(level == 58 && checkQuestionMarkCondition(st, 8) && !player.getClassId().isOfRace(Race.ERTHEIA)) // О камалоке 58-го уровня.
				{
					if(st.getInt("lvl") < 58)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "58");
						st.showQuestionMark(8);
					}
				}
				else if(level == 60 && checkQuestionMarkCondition(st, 9)) // Страница книги путешественников: Глава "Путишествия".
				{
					if(st.getInt("lvl") < 60)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "60");
						st.showQuestionMark(9);
					}
				}
				else if(level >= 61 && checkQuestionMarkCondition(st, 19)) // Квест 10785/10404
				{
					if(st.getInt("lvl") < 61)
					{
						if(player.getClassId().isOfRace(Race.ERTHEIA))
						{
							player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ2, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
							st.playSound(SOUND_TUTORIAL);
							Quest quest = QuestManager.getQuest2("_10785_LettersfromtheQueenFieldsofMassacre");
							QuestState qs10785 = player.getQuestState(quest.getName());
							if(qs10785 == null)
								qs10785 = quest.newQuestState(player, Quest.STARTED);
							qs10785.setState(Quest.STARTED);
							qs10785.setCond(1);
							st.set("lvl", "61");
							st.giveItems(39576, 1, false);
							st.showQuestionMark(19);
						}
						else
						{
							player.sendPacket(new ExShowScreenMessage(NpcString.KEKROPUS_LETTER_HAS_ARRIVEDNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
							st.playSound(SOUND_TUTORIAL);
							Quest quest = QuestManager.getQuest2("_10404_KekropusLetterAHiddenMeaning");
							QuestState qs10404 = player.getQuestState(quest.getName());
							if(qs10404 == null)
								qs10404 = quest.newQuestState(player, Quest.STARTED);
							qs10404.setState(Quest.STARTED);
							qs10404.setCond(1);
							st.set("lvl", "61");
							st.giveItems(37116, 1, false);
							st.showQuestionMark(19);
						}
					}
				}
				else if(level == 61 && checkQuestionMarkCondition(st, 8) && !player.getClassId().isOfRace(Race.ERTHEIA)) // О пайлаке 61-го уровня.
				{
					if(st.getInt("lvl") < 61)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "61");
						st.showQuestionMark(8);
					}
				}
				else if(level >= 65 && checkQuestionMarkCondition(st, 20)) // Квест 10792/1789/10408.
				{
					if(st.getInt("lvl") < 65)
					{
						if(player.getClassId().isOfRace(Race.ERTHEIA))
						{
							player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ2, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
							st.playSound(SOUND_TUTORIAL);
							st.set("lvl", "65");
							st.giveItems(39582, 1, false);
							st.showQuestionMark(20);
							if(player.isMageClass())
							{
								Quest quest = QuestManager.getQuest2("_10792_LettersfromtheQueenForestoftheDead");
								QuestState qs10792 = player.getQuestState(quest.getName());
								if(qs10792 == null)
									qs10792 = quest.newQuestState(player, Quest.STARTED);
								qs10792.setState(Quest.STARTED);
								qs10792.setCond(1);
							}
							else
							{
								Quest quest = QuestManager.getQuest2("_10789_LettersfromtheQueenSwampofScreams");
								QuestState qs10789 = player.getQuestState(quest.getName());
								if(qs10789 == null)
									qs10789 = quest.newQuestState(player, Quest.STARTED);
								qs10789.setState(Quest.STARTED);
								qs10789.setCond(1);
							}
						}
						else
						{
							player.sendPacket(new ExShowScreenMessage(NpcString.KEKROPUS_LETTER_HAS_ARRIVEDNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
							st.playSound(SOUND_TUTORIAL);
							st.set("lvl", "65");
							st.giveItems(37117, 1, false);
							st.showQuestionMark(20);
							if(!player.isMageClass())
							{
								Quest quest = QuestManager.getQuest2("_10408_KekropusLetterTheSwampOfScreams");
								QuestState qs10408 = player.getQuestState(quest.getName());
								if(qs10408 == null)
									qs10408 = quest.newQuestState(player, Quest.STARTED);
								qs10408.setState(Quest.STARTED);
								qs10408.setCond(1);
							}
							else
							{
								Quest quest = QuestManager.getQuest2("_10411_KekporusLetterTheForestOfTheDead");
								QuestState qs10411 = player.getQuestState(quest.getName());
								if(qs10411 == null)
									qs10411 = quest.newQuestState(player, Quest.STARTED);
								qs10411.setState(Quest.STARTED);
								qs10411.setCond(1);
							}
						}
					}
				}
				else if(level == 66 && checkQuestionMarkCondition(st, 9)) // Страница книги путешественников: Глава "Путишествия".
				{
					if(st.getInt("lvl") < 66)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "65");
						st.showQuestionMark(9);
					}
				}
				else if(level == 68 && checkQuestionMarkCondition(st, 8)) // О камалоке 68-го уровня.
				{
					if(st.getInt("lvl") < 68)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "68");
						st.showQuestionMark(8);
					}
				}
				else if(level >= 70 && checkQuestionMarkCondition(st, 21)) // Квест 10795/10414/10415
				{
					if(st.getInt("lvl") < 70)
					{
						if(player.getClassId().isOfRace(Race.ERTHEIA))
						{
							player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ2, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
							st.playSound(SOUND_TUTORIAL);
							Quest quest = QuestManager.getQuest2("_10795_LettersfromtheQueenWallofArgos");
							QuestState qs10795 = player.getQuestState(quest.getName());
							if(qs10795 == null)
								qs10795 = quest.newQuestState(player, Quest.STARTED);
							qs10795.setState(Quest.STARTED);
							qs10795.setCond(1);
							st.set("lvl", "70");
							st.giveItems(39584, 1, false);
							st.showQuestionMark(21);
						}
						else
						{
							player.sendPacket(new ExShowScreenMessage(NpcString.KEKROPUS_LETTER_HAS_ARRIVEDNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
							st.playSound(SOUND_TUTORIAL);
							st.set("lvl", "70");
							st.giveItems(37117, 1, false);
							st.showQuestionMark(21);
							if(player.isMageClass())
							{
								Quest quest = QuestManager.getQuest2("_10415_WithWisdom");
								QuestState qs10415 = player.getQuestState(quest.getName());
								if(qs10415 == null)
									qs10415 = quest.newQuestState(player, Quest.STARTED);
								qs10415.setState(Quest.STARTED);
								qs10415.setCond(1);
							}
							else
							{
								Quest quest = QuestManager.getQuest2("_10414_KekporusLetterWithCourage");
								QuestState qs10414 = player.getQuestState(quest.getName());
								if(qs10414 == null)
									qs10414 = quest.newQuestState(player, Quest.STARTED);
								qs10414.setState(Quest.STARTED);
								qs10414.setCond(1);
							}
						}
					}
				}
				else if(level == 73 && checkQuestionMarkCondition(st, 8)) // О пайлаке 73-го уровня.
				{
					if(st.getInt("lvl") < 73)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "73");
						st.showQuestionMark(8);
					}
				}
				else if(level == 75 && checkQuestionMarkCondition(st, 9)) // Страница книги путешественников: Глава "Путишествия".
				{
					if(st.getInt("lvl") < 75)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "75");
						st.showQuestionMark(9);
					}
				}
				else if(level >= 76 && checkQuestionMarkCondition(st, 6)) // О Квесте на 3ю професиию.
				{
					if(st.getInt("lvl") < 76)
					{
						// st.playTutorialVoice("tutorial_voice_???");
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "76");
						st.showQuestionMark(6);
					}
				}
				else if(level >= 76 && checkQuestionMarkCondition(st, 6) && player.getClassId().isOfRace(Race.ERTHEIA)) // О 2-м Освобождении Артей.
				{
					if(st.getInt("lvl") < 76)
					{
						// st.playTutorialVoice("tutorial_voice_???");
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "76");
						st.showQuestionMark(6);
						if(player.getClassId().isMage())
							player.sendPacket(new ExShowScreenMessage(NpcString.MAGISTER_AYANTHE_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
						else
							player.sendPacket(new ExShowScreenMessage(NpcString.MASTER_KATALIN_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
					}
				}
				else if(level == 79 && checkQuestionMarkCondition(st, 8)) // О цепочки квестов 7ми печатей.
				{
					if(st.getInt("lvl") < 79)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "79");
						st.showQuestionMark(8);
					}
				}
				else if(level == 80 && checkQuestionMarkCondition(st, 9)) // Страница книги путешественников: Глава "Путишествия".
				{
					if(st.getInt("lvl") < 80)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "80");
						st.showQuestionMark(9);
					}
				}
				else if(level >= 81 && player.getClassId().isOfLevel(ClassLevel.THIRD) && !player.getClassId().isOfRace(Race.ERTHEIA)) // Квест 10430/10433/10436/10439
				{
					if(st.getInt("lvl") < 81)
					{
						player.sendPacket(new ExShowScreenMessage(NpcString.KEKROPUS_LETTER_HAS_ARRIVEDNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "81");
						st.showQuestionMark(24);
					}
				}
				else if(level == 85 && checkQuestionMarkCondition(st, 9)) // Страница книги путешественников: Глава "Перерождение".
				{
					if(st.getInt("lvl") < 85)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "85");
						st.showQuestionMark(9);
					}
				}
				else if(level >= 85 && checkQuestionMarkCondition(st, 10) && st.getInt("radas_letter") == 0)
				{
					st.playSound(SOUND_TUTORIAL);
					st.set("radas_letter", 1);
					st.showQuestionMark(10);
				}
				else if(level >= 85 && checkQuestionMarkCondition(st, 11)) // О 3-м Освобождении Артей.
				{
					if(st.getInt("ertheia_3rd_exemption") == 0)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("ertheia_3rd_exemption", 1);
						st.showQuestionMark(11);
						if(player.getClassId().isMage())
							player.sendPacket(new ExShowScreenMessage(NpcString.MAGISTER_AYANTHE_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
						else
							player.sendPacket(new ExShowScreenMessage(NpcString.MASTER_KATALIN_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
					}
				}
				else if(level >= 85 && checkQuestionMarkCondition(st, 12)) // О Получении Дуалкласса Артей.
				{
					if(st.getInt("ertheia_dualclass") == 0)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("ertheia_dualclass", 1);
						st.showQuestionMark(12);
						player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_NAVARI_HAS_SENT_A_LETTERNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
					}
				}
				else if(level == 90 && checkQuestionMarkCondition(st, 9)) // Страница книги путешественников: Глава "Перерождение".
				{
					if(st.getInt("lvl") < 90)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "90");
						st.showQuestionMark(9);
					}
				}
				else if(level == 95 && checkQuestionMarkCondition(st, 9)) // Страница книги путешественников: Глава "Перерождение".
				{
					if(st.getInt("lvl") < 95)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "95");
						st.showQuestionMark(9);
					}
				}
				checkHermenkusMsg(st);
			}
		}

		// Question mark clicked
		else if(event.startsWith("QM"))
		{
			int MarkId = Integer.valueOf(event.substring(2));
			if(!checkQuestionMarkCondition(st, MarkId) && MarkId < 13)
				return null;

			if(MarkId == 1) // Способ перемещения.
			{
				st.set("ex_state", "-1");
				if(player.getClassId().isOfRace(Race.ERTHEIA))
					html = "tutorial_way_to_move_e.htm";
				else
					html = "tutorial_way_to_move.htm";
			}
			else if(MarkId == 2) // О штрафе при смерте.
				html = "tutorial_death_penalty.htm";
			else if(MarkId == 3) // О квесте на волка.
				html = "tutorial_pet_quest.htm";
			else if(MarkId == 4) // О Квесте на 1ю професиию.
			{
				if(QMCc1.containsKey(player.getRace().ordinal()))
					html = QMCc1.get(player.getRace().ordinal());
			}
			else if(MarkId == 5) // О Квесте на 2ю професиию (1-е Освобождение Артей).
			{
				if(player.getClassId().isOfRace(Race.ERTHEIA))
					html = "tutorial_1st_ct_ertheia.htm";
				else if(player.getRace() == Race.HUMAN && player.isMageClass())
					html = "tutorial_2nd_ct_human_m.htm";
				else if(player.getRace() == Race.ELF && player.isMageClass())
					html = "tutorial_2nd_ct_elf_m.htm";
				else if(QMCc2.containsKey(player.getRace().ordinal()))
					html = QMCc2.get(player.getRace().ordinal());
			}
			else if(MarkId == 6) // О Квесте на 3ю професиию (2-е Освобождение Артей).
			{
				if(player.getClassId().isOfRace(Race.ERTHEIA))
					html = "tutorial_2nd_ct_ertheia.htm";
				else if(QMCc3.containsKey(player.getRace().ordinal()))
					html = QMCc3.get(player.getRace().ordinal());
			}
			else if(MarkId == 7) // Смерть.
				html = "tutorial_die.htm";
			else if(MarkId == 8) // О Квестах
			{
				int lvl = player.getLevel();
				if(lvl == 28) // О камалоке 28-го уровня.
				{
					html = "tutorial_kama_28.htm";
					st.addRadar(18199, 146081, -3080);
				}
				else if(lvl == 43) // О камалоке 43-го уровня.
				{
					html = "tutorial_kama_43.htm";
					st.addRadar(108384, 221563, -3592);
				}
				else if(lvl == 49) // О пайлаке 49-го уровня.
					html = "tutorial_pailaka_49.htm";
				else if(lvl == 58) // О камалоке 58-го уровня.
				{
					html = "tutorial_kama_58.htm";
					st.addRadar(85868, -142164, -1336);
				}
				else if(lvl == 61) // О пайлаке 61-го уровня.
					html = "tutorial_pailaka_61.htm";
				else if(lvl == 68) // О камалоке 68-го уровня.
				{
					html = "tutorial_kama_68.htm";
					st.addRadar(42596, -47988, -792);
				}
				else if(lvl == 73) // О пайлаке 73-го уровня.
					html = "tutorial_pailaka_73.htm";
				else if(lvl == 79) // О цепочки квестов 7ми печатей.
					html = "tutorial_epic_quest.htm";
			}
			else if(MarkId == 9) // О локациях.
			{
				int lvl = player.getLevel();
				if(lvl == 40) // Башня Слоновой Кости.
					st.showTutorialClientHTML("Guide_Ad_4050_01_ivorytower"); // На оффе этого нету.
				else if(lvl == 50) // Лес разбойников.
					st.showTutorialClientHTML("Guide_Ad_5055_01_outlaws");
				else if(lvl == 55) // Забытые Равнины.
					st.showTutorialClientHTML("Guide_Ad_5560_01_forsaken");
				else if(lvl == 60) // Глава "Путишествия".
					st.showTutorialClientHTML("Guide_Ad_6065_00_main"); // На оффе этого нету.
				else if(lvl == 65) // Глава "Путишествия".
					st.showTutorialClientHTML("Guide_Ad_6570_00_main");
				else if(lvl == 70) // Глава "Путишествия".
					st.showTutorialClientHTML("Guide_Ad_7075_00_main");
				else if(lvl == 75) // Глава "Путишествия".
					st.showTutorialClientHTML("Guide_Ad_7580_00_main");
				else if(lvl == 80) // Глава "Путишествия".
					st.showTutorialClientHTML("Guide_Ad_8085_00_main");
				else if(lvl == 85) // Глава "Перерождение".
					st.showTutorialClientHTML("Guide_Aw_8590_00_main");
				else if(lvl == 90) // Глава "Перерождение".
					st.showTutorialClientHTML("Guide_Aw_9095_00_main");
				else if(lvl == 95) // Глава "Перерождение".
					st.showTutorialClientHTML("Guide_Aw_9599_00_main");
				return null;
			}
			else if(MarkId == 10) // Письмо Рады.
			{
				html = "tutorial_radas_letter.htm";
				st.giveItems(17725, 1);
				st.playSound(SOUND_MIDDLE);
			}
			else if(MarkId == 11) // О 3-м Освобождении Артей.
				html = "tutorial_3rd_ct_ertheia.htm";
			else if(MarkId == 12)
				html = "tutorial_ertheia_dualclass.htm";
			else if(MarkId == 13)
				html = "tutorial_10755.htm";
			else if(MarkId == 14)
				html = "tutorial_10760.htm";
			else if(MarkId == 15)
				if(player.getClassId().isOfRace(Race.ERTHEIA))
					html = "tutorial_10769.htm";
				else
					html = "tutorial_10390.htm";
			else if(MarkId == 16)
				if(player.getClassId().isOfRace(Race.ERTHEIA))
					html = "tutorial_10774.htm";
				else
					html = "tutorial_10393.htm";
			else if(MarkId == 17)
				if(player.getClassId().isOfRace(Race.ERTHEIA))
					html = "tutorial_10779.htm";
				else
					html = "tutorial_10397.htm";
			else if(MarkId == 18)
				if(player.getClassId().isOfRace(Race.ERTHEIA))
					html = "tutorial_10782.htm";
				else
					html = "tutorial_10401.htm";
			else if(MarkId == 19)
				if(player.getClassId().isOfRace(Race.ERTHEIA))
					html = "tutorial_10785.htm";
				else
					html = "tutorial_10404.htm";
			else if(MarkId == 20)
			{
				if(player.getClassId().isOfRace(Race.ERTHEIA))
				{
					if(player.isMageClass())
						html = "tutorial_10792.htm";
					else
						html = "tutorial_10789.htm";
				}
				else
				{
					if(player.isMageClass())
						html = "tutorial_10411.htm";
					else
						html = "tutorial_10408.htm";
				}
			}
			else if(MarkId == 21)
				if(player.getClassId().isOfRace(Race.ERTHEIA))
					html = "tutorial_10795.htm";
				else
				{
					if(player.isMageClass())
						html = "tutorial_10415.htm";
					else
						html = "tutorial_10414.htm";
				}
			else if(MarkId == 22)
				html = "tutorial_10798.htm";
			else if(MarkId == 23)
			{
				if(checkQuestionMarkCondition(st, 23))
				{
					player.sendPacket(new ExShowScreenMessage(NpcString.KEKROPUS_LETTER_HAS_ARRIVEDNCLICK_THE_QUESTIONMARK_ICON_TO_READ, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
					st.giveItems(37119, 1, false);
					if(player.isMageClass())
					{
						Quest quest = QuestManager.getQuest2("_10424_WhereIsBelus");
						QuestState qs10424 = player.getQuestState(quest.getName());
						if(qs10424 == null)
							qs10424 = quest.newQuestState(player, Quest.STARTED);
						qs10424.setState(Quest.STARTED);
						qs10424.setCond(1);
						html = "tutorial_10424.htm";
					}
					else
					{
						Quest quest = QuestManager.getQuest2("_10419_WhereIsTheCamp");
						QuestState qs10419 = player.getQuestState(quest.getName());
						if(qs10419 == null)
							qs10419 = quest.newQuestState(player, Quest.STARTED);
						qs10419.setState(Quest.STARTED);
						qs10419.setCond(1);
						html = "tutorial_10419.htm";
					}
				}
			}
			else if(MarkId == 24)
			{
				if(checkQuest(player, 10430))
				{
					Quest quest = QuestManager.getQuest2("_10430_TrackingTheEvil");
					QuestState qs10430 = player.getQuestState(quest.getName());
					if(qs10430 == null)
						qs10430 = quest.newQuestState(player, Quest.STARTED);
					qs10430.setState(Quest.STARTED);
					qs10430.setCond(1);
					if(player.getInventory().getCountOf(37124) == 0)
						st.giveItems(37123, 1, false);
					html = "tutorial_10430.htm";
				}
				else if(checkQuest(player, 10433))
				{
					Quest quest = QuestManager.getQuest2("_10433_RegardingASeal");
					QuestState qs10433 = player.getQuestState(quest.getName());
					if(qs10433 == null)
						qs10433 = quest.newQuestState(player, Quest.STARTED);
					qs10433.setState(Quest.STARTED);
					qs10433.setCond(1);
					if(player.getInventory().getCountOf(37124) == 0)
						st.giveItems(37124, 1, false);
					html = "tutorial_10433.htm";
				}
				else if(checkQuest(player, 10436))
				{
					Quest quest = QuestManager.getQuest2("_10436_theSealOfPunishment");
					QuestState qs10436 = player.getQuestState(quest.getName());
					if(qs10436 == null)
						qs10436 = quest.newQuestState(player, Quest.STARTED);
					qs10436.setState(Quest.STARTED);
					qs10436.setCond(1);
					if(player.getInventory().getCountOf(37124) == 0)
						st.giveItems(37124, 1, false);
					html = "tutorial_10436.htm";
				}
				else if(checkQuest(player, 10439))
				{
					Quest quest = QuestManager.getQuest2("_10439_TheOriginsOfARumor");
					QuestState qs10439 = player.getQuestState(quest.getName());
					if(qs10439 == null)
						qs10439 = quest.newQuestState(player, Quest.STARTED);
					qs10439.setState(Quest.STARTED);
					qs10439.setCond(1);
					if(player.getInventory().getCountOf(37127) == 0)
						st.giveItems(37127, 1, false);
					html = "tutorial_10439.htm";
				}
			}
		}

		if(html.isEmpty())
			return null;

		st.showTutorialHTML(html);
		return null;
	}

	private boolean checkQuestionMarkCondition(QuestState st, int markId)
	{
		Player player = st.getPlayer();

		if(markId == 4)
		{
			if(player.getClassId().isOfLevel(ClassLevel.NONE) && checkQuest(player, 10331))
				return true;
			return false;
		}
		else if(markId == 5)
		{
			if(player.getClassId().isOfLevel(ClassLevel.FIRST) && checkQuest(player, 10360))
				return true;
			if(player.getClassId().isOfLevel(ClassLevel.NONE) && checkQuest(player, 10751))
				return true;
			return false;
		}
		else if(markId == 6)
		{
			if(player.getClassId().isOfLevel(ClassLevel.SECOND))
			{
				if(checkQuest(player, 10341))
					return true;
				if(checkQuest(player, 10342))
					return true;
				if(checkQuest(player, 10343))
					return true;
				if(checkQuest(player, 10344))
					return true;
				if(checkQuest(player, 10345))
					return true;
				if(checkQuest(player, 10346))
					return true;
			}

			if(player.getClassId().isOfLevel(ClassLevel.FIRST) && checkQuest(player, 10752))
				return true;
			return false;
		}
		else if(markId == 8)
		{
			if(checkQuest(player, 10277))
				return true;
			if(checkQuest(player, 10278))
				return true;
			if(checkQuest(player, 128))
				return true;
			if(checkQuest(player, 10280))
				return true;
			if(checkQuest(player, 129))
				return true;
			if(checkQuest(player, 10281))
				return true;
			if(checkQuest(player, 144))
				return true;
			if(checkQuest(player, 192))
				return true;
			return false;
		}
		else if(markId == 10)
		{
			if(checkQuest(player, 10301) && !st.haveQuestItem(17725))
				return true;
			return false;
		}
		else if(markId == 11)
		{
			if(checkQuest(player, 10753))
				return true;
			return false;
		}
		else if(markId == 12)
		{
			if(checkQuest(player, 10472))
				return true;
			return false;
		}
		else if(markId == 13)
		{
			if(checkQuest(player, 10755))
				return true;
			return false;
		}
		else if(markId == 14)
		{
			if(checkQuest(player, 10760))
				return true;
			return false;
		}
		else if(markId == 15)
		{
			if(player.getClassId().isOfRace(Race.ERTHEIA))
			{
				if(checkQuest(player, 10769))
					return true;
			}
			else
			{
				if(checkQuest(player, 10390))
					return true;
			}
			return false;
		}
		else if(markId == 16)
		{
			if(player.getClassId().isOfRace(Race.ERTHEIA))
			{
				if(checkQuest(player, 10774))
					return true;
			}
			else
			{
				if(checkQuest(player, 10393))
					return true;
			}
			return false;
		}
		else if(markId == 17)
		{
			if(player.getClassId().isOfRace(Race.ERTHEIA))
			{
				if(checkQuest(player, 10779))
					return true;
			}
			else
			{
				if(checkQuest(player, 10397))
					return true;
			}
			return false;
		}
		else if(markId == 18)
		{
			if(player.getClassId().isOfRace(Race.ERTHEIA))
			{
				if(checkQuest(player, 10782))
					return true;
			}
			else
			{
				if(checkQuest(player, 10401))
					return true;
			}
			return false;
		}
		else if(markId == 19)
		{
			if(player.getClassId().isOfRace(Race.ERTHEIA))
			{
				if(checkQuest(player, 10785))
					return true;
			}
			else
			{
				if(checkQuest(player, 10404))
					return true;
			}
			return false;
		}
		else if(markId == 20)
		{
			if(player.getClassId().isOfRace(Race.ERTHEIA))
			{
				if(player.isMageClass())
				{
					if(checkQuest(player, 10792))
						return true;
				}
				else
				{
					if(checkQuest(player, 10789))
					return true;
				}
			}
			else
			{
				if(player.isMageClass())
				{
					if(checkQuest(player, 10411))
						return true;
				}
				else
				{
					if(checkQuest(player, 10408))
						return true;
				}
			}
			return false;
		}
		else if(markId == 21)
		{
			if(player.getClassId().isOfRace(Race.ERTHEIA))
			{
				if(checkQuest(player, 10795))
					return true;
			}
			else
			{
				if(player.isMageClass())
				{
					if(checkQuest(player, 10415))
						return true;
				}
				else
				{
					if(checkQuest(player, 10414))
						return true;
				}
			}
			return false;
		}
		else if(markId == 22)
		{
			if(checkQuest(player, 10798))
				return true;
			return false;
		}
		else if(markId == 23)
		{
			if(player.isMageClass())
			{
				if(checkQuest(player, 10424))
					return true;
			}
			else
			{
				if(checkQuest(player, 10419))
					return true;
			}
			return false;
		}
 		else if(markId == 24)
		{
			if(checkQuest(player, 10430))
				return true;
			if(checkQuest(player, 10433))
				return true;
			if(checkQuest(player, 10436))
				return true;
			if(checkQuest(player, 10439))
				return true;
			return false;
		}
		return true;
	}

	private boolean checkQuest(Player player, int questId)
	{
		Quest q = QuestManager.getQuest(questId);
		if(q != null)
		{
			QuestState st = player.getQuestState(q.getClass());
			if(st == null)
				return q.checkStartCondition(player);
			else if(st.isCreated())
				return true;
		}

		return false;
	}

	private void checkHermenkusMsg(QuestState st)
	{
		Player player = st.getPlayer();
		if(player == null)
			return;

		// Сообщение от гермункуса.
		if(!player.getClassId().isOfRace(Race.ERTHEIA) && player.getLevel() >= 85 && player.isBaseClassActive() && player.getClassId().isOfLevel(ClassLevel.THIRD))
		{
			if(st.getInt("herm_msg_showed") == player.getClassId().getId())
				return;

			int classId = 0;
			for(ClassId c : ClassId.VALUES)
			{
				if(c.isOfLevel(ClassLevel.AWAKED) && c.childOf(player.getClassId()))
				{
					classId = c.getId();
					break;
				}
			}
			if(!player.getVarBoolean("GermunkusUSM"))
			{
				player.sendPacket(new ExCallToChangeClass(classId, false));
				player.sendPacket(new ExShowScreenMessage(NpcString.FREE_THE_GIANT_FROM_HIS_IMPRISONMENT_AND_AWAKEN_YOUR_TRUE_POWER, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				st.set("herm_msg_showed", player.getClassId().getId(), false);
			}
		}
	}

	@Override
	public boolean isVisible(Player player)
	{
		return false;
	}
}
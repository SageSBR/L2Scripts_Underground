package l2s.gameserver.network.l2.c2s;

import java.util.Calendar;
import java.util.List;

import l2s.gameserver.network.l2.s2c.*;
import org.apache.commons.lang3.tuple.Pair;
import l2s.gameserver.Announcements;
import l2s.gameserver.Config;
import l2s.gameserver.dao.MailDAO;
import l2s.gameserver.data.string.StringsHolder;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.data.xml.holder.ResidenceHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.instancemanager.CoupleManager;
import l2s.gameserver.instancemanager.CursedWeaponsManager;
//import l2s.gameserver.instancemanager.DailyQuestsManager;
import l2s.gameserver.instancemanager.PetitionManager;
import l2s.gameserver.instancemanager.PlayerMessageStack;
import l2s.gameserver.instancemanager.QuestManager;
import l2s.gameserver.listener.actor.player.OnAnswerListener;
import l2s.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.World;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.model.base.InvisibleType;
import l2s.gameserver.model.entity.FreePAManager;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.mail.Mail;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.network.l2.GameClient;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.skills.AbnormalEffect;
import l2s.gameserver.stats.triggers.TriggerType;
//import l2s.gameserver.utils.GameStats;
import l2s.gameserver.utils.HtmlUtils;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.TradeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnterWorld extends L2GameClientPacket
{
	private static final Object _lock = new Object();

	private static final Logger _log = LoggerFactory.getLogger(EnterWorld.class);

	@Override
	protected void readImpl()
	{
		//readS(); - клиент всегда отправляет строку "narcasse"
	}

	@Override
	protected void runImpl()
	{
		GameClient client = getClient();
		Player activeChar = client.getActiveChar();

		if(activeChar == null)
		{
			client.closeNow(false);
			return;
		}

		int MyObjectId = activeChar.getObjectId();
		Long MyStoreId = activeChar.getStoredId();

		synchronized (_lock)//TODO [G1ta0] че это за хуйня, и почему она тут
		{
			for(Player cha : GameObjectsStorage.getAllPlayersForIterate())
			{
				if(MyStoreId == cha.getStoredId())
					continue;
				try
				{
					if(cha.getObjectId() == MyObjectId)
					{
						_log.warn("Double EnterWorld for char: " + activeChar.getName());
						cha.kick();
					}
				}
				catch(Exception e)
				{
					_log.error("", e);
				}
			}
		}

		boolean first = activeChar.entering;

		activeChar.sendPacket(ExLightingCandleEvent.DISABLED);
		//TODO: activeChar.sendPacket(new ExChannlChatEnterWorld(activeChar));
		//TODO: activeChar.sendPacket(new ExChannlChatPlegeInfo(activeChar));
		activeChar.sendPacket(new ExPeriodicHenna(activeChar));
		activeChar.sendAbilitiesInfo();
		activeChar.sendPacket(new HennaInfoPacket(activeChar));

		List<Castle> castleList = ResidenceHolder.getInstance().getResidenceList(Castle.class);
		for(Castle c : castleList)
			activeChar.sendPacket(new ExCastleState(c));

		activeChar.sendSkillList();
		activeChar.sendPacket(new EtcStatusUpdatePacket(activeChar));

		activeChar.sendPacket(new ExUserInfo(activeChar));
		activeChar.sendPacket(new ExUserInfoInvenWeight(activeChar));
		activeChar.sendPacket(new ExUserInfoEquipSlot(activeChar));
		activeChar.sendPacket(new ExUserInfoCubic(activeChar));
		activeChar.sendPacket(new ExUserInfoAbnormalVisualEffect(activeChar));
		activeChar.sendPacket(new ExVitalityEffectInfo(activeChar));

		//TODO: Что за хрень?
		activeChar.sendPacket(new ExChangeMPCost(0, -20));
		activeChar.sendPacket(new ExChangeMPCost(1, -10));
		activeChar.sendPacket(new ExChangeMPCost(3, -20));
		activeChar.sendPacket(new ExChangeMPCost(1, -14.5));
		activeChar.sendPacket(new ExChangeMPCost(0, -27.999999999999986));
		activeChar.sendPacket(new ExChangeMPCost(3, -27.999999999999986));
		activeChar.sendPacket(new ExChangeMPCost(1, -23.049999999999997));
		activeChar.sendPacket(new ExChangeMPCost(0, -31.599999999999994));
		activeChar.sendPacket(new ExChangeMPCost(1, -34.5925));
		activeChar.sendPacket(new ExChangeMPCost(3, -31.599999999999994));

		activeChar.sendPacket(new QuestListPacket(activeChar));

		for (int i = 0; i < ExAutoSoulShot.Type.MaxType.ordinal(); ++i) {
			activeChar.sendPacket(new ExAutoSoulShot(i, i));
		}

		activeChar.sendPacket(new ExGetBookMarkInfoPacket(activeChar));

		activeChar.sendItemList(false);
		activeChar.sendPacket(new ExAdenaInvenCount(activeChar));
		activeChar.sendPacket(new ShortCutInitPacket(activeChar));
		activeChar.sendPacket(new ExBasicActionList(activeChar));
		
		activeChar.getMacroses().sendMacroses();

		if(first)
		{
			activeChar.setOnlineStatus(true);
			if(activeChar.getPlayerAccess().GodMode && !Config.SHOW_GM_LOGIN)
			{
				activeChar.setInvisibleType(InvisibleType.GM);
				activeChar.startAbnormalEffect(AbnormalEffect.STEALTH);
			}

			activeChar.setNonAggroTime(Long.MAX_VALUE);
			activeChar.setNonPvpTime(System.currentTimeMillis() + Config.NONPVP_TIME_ONTELEPORT);
			activeChar.setPendingLfcEnd(false);

			if(activeChar.getVar("lfcfirst") == null)
			{
				activeChar.setVar("lfcNotes", "on", -1);
				activeChar.setVar("lfcfirst", "on", -1);
			}
			
			if(activeChar.isInStoreMode())
			{
				if(!TradeHelper.checksIfCanOpenStore(activeChar, activeChar.getPrivateStoreType()))
				{
					activeChar.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
					activeChar.standUp();
				}
			}

			activeChar.setRunning();
			activeChar.standUp();
			activeChar.spawnMe();
			activeChar.startTimers();

			//DailyQuestsManager.checkAndRemoveDisabledQuests(activeChar);
		}

		activeChar.sendPacket(new ExSetCompassZoneCode(activeChar));
		activeChar.sendPacket(SystemMsg.WELCOME_TO_THE_WORLD_OF_LINEAGE_II);
		//TODO: Исправить посылаемые данные.
		activeChar.sendPacket(new MagicAndSkillList(activeChar, 3503292, 730502));
		activeChar.sendPacket(new ExStorageMaxCountPacket(activeChar));
		activeChar.sendPacket(new ExVoteSystemInfoPacket(activeChar));
		activeChar.sendPacket(new ExBeautyItemList(activeChar));
		activeChar.sendPacket(new SkillCoolTimePacket(activeChar));
		activeChar.sendPacket(new ExReceiveShowPostFriend(activeChar));

		Announcements.getInstance().showAnnouncements(activeChar);

		if(Config.ALLOW_WORLD_CHAT)
			activeChar.sendPacket(new ExWorldChatCnt(activeChar));

		activeChar.sendPacket(new ExConnectedTimeAndGetTableReward());

		checkNewMail(activeChar);

		if (Config.ALLOW_FREE_PA)
			FreePAManager.checkAndReward(activeChar);

		if (first)
			activeChar.getListeners().onEnter();

		if(first && activeChar.getCreateTime() > 0)
		{
			Calendar create = Calendar.getInstance();
			create.setTimeInMillis(activeChar.getCreateTime());
			Calendar now = Calendar.getInstance();

			int day = create.get(Calendar.DAY_OF_MONTH);
			if(create.get(Calendar.MONTH) == Calendar.FEBRUARY && day == 29)
				day = 28;

			int myBirthdayReceiveYear = activeChar.getVarInt(Player.MY_BIRTHDAY_RECEIVE_YEAR, 0);
			if(create.get(Calendar.MONTH) == now.get(Calendar.MONTH) && create.get(Calendar.DAY_OF_MONTH) == day)
			{
				if((myBirthdayReceiveYear == 0 && create.get(Calendar.YEAR) != now.get(Calendar.YEAR)) || myBirthdayReceiveYear > 0 && myBirthdayReceiveYear != now.get(Calendar.YEAR))
				{
					Mail mail = new Mail();
					mail.setSenderId(1);
					mail.setSenderName(StringsHolder.getInstance().getNotNull(activeChar, "birthday.npc"));
					mail.setReceiverId(activeChar.getObjectId());
					mail.setReceiverName(activeChar.getName());
					mail.setTopic(StringsHolder.getInstance().getNotNull(activeChar, "birthday.title"));
					mail.setBody(StringsHolder.getInstance().getNotNull(activeChar, "birthday.text"));

					ItemInstance item = ItemFunctions.createItem(21169);
					item.setLocation(ItemInstance.ItemLocation.MAIL);
					item.setCount(1L);
					item.save();

					mail.addAttachment(item);
					mail.setUnread(true);
					mail.setType(Mail.SenderType.BIRTHDAY);
					mail.setExpireTime(720 * 3600 + (int) (System.currentTimeMillis() / 1000L));
					mail.save();

					activeChar.setVar(Player.MY_BIRTHDAY_RECEIVE_YEAR, String.valueOf(now.get(Calendar.YEAR)), -1);
				}
			}
		}

		activeChar.checkAndDeleteHeroWpn();

		if(activeChar.getClan() != null)
		{
			activeChar.getClan().loginClanCond(activeChar, true);

			activeChar.sendPacket(activeChar.getClan().listAll());
			activeChar.sendPacket(new PledgeShowInfoUpdatePacket(activeChar.getClan()), new PledgeSkillListPacket(activeChar.getClan()));
		}
		else
			activeChar.sendPacket(new ExPledgeCount(0));

		// engage and notify Partner
		if(first && Config.ALLOW_WEDDING)
		{
			CoupleManager.getInstance().engage(activeChar);
			CoupleManager.getInstance().notifyPartner(activeChar);
		}

		if(first)
		{
			activeChar.getFriendList().notifyFriends(true);
			//activeChar.restoreDisableSkills(); Зачем дважды ресторить откат скиллов?
			activeChar.mentoringLoginConditions();
		}

		activeChar.checkHpMessages(activeChar.getMaxHp(), activeChar.getCurrentHp());
		activeChar.checkDayNightMessages();

		if(Config.SHOW_HTML_WELCOME)
		{
			String html = HtmCache.getInstance().getNotNull("welcome.htm", activeChar);
			NpcHtmlMessagePacket msg = new NpcHtmlMessagePacket(5);
			msg.setHtml(HtmlUtils.bbParse(html));
			activeChar.sendPacket(msg);
		}

		if(Config.PETITIONING_ALLOWED)
			PetitionManager.getInstance().checkPetitionMessages(activeChar);

		if(!first)
		{
			boolean dualCast = activeChar.isDualCastingNow();
			if(activeChar.isCastingNow())
			{
				Creature castingTarget = activeChar.getCastingTarget();
				Skill castingSkill = activeChar.getCastingSkill();
				long animationEndTime = activeChar.getAnimationEndTime();
				if(castingSkill != null && castingTarget != null && castingTarget.isCreature() && animationEndTime > 0)
					sendPacket(new MagicSkillUse(activeChar, castingTarget, castingSkill.getId(), castingSkill.getLevel(), (int) (animationEndTime - System.currentTimeMillis()), 0, dualCast));
			}

			if(dualCast)
			{
				Creature castingTarget = activeChar.getDualCastingTarget();
				Skill castingSkill = activeChar.getDualCastingSkill();
				long animationEndTime = activeChar.getDualAnimationEndTime();
				if(castingSkill != null && castingTarget != null && castingTarget.isCreature() && animationEndTime > 0)
					sendPacket(new MagicSkillUse(activeChar, castingTarget, castingSkill.getId(), castingSkill.getLevel(), (int) (animationEndTime - System.currentTimeMillis()), 0, dualCast));
			}

			if(activeChar.isInBoat())
				activeChar.sendPacket(activeChar.getBoat().getOnPacket(activeChar, activeChar.getInBoatPosition()));

			if(activeChar.isMoving || activeChar.isFollow)
				sendPacket(activeChar.movePacket());

			if(activeChar.getMountNpcId() != 0)
				sendPacket(new RidePacket(activeChar));

			if(activeChar.isFishing())
				activeChar.stopFishing();
		}

		activeChar.entering = false;

		if(activeChar.isSitting())
			activeChar.sendPacket(new ChangeWaitTypePacket(activeChar, ChangeWaitTypePacket.WT_SITTING));

		if(activeChar.getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
		{
			if(activeChar.getPrivateStoreType() == Player.STORE_PRIVATE_BUY)
				sendPacket(new PrivateStoreBuyMsg(activeChar));
			else if(activeChar.getPrivateStoreType() == Player.STORE_PRIVATE_SELL || activeChar.getPrivateStoreType() == Player.STORE_PRIVATE_SELL_PACKAGE)
				sendPacket(new PrivateStoreMsg(activeChar));
			else if(activeChar.getPrivateStoreType() == Player.STORE_PRIVATE_MANUFACTURE)
				sendPacket(new RecipeShopMsgPacket(activeChar));
		}

		activeChar.unsetVar("offline");

		// на всякий случай
		activeChar.sendActionFailed();

		if(first && activeChar.isGM() && Config.SAVE_GM_EFFECTS && activeChar.getPlayerAccess().CanUseGMCommand)
		{
			//silence
			if(activeChar.getVarBoolean("gm_silence"))
			{
				activeChar.setMessageRefusal(true);
				activeChar.sendPacket(SystemMsg.MESSAGE_REFUSAL_MODE);
			}
			//invul
			if(activeChar.getVarBoolean("gm_invul"))
			{
				activeChar.setIsInvul(true);
				activeChar.startAbnormalEffect(AbnormalEffect.INVINCIBILITY);
				activeChar.sendMessage(activeChar.getName() + " is now immortal.");
			}
			//gmspeed
			try
			{
				int var_gmspeed = Integer.parseInt(activeChar.getVar("gm_gmspeed"));
				if(var_gmspeed >= 1 && var_gmspeed <= 4)
					activeChar.doCast(SkillHolder.getInstance().getSkill(7029, var_gmspeed), activeChar, true);
			}
			catch(Exception E)
			{}
		}

		PlayerMessageStack.getInstance().CheckMessages(activeChar);

		Pair<Integer, OnAnswerListener> entry = activeChar.getAskListener(false);
		if(entry != null && entry.getValue() instanceof ReviveAnswerListener)
			sendPacket(new ConfirmDlgPacket(SystemMsg.C1_IS_MAKING_AN_ATTEMPT_TO_RESURRECT_YOU_IF_YOU_CHOOSE_THIS_PATH_S2_EXPERIENCE_WILL_BE_RETURNED_FOR_YOU, 0).addString("Other player").addString("some"));

		if(activeChar.isCursedWeaponEquipped())
			CursedWeaponsManager.getInstance().showUsageTime(activeChar, activeChar.getCursedWeaponEquippedId());

		if(!first)
		{
			//Персонаж вылетел во время просмотра
			if(activeChar.isInObserverMode())
			{
				if(activeChar.getObserverMode() == Player.OBSERVER_LEAVING)
					activeChar.returnFromObserverMode();
				else if(activeChar.getOlympiadObserveGame() != null)
					activeChar.leaveOlympiadObserverMode(true);
				else
					activeChar.leaveObserverMode();
			}
			else if(activeChar.isVisible())
				World.showObjectsToPlayer(activeChar);

			Servitor[] servitors = activeChar.getServitors();
			if(servitors.length > 0)
			{
				for(Servitor servitor : servitors)
					sendPacket(new PetInfoPacket(servitor));
			}

			if(activeChar.isInParty())
			{
				//sends new member party window for all members
				//we do all actions before adding member to a list, this speeds things up a little
				sendPacket(new PartySmallWindowAllPacket(activeChar.getParty(), activeChar));

				for(Player member : activeChar.getParty().getPartyMembers())
					if(member != activeChar)
					{
						sendPacket(new PartySpelledPacket(member, true));
						if(servitors.length > 0)
						{
							for(Servitor servitor : servitors)
								sendPacket(new PartySpelledPacket(servitor, true));
						}

						sendPacket(RelationChangedPacket.update(activeChar, member, activeChar));
					}

				// Если партия уже в СС, то вновь прибывшем посылаем пакет открытия окна СС
				if(activeChar.getParty().isInCommandChannel())
					sendPacket(ExOpenMPCCPacket.STATIC);
			}

			for(int shotId : activeChar.getAutoSoulShot())
				sendPacket(new ExAutoSoulShot(shotId, true));

			for(Effect e : activeChar.getEffectList().getFirstEffects())
			{
				if(e.getSkill().isToggle())
					sendPacket(new MagicSkillLaunchedPacket(activeChar.getObjectId(), e.getSkill().getId(), e.getSkill().getLevel(), activeChar));
			}

			activeChar.broadcastCharInfo();
		}

		if(activeChar.isDead())
			sendPacket(new DiePacket(activeChar));

		activeChar.updateEffectIcons();
		activeChar.updateStats();

		if(Config.ALT_PCBANG_POINTS_ENABLED)
		{
			if(!Config.ALT_PCBANG_POINTS_ONLY_PREMIUM || activeChar.hasBonus())
				activeChar.sendPacket(new ExPCCafePointInfoPacket(activeChar, 0, 1, 2, 12));
		}

		if(!activeChar.getPremiumItemList().isEmpty())
			activeChar.sendPacket(ExNotifyPremiumItem.STATIC);
		
		activeChar.checkLevelUpReward();

		if(first)
		{
			activeChar.useTriggers(activeChar, TriggerType.ON_ENTER_WORLD, null, null, 0);
			loadTutorial(activeChar);
		}
		activeChar.sendPacket(new ExBR_PremiumStatePacket(activeChar, activeChar.hasBonus()));
	}

	private void loadTutorial(Player player)
	{
		Quest q = QuestManager.getQuest(255);
		if(q != null)
			player.processQuestEvent(q.getName(), "UC", null);
	}

	private void checkNewMail(Player activeChar)
	{
		activeChar.sendPacket(new ExUnReadMailCount(activeChar));
		for(Mail mail : MailDAO.getInstance().getReceivedMailByOwnerId(activeChar.getObjectId()))
		{
			if(mail.isUnread())
			{
				activeChar.sendPacket(ExNoticePostArrived.STATIC_FALSE);
				break;
			}
		}
	}
}
package ai;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.Location;

/**
 * Яйца в инстансе Teredor по дороге к боссу.
 * При агре на персонажа суммонят рандомного монстра и на всю пати вешает пойзон
 *
 * @author coldy
 */
public class TeredorEggs extends Fighter
{
	//Teredor info
	// 19160 - под землей (или на проходе или стадия под землей)
	// 19172 - тоже самое что и 19160
	// 25785 - тоже самое
	// 25801 - так же

	// Eggs info
	// 18996 - обычное
	// 18997 - синее
	private static int teredorEgg = 18997;
							   //Hatched Millipede, Hatched Worm
	private static int[] eggMonsters = { 18993, 18994, 18995};

	private static int monsterSpawnDelay = 3; // секунды
	
	//14561	1	a,Teredor Poison Area\0
	private static int poisonId = 14561;
	private static int poisonLevel = 1;
	private static int distanceToDebuff = 400;

	boolean _activated = false;

	private NpcInstance actor = getActor();

	public TeredorEggs(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}

	@Override
	protected void thinkAttack()
	{
		if(!_activated)
		{
			if(actor.getAggroList().getMostHated(getMaxPursueRange()) == null || !actor.getAggroList().getMostHated(getMaxPursueRange()).isPlayer())
				return;
			Player player = (Player) actor.getAggroList().getMostHated(getMaxPursueRange());
			Reflection ref = actor.getReflection();
			ThreadPoolManager.getInstance().schedule(new SpawnMonster(actor, player, ref), monsterSpawnDelay*1000);

			// TODO: реализовать через зоны. (или это скилл с типом аура?)
			// пойзон должен накладываться при заходе в зону. Зона активируется при входе в агрорендж яйца.
			// Вешаем на всю пати, в том числе и на петов пойзон
			if(player.getParty() != null)
			{
				for (Playable playable : player.getParty().getPartyMembersWithPets()){
					// Вешаем пойзон, если мембер пати не дальше 400 (регулируется переменной)
					if(playable != null && actor.getDistance(playable.getLoc()) <= distanceToDebuff)
						actor.doCast(SkillHolder.getInstance().getSkill(poisonId, poisonLevel), playable, true);
				}
			}
			_activated = true;
		}
	}

	private class SpawnMonster extends RunnableImpl
	{
		NpcInstance _npc;
		Player _player;
		Reflection _ref;

		public SpawnMonster(NpcInstance npc, Player player, Reflection ref)
		{
			_npc = npc;
			_player = player;
			_ref = ref;
		}

		@Override
		public void runImpl()
		{
			if((_npc != null) && (!_npc.isDead()))
			{
				if(_player != null)
				{
					_npc.decayMe();
					int chosenMonster = eggMonsters[Rnd.get(eggMonsters.length)]; // Выбираем рандомно одного из 2х монстров в массиве
					Location coords = Location.findPointToStay(actor,100,120);
					NpcInstance npc = _ref.addSpawnWithoutRespawn(chosenMonster, coords, 0);

					if(npc.getNpcId() == 18994)
						npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, _player, Rnd.get(1, 100));
				}
				else
					_npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			}
		}
	}

}
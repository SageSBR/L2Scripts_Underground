package instances;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.EarthQuakePacket;
import quests.*;

import java.util.HashMap;
import java.util.Map;


/**
 * Author: Cain
 */
// reworked by Evil_dnk
public class IncubatorOfEvil extends Reflection {
    protected static int[][] QuestRace = new int[][] { { 0 }, { 1 }, { 2 }, { 3 }, { 4 }, { 5 } };

    protected static Map<Integer, Class<?>> Quests = new HashMap<Integer, Class<?>>();

	protected int playerQuestId;

    static
    {
        Quests.put(10341, _10341_DayOfDestinyHumanFate.class);
        Quests.put(10342, _10342_DayOfDestinyElvenFate.class);
        Quests.put(10343, _10343_DayOfDestinyDarkElfsFate.class);
        Quests.put(10344, _10344_DayOfDestinyOrcsFate.class);
        Quests.put(10345, _10345_DayOfDestinyDwarfsFate.class);
        Quests.put(10346, _10346_DayOfDestinyKamaelsFate.class);
    }


    private int[] mobsIds = {
            27434,          //Guardian of Darknes
            27431,          //Slayer
            27432,          //Pursuer
            27433,          //Priest of Darkness
            27430           //Screaming Shaman
    };

    private static final String STAGE_1 = "ioe_attack1_1";
    private static final String STAGE_2 = "ioe_attack1_2";
    private static final String STAGE_3 = "ioe_attack1_3";
    private static final String STAGE_4 = "ioe_attack1_4";
    private static final String STAGE_5 = "ioe_attack1_5";
    private static final String STAGE_6 = "ioe_attack2_1";
    private static final String STAGE_7 = "ioe_attack2_2";
    private static final String STAGE_8 = "ioe_attack2_3";
    private static final String STAGE_9 = "ioe_attack2_4";
    private static final String STAGE_10 = "ioe_attack2_5";

    private DeathListener _deathListener = new DeathListener();

    private int stage = 0;

    public IncubatorOfEvil(Player player, int qId) {
        setReturnLoc(player.getLoc());
		playerQuestId = qId;
    }

    @Override
    public void onPlayerEnter(final Player player) {
        super.onPlayerEnter(player);
    }


    private class SpawnStage extends RunnableImpl {
        private String stage;

        public SpawnStage(String stage) {
            this.stage = stage;
        }
        
        @Override
        public void runImpl() throws Exception {
            spawnByGroup(stage);
            invokeDeathListener();
        }
    }

    public void nextStage()
    {
        stageStart(stage + 1);
    }

    public int getStage()
    {
        return stage;
    }

    public void stageStart(int nStage)
    {
        stage = nStage;

        switch (nStage){
            case 1: ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_1), 3000);
                break;
            case 2: ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_2), 1000);
                break;
            case 3: ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_3), 1000);
                break;
            case 4: ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_4), 1000);
                break;
            case 5: ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_5), 1000);
                break;
            case 6: ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_6), 1000);
                break;
            case 7: ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_7), 1000);
                break;
            case 8: ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_8), 1000);
                break;
            case 9: ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_9), 1000);
                break;
            case 10: ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_10), 1000);
                break;
        }
    }

    private void invokeDeathListener() {
        for (int mobid : mobsIds)
            for (NpcInstance mob : getAllByNpcId(mobid, true))
                mob.addListener(_deathListener);
    }

    public QuestState findQuest(Player player)
    {
		return player.getQuestState(Quests.get(playerQuestId));
		/*
        QuestState st = null;
        for(Integer q : Quests.keySet())
        {
            st = player.getQuestState(Quests.get(q));
            if(st != null)
            {
                int[] qc = QuestRace[q - 10341];
                for(int c : qc)
                {
                    if(player.getRace().ordinal() == c)
                        return st;
                }
            }
        }
        return null;*/
    }

    private class DeathListener implements OnDeathListener {
        @Override
        public void onDeath(Creature victim, Creature killer) {

            QuestState qs = findQuest(getPlayers().get(0));

            if (getAllByNpcId(27434, true).isEmpty() && getAllByNpcId(27431,true).isEmpty() && getAllByNpcId(27432,true).isEmpty() && getAllByNpcId(27433,true).isEmpty() && getAllByNpcId(27430,true).isEmpty())
            {
                if (getStage() == 1)
                {
                    getPlayers().get(0).sendPacket(new ExShowScreenMessage(NpcString.CREATURES_RESURRECTED__DEFEND_YOURSELF, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
                }
                if (getStage() == 2)
                {
                    getPlayers().get(0).sendPacket(new ExShowScreenMessage(NpcString.CREATURES_RESURRECTED__DEFEND_YOURSELF, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
                }
                if (getStage() == 3)
                {
                    getPlayers().get(0).sendPacket(new ExShowScreenMessage(NpcString.CREATURES_RESURRECTED__DEFEND_YOURSELF, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
                }
                if (getStage() == 4)
                {
                    getPlayers().get(0).sendPacket(new ExShowScreenMessage(NpcString.CREATURES_RESURRECTED__DEFEND_YOURSELF, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
                }
                if (getStage() == 5)
                {
                    qs.setCond(9);
                    getPlayers().get(0).sendPacket(new ExShowScreenMessage(NpcString.CREATURES_HAVE_STOPPED_THEIR_ATTACK_REST_AND_THEN_SPEAK_WITH_ADOLPH, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
                    return;
                }
                if (getStage() == 6)
                {
                    getPlayers().get(0).sendPacket(new ExShowScreenMessage(NpcString.CREATURES_RESURRECTED__DEFEND_YOURSELF, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
                }
                if (getStage() == 7)
                {
                    getPlayers().get(0).sendPacket(new ExShowScreenMessage(NpcString.CREATURES_RESURRECTED__DEFEND_YOURSELF, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
                }
                if (getStage() == 8)
                {
                    getPlayers().get(0).sendPacket(new ExShowScreenMessage(NpcString.CREATURES_RESURRECTED__DEFEND_YOURSELF, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
                }
                if (getStage() == 9)
                {
                    getPlayers().get(0).sendPacket(new ExShowScreenMessage(NpcString.I_DEATH_WOUND_CHAMPION_OF_SHILEN_SHALL_END_YOUR_WORLD, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
                    getPlayers().get(0).broadcastPacket(new EarthQuakePacket(getPlayers().get(0).getLoc(), 40, 10));

                }

                if (getStage() < 10)
                    nextStage();
                else
                {
                    getPlayers().get(0).sendPacket(new ExShowScreenMessage(NpcString.AGH_HUMANS_HA_IT_DOES_NOT_MATTER_YOUR_WORLD_WILL_END_ANYWAYS, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
                }
            }
            else
                victim.removeListener(_deathListener);
        }
    }


}

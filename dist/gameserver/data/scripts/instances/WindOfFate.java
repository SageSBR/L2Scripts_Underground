package instances;

import ai.FollowNpc;
import ai.incubatorOfEvil.FerrinFAI;
import ai.incubatorOfEvil.VanHolterAAI;
import ai.incubatorOfEvil.VanHolterFAI;
import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.utils.Location;
import quests.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

//by Evil_dnk

public class WindOfFate extends Reflection {

    protected static Map<Integer, Class<?>> Quests = new HashMap<Integer, Class<?>>();

    static
    {
        Quests.put(10341, _10341_DayOfDestinyHumanFate.class);
    }


    private int[] mobsIds = {
            19569,
            19570,
            19571,
            19572,
            19573,
            19568
    };

    private static final String STAGE_1 = "wof_room1";
    private static final String STAGE_2 = "wof_room2";
    private static final String STAGE_3 = "wof_room2_1";
    private static final String STAGE_4 = "wof_room3";
    private static final String STAGE_5 = "wof_room3_2";
    private static final String STAGE_6 = "wof_room4";

    private NpcInstance FERIN = null;
    private NpcInstance Makkum = null;
    private NpcInstance Halter = null;

    private ScheduledFuture<?> _followtask;


    private DeathListener _deathListener = new DeathListener();

    private int stage = 0;


    public WindOfFate(Player player) {
        setReturnLoc(player.getLoc());
    }

    @Override
    public void onPlayerEnter(final Player player) {
        if(getFerrin() != null && stage > 1 && stage < 6)
            getFerrin().teleToLocation(player.getLoc());
        getFerrin().setFollowTarget(player);

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
            case 1:
                ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_1), 3000);
                break;
            case 2:
                ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_2), 1000);
                openDoor(17230102);
                break;
            case 3:
                ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_3), 1000);
                break;
            case 4:
                ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_4), 1000);
                openDoor(17230103);
                break;
            case 5:
                ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_5), 1000);
                break;
            case 6:
                ThreadPoolManager.getInstance().schedule(new SpawnStage(STAGE_6), 1000);
                openDoor(17230104);
                getFerrin().setFollowTarget(getVanHalter());
                Functions.npcSay(getVanHalter(), NpcString.THATS_THE_MONSTER_THAT_ATTACKED_FAERON_YOURE_OUTMATCHED_HERE_GO_AHEAD_ILL_CATCH_UP);
                if(getPlayers() != null)
                    getPlayers().get(0).sendPacket(new ExShowScreenMessage(NpcString.LEAVE_THIS_PLACE_TO_KAINNGO_TO_THE_NEXT_ROOM, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                spawnByGroup("q10753_16_instance_grail");
                break;
        }
    }

    private void invokeDeathListener() {
        for (int mobid : mobsIds)
            for (NpcInstance mob : getAllByNpcId(mobid, true))
                mob.addListener(_deathListener);
    }


    private class DeathListener implements OnDeathListener {
        @Override
        public void onDeath(Creature victim, Creature killer) {

            if (getAllByNpcId(19569, true).isEmpty() && getAllByNpcId(19570,true).isEmpty() && getAllByNpcId(19571,true).isEmpty() && getAllByNpcId(19572,true).isEmpty() && getAllByNpcId(19573,true).isEmpty() && getAllByNpcId(19568,true).isEmpty())
            {

                if (getStage() < 10)
                    nextStage();
            }
            else
                victim.removeListener(_deathListener);
        }
    }

    public NpcInstance getVanHalter()
    {
        Halter = getAllByNpcId(33979, true).get(0);
        return Halter;
    }

    public NpcInstance getMakkum()
    {
        Makkum = getAllByNpcId(19571, true).get(0);
        return Makkum;
    }

    public NpcInstance getFerrin()
    {
        FERIN = getAllByNpcId(34001, true).get(0);
        return FERIN;
    }

    public void initFriend(Player player)
    {
        // spawn npc helpers
        getVanHalter().deleteMe();
        getFerrin().deleteMe();
        NpcInstance kain = addSpawnWithoutRespawn(33979, new Location(-88504, 184680, -10476, 49151), 150);
        kain.setAI(new FollowNpc(kain));
        kain.setRunning();
        kain.setFollowTarget(player);
        NpcInstance fairy = addSpawnWithoutRespawn(34001, new Location(-88504, 184680, -10476, 49151), 150);
        fairy.setAI(new FerrinFAI(fairy));
        fairy.setRunning();
        fairy.setFollowTarget(player);
        fairy.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, player, 100);
        if(_followtask != null)
            _followtask.cancel(true);

        _followtask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new FollowTask(), 2000L, 7000L);
        openDoor(17230101);
    }

    public class FollowTask extends RunnableImpl
    {
        public FollowTask()
        {
            //
        }

        @Override
        public void runImpl()
        {
            List<NpcInstance> around = getVanHalter().getAroundNpc(800, 150);
            if (around != null && !around.isEmpty())
            {
                for (NpcInstance npc : around)
                {
                    int _id = npc.getNpcId();
                    if (_id != 34001)
                    {
                        getVanHalter().setSpawnedLoc(getVanHalter().getLoc());
                        getVanHalter().broadcastCharInfoImpl();
                        getVanHalter().setAI(new VanHolterAAI(getVanHalter()));
                        break;
                    }
                    else
                    {
                        if (getVanHalter().getTarget() == null && around.size() == 1)
                        {
                            getVanHalter().setSpawnedLoc(getVanHalter().getLoc());
                            getVanHalter().broadcastCharInfoImpl();
                            getVanHalter().setAI(new VanHolterFAI(getVanHalter()));
                            if(getPlayers() != null)
                                getVanHalter().setFollowTarget(getPlayers().get(0));
                        }
                    }
                }
            }
            else
            {
                getVanHalter().setSpawnedLoc(getVanHalter().getLoc());
                getVanHalter().broadcastCharInfoImpl();
                getVanHalter().setAI(new VanHolterFAI(getVanHalter()));
                if(getPlayers() != null)
                    getVanHalter().setFollowTarget(getPlayers().get(0));
            }
        }
    }

    public void clear()
    {
        getVanHalter().deleteMe();
        getFerrin().deleteMe();
        if(_followtask != null)
            _followtask.cancel(true);
        getMakkum().deleteMe();
    }
}

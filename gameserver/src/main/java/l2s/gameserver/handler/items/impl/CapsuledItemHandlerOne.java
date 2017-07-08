package l2s.gameserver.handler.items.impl;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.item.CapsuledItem;
import l2s.gameserver.utils.ItemFunctions;

import java.util.List;

/**
 * Created by ���� on 23.02.2016.
 */
public class CapsuledItemHandlerOne extends DefaultItemHandler
{
    @Override
    public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
    {
        Player player;
        if(playable.isPlayer())
            player = (Player) playable;
        else if(playable.isPet())
            player = playable.getPlayer();
        else
            return false;

        int itemId = item.getItemId();

        if(!canBeExtracted(player, item))
            return false;

        if(!reduceItem(player, item))
            return false;

        List<CapsuledItem> capsuled_items = item.getTemplate().getCapsuledItems();

        int vChance = Rnd.get(capsuled_items.size()-1);
        if(vChance >= 0 && vChance < (capsuled_items.size()-1))
        {
            CapsuledItem oItem = capsuled_items.get(vChance);
            if(oItem != null) {
                ItemFunctions.addItem(player, oItem.getItemId(), 1, true);
            }
        }
        /*for(CapsuledItem ci : capsuled_items)
        {
            if(Rnd.chance(ci.getChance()))
            {
                ItemFunctions.addItem(player, ci.getItemId(), 1, true);
            }
        }*/

        player.sendPacket(SystemMessagePacket.removeItems(itemId, 1));
        return true;
    }
}

package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.templates.item.product.ProductItem;
import l2s.gameserver.templates.item.product.ProductItemComponent;

public abstract class L2GameProductPacket extends L2GameServerPacket {
    protected void writeComponents(ProductItem product) {
        for (ProductItemComponent component : product.getComponents()) {
            writeD(component.getItemId());                      // item id
            writeD(component.getCount());                       // quality
            writeD(component.getWeight());                      // weight
            writeD(component.isDropable() ? 1 : 0);             // FIXME: 01.03.2016 is tradable, not dropable
        }
    }
}

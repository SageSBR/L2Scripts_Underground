package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.data.xml.holder.ProductDataHolder;
import l2s.gameserver.templates.item.product.ProductItem;
import l2s.gameserver.templates.item.product.ProductItemComponent;

public class ExBR_ProductInfoPacket extends L2GameProductPacket {
    private ProductItem product;

    public ExBR_ProductInfoPacket(int id) {
        this.product = ProductDataHolder.getInstance().getProduct(id);
    }

    @Override
    protected void writeImpl() {
        if (product == null) {
            return;
        }

        writeD(product.getId()); //product id
        writeD(product.getPrice(true)); // points
        writeD(product.getComponents().size()); //size
        writeComponents(product);
    }
}
package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.data.xml.holder.ProductDataHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.templates.item.product.ProductItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * upgradet to Ertheia [603] by Bonux
 **/
public class ExBR_ProductListPacket extends L2GameProductPacket {
    private final long adena;
    private final boolean history;
    private List<ProductItem> productItemList = new ArrayList<>();

    public ExBR_ProductListPacket(Player player, boolean history) {
        this.adena = player.getAdena();
        this.history = history;
        if (history) {
            this.productItemList.addAll(player.getProductHistoryList().productValues());
        }
        else {
            this.productItemList.addAll(ProductDataHolder.getInstance().getProductsOnSale());
            Collections.sort(productItemList);
        }
    }

    @Override
    protected void writeImpl() {
        writeQ(adena);                                              // Player Adena Count
        writeQ(0x00);                                               // hero coin
        writeC(history);                                            // producst list type (0 - store)
        writeD(productItemList.size());
        for (ProductItem product : productItemList) {
            writeD(product.getId());                                // product id
            writeC(product.getCategory());                          // category 1 - Main (?) 2 - supplies 3 - Cosmetic 4 - Species  5 - enchant
            writeC(product.getPriceUnit());                         // price unit (0 is coin)
            writeD(product.getPrice(true));                         // price
            writeC(product.getTabId());                             // tab id
            writeC(product.getSection());                           // section: 0 - not on front page, 1 - Featured, 2 - Recommended, 4 - Popular
            writeC(0x00);
            writeC(0x00);
            writeC(0x00);
            writeD((int) (product.getStartTimeSale() / 1000));      // start sale unix date in seconds
            writeD((int) (product.getEndTimeSale() / 1000));        // end sale unix date in seconds
            writeC(127);                                            // day week (127 = not daily goods)
            writeC(product.getStartHour());                         // start hour
            writeC(product.getStartMin());                          // start min
            writeC(product.getEndHour());                           // end hour
            writeC(product.getEndMin());                            // end min
            writeD(0x00);                                           // stock -1/0
            writeD(-1);                                             // max stock -1/0
            writeC(product.getDiscount()); // % скидки
            writeC(0x00);                                           // Level restriction
            writeC(0x00);                                           // UNK
            writeD(0x00);                                           // UNK
            writeD(0x00);                                           // UNK
            writeD(0x00);                                           // Repurchase interval (days)
            writeD(0x00);                                           // Amount (per account)
            writeC(product.getComponents().size());                 // Related Items
            writeComponents(product);
        }
    }
}
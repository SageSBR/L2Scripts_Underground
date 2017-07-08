package l2s.gameserver.data.xml.parser;

import l2s.commons.data.xml.AbstractFileParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.ProductDataHolder;
import l2s.gameserver.templates.item.product.ProductItem;
import l2s.gameserver.templates.item.product.ProductItemComponent;
import org.dom4j.Element;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;


public final class ProductDataParser extends AbstractFileParser<ProductDataHolder> {
    private static final ProductDataParser _instance = new ProductDataParser();

    public static ProductDataParser getInstance() {
        return _instance;
    }

    private ProductDataParser() {
        super(ProductDataHolder.getInstance());
    }

    @Override
    public File getXMLFile() {
        return new File(Config.DATAPACK_ROOT, "data/product_data.xml");
    }

    @Override
    protected void readData(Element rootElement) throws Exception {
        for (Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext(); ) {
            Element element = iterator.next();
            if ("config".equalsIgnoreCase(element.getName())) {
                Config.IM_PAYMENT_ITEM_ID = element.attributeValue("payment_item_id") == null ? -1 : Integer.parseInt(element.attributeValue("payment_item_id"));
                Config.IM_MAX_ITEMS_IN_RECENT_LIST = element.attributeValue("recent_list_size") == null ? 5 : Integer.parseInt(element.attributeValue("recent_list_size"));
            }
            else if ("product".equalsIgnoreCase(element.getName())) {
                int productId = Integer.parseInt(element.attributeValue("id"));
                int category = Integer.parseInt(element.attributeValue("category"));
                int price = Integer.parseInt(element.attributeValue("price"));
                boolean isEvent = element.attributeValue("is_event") != null && Boolean.parseBoolean(element.attributeValue("is_event"));
                boolean isBest = element.attributeValue("is_best") != null && Boolean.parseBoolean(element.attributeValue("is_best"));
                boolean isNew = element.attributeValue("is_new") != null && Boolean.parseBoolean(element.attributeValue("is_new"));
                boolean onSale = element.attributeValue("on_sale") != null && Boolean.parseBoolean(element.attributeValue("on_sale"));
                long startTimeSale = element.attributeValue("sale_start_date") == null ? 0 : getMillisecondsFromString(element.attributeValue("sale_start_date"));
                long endTimeSale = element.attributeValue("sale_end_date") == null ? 0 : getMillisecondsFromString(element.attributeValue("sale_end_date"));
                int discount = element.attributeValue("discount") == null ? 0 : Integer.parseInt(element.attributeValue("discount"));
                int locationId = element.attributeValue("location_id") == null ? -1 : Integer.parseInt(element.attributeValue("location_id"));

                ProductItem product = new ProductItem(productId, category, price, startTimeSale, endTimeSale, onSale, discount, locationId, isBest, isEvent, isNew);
                for (Iterator<Element> subIterator = element.elementIterator(); subIterator.hasNext(); ) {
                    Element subElement = subIterator.next();
                    if ("component".equalsIgnoreCase(subElement.getName())) {
                        int item_id = Integer.parseInt(subElement.attributeValue("item_id"));
                        int count = Integer.parseInt(subElement.attributeValue("count"));

                        product.addComponent(new ProductItemComponent(item_id, count));
                    }
                }

                getHolder().addProduct(product);
            }
        }
    }

    private static long getMillisecondsFromString(String datetime) {
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        try {
            Date time = df.parse(datetime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(time);

            return calendar.getTimeInMillis();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}
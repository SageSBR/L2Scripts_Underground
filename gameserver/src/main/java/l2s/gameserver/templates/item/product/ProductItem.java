package l2s.gameserver.templates.item.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

import l2s.commons.util.Rnd;

public class ProductItem implements Comparable<ProductItem> {
    private static final int EVENT_MASK = 1 << 0;
    private static final int BEST_MASK = 1 << 1;
    private static final int NEW_MASK = 1 << 2;

    // Базовые параметры, если продукт не имеет лимита времени продаж
    public static final long NOT_LIMITED_START_TIME = 315547200000L;
    public static final long NOT_LIMITED_END_TIME = 2127445200000L;
    public static final int NOT_LIMITED_START_HOUR = 0;
    public static final int NOT_LIMITED_END_HOUR = 23;
    public static final int NOT_LIMITED_START_MIN = 0;
    public static final int NOT_LIMITED_END_MIN = 59;

    private final int id;
    private final int category;
    private final int priceUnit;
    private final int price;
    private final int tabId;
    private final int locationId;

    private final long startTimeSale;
    private final long endTimeSale;
    private final int startHour;
    private final int endHour;
    private final int startMin;
    private final int endMin;
    private final int discount;
    private final int mainPageCategory;
    private final int section;

    private int boughtCount = 0;

    private final boolean onSale;
    private final boolean isBest;
    private final boolean isEvent;
    private final boolean isNew;

    private final List<ProductItemComponent> components = new ArrayList<>();

    public ProductItem(int id, int category, int price, long startTimeSale, long endTimeSale, boolean onSale, int discount, int locationId,
                       boolean isBest, boolean isEvent, boolean isNew) {
        this.id = id;
        this.category = category;
        this.price = price;
        this.onSale = onSale;
        this.discount = discount;
        this.locationId = locationId;
        this.mainPageCategory = Rnd.get(new int[]{0, 1, 2, 4});
        this.isBest = isBest;
        this.isEvent = isEvent;
        this.isNew = isNew;
        this.tabId = getProductTabId();
        this.priceUnit = 0;

        int rnd = Rnd.get(0, 5);
        this.section = rnd == 3 ? 0 : rnd; // because is there no 3 section in sniffs

        Calendar calendar;
        if (startTimeSale > 0) {
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(startTimeSale);

            this.startTimeSale = startTimeSale;
            this.startHour = calendar.get(Calendar.HOUR_OF_DAY);
            this.startMin = calendar.get(Calendar.MINUTE);
        }
        else {
            this.startTimeSale = NOT_LIMITED_START_TIME;
            this.startHour = NOT_LIMITED_START_HOUR;
            this.startMin = NOT_LIMITED_START_MIN;
        }

        if (endTimeSale > 0) {
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(endTimeSale);

            this.endTimeSale = endTimeSale;
            this.endHour = calendar.get(Calendar.HOUR_OF_DAY);
            this.endMin = calendar.get(Calendar.MINUTE);
        }
        else {
            this.endTimeSale = NOT_LIMITED_END_TIME;
            this.endHour = NOT_LIMITED_END_HOUR;
            this.endMin = NOT_LIMITED_END_MIN;
        }
    }

    public void addComponent(ProductItemComponent component) {
        components.add(component);
    }

    public List<ProductItemComponent> getComponents() {
        return components;
    }

    public int getId() {
        return id;
    }

    public int getCategory() {
        return category;
    }

    public int getPrice(boolean withDiscount) {
        if (withDiscount) {
            return (int) (price * ((100 - discount) * 0.01));
        }
        return price;
    }

    public int getPriceUnit() {
        return priceUnit;
    }

    public int getTabId() {
        return tabId;
    }

    public int getSection() {
        return section;
    }

    public int getLocationId() {
        return locationId;
    }

    public long getStartTimeSale() {
        return startTimeSale;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMin() {
        return startMin;
    }

    public long getEndTimeSale() {
        return endTimeSale;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMin() {
        return endMin;
    }

    public boolean isOnSale() {
        return onSale;
    }

    public int getDiscount() {
        return discount;
    }

    public int getMainPageCategory() {
        return mainPageCategory;
    }

    public void setBoughtCount(int val) {
        boughtCount = val;
    }

    public int getBoughtCount() {
        return boughtCount;
    }

    public int getProductTabId() {
        int val = 0;

        if (isEvent) {
            val |= EVENT_MASK;
        }

        if (isBest) {
            val |= BEST_MASK;
        }

        if (isNew) {
            val |= NEW_MASK;
        }

        return val;
    }

    @Override
    public int compareTo(ProductItem o) {
        return o.getId() - getId();
    }
}
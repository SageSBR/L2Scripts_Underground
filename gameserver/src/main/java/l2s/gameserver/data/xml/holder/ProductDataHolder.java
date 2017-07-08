package l2s.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Collection;
import java.util.stream.Collectors;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.templates.item.product.ProductItem;

/**
 * @author Bonux
 **/
public final class ProductDataHolder extends AbstractHolder {
    private static final ProductDataHolder instance = new ProductDataHolder();
    private final TIntObjectMap<ProductItem> products = new TIntObjectHashMap<>();

    public static ProductDataHolder getInstance() {
        return instance;
    }

    public void addProduct(ProductItem product) {
        products.put(product.getId(), product);
    }

    public Collection<ProductItem> getProducts() {
        return products.valueCollection();
    }

    public Collection<ProductItem> getProductsOnSale() {
        long st = System.currentTimeMillis();
        return getProducts().stream().filter((p) -> p.isOnSale() && p.getStartTimeSale() < st && p.getEndTimeSale() > st).collect(Collectors.toList());
    }

    public ProductItem getProduct(int id) {
        return products.get(id);
    }

    @Override
    public int size() {
        return products.size();
    }

    @Override
    public void clear() {
        products.clear();
    }
}

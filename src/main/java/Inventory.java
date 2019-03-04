import java.util.*;

/***
 * Abstraction for inventory/product bookkeeping. public methods are synchronized to prevent concurrent writes.
 * Requirements:
 *  - Each seller starts with n items (e.g., n boars) to sell; upon selling all n items,
 *  the seller picks another item at random and becomes a seller of that item.
 */
public class Inventory {
    private Map<ItemType, Integer> inventoryMap;
    private final static List<ItemType> productList = Arrays.asList(ItemType.values());
    public Inventory(ItemType itemType){
        this.resetToItemType(itemType);
    }

    public Inventory(){
        ItemType itemType = randomizeItemType();
        this.resetToItemType(itemType);
    }

    public synchronized boolean isSellingItem(ItemType itemType){
        return getSellingItem().equals(itemType);
    }

    public synchronized ItemType getSellingItem(){
        if (this.inventoryMap.keySet().size() == 0) return null;
        return this.inventoryMap.keySet().iterator().next();

    }

    public boolean take(ItemType product){
        if (!this.inventoryMap.containsKey(product) || this.inventoryMap.get(product) <= 0){
            return false;
        }
        int newProductCount = this.inventoryMap.get(product) - 1;
        if (newProductCount <= 0){
            this.resetToItemType(randomizeItemType());
        } else {
            this.inventoryMap.put(product, newProductCount);
        }

        return true;
    }

    public synchronized  boolean add(ItemType product){
        if (!this.inventoryMap.containsKey(product)){
            return false;
        }

        this.inventoryMap.put(product, this.inventoryMap.get(product) + 1);
        return true;
    }

    public synchronized void resetToItemType(ItemType itemType){
        this.inventoryMap = new HashMap<>();
        this.inventoryMap.put(itemType, ConfigService.getInstance().getInventoryCount());
    }

    public static ItemType randomizeItemType(){
        int i = new Random().nextInt(Inventory.productList.size());
        return Inventory.productList.get(i);
    }

    @Override
    public String toString(){
        ItemType key = getSellingItem();
        if (key == null) return "Inventory[Empty]";
        return String.format("Inventory[%s = %s]", key, this.inventoryMap.getOrDefault(key, null));
    }

    public static Inventory buildInventoryForPeerType(PeerType type){

        if (type.equals(PeerType.BUYER)){
            return new Inventory();
        } else if (type.equals(PeerType.SELLER)){
            return new Inventory();
        } else {
            throw new IllegalArgumentException("Unable to handle PeerType:" + type);
        }
    }
}

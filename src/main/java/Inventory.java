import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * Abstraction for inventory/product bookkeeping.
 * Requirements:
 *  - Each seller starts with n items (e.g., n boars) to sell; upon selling all n items,
 *  the seller picks another item at random and becomes a seller of that item.
 */
public class Inventory {
    private Map<String, Integer> inventoryMap;

    public Inventory(int n){
        this.inventoryMap = new HashMap<>();
        List<String> productList = Arrays.asList("boar", "salt", "fish");
        for(String product : productList){
            this.inventoryMap.put(product, n);
        }
    }

    public Inventory(){
        this(0);
    }

    public synchronized boolean take(String product){
        if (!this.inventoryMap.containsKey(product) || this.inventoryMap.get(product) <= 0){
            return false;
        }
        this.inventoryMap.put(product, this.inventoryMap.get(product) - 1);
        return true;
    }

    public synchronized boolean add(String product){
        if (!this.inventoryMap.containsKey(product)){
            return false;
        }

        this.inventoryMap.put(product, this.inventoryMap.get(product) + 1);
        return true;
    }

    public static Inventory buildInventoryForPeerType(PeerType type){
        if (type.equals(PeerType.BUYER)){
            return new Inventory();
        } else if (type.equals(PeerType.SELLER)){
            return new Inventory(ConfigService.getInventoryCount());
        } else {
            throw new IllegalArgumentException("Unable to handle PeerType:" + type);
        }
    }
}

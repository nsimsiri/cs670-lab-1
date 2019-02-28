import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory {
    private Map<String, Integer> inventoryMap;

    public Inventory(int n){
        this.inventoryMap = new HashMap<>();
        List<String> productList = Arrays.asList("boar", "salt", "fish");
        for(String product : productList){
            this.inventoryMap.put(product, n);
        }
    }

    public synchronized boolean take(String product){
        if (!this.inventoryMap.containsKey(product) || this.inventoryMap.get(product) < 0){
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
}

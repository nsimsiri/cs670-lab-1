import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/***
 * This class determines which if transactions (buyer, product, time) to other nodes have completed
 * The way we use this class is
 * - In lookup(..) we iterate through neighbors. This class keeps track of how many paths we've taken
 * - In lookup(..) if a transaction has happened, we know we have already been here, so we do not traverse the neighbor
 * - In reply(..) we only reply to the path we've traversed. We check if this transaction's count is 0
 * -    > if count is not 0 --> don't reply, wait until everybody else finished.
 * -    > if count is 0 --> reply since we know everybody finished.
 */
public class StackMerger {

    private HashMap<Lookup, LookupInfo> map;

    public StackMerger(){
        this.map = new HashMap<>();
    }

    public synchronized boolean containsLookup(Lookup lookup){
        return this.map.containsKey(lookup);
    }

    public synchronized void createLookup(Lookup lookup, int count){
        LookupInfo info = new LookupInfo(count);
        this.map.put(lookup, info);
    }

    public synchronized void removeLookup(Lookup lookup){
        this.map.remove(lookup);
    }

    public synchronized Integer getLookupCount(Lookup lookup){
        if (!this.map.containsKey(lookup)){
            throw new IllegalArgumentException("Look up not found " + lookup);
        }

        LookupInfo info = this.map.get(lookup);
        return info.count;
    }

    public synchronized Integer decrementLookup(Lookup lookup){
        if (!this.map.containsKey(lookup)){
            throw new IllegalArgumentException("Look up not found " + lookup);
        }

        LookupInfo info = this.map.get(lookup);
        if (info.count == 1){
            removeLookup(lookup);
        } else {
            info.count--;
        }
        return info.count;
    }

    public synchronized List<String> getLookupSellers(Lookup lookup){
        if (!this.map.containsKey(lookup)){
            throw new IllegalArgumentException("Look up not found " + lookup);
        }

        LookupInfo info = this.map.get(lookup);
        return info.potentialSellers;
    }

    public synchronized void addLookupSellers(Lookup lookup, List<String> sellers){
        if (!this.map.containsKey(lookup)){
            throw new IllegalArgumentException("Look up not found " + lookup);
        }

        LookupInfo info = this.map.get(lookup);
        info.potentialSellers.addAll(sellers);
    }

    public static class LookupInfo {
        public Integer count;
        public ArrayList<String> potentialSellers;
        public LookupInfo(Integer count){
            this.count = count;
            this.potentialSellers = new ArrayList<>();
        }

        @Override
        public String toString(){
            return String.format("LookupInfo[%s %s]", count, potentialSellers);
        }
    }

    @Override
    public String toString(){
        return String.format("StackMerger[%s]", this.map);
    }

    public static void main(String[] args){
        System.out.println(System.getProperty("user.dir"));
    }
}

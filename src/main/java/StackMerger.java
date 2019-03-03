import java.time.Instant;
import java.util.*;

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
        if (containsLookup(lookup)){
            throw new IllegalArgumentException("Look up already created " + lookup);
        }
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
        info.count--;
        return info.count;
    }

    public synchronized Set<String> getLookupSellers(Lookup lookup){
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

    public synchronized Integer getTotalCount(Lookup lookup){
        if (!this.map.containsKey(lookup)){
            throw new IllegalArgumentException("Look up not found " + lookup);
        }
        return this.map.get(lookup).totalCount;
    }

    public static class LookupInfo {
        public Integer count;
        public Integer totalCount;
        public HashSet<String> potentialSellers;
        public LookupInfo(Integer count){
            this.count = count;
            this.potentialSellers = new HashSet<>();
            this.totalCount = count;
        }

        @Override
        public String toString(){
            return String.format("LookupInfo[cnt=%s sell=%s]", count, potentialSellers);
        }
    }

    @Override
    public String toString(){
        return String.format("StackMerger[%s]", this.map);
    }

    public static void main(String[] args){
        System.out.println(System.getProperty("user.dir"));
        Long timestamp = Instant.now().toEpochMilli();
        System.out.println(timestamp);
        Lookup a = new Lookup("natcha", ItemType.BOARS, timestamp);
        Lookup b = new Lookup("natcha", ItemType.BOARS, timestamp);
        Lookup c = new Lookup("natcha", ItemType.SALT, timestamp);
        Lookup d = new Lookup("tom", ItemType.BOARS, timestamp);
        Lookup e = new Lookup("natcha", ItemType.BOARS, timestamp+1);

        StackMerger sm = new StackMerger();
        sm.createLookup(a, 2);
        sm.createLookup(e, 5);
        System.out.println(sm + " " + sm.getLookupCount(a));
        sm.decrementLookup(a);
        System.out.println(sm + " " + sm.getLookupCount(a));
        sm.decrementLookup(a);
        System.out.println(sm);
        sm.removeLookup(a);
        sm.createLookup(b, 3);
        sm.addLookupSellers(b, Arrays.asList("a","b"));
        System.out.println(sm);
        sm.addLookupSellers(b, Arrays.asList("a","d","e"));
        System.out.println(sm);
    }
}

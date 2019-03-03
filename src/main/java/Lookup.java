import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public class Lookup implements Serializable {
    private String buyerID;
    private ItemType productName;
    private Long timestamp;

    public Lookup(String buyerID, ItemType productName, Long timestamp){
        this.buyerID = buyerID;
        this.productName = productName;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o){
        Lookup other = (Lookup) o;
        return other.getBuyerID().equals(buyerID) &&
                other.getProductName().equals(productName) &&
                other.getTimestamp().equals(timestamp);
    }

    @Override
    public int hashCode(){
        return Objects.hash(buyerID, productName, timestamp);
    }

    public String getBuyerID() {
        return buyerID;
    }

    public void setBuyerID(String buyerID) {
        this.buyerID = buyerID;
    }

    public ItemType getProductName() {
        return productName;
    }

    public void setProductName(ItemType productName) {
        this.productName = productName;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString(){
        return String.format("Lookup[%s,%s,%s]", buyerID, productName, timestamp);
    }

    public static void main(String[] args){
        Long timestamp = Instant.now().toEpochMilli();
        System.out.println(timestamp);
        Lookup a = new Lookup("natcha", ItemType.BOARS, timestamp);
        Lookup b = new Lookup("natcha", ItemType.BOARS, timestamp);
        Lookup c = new Lookup("natcha", ItemType.SALT, timestamp);
        Lookup d = new Lookup("tom", ItemType.BOARS, timestamp);
        Lookup e = new Lookup("natcha", ItemType.BOARS, timestamp+1);
        System.out.println(a.equals(b));
        System.out.println(!a.equals(c));
        System.out.println(!a.equals(d));
        System.out.println(!a.equals(e));
        System.out.println(a);

    }
}

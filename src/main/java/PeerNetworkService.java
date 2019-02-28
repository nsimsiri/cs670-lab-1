import java.util.Arrays;
import java.util.List;

/***
 * Read only singleton, for managing registry.
 */
public class PeerNetworkService {
    private static PeerNetworkService self;
    private PeerNetworkService(){}

    public static PeerNetworkService getInstance(){
        if (self != null){
            self = new PeerNetworkService();
        }
        return self;
    }

    /***
     * Currently stub implementation.
     * @param myName - caller's name/identification
     * @return
     */
    public List<String> findNeighborNames(String myName){
        return Arrays.asList(myName.equals("A") ? "B" : "A");
    }

}

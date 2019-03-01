import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;

/***
 * Read only singleton, for managing registry.
 */
public class PeerNetworkService {
    private static PeerNetworkService self;

    private PeerNetworkService(){}

    private Registry localRegistry;
    private Registry remoteRegistry;

    public static PeerNetworkService getInstance() throws RemoteException {
        if (self != null){
            Registry remoteRegistry = LocateRegistry.getRegistry("edlab-ip", Registry.REGISTRY_PORT);
            Registry localRegistry = LocateRegistry.getRegistry("localhost", Registry.REGISTRY_PORT);
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

    public static void main(String[] args) throws Exception{
        PeerNetworkService pns = PeerNetworkService.getInstance();
    }

}

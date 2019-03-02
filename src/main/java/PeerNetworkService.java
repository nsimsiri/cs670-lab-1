import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/***
 * Read only singleton, for managing registry.
 */
public class PeerNetworkService {
    private static PeerNetworkService self;

    private PeerNetworkService() throws RemoteException {
//        Registry remoteRegistry = LocateRegistry.getRegistry("edlab-ip", Registry.REGISTRY_PORT);
//        Registry localRegistry = LocateRegistry.getRegistry(Registry.REGISTRY_PORT);
        Registry registry = null;
        try {
            registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        } catch(Exception e){
            registry = LocateRegistry.getRegistry(Registry.REGISTRY_PORT);
        } finally {
            this.localRegistry = registry;
        }
    }

    private Registry localRegistry;
    private Registry remoteRegistry;

    public static PeerNetworkService getInstance() throws RemoteException {
        if (self == null){
            self = new PeerNetworkService();
        }
        return self;
    }

    /***
     * Currently stub implementation.
     * @param myName - caller's name/identification
     * @return
     */
    public List<String> getNeighbors(String myName){
        ConfigService configService = ConfigService.getInstance();

        List<String> neighborStrings = new ArrayList<>();
        Map<String, String> stub = new HashMap<>();
        stub.put("A", "B");
        stub.put("B", "C");
        stub.put("C", "D");
        String x = stub.getOrDefault(myName, null);
        if (x!=null) neighborStrings.add(x);
        return neighborStrings;
    }

    public IPeer getPeerByName(String peerName){
        try {
            IPeer peer = (IPeer) this.localRegistry.lookup(peerName);
            return peer;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception{
        PeerNetworkService pns = PeerNetworkService.getInstance();
    }

}

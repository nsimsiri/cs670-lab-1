import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/***
 * Read only singleton, for managing registry.
 */
public class PeerNetworkService {
    private static PeerNetworkService self;
    private Logger logger = new Logger(PeerNetworkService.class.getSimpleName());

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
        /*
          neighbors_map = configService.edgeList();
          return neighbors_map.get(myName);
        */
        return neighborStrings;
    }

    public IPeer getPeerByName(String peerName){
        try {
            String[] info = peerName.split(",");
            String host = info[0];
            int port = Integer.parseInt(info[1]);
            Registry registry = LocateRegistry.getRegistry(host,port);
            IPeer peer = (IPeer) registry.lookup(peerName);
            return peer;
        } catch (Exception e){
            logger.severe(e.getMessage() + " " + Arrays.toString(e.getStackTrace()));
        }
        return null;
    }

    public Registry getLocalRegistry(){
        return this.localRegistry;
    }

    public static void main(String[] args) throws Exception{
        PeerNetworkService pns = PeerNetworkService.getInstance();
        List<String> x = pns.getNeighbors("A");
        System.out.println(x);
    }


}

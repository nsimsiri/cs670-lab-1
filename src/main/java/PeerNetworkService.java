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
    public Set<String> getNeighbors(String myName){
       return _getNeighbors(myName);
    }

    public Set<String> _getNeighbors(String myName){

        List<String> neighborStrings = new ArrayList<>();

        // test case 1 - line
//        Map<String, String> stub = new HashMap<>();
//        stub.put("A", "B"); stub.put("B", "C"); stub.put("C", "D");
//        String x = stub.getOrDefault(myName, null);
//        if (x!=null) neighborStrings.add(x);

        // test case 1 - star
        Map<String, List<String>> stub = new HashMap<>();
        stub.put("A", Arrays.asList("B", "C", "D"));
        neighborStrings = stub.getOrDefault(myName,new ArrayList<>());


        return new HashSet<>(neighborStrings);

    }

    public IPeer getPeerByName(String peerName){
        try {
            IPeer peer = (IPeer) this.localRegistry.lookup(peerName);
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
        Set<String> x = pns.getNeighbors("A");
        Set<String> y = pns.getNeighbors("B");

        System.out.println(x);
        System.out.println(y);
    }


}

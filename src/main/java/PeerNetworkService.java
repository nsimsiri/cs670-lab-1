import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.rmi.AlreadyBoundException;
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
    public Map<String, String[]> ipconfigmap;
    public Map<String, HashSet<String>> graphconfigmap;
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
        ConfigService config = ConfigService.getInstance();
        this.ipconfigmap = config.ipConfig();
        this.graphconfigmap = config.edgeList();
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
        return this.graphconfigmap.get(myName);

        /* Stubs start here */
//        return oneBuyerAllSellers(myName);
    }

    public Set<String> oneBuyerAllSellers(String myName){

        List<String> neighborStrings = new ArrayList<>();

        // test case 1 - line
//        Map<String, String> stub = new HashMap<>();
//        stub.put("A", "B"); stub.put("B", "C"); stub.put("C", "D");
//        String x = stub.getOrDefault(myName, null);
//        if (x!=null) neighborStrings.add(x);

        // test case 1 - star
//        Map<String, List<String>> stub = new HashMap<>();
//        stub.put("A", Arrays.asList("B", "C", "D"));
//        neighborStrings = stub.getOrDefault(myName,new ArrayList<>());

        Map<String, List<String>> stub = new HashMap<>();
        stub.put("A", Arrays.asList("B", "C"));
        stub.put("B", Arrays.asList("E", "F"));
        stub.put("C", Arrays.asList("G", "H"));
        neighborStrings = stub.getOrDefault(myName,new ArrayList<>());
        return new HashSet<>(neighborStrings);
    }

    public IPeer getPeerByName(String peerName){
        try {
            String[] rmi_array = this.ipconfigmap.get(peerName);
            String host = rmi_array[0];
            int port = Integer.parseInt(rmi_array[1]);
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

    public List<String> getNamesOnThisMachine(){
        String sep = File.separator;
        String path = System.getProperty("user.dir")+ String.format("%ssrc%smain%sresources%sMachineIP",
                sep,sep, sep, sep);
        ArrayList<String> nameList = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String machineip = br.readLine();
            for (Map.Entry<String, String[]> entry : this.ipconfigmap.entrySet()) {
                if(entry.getValue()[0].equals(machineip)){
                    nameList.add(entry.getKey());
                }
            }
        }
        catch(Exception e){
         e.printStackTrace();
        }

        return nameList;
    }

    public static void main(String[] args) throws Exception{
        PeerNetworkService pns = PeerNetworkService.getInstance();
        Set<String> x = pns.getNeighbors("5");
        System.out.println(x);


    }


}

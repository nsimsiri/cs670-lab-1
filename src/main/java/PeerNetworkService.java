
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
        Set<String> out = null;
        out = this.graphconfigmap.get(myName);

        /* stub tests start here */
//        out = line(myName);
//        out = star(myName);
//        out = tree(myName);
//        out = undirectedLine(myName);
//        out = undirectedStar(myName);
//        out = undirectedTree(myName);
//        out = undirectedSmallGraph(myName);
//        out = undirectedGraph(myName);
        /* stub tests ends */

        return out;
    }
    public Set<String> line(String myName){
        List<String> neighborStrings = new ArrayList<>();
        // test case 1 - line
        Map<String, String> stub = new HashMap<>();
        stub.put("A", "B"); stub.put("B", "C"); stub.put("C", "D");
        String x = stub.getOrDefault(myName, null);
        if (x!=null) neighborStrings.add(x);
        return new HashSet<>(neighborStrings);
    }

    public Set<String> star(String myName){
        List<String> neighborStrings = new ArrayList<>();
        // test case 1 - star
        Map<String, List<String>> stub = new HashMap<>();
        stub.put("A", Arrays.asList("B", "C", "D"));
        neighborStrings = stub.getOrDefault(myName,new ArrayList<>());
        return new HashSet<>(neighborStrings);
    }

    public Set<String> undirectedLine(String myName){
        String[][] edges = new String[][]{{"A", "B"}, {"B", "C"}, {"C", "D"}};
        Map<String,Set<String>> map = buildUndirectedGraph(edges);
        return map.getOrDefault(myName, new HashSet<>());
    }

    public Set<String> undirectedSmallGraph(String myName){
        String[][] edges = new String[][]{{"A", "B"}, {"B", "C"}, {"C", "D"}, {"D", "B"}};
        Map<String,Set<String>> map = buildUndirectedGraph(edges);
        return map.getOrDefault(myName, new HashSet<>());
    }

    public Set<String> undirectedStar(String myName){
        String[][] edges = new String[][]{{"A", "B"}, {"A", "C"}, {"A", "D"}};
        Map<String,Set<String>> map = buildUndirectedGraph(edges);
        return map.getOrDefault(myName, new HashSet<>());
    }

    public Set<String> undirectedTree(String myName){
        String[][] edges = new String[][]{{"A", "B"}, {"A", "C"}, {"B", "E"},
                {"B", "F"}, {"C", "G"}, {"C", "H"}, {"G", "D"}};
        Map<String,Set<String>> map = buildUndirectedGraph(edges);
        return map.getOrDefault(myName, new HashSet<>());
    }

    public Set<String> undirectedGraph(String myName){
        String[][] edges = new String[][]{{"A", "B"}, {"A", "C"}, {"B", "E"},
                {"B", "F"}, {"C", "G"}, {"C", "H"}, {"G", "D"}, {"D", "F"},
                {"A", "F"}, {"C", "F"}, {"B", "G"}};
        Map<String,Set<String>> map = buildUndirectedGraph(edges);
        return map.getOrDefault(myName, new HashSet<>());
    }

    public Set<String> tree(String myName){
        List<String> neighborStrings = new ArrayList<>();

        Map<String, List<String>> stub = new HashMap<>();
        stub.put("A", Arrays.asList("B", "C"));
        stub.put("B", Arrays.asList("E", "F"));
        stub.put("C", Arrays.asList("G", "H"));
        stub.put("G", Arrays.asList("D"));
        neighborStrings = stub.getOrDefault(myName,new ArrayList<>());
        return new HashSet<>(neighborStrings);
    }

    public Map<String, Set<String>> buildUndirectedGraph(String[][] edges){
        Map<String, Set<String>> graph = new HashMap<String, Set<String>>();
        for(String[] edge : edges){
            String u = edge[0], v = edge[1];
            if (graph.containsKey(u)) graph.get(u).add(v);
            else graph.put(u, new HashSet<>(new ArrayList<>(Arrays.asList(v))));
            if (graph.containsKey(v)) graph.get(v).add(u);
            else graph.put(v, new HashSet<>(new ArrayList<>(Arrays.asList(u))));
        }
        return graph;
    }

    public IPeer getPeerByName(String peerName){
        try {
            String[] rmi_array = this.ipconfigmap.get(peerName);
            String host = rmi_array[0];
            int port = Integer.parseInt(rmi_array[1]);
            Registry registry = LocateRegistry.getRegistry(host,port);
            //System.out.println(registry);
            IPeer peer = (IPeer) registry.lookup(peerName);
            return peer;
        } catch (Exception e){
            logger.severe(e.getMessage() + " " + Arrays.toString(e.getStackTrace()));
            throw new IllegalArgumentException("no peers " + peerName + " init.");
        }
    }

    public Registry getLocalRegistry(){
        return this.localRegistry;
    }

    public List<String> getNamesOnThisMachine(){
        String sep = File.separator;
        String path = System.getProperty("user.dir")+ String.format("%ssrc%smain%sresources%sMachineIP",
                sep,sep, sep, sep);
        ArrayList<String> nameList = new ArrayList<>();
        System.out.println(path);
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
    public void waitPeers(int wait) {
        boolean x = true;
        while(x){
            try {
                for (Map.Entry<String, String[]> entry : this.ipconfigmap.entrySet()) {
                    getPeerByName(entry.getKey());
                }
                x = false;
            } catch (Exception f) {
                try {
                    System.out.println("test");
                    Thread.sleep(wait);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
    }
        System.out.println("All peers have been initialized.");
    }

    public static void main(String[] args) throws Exception{
        PeerNetworkService pns = PeerNetworkService.getInstance();

        pns.waitPeers(10000);
    }


}

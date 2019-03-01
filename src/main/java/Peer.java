import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Stack;

/***
 * Both Server and Client
 * Two ideas on peer communication.
 * - (1) each peer holds a thread
 */
public class Peer extends UnicastRemoteObject implements ITradable {
    private String name;
    private Logger logger;
    private final static Logger staticLogger = new Logger(Peer.class.getSimpleName());

    public Peer () throws RemoteException {}
    public Peer(String name) throws RemoteException {
        this.name = name;
        this.logger = new Logger(name);
    }
    
    @Override
    public synchronized void lookup(String productName, int hopCount, Stack<String> stack) {
    }

    /***
     * Seller sells to buyer
     * @param sellerID
     */
    @Override
    public synchronized void reply(Long sellerID, Stack<String> stack) {
    }

    /***
     * Client buys from seller
     * @param peerID
     */
    @Override
    public synchronized void buy(Long peerID) {
    }

    public String greet(String peerID){
        return "hello " + peerID;
    }


    public static void build(String name){
        try {
            Peer server = new Peer(name);
            ITradable serverStub = (ITradable) UnicastRemoteObject.exportObject(server, 0);

            Registry registry = LocateRegistry.getRegistry();
            registry.bind(name, serverStub);

        } catch (Exception e){
            e.printStackTrace();
            staticLogger.severe(e.getMessage());
        }
    }

    public String getName() throws RemoteException{
        return this.name;
    }

    public static void main(String[] args){

    }
}


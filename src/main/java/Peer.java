import sun.security.krb5.Config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/***
 * Both Server and Client
 * Two ideas on peer communication.
 * - (1) each peer holds a thread
 */
public class Peer implements ITradable {
    private String name;
    private Logger logger;
    private final static Logger staticLogger = new Logger(Peer.class.getSimpleName());

    public Peer () throws RemoteException {}
    public Peer(String name) throws RemoteException {
        this.name = name;
        this.logger = new Logger(name);
    }
    
    @Override
    public synchronized void lookup(String productName, int hopCount) {
    }

    /***
     * Seller sells to buyer
     * @param sellerID
     */
    @Override
    public synchronized void reply(Long sellerID) {

    }

    /***
     * Client buys from seller
     * @param peerID
     */
    @Override
    public synchronized void buy(Long peerID) {
        logger.info("greetings from " + peerID);
    }

    public void runBuyer(){
        Runnable task = () -> {
            ConfigService configService = ConfigService.getInstance();
            Long delay = configService.getBuyerDelay(true);
            logger.info("buyer START");
            while(true){
                try {
                    logger.info("sleep " + delay + " ms");
                    Thread.sleep(delay);

//                    this.lookup(); `
                } catch (InterruptedException e){
                    logger.severe("- PEER BROKEN - " + e.getMessage());
                    break;
                } catch (Exception e){
                    logger.warning("- PEER EXCEPTION, CONT.- " + e.getMessage());
                }
            }
            logger.info("buyer END");

        };

        try {
            String threadName = String.format("%s Thread", this.name);
            Thread t = new Thread(task, threadName);
            t.start();
        } catch(Exception e){
            logger.severe(e.getMessage());
            e.printStackTrace();
        }
    }


    public static void build(Registry registry, String name){
        try {
            Peer server = new Peer(name);
            ITradable serverStub = (ITradable) UnicastRemoteObject.exportObject(server, 8002);
//            String hardName = String.format("//128.119.202.183/"+name);
            staticLogger.info("starting peer... " + name);

            registry.bind(name, serverStub);
            staticLogger.info("node server initiated");
            server.runBuyer();
        } catch (Exception e){

            e.printStackTrace();
            staticLogger.severe(e.getMessage());
        }
    }

    public String getName() throws RemoteException{
        return this.name;
    }

    public static void main(String[] args) throws RemoteException, IOException {
//        Properties p = new Properties();
        staticLogger.info(Registry.REGISTRY_PORT);
        Registry registry;
        try {
            registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            staticLogger.info("properly created registry");
        } catch(Exception e){
            staticLogger.warning("rebooting registry");
            registry = LocateRegistry.getRegistry();
        }
        Peer.build(registry, "A");

    }
}


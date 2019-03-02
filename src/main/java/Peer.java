
import java.io.IOException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/***
 * Both Server and Client
 * Two ideas on peer communication.
 * - (1) each peer holds a thread
 */
public class Peer implements IPeer {
    private String name;
    private Logger logger;
    private final static Logger staticLogger = new Logger(Peer.class.getSimpleName());
    private Inventory inventory;

    public Peer () throws RemoteException {}
    public Peer(String name) throws RemoteException {
        this.name = name;
        this.logger = new Logger(name);
    }
    
    @Override
    public synchronized void lookup(ItemType productName, int hopCount, Stack<String> path,
                                            List<String> potentialSellers) throws RemoteException {

        PeerNetworkService pns = PeerNetworkService.getInstance();
        path.push(this.name);

        if (this.inventory.isSellingItem(productName)){
            potentialSellers.add(this.name);
        }

        List<String> neighborNames = pns.getNeighbors(this.name);
        for(String neighborName : neighborNames) {
            IPeer neighbor = pns.getPeerByName(neighborName);
            if (hopCount <= 0) {
                //last seller, reply.
                neighbor.reply(this.name, productName, path, potentialSellers);
            } else {
                neighbor.lookup(productName, hopCount - 1, path, potentialSellers);
            }
        }
    }

    /***
     * Seller sells to buyer
     * @param sellerID
     */
    @Override
    public synchronized void reply(String sellerID, ItemType productName, Stack<String> stack,
                                   List<String> potentialSellers) throws RemoteException {
        /* our invariant gaurantes stack is not empty */
        stack.pop();
        PeerNetworkService pns = PeerNetworkService.getInstance();
        if (stack.isEmpty()){
            // we're the buyer, call buy
            int nSellers = potentialSellers.size();
            if (nSellers == 0) return;
            int randIdx = new Random().nextInt(nSellers);
            String sellerCandidateID = potentialSellers.get(randIdx);
            IPeer seller = pns.getPeerByName(sellerCandidateID);
            seller.buy(this.name, productName);

        } else {
            String middlemanName = stack.pop();
            IPeer middleman = pns.getPeerByName(middlemanName);
            middleman.reply(sellerID, productName, stack, potentialSellers);
        }


    }

    /***
     * Client buys from seller
     * @param peerID
     */
    @Override
    public synchronized void buy(String peerID, ItemType productName) {
        logger.info("");
        if (this.inventory.isSellingItem(productName)){
            this.inventory.take(productName);
        }
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
            IPeer serverStub = (IPeer) UnicastRemoteObject.exportObject(server, 8002);
//            String hardName = String.format("//128.119.202.183/"+name);
            staticLogger.info("starting peer... " + name);

            registry.bind(name, serverStub);
            staticLogger.info("node server initiated");

//            serverStub.lookup();
//            server.runBuyer();
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


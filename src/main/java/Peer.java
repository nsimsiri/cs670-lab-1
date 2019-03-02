
import java.io.IOException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import java.util.ArrayList;
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
    private PeerType peerType;
    private StackMerger merger;

    public Peer () throws RemoteException {}
    public Peer(String name, PeerType peerType, ItemType item) throws RemoteException {
        this.name = name;
        this.logger = new Logger(name);
        this.peerType = peerType;
        if (peerType.equals(PeerType.SELLER)){
            this.inventory = new Inventory(item);
        }
    }


    /***
     *
     * PROBLEM: we need to "Merge" all the replies from path, because we need.
     * - spawn a
     *
     */
    @Override
    public void lookup(ItemType productName, int hopCount, Stack<String> path,
                                            List<String> potentialSellers) throws RemoteException {

        logger.info("%s lookup %s %s %s %s", this.toString(), productName, hopCount, path, potentialSellers);
        PeerNetworkService pns = PeerNetworkService.getInstance();
        String previousNodeName = path.isEmpty() ? new String() : path.peek();


        if (this.peerType.equals(PeerType.SELLER) && this.inventory.isSellingItem(productName)){
            potentialSellers.add(this.name);
        }
        List<String> neighborNames = pns.getNeighbors(this.name);
        if (hopCount <= 0 || neighborNames.size() == 0) {
            //last seller, reply.
            this.reply(this.name, productName, path, potentialSellers);
        } else {
            path.push(this.name);
            logger.info("looking up neighbors " + neighborNames);
            for(String neighborName : neighborNames) {
                logger.info("searching " + neighborName);
                if (previousNodeName.equals(neighborName)) continue;

                IPeer neighbor = pns.getPeerByName(neighborName);
                if (neighbor == null) continue;

                neighbor.lookup(productName, hopCount - 1, path, potentialSellers);
            }

        }
    }

    /***
     * Seller sells to buyer
     * @param sellerID
     */
    @Override
    public void reply(String sellerID, ItemType productName, Stack<String> stack,
                                   List<String> potentialSellers) throws RemoteException {
        logger.info("REPLY %s %s %s %s", sellerID, productName, stack, potentialSellers);
        PeerNetworkService pns = PeerNetworkService.getInstance();
        if (stack.isEmpty()){
            logger.info("REPLY - empty, will attempt to buy");
            // we're the buyer, call buy
            int nSellers = potentialSellers.size();
            if (nSellers == 0) return;
            int randIdx = new Random().nextInt(nSellers);
            String sellerCandidateID = potentialSellers.get(randIdx);
            IPeer seller = pns.getPeerByName(sellerCandidateID);
            seller.buy(this.name, productName);
        } else {
            String previousPeerID = stack.pop();
            IPeer previousPeer = pns.getPeerByName(previousPeerID);
            previousPeer.reply(sellerID, productName, stack, potentialSellers);
        }
    }

    /***
     * Client buys from seller
     * @param peerID
     */
    @Override
    public void buy(String peerID, ItemType productName) {
        logger.info("BUY %s %s", productName, this.toString());
        synchronized(this.inventory){
            if (this.inventory.isSellingItem(productName)){
                this.inventory.take(productName);
                logger.info("!! BOUGHT " + productName);
            }
        }

    }

    public void runBuyer(){
        Runnable task = () -> {
            ConfigService configService = ConfigService.getInstance();
            int hopCount = 10;
            Long delay = configService.getBuyerDelay(true);
            logger.info("buyer START");
            while(true){
                try {
                    logger.info("sleep " + delay + " ms");
                    Thread.sleep(delay);
                    ItemType randomizedItem = ItemType.BOARS; //inventory.randomizeItemType();
                    this.lookup(randomizedItem, 10, new Stack<>(), new ArrayList<>());
//                    this.lookup();
                } catch (InterruptedException e){
                    logger.severe("- PEER BROKEN - " + e.getMessage());
                    e.printStackTrace();
                    break;
                } catch (Exception e){
                    logger.warning("- PEER EXCEPTION, CONT.- " + e.getMessage());
                    e.printStackTrace();
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

    @Override
    public String toString(){
        String inv = inventory == null ? "Nil" : this.inventory.toString();
        return String.format("Peer[%s %s %s]", name, peerType, inv);
    }

    public static PeerType getRandomPeerType(){
        int r = new Random().nextInt(2);
        if (r==0){
            return PeerType.BUYER;
        }
        return PeerType.SELLER;
    }


    public static Peer build(Registry registry, String name, PeerType peerType, ItemType sellingItem){
        Peer server = null;
        try {
            peerType = peerType == null ? getRandomPeerType() : peerType;
            server = new Peer(name, peerType, sellingItem);
            IPeer serverStub = (IPeer) UnicastRemoteObject.exportObject(server, 0);
//            String hardName = String.format("//128.119.202.183/"+name);
            staticLogger.info("starting peer... " + server);
            registry.bind(name, serverStub);
            staticLogger.info(name + " STARTED");

            if (peerType.equals(peerType.BUYER)){
//                serverStub.lookup(ItemType.BOARS, 1, new Stack<>(), new ArrayList<>());
                server.runBuyer();
            }

        } catch (Exception e){

            e.printStackTrace();
            staticLogger.severe(e.getMessage());
        } finally{
            return server;
        }
    }

    public static Peer build(Registry registry, String name, PeerType peerType){
        return build(registry, name, peerType, null);
    }

    public String getName() throws RemoteException{
        return this.name;
    }

    public static void main(String[] args) throws RemoteException, IOException {
//        Properties p = new Properties();
        PeerNetworkService pns = PeerNetworkService.getInstance();
        Registry registry = pns.getLocalRegistry();
        Peer.build(registry, "A", PeerType.BUYER);

    }
}


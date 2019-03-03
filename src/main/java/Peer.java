
import java.io.IOException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import java.time.Instant;
import java.util.*;
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
        this.merger = new StackMerger();
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
    public void lookup(Lookup lookup, int hopCount, Stack<String> path,
                                            List<String> potentialSellers) throws RemoteException {

        ItemType productName = lookup.getProductName();
        logger.info("%s lookup %s %s %s %s", this.toString(), productName, hopCount, path, potentialSellers);

        PeerNetworkService pns = PeerNetworkService.getInstance();
        String previousNodeName = path.isEmpty() ? new String() : path.peek();

        if (this.peerType.equals(PeerType.SELLER) && this.inventory.isSellingItem(productName)){
            potentialSellers.add(this.name);
        }
        Set<String> neighborNames = pns.getNeighbors(this.name);
        if (hopCount <= 0 || neighborNames.size() == 0) {
            // last seller, reply.
            this.reply(this.name, lookup, path, potentialSellers);
        } else {
            synchronized (this.merger){
                // this node has been visited before, we will not traverse and our hop ends here.
                if (this.merger.containsLookup(lookup)){
                    return;
                }
            }
            //
            path.push(this.name);
            logger.info("looking up neighbors " + neighborNames);

            this.merger.createLookup(lookup, neighborNames.size());

            for(String neighborName : neighborNames) {
                logger.info("searching " + neighborName);
                if (previousNodeName.equals(neighborName)) continue;

                IPeer neighbor = pns.getPeerByName(neighborName);
                if (neighbor == null) continue;

                neighbor.lookup(lookup, hopCount - 1, path, potentialSellers);
            }

        }
    }

    /***
     * Seller sells to buyer
     * @param sellerID
     */
    @Override
    public void reply(String sellerID, Lookup lookup, Stack<String> stack,
                                   List<String> potentialSellers) throws RemoteException {
        ItemType productName = lookup.getProductName();
        logger.info("REPLY %s %s %s %s", sellerID, productName, stack, potentialSellers);
        PeerNetworkService pns = PeerNetworkService.getInstance();
        if (stack.isEmpty()){
            // stack empty means, we're the buyer, call buy
            logger.info("REPLY - empty, will attempt to buy");

            int nSellers = potentialSellers.size();
            if (nSellers == 0) return;
            int randIdx = new Random().nextInt(nSellers);
            String sellerCandidateID = potentialSellers.get(randIdx);
            IPeer seller = pns.getPeerByName(sellerCandidateID);
            seller.buy(this.name, productName);
        } else {
            // stack is not empty, thus not a seller. We will wait for our merger's count for the lookup to be 0
            // not last seller, will pass message along after we wait for lookup.
            String previousPeerID = stack.pop();
            int count = 0;
            synchronized (this.merger){
                count = this.merger.getLookupCount(lookup);

                if (count <= 0){
                    // all forked messages have returned
                    IPeer previousPeer = pns.getPeerByName(previousPeerID);
                    Set<String> storedPotentialSellers = this.merger.getLookupSellers(lookup);
                    List<String> _storedPotentialSellers = new ArrayList<>(storedPotentialSellers);
                    previousPeer.reply(sellerID, lookup, stack, _storedPotentialSellers);
                } else {
                    // messages have not returned, we store in our concurrent store.
                    this.merger.addLookupSellers(lookup, potentialSellers);
                }
                this.merger.decrementLookup(lookup);
            }
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

    private Lookup buildLookup(String buyerID, ItemType productName){
        Long timestamp = Instant.now().toEpochMilli();
        Lookup lookup = new Lookup(buyerID, productName, timestamp);
        return lookup;
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
                    Lookup lookup = buildLookup(this.name, randomizedItem);

                    this.lookup(lookup, 10, new Stack<>(), new ArrayList<>());

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


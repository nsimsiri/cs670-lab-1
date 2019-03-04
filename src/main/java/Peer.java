
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.time.Instant;
import java.util.stream.Collectors;

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
     * - spawn a
     *
     */
    @Override
    public void lookup(Lookup lookup, int hopCount, Stack<String> path,
                                            List<String> potentialSellers) throws RemoteException {

        ItemType productName = lookup.getProductName();
        PeerNetworkService pns = PeerNetworkService.getInstance();
        String previousNodeName = path.isEmpty() ? new String() : path.peek();

        logger.info("LOOKUP: %s-> %s %s hop=%s path=%s sellers=%s",
                previousNodeName, this.toString(), productName, hopCount, path, potentialSellers);

        // as we hope along the network, we collect our potential sellers.
        if (this.peerType.equals(PeerType.SELLER) && this.inventory.isSellingItem(productName)){
            potentialSellers.add(this.name);
        }

        // we get our neighbors from PeerNetworkService which encodes our network's topology.
        Set<String> neighborNames = pns.getNeighbors(this.name);
        neighborNames = neighborNames.stream().filter(x -> !x.equals(previousNodeName)).collect(Collectors.toSet());

        // we begin our look-up search logic

        // this node has been visited before, we will not traverse and our hop ends here.
        boolean cycleFound = false;
        synchronized (this.merger){
            if (this.merger.containsLookup(lookup)){
                logger.warning("Cycle detected, " + previousNodeName + " revisited" + this.name + " on lookup " + lookup);
                cycleFound = true;
            }
        }

        if (hopCount <= 0 || neighborNames.size() == 0 || cycleFound) {
            // last hop count or no more neighbor means we traverse
            // the path we took back to the buyer with reply to previous peer
            if (previousNodeName.isEmpty()) return; //
            IPeer neighbor = pns.getPeerByName(previousNodeName);
            path.pop();
            Runnable task = () -> {
                try {
                    neighbor.reply(this.name, lookup, path, potentialSellers);
                } catch(Exception e){
                    logger.warning("Thread");
                    e.printStackTrace();
                }
            };
            Thread thread = new Thread(task);
            thread.start();
        } else {
            // we still have hops, so we hop to our neighbors
            // keep track of who we've traveled along so far.
            path.push(this.name);
//            logger.info("looking up neighbors " + neighborNames);

            // we keep track of this visited node for the current look-up (i.e buyer, product, time)
            // and keep count of how many neighbors we will be receiving replies from to gather sellers
            // which is implemented in reply()
            synchronized (this.merger){

                this.merger.createLookup(lookup, neighborNames.size());
//                logger.info("new visit " + this.name + " - " + lookup + " - neigh - " + neighborNames.size() + " - merger- " + this.merger);
            }

            // asynchronous traversal of each neighbors, decrementing the count as we go and tracking state as we go.
            for(String neighborName : neighborNames) {
                IPeer neighbor = pns.getPeerByName(neighborName);
                if (neighbor == null) continue;

                Runnable task = () -> {
                    try {
                        neighbor.lookup(lookup, hopCount - 1, path, potentialSellers);
                    } catch(Exception e){
                        logger.warning("Thread");
                        e.printStackTrace();
                    }
                };
                Thread thread = new Thread(task);
                thread.start();
            }
        }
    }

    /***
     * This method backtracks the look-ups, initially triggered on the final look-up hop.
     * However, we won't reply until the look-up on neighbors of this peer has finished.
     * We use the this.merger to keep track of who has arrived, and aggregate the sellers as they come.
     * We use reentrant synchronization to help us guarantee mergers' concurrent .
     * @param sellerID
     */
    @Override
    public void reply(String sellerID, Lookup lookup, Stack<String> stack,
                                   List<String> potentialSellers) throws RemoteException {
        synchronized (this.merger){
            // a peer has returned, decrement.
            this.merger.decrementLookup(lookup);

            ItemType productName = lookup.getProductName();

            PeerNetworkService pns = PeerNetworkService.getInstance();
            int count = this.merger.getLookupCount(lookup);
            logger.info("REPLY %s %s path=%s sellers=%s -- count: %s/%s", sellerID, productName, stack,
                    potentialSellers, count, this.merger.getTotalCount(lookup));
//            logger.warning("--merger: " + this.merger);
            this.merger.addLookupSellers(lookup, potentialSellers);
            if (count <= 0){
                // all forked messages have returned
                if (stack.isEmpty()){
                    // stack empty means, we're the buyer, call buy
                    Set<String> sellers = this.merger.getLookupSellers(lookup); // get other stored-sellers

                    logger.info("REPLY(1) - At initial buyer peer, all neighbors have returned - buy from=" + sellers);

                    int nSellers = sellers.size();

                    // case no sellers
                    if (nSellers == 0) return;

                    // case have seller

                    int randIdx = new Random().nextInt(nSellers);
                    List<String> sellersList = new ArrayList<>(sellers);
                    String sellerCandidateID = sellersList.get(randIdx);
                    IPeer seller = pns.getPeerByName(sellerCandidateID);
                    seller.buy(this.name, productName);
                    this.merger.removeLookup(lookup);
                } else {
                    // stack is not empty, thus not a seller. We will wait for our merger's count for the lookup to be 0
                    // not last seller, will pass message along after we wait for lookup.
                    String previousPeerID = stack.pop();
                    logger.info("REPLY(2) - At middleman, all neighbors have returned - initiate pass-back to " +  previousPeerID);

                    IPeer previousPeer = pns.getPeerByName(previousPeerID);

                    Set<String> storedPotentialSellers = this.merger.getLookupSellers(lookup);
                    List<String> _storedPotentialSellers = new ArrayList<>(storedPotentialSellers);

                    Runnable task = () -> {
                        try {
                            previousPeer.reply(sellerID, lookup, stack, _storedPotentialSellers);
                        } catch(Exception e){
                            logger.warning("Thread");
                            e.printStackTrace();
                        }
                    };
                    Thread thread = new Thread(task);
                    thread.start();


                }

            } else {
                // messages have not returned do nothing and our concurrent store.
//                logger.info("REPLY(3) - not all neighbors have returned - sellers=" + this.merger.getLookupSellers(lookup));
            }
        }
    }

    /***
     * Client buys from seller
     * @param peerID
     */
    @Override
    public void buy(String peerID, ItemType productName) {
        logger.info("$$ -> %s ATTEMPTS BUY %s FROM %s with inventory %s",
                peerID, productName, this.toString(), this.inventory);
        synchronized(this.inventory){
            if (this.inventory.isSellingItem(productName)){
                this.inventory.take(productName);
                logger.info("$$ -> %s PURCHASED %s FROM %s with inventory %s",
                        peerID, productName, this.toString(), this.inventory);
            } else {
                logger.info("%s no longer sold by %s", productName, this);
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
            int hopCount = configService.getHopCount();
            Long delay = configService.getBuyerDelay(true);
//            logger.info("buyer START");
            while(true){
                try {
                    delay = new Double(delay * Math.random()).longValue() + 500L;
                    logger.info("sleep " + delay + " ms");
                    Thread.sleep(delay);

                    ItemType randomizedItem = inventory.randomizeItemType(); //ItemType.BOARS;
                    Lookup lookup = buildLookup(this.name, randomizedItem);

                    this.lookup(lookup, hopCount, new Stack<>(), new ArrayList<>());

                } catch (InterruptedException e){
                    logger.severe("- PEER BROKEN - " + e.getMessage());
                    e.printStackTrace();
                    break;
                } catch (Exception e){
                    logger.warning("- PEER EXCEPTION, CONT.- " + e.getMessage());
                    e.printStackTrace();
                }
            }
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

    public PeerType getPeerType(){
        return this.peerType;
    }

    public static Peer build(Registry registry, String name, PeerType peerType, int port, ItemType sellingItem){
        Peer server = null;
        try {
            peerType = peerType == null ? getRandomPeerType() : peerType;
            server = null;
            if (peerType.equals(peerType.BUYER)){
                server = new Peer(name, peerType, null);
            } else {
                server = new Peer(name, peerType, sellingItem);
            }

            IPeer serverStub = (IPeer) UnicastRemoteObject.exportObject(server, port);
            registry.bind(name, serverStub);
            staticLogger.info("ONLINE: " + server);

        } catch (Exception e){
            e.printStackTrace();
            staticLogger.severe(e.getMessage());
        } finally{
            return server;
        }
    }

    public static Peer build(Registry registry, String name, PeerType peerType, int port){
        return build(registry, name, peerType, port, null);
    }

    public String getName() throws RemoteException{
        return this.name;
    }

    public static void main(String[] args) throws RemoteException, IOException {
        PeerNetworkService pns = PeerNetworkService.getInstance();
        Registry registry = pns.getLocalRegistry();
        Peer peer = Peer.build(registry, "A", PeerType.BUYER, 0);
        peer.runBuyer();


    }
}


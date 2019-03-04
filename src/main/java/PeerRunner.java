import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class PeerRunner {
    public void run() throws RemoteException{
        PeerNetworkService pns = PeerNetworkService.getInstance();
        System.out.println();
        List<String> peerNames = pns.getNamesOnThisMachine();
        Registry registry = pns.getLocalRegistry();
        List<PeerType> peerTypes = new ArrayList<>();
        for(int i = 0; i < peerNames.size(); i++){
            PeerType peerType = new Random().nextInt(2) % 2 == 0 ? PeerType.BUYER : PeerType.SELLER;
//            peerTypes.add(peerType);
            peerTypes.add(PeerType.SELLER);
        }

        peerTypes.set(3, PeerType.BUYER);
        peerTypes.set(5, PeerType.BUYER);
        System.out.println(peerTypes);
        List<Peer> peers = new ArrayList<>();
        for(int i = 0; i < peerNames.size(); i++){
            String peerID = peerNames.get(i);
            PeerType peerType = peerTypes.get(i);
            ItemType item = Inventory.randomizeItemType();
            Peer p = Peer.build(registry, peerID, peerType, 0, item);
            peers.add(p);
        }

        for(Peer peer: peers){
            if (peer.getPeerType().equals(PeerType.BUYER)) {
                peer.runBuyer();
            }
        }

//        pns.waitOnPeers();

    }

	public static void main(String args[]) throws RemoteException {
		PeerRunner runner = new PeerRunner();
		runner.run();

//        PeerNetworkService pns = PeerNetworkService.getInstance();
//        try {
//            pns.getPeerByName("A");
//        } catch (Exception e){
//            System.out.println(e.getMessage());
//            Registry reg = pns.getLocalRegistry();
//            IPeer p = Peer.build(reg, "A", PeerType.BUYER, 1099);
//            System.out.println(p.getName());
//        }



	}
}
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class Sandbox {
    public static void main(String[] args) throws RemoteException {
        try {
            PeerNetworkService pns = PeerNetworkService.getInstance();
            Registry localRegistry = pns.getLocalRegistry();
//            System.out.println(registry);
//            IPeer peerStub = (IPeer) registry.lookup(pA");
//            IPeer peerStub = pns.getPeerByName("A");
//            ItemType item = ItemType.valueOf("BOARS");
//            int hopCount = 10;
//            Stack<String> path = new Stack<>();
//            path.push("a"); path.push("b");
//            List<String> sellers = Arrays.asList("kk", "dskfjd");
//            System.out.println(peerStub);
//            peerStub.lookup(item, hopCount, path, sellers);
            IPeer B = Peer.build(localRegistry, "B", PeerType.SELLER, ItemType.FISH);
            IPeer C = Peer.build(localRegistry, "C", PeerType.SELLER, ItemType.FISH);
            IPeer D = Peer.build(localRegistry, "D", PeerType.SELLER, ItemType.BOARS);
            System.out.println(B);
            System.out.println(C);
            System.out.println(D);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

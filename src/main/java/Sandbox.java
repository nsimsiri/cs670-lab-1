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
//            Registry registry = LocateRegistry.getRegistry(Registry.REGISTRY_PORT);
//            System.out.println(registry);
//            IPeer peerStub = (IPeer) registry.lookup("A");
            PeerNetworkService pns = PeerNetworkService.getInstance();
            IPeer peerStub = pns.getPeerByName("A");
            ItemType item = ItemType.valueOf("BOARS");
            int hopCount = 10;
            Stack<String> path = new Stack<>();
            path.push("a"); path.push("b");
            List<String> sellers = Arrays.asList("kk", "dskfjd");
            System.out.println(peerStub);
            peerStub.lookup(item, hopCount, path, sellers);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class NatchaServerSandbox {

    public static void main(String[] args) throws Exception {
        Registry registry = null;
        /*
        System.setProperty("java.rmi.server.hostname","192.168.43.16");
        try {
            registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        } catch(Exception e){
            registry = LocateRegistry.getRegistry("192.168.43.16",Registry.REGISTRY_PORT);

        }

        Peer server = new Peer("1", PeerType.SELLER, null);
        System.out.println("Peer Made: " + server);
        IPeer serverStub = (IPeer) UnicastRemoteObject.exportObject(server, 0);
        registry.bind(server.getName(), serverStub);
        System.out.println("Peer Server Started!");
        */


        PeerNetworkService pns = PeerNetworkService.getInstance();
        Registry reg = pns.getLocalRegistry();
        Peer server = Peer.build(reg, "2", PeerType.BUYER, 0);


    }
}

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerSandbox {

    public static void main(String[] args) throws Exception {
        Registry registry = null;
        try {
            registry = LocateRegistry.createRegistry(1234);
        } catch(Exception e){
            registry = LocateRegistry.getRegistry(1234);

        }

        Peer server = new Peer("Natcha", PeerType.SELLER, null);
        System.out.println("Peer Made: " + server);
        IPeer serverStub = (IPeer) UnicastRemoteObject.exportObject(server, 5001);
        registry.bind(server.getName(), serverStub);
        System.out.println("Peer Server Started!");

    }
}

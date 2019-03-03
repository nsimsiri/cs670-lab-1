import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerSandbox {

    public static void main(String[] args) throws Exception {
        Registry registry = null;
        System.setProperty("java.rmi.server.hostname","192.168.43.16");
        try {
            registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        } catch(Exception e){
            registry = LocateRegistry.getRegistry("192.168.43.16",Registry.REGISTRY_PORT);

        }

        Peer server = new Peer("Natcha", PeerType.SELLER, null);
        System.out.println("Peer Made: " + server);
        IPeer serverStub = (IPeer) UnicastRemoteObject.exportObject(server, Registry.REGISTRY_PORT);
        registry.bind(server.getName(), serverStub);
        System.out.println("Peer Server Started!");

    }
}

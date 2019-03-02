import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerSandbox {

    public static void main(String[] args) throws Exception {
        Registry registry = null;
        String ip = "192.168.43.16";

        try {
            registry = LocateRegistry.createRegistry(5005);
            System.out.println("create registry");
        } catch(Exception e){
            registry = LocateRegistry.getRegistry(5005);
            System.out.println("get registry");

        }

        Peer server = new Peer("Natcha", PeerType.SELLER, null);
        System.out.println("Peer Made: " + server);
        IPeer serverStub = (IPeer) UnicastRemoteObject.exportObject(server, 5005);
        registry.bind(server.getName(), serverStub);
        System.out.println("Peer Server Started!");

    }
}

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Sandbox {
    public static void main(String[] args) throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(Registry.REGISTRY_PORT);
            System.out.println(registry);
            ITradable peerStub = (ITradable) registry.lookup("A");
            peerStub.buy(69L);
        } catch (Exception e){
            e.printStackTrace();
        }



    }
}

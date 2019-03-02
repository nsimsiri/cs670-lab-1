import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientSanbox {
//    static Logger logger = new Logger("ClientSandbox");
    public static void main(String[] args) throws Exception{
        Registry registry = null;
        try {
            registry = LocateRegistry.createRegistry(1234);
        } catch(Exception e){
            registry = LocateRegistry.getRegistry(1234);

        }

        IPeer p = (IPeer) registry.lookup("Natcha");
        System.out.println(p);

    }
}


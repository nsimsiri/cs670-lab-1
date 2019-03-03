import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;

public class ClientSanbox {
    static Logger logger = new Logger("ClientSandbox");
    public static void main(String[] args) throws Exception{
        Registry registry = null;
        String ip = "192.168.43.16";
        registry = LocateRegistry.getRegistry("192.168.43.16", Registry.REGISTRY_PORT);
        System.out.println(registry);
        IPeer p = (IPeer) registry.lookup("Natcha");
        System.out.println(p.getName());
    }
}


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;

public class ClientSanbox {
//    static Logger logger = new Logger("ClientSandbox");
    public static void main(String[] args) throws Exception{
        Registry registry = null;
        registry = LocateRegistry.getRegistry("192.168.43.16", 5005);
        System.out.println(registry);
//        System.out.println(Arrays.toString(registry.list()));



        IPeer p = (IPeer) registry.lookup("Natcha");
        System.out.println(p.getName());
//        System.out.println(p);
//192.168.43.16
    }
}


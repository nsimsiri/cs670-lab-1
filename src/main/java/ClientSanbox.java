import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;

public class ClientSanbox {
//    static Logger logger = new Logger("ClientSandbox");
    public static void main(String[] args) throws Exception{
        Registry registry = null;
        String ip = "192.168.43.16";
        String tomip = "192.168.43.20";
//        String addr = "rmi:"
        try {
            registry = LocateRegistry.getRegistry(ip, 5005);
            System.out.println("getRegistry");

        } catch(Exception e){
            registry = LocateRegistry.createRegistry(5005);
            System.out.println("createRegistry");
        }
        System.out.println(registry);
        IPeer p = (IPeer) registry.lookup("Natcha");
        System.out.println(p.getName());
//        System.out.println(Arrays.toString(registry.list()));

//        IPeer p = (IPeer) registry.lookup("Natcha");
//        String x = "//" + ip + "/Natcha";
//        System.out.println(x);


    }
}


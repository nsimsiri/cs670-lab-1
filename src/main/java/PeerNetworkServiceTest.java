import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Set;

public class PeerNetworkServiceTest {

    public static void main(String args[]){

        try{
        PeerNetworkService pns = PeerNetworkService.getInstance();





        //Test for getPeerByName
            Registry registry = pns.getLocalRegistry();
            String name = "1";
            Peer one = Peer.build(registry,name, PeerType.BUYER, Integer.parseInt(pns.ipconfigmap.get(name)[1]));
            //Peer two = Peer.build(registry, "2", PeerType.BUYER, Integer.parseInt(pns.ipconfigmap.get("2")[1]));
            IPeer tom = pns.getPeerByName("1");
            System.out.println(tom.getName());

            IPeer n = pns.getPeerByName("2");
            System.out.println(n.getName());
        }
        catch(Exception e){
            e.printStackTrace();
        }


            }
}

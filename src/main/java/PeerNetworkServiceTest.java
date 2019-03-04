import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Set;

public class PeerNetworkServiceTest {

    public static void main(String args[]){

        try{
            PeerNetworkService pns = PeerNetworkService.getInstance();
//            try {
//                pns.getPeerByName("A");
//            } catch (Exception e){
//                System.out.println(e.getMessage());
//                Registry reg = pns.getLocalRegistry();
//                IPeer p = Peer.build(reg, "A", PeerType.BUYER, 1099);
//                System.out.println(p.getName());
//            }


        //Test for getPeerByName
            Registry registry = pns.getLocalRegistry();
            String name = "1";
            Peer one = Peer.build(registry,name, PeerType.BUYER, 0);
            //Peer two = Peer.build(registry, "2", PeerType.BUYER, 0);
            Peer three = Peer.build(registry,"3", PeerType.BUYER,0);
            Peer four = Peer.build(registry, "4", PeerType.BUYER, 0);
            Peer five = Peer.build(registry,"5", PeerType.BUYER, 0);
            Peer six = Peer.build(registry,"6", PeerType.BUYER, 0);
            IPeer tom = pns.getPeerByName("1");
            System.out.println(tom.getName());

            //IPeer n = pns.getPeerByName("2");
            //System.out.println("still getting " + n.getName());
        }
        catch(Exception e){
            e.printStackTrace();
        }

        /*
         Peer one = Peer.build(registry,name, PeerType.BUYER, Integer.parseInt(pns.ipconfigmap.get(name)[1]));
            Peer two = Peer.build(registry, "2", PeerType.BUYER, Integer.parseInt(pns.ipconfigmap.get("2")[1]));
            Peer three = Peer.build(registry,"3", PeerType.BUYER, Integer.parseInt(pns.ipconfigmap.get("3")[1]));
            Peer four = Peer.build(registry, "4", PeerType.BUYER, Integer.parseInt(pns.ipconfigmap.get("4")[1]));
            Peer five = Peer.build(registry,"5", PeerType.BUYER, Integer.parseInt(pns.ipconfigmap.get("5")[1]));
        */


            }
}

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class Sandbox {
    public static void initAllSellers(Registry localRegistry){
        System.out.println("Init All Sellers");

        IPeer B = Peer.build(localRegistry, "B", PeerType.SELLER, 0, ItemType.BOARS);
        IPeer C = Peer.build(localRegistry, "C", PeerType.SELLER, 0, ItemType.BOARS);
        IPeer D = Peer.build(localRegistry, "D", PeerType.SELLER, 0, ItemType.FISH);
        IPeer E = Peer.build(localRegistry, "E", PeerType.SELLER, 0, ItemType.BOARS);
        IPeer F = Peer.build(localRegistry, "F", PeerType.SELLER, 0, ItemType.BOARS);
        IPeer G = Peer.build(localRegistry, "G", PeerType.SELLER, 0, ItemType.FISH);
        IPeer H = Peer.build(localRegistry, "H", PeerType.SELLER, 0, ItemType.FISH);
    }

    public static void initSomeBuyers(Registry localRegistry){
        System.out.println("Init Some Buyers");

        IPeer B = Peer.build(localRegistry, "B", PeerType.BUYER, 0);
        IPeer C = Peer.build(localRegistry, "C", PeerType.SELLER, 0, ItemType.BOARS);
        IPeer D = Peer.build(localRegistry, "D", PeerType.SELLER, 0, ItemType.BOARS);

        IPeer E = Peer.build(localRegistry, "E", PeerType.BUYER, 0);
        IPeer F = Peer.build(localRegistry, "F", PeerType.SELLER, 0, ItemType.BOARS);
        IPeer G = Peer.build(localRegistry, "G", PeerType.BUYER, 0);
        IPeer H = Peer.build(localRegistry, "H", PeerType.SELLER, 0, ItemType.FISH);
    }


    public static void main(String[] args) throws RemoteException {
        try {
            PeerNetworkService pns = PeerNetworkService.getInstance();
            Registry localRegistry = pns.getLocalRegistry();

            initSomeBuyers(localRegistry);


        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

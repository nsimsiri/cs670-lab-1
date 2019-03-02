import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class PeerRunner {
	public static void main(String args[]) throws RemoteException {
		ConfigService config = ConfigService.getInstance();
		PeerNetworkService pns = PeerNetworkService.getInstance();
		List<String> peerNamesOnThisMachine = pns.getNamesOnThisMachine();
		Registry localRegistry = pns.getLocalRegistry();
		for(String name : peerNamesOnThisMachine){
			PeerType type = new Random().nextInt(2) % 2 == 0 ? PeerType.SELLER : PeerType.BUYER;
			Peer.build(localRegistry, name, type);
		}


		/*
		Properties configProp = config.ipConfig();
		configProp.forEach((key,value)-> {
			try {
				if (value.equals(InetAddress.getLocalHost().getHostAddress().trim())) {
					System.out.println(key + " : " + value);
					//Peer.build(PeerType)
				}
			}
			catch(UnknownHostException e){
				e.printStackTrace();
			}
		}
			);
		*/
		//System.out.println(configProp.getProperty("1330"));
		//System.out.println("rmi://172.30.121.74:1335");
	}
}
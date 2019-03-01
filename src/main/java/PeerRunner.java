import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
public class PeerRunner {
	public static void main(String args[]){
		ConfigService config = ConfigService.getInstance();
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
		//System.out.println(configProp.getProperty("1330"));
		//System.out.println("rmi://172.30.121.74:1335");
	}
}
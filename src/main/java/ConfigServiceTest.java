import java.util.HashSet;
import java.util.Map;

public class ConfigServiceTest {

    public static void main(String args[]){
        ConfigService config = ConfigService.getInstance();
        System.out.println(config.filepath);

        Map<String, String[]> ipconfigmap = config.ipConfig();
        for (Map.Entry<String, String[]> entry : ipconfigmap.entrySet()) {
            System.out.println("Vertex: " + entry.getKey());
            System.out.println("    IP: " + entry.getValue()[0]);
            System.out.println("    Port: " + entry.getValue()[1]);
        }

        Map<String, HashSet<String>> graphconfigmap = config.edgeList();
        for (Map.Entry<String, HashSet<String>> entry : graphconfigmap.entrySet()) {
            System.out.println("Vertex: " + entry.getKey()+ "   Neighbors: "+entry.getValue());
        }
        for(int i = 0; i < 5; i++) {
            System.out.println("Random Delay " + i + ": " +config.getBuyerDelay(false));
        }
        System.out.println("Fixed Delay: " +config.getBuyerDelay(true));

        System.out.println("Inventory Count: "+config.getInventoryCount());
        System.out.println("Hop Count: "+config.getHopCount());


    }}

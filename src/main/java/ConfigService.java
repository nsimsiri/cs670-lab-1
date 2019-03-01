import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigService {
    private static final Integer INVENTORY_COUNT = 10;

    public static Integer getInventoryCount() {
        return INVENTORY_COUNT;
    }

    public static Properties ipConfig() {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "tpalaschak_config.txt";
        //String path = "tpalaschak_config.txt";
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(path));
            return prop;
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return prop;
    }

    public void getNeighbors(){
        //List list = new List();
        //return list;
    }
}
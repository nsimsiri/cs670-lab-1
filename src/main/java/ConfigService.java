import java.util.List;
import java.util.Properties;

public class ConfigService {
    private static final Integer INVENTORY_COUNT = 10;

    public static Integer getInventoryCount() {
        return INVENTORY_COUNT;
    }

    public Properties ipConfig() {
        Properties prop = new Properties();
        return prop;
    }

    public void getNeighbors(){
    }
}
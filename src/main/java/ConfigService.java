import java.util.List;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

public class ConfigService {
    private static final Integer INVENTORY_COUNT = 10;
    private static final Long BUYER_DELAY = 3000L;

    private static ConfigService configService;

    private ConfigService(){}
    public static ConfigService getInstance(){
        if (configService == null){
            configService = new ConfigService();
        }
        return configService;
    }

    public Long getBuyerDelay(Boolean fixed){
        if (fixed) return BUYER_DELAY;
        return new Double(Math.random() * BUYER_DELAY).longValue();
    }

    public  Integer getInventoryCount() {
        return INVENTORY_COUNT;
    }

    public  Properties ipConfig() {
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
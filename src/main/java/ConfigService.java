import com.sun.tools.classfile.ConstantPool;

import java.util.List;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

public class ConfigService {
    private static final Integer INVENTORY_COUNT = 2;
    private static final Long BUYER_DELAY = 3000L;
    private static final Integer HOP_COUNT = 10;

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

    public Integer getInventoryCount() {
        return INVENTORY_COUNT;
    }
    public Integer getHopCount() { return HOP_COUNT; }


    public Properties ipConfig() {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "tpalaschak_config.txt";
        System.out.println(path);
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

    public static void main(String[] args){
        ConfigService configService = ConfigService.getInstance();
//        Properties p = configService.ipConfig();
//        System.out.println(p);
//        String x = (String) p.get("1335");
//        System.out.println

    }
}
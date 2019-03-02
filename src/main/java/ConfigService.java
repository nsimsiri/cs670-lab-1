//import com.sun.tools.classfile.ConstantPool;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.FileInputStream;
import java.io.IOException;

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


    public void ipConfig() {
        String path = System.getProperty("user.dir")+"\\src\\main\\resources\\Graph.txt";
        System.out.println(path);
        File file = new File(path);
        System.out.println(file);
    }

    //public Map<String, Set<String>> edgeList(){

    //}

    public static void main(String[] args){
        ConfigService configService = ConfigService.getInstance();
        configService.ipConfig();
//        System.out.println(System.getProperty("user.dir"));

//        Properties p = configService.ipConfig();
//        System.out.println(p);
//        String x = (String) p.get("1335");
//        System.out.println

    }
}
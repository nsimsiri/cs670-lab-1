import java.util.List;
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
    public Integer getInventoryCount(){
        return INVENTORY_COUNT;
    }

    public Long getBuyerDelay(Boolean fixed){
        if (fixed) return BUYER_DELAY;
        return new Double(Math.random() * BUYER_DELAY).longValue();
    }

}


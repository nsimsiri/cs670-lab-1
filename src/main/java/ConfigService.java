import java.io.*;
import java.util.*;

public class ConfigService {
    private static final Integer INVENTORY_COUNT = 2;
    private static final Long BUYER_DELAY = 3000L;
    private static final Integer HOP_COUNT = 2;
    private static final String sep = File.separator;
    private static final String path = System.getProperty("user.dir")+ String.format("%ssrc%smain%sresources%slocal_test%s",
            sep,sep,sep,sep,sep);

    private static ConfigService configService;
    public String filepath;
    private ConfigService(){
        this.filepath = path;
    }
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


    public Map<String,String[]> ipConfig() {
        //String sep = File.separator;
        //String path = System.getProperty("user.dir")+ String.format("%ssrc%smain%sresources%sBuild_Config",
        //        sep,sep, sep, sep);
        //File file = new File(path);
        HashMap<String, String[]> map = new HashMap<String, String[]>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(this.filepath+"Build_Config"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String ipport = values[0] + values[1];
                String vertex = values[2];
                map.put(vertex, new String[]{values[0], values[1]});
            }
        }
        catch(Exception e){
                e.printStackTrace();
            }
        return map;
        }


    public Map<String, HashSet<String>> edgeList(){
        //String sep = File.separator;
        //String path = System.getProperty("user.dir")+ String.format("%ssrc%smain%sresources%sGraph",
        //       sep,sep, sep, sep);
        //System.out.println(path);
        HashMap<String, HashSet<String>> map = new HashMap<String, HashSet<String>>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(this.filepath+"Graph"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(" ");
                String from = values[0];
                String to = values[1];
                if (map.containsKey(from)) {
                HashSet<String> set = map.get(from);
                set.add(to);
                }
                else{
                    HashSet<String> set = new HashSet<String>();
                    set.add(to);
                    map.put(from, set);
                }
                if (map.containsKey(to)) {
                    HashSet<String> set = map.get(to);
                    set.add(from);
                }
                else{
                    HashSet<String> set = new HashSet<String>();
                    set.add(from);
                    map.put(to, set);
                }

            }
        }
        catch(Exception e){
            e.printStackTrace();

        }
        return map;

    }

    public static void main(String[] args){
        ConfigService configService = ConfigService.getInstance();
        Map<String, String[]> config = configService.ipConfig();
        System.out.println(config.get("1")[0]);
        Map<String,HashSet<String>> config2 = configService.edgeList();
        System.out.println(config2.get("5"));

    }
}
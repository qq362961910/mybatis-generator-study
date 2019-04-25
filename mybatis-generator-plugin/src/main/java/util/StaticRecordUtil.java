package util;

import java.util.HashMap;
import java.util.Map;

public class StaticRecordUtil {

    private static final Map<String, String> TABLE_2_UNION_KEY_PROPERTY_MAPPING = new HashMap<>();


    public static void addTableEntityMapping(String tableName, String unionKeyPropertyName){
        TABLE_2_UNION_KEY_PROPERTY_MAPPING.put(tableName, unionKeyPropertyName);
    }

    public static String getNnionKeyPropertyName(String tableName) {
        return TABLE_2_UNION_KEY_PROPERTY_MAPPING.get(tableName);
    }


}

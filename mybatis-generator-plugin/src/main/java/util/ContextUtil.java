package util;

import java.util.HashMap;
import java.util.Map;

public class ContextUtil {

    private static final Map<String, String> TABLE_ENTITY_MAPPING = new HashMap<>();

    private static final Map<String, String> TABLE_ID_CLASS_MAPPING = new HashMap<>();


    public static void addTableEntityMapping(String tableName, String qualifiedClassName){
        TABLE_ENTITY_MAPPING.put(tableName, qualifiedClassName);
    }

    public static String getTableEntityName(String tableName) {
        return TABLE_ENTITY_MAPPING.get(tableName);
    }

    public static void addTableIdClassMapping(String tableName, String idClassName){
        TABLE_ID_CLASS_MAPPING.put(tableName, idClassName);
    }

    public static String getTableIdClass(String tableName) {
        return TABLE_ID_CLASS_MAPPING.get(tableName);
    }

}

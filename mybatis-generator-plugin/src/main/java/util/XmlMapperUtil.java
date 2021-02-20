package util;

/**
 * @author <a href="mailto:yangjian@ifenxi.com">研发部-杨建</a>
 * @version V1.0
 * @since 2021-02-20 14:16
 **/
public class XmlMapperUtil {
    public static String getSelectByUnionKeyId(String unionKeyJavaTypeName) {
        return "selectBy" + unionKeyJavaTypeName;
    }
    public static String getUpdateByUnionKeyId(String unionKeyJavaTypeName) {
        return "updateBy" + unionKeyJavaTypeName;
    }
    public static String getUpdateSelectiveByUnionKeyId(String unionKeyJavaTypeName) {
        return "updateSelectiveBy" + unionKeyJavaTypeName;
    }
    public static String getDeleteByUnionKeyId(String unionKeyJavaTypeName) {
        return "deleteBy" + unionKeyJavaTypeName;
    }
}

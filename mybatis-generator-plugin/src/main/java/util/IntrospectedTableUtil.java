package util;

import cn.t.util.common.CollectionUtil;
import mybatis3.ExtendedIntrospectedTableMyBatis3Impl;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;

import java.util.List;

public class IntrospectedTableUtil {

    public static boolean isUnionKeyTable(IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> introspectedColumns = introspectedTable.getPrimaryKeyColumns();
        return !CollectionUtil.isEmpty(introspectedColumns) && introspectedColumns.size() > 1;
    }

    public static void setUnionKeyMapId(IntrospectedTable introspectedTable, String mapId) {
        ExtendedIntrospectedTableMyBatis3Impl introspectedTableMyBatis3 = convertToExtendedIntrospectedTableMyBatis3Impl(introspectedTable);
        if(introspectedTable != null) {
            introspectedTableMyBatis3.setUnionKeyMapId(mapId);
        }
    }

    public static String getUnionKeyMapId(IntrospectedTable introspectedTable) {
        ExtendedIntrospectedTableMyBatis3Impl introspectedTableMyBatis3 = convertToExtendedIntrospectedTableMyBatis3Impl(introspectedTable);
        if(introspectedTable != null) {
            introspectedTableMyBatis3.getUnionKeyMapId();
        }
        return null;
    }

    public static boolean exampleClassSeparate(JavaModelGeneratorConfiguration javaModelGeneratorConfiguration) {
        String str = javaModelGeneratorConfiguration.getProperty("exampleClassSeparate");
        if(str == null || str.trim().length() == 0) {
            return false;
        }
        try {
            return Boolean.valueOf(str);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean unionKeyClassSeparate(JavaModelGeneratorConfiguration javaModelGeneratorConfiguration) {
        String str = javaModelGeneratorConfiguration.getProperty("unionKeyClassSeparate");
        if(str == null || str.trim().length() == 0) {
            return false;
        }
        try {
            return Boolean.valueOf(str);
        } catch (Exception e) {
            return false;
        }
    }

    private static ExtendedIntrospectedTableMyBatis3Impl convertToExtendedIntrospectedTableMyBatis3Impl(IntrospectedTable introspectedTable) {
        if(introspectedTable instanceof ExtendedIntrospectedTableMyBatis3Impl) {
            return (ExtendedIntrospectedTableMyBatis3Impl)introspectedTable;
        }
        return null;
    }



}

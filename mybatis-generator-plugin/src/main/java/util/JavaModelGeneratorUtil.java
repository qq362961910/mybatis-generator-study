package util;

import cn.t.util.common.CollectionUtil;
import constants.ClassConstants;
import constants.JavaModelGeneratorConstants;
import constants.PackageConstants;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;

import java.util.List;

public class JavaModelGeneratorUtil {

    public static boolean generateBaseRecordClass(JavaModelGeneratorConfiguration javaModelGeneratorConfiguration) {
        String str = javaModelGeneratorConfiguration.getProperty(JavaModelGeneratorConstants.GENERATE_BASE_RECORD_CLASS);
        if(str == null || str.trim().length() == 0) {
            return false;
        }
        try {
            return Boolean.parseBoolean(str);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean extendBaseExampleClass(JavaModelGeneratorConfiguration javaModelGeneratorConfiguration) {
        String str = javaModelGeneratorConfiguration.getProperty(JavaModelGeneratorConstants.EXTEND_BASE_EXAMPLE_CLASS);
        if(str == null || str.trim().length() == 0) {
            return false;
        }
        try {
            return Boolean.parseBoolean(str);
        } catch (Exception e) {
            return false;
        }
    }

    public static String getPrimaryKeyType(IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> introspectedColumnList = introspectedTable.getPrimaryKeyColumns();
        if(CollectionUtil.isEmpty(introspectedColumnList)) {
            return null;
        } else if(introspectedColumnList.size() == 1){
            return introspectedColumnList.get(0).getFullyQualifiedJavaType().getFullyQualifiedName();
        } else {
            return introspectedTable.getPrimaryKeyType();
        }
    }

    public static Field generateSerialVersionUidField() {
        Field serialVersionUID = new Field();
        serialVersionUID.setName("serialVersionUID");
        serialVersionUID.setVisibility(JavaVisibility.PRIVATE);
        serialVersionUID.setStatic(true);
        serialVersionUID.setFinal(true);
        serialVersionUID.setType(new FullyQualifiedJavaType("long"));
        serialVersionUID.setInitializationString("1L");
        return serialVersionUID;
    }

    public static String getSimpleClassName(String fullClassName) {
        int simplyClassNameIndex = fullClassName.lastIndexOf(".") + 1;
        if(simplyClassNameIndex == 0) {
            return fullClassName;
        }
        return fullClassName.substring(simplyClassNameIndex);
    }

    public static String getBasedRecordClassQualifiedName(String baseRecordType) {
        int shortBaseRecordTypeIndex = baseRecordType.lastIndexOf(".") + 1;
        if(shortBaseRecordTypeIndex == 0) {
            return PackageConstants.BASE_RECORD_CLASS_PACKAGE.concat(".").concat(baseRecordType);
        } else {
            String shortBaseRecordType = baseRecordType.substring(shortBaseRecordTypeIndex);
            return baseRecordType.substring(0, shortBaseRecordTypeIndex).concat(PackageConstants.BASE_RECORD_CLASS_PACKAGE).concat(".").concat(shortBaseRecordType).concat(ClassConstants.BASE_CLASS_SUFFIX);
        }
    }

    public static String getUnionKeyClassQualifiedName(String baseRecordType, boolean unionKeyClassSeparate) {
        int shortBaseRecordTypeIndex = baseRecordType.lastIndexOf(".") + 1;
        if(shortBaseRecordTypeIndex == 0) {
            if(unionKeyClassSeparate) {
                return PackageConstants.BASE_RECORD_CLASS_PACKAGE.concat(".").concat(baseRecordType).concat(ClassConstants.UNION_KEY_CLASS_SUFFIX);
            } else {
                return baseRecordType.concat(ClassConstants.UNION_KEY_CLASS_SUFFIX);
            }
        } else {
            String shortBaseRecordType = baseRecordType.substring(shortBaseRecordTypeIndex);
            StringBuilder sb = new StringBuilder(baseRecordType.substring(0, shortBaseRecordTypeIndex));
            if(unionKeyClassSeparate) {
                sb.append(PackageConstants.UNION_KEY_CLASS_PACKAGE);
                sb.append('.');
            }
            sb.append(shortBaseRecordType);
            sb.append(ClassConstants.UNION_KEY_CLASS_SUFFIX);
            return sb.toString();
        }
    }
}

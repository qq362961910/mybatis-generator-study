package util;

import constants.JavaModelGeneratorConstants;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;

public class JavaModelGeneratorUtil {

    public static boolean generateBaseRecordClass(JavaModelGeneratorConfiguration javaModelGeneratorConfiguration) {
        String str = javaModelGeneratorConfiguration.getProperty(JavaModelGeneratorConstants.GENERATE_BASE_RECORD_CLASS);
        if(str == null || str.trim().length() == 0) {
            return false;
        }
        try {
            return Boolean.valueOf(str);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean generateBaseExampleClass(JavaModelGeneratorConfiguration javaModelGeneratorConfiguration) {
        String str = javaModelGeneratorConfiguration.getProperty(JavaModelGeneratorConstants.GENERATE_BASE_EXAMPLE_CLASS);
        if(str == null || str.trim().length() == 0) {
            return false;
        }
        try {
            return Boolean.valueOf(str);
        } catch (Exception e) {
            return false;
        }
    }

}

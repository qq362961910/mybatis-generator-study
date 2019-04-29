package plugin;

import constants.ClassConstants;
import constants.FieldConstants;
import constants.SqlConstants;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import util.IntrospectedTableUtil;

import java.util.List;

public class ExamplePlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * 该代码时解决当IntrospectedColumn被修改成内联java属性时导致的Example类方法名称的不正确性
     * */
    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if(IntrospectedTableUtil.isUnionKeyTable(introspectedTable)) {
            List<InnerClass> innerClassList = topLevelClass.getInnerClasses();
            for(InnerClass innerClass: innerClassList) {
                String fullyQualifiedName = innerClass.getType().getFullyQualifiedName();
                if(ClassConstants.GENERATED_CRITERIA_CLASS_NAME.equals(fullyQualifiedName)) {
                    List<Method> methodList = innerClass.getMethods();
                    for (Method method: methodList) {
                        if(method.getName().contains(".")) {
                            StringBuilder replacedBuilder = new StringBuilder(SqlConstants.LOGIC_AND);
                            replacedBuilder.append(Character.toUpperCase(FieldConstants.UNION_KEY_PROPERTY_NAME.charAt(0)));
                            replacedBuilder.append(FieldConstants.UNION_KEY_PROPERTY_NAME.substring(1));
                            replacedBuilder.append('.');
                            String newMethodName = method.getName().replace(replacedBuilder.toString(), "");
                            newMethodName = SqlConstants.LOGIC_AND.concat(String.valueOf(Character.toUpperCase(newMethodName.charAt(0))).concat(newMethodName.substring(1)));
                            method.setName(newMethodName);
                        }
                    }
                }
            }
        }
        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }
}

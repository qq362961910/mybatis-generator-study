package plugin;

import constants.FieldConstants;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import util.JavaModelGeneratorUtil;

import java.util.List;

/**
 * 1. modelExampleClassGenerated
 *
 * loop
 * 2. modelFieldGenerated
 * 3. modelGetterMethodGenerated
 * 4. modelSetterMethodGenerated
 * end loop
 *
 * 5.modelBaseRecordClassGenerated
 *
 * */
public class ModelPlugin extends PluginAdapter {


    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * 生成实体
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Field serialVersionUID = JavaModelGeneratorUtil.generateSerialVersionUidField();
        topLevelClass.getFields().add(0, serialVersionUID);
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    /**
     * 生成主键类
     * */
    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        topLevelClass.setSuperClass((FullyQualifiedJavaType)null);
        topLevelClass.addSuperInterface(FieldConstants.SERIALIZABLE);
        topLevelClass.getImportedTypes().clear();
        topLevelClass.addImportedType(FieldConstants.SERIALIZABLE);
        return true;
    }
}

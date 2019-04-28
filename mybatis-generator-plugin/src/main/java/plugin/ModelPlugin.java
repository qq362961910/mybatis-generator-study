package plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;

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

    private static final FullyQualifiedJavaType SERIALIZABLE = new FullyQualifiedJavaType("java.io.Serializable");

    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * 生成实体
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Field serialVersionUID = new Field();
        serialVersionUID.setName("serialVersionUID");
        serialVersionUID.setVisibility(JavaVisibility.PRIVATE);
        serialVersionUID.setStatic(true);
        serialVersionUID.setFinal(true);
        serialVersionUID.setType(new FullyQualifiedJavaType("long"));
        serialVersionUID.setInitializationString("1L");
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
        topLevelClass.addSuperInterface(SERIALIZABLE);
        topLevelClass.getImportedTypes().clear();
        topLevelClass.addImportedType(SERIALIZABLE);
        return true;
    }
}

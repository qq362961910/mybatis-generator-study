package plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

public class MapperPlugin extends PluginAdapter {

    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        //主键默认采用java.lang.Long
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("BaseMapper<"
            + introspectedTable.getBaseRecordType() + ","
            + introspectedTable.getExampleType() + ","
            + "java.lang.Long" + ">");
        FullyQualifiedJavaType imp = new FullyQualifiedJavaType(
            "com.yuntong.backend.dao.BaseMapper");
        //添加 extends MybatisBaseMapper
        interfaze.addSuperInterface(fqjt);

        //添加com.study.security.dao.BaseMapper
        interfaze.addImportedType(imp);
        //方法不需要
        interfaze.getMethods().clear();
        interfaze.getAnnotations().clear();
        return true;
    }

}

package plugin;

import org.mybatis.generator.api.IntrospectedColumn;
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
        if(introspectedTable.hasPrimaryKeyColumns()) {

            //获取主键类型
            List<IntrospectedColumn> introspectedColumns = introspectedTable.getPrimaryKeyColumns();
            String primaryKeyType;
            if(introspectedColumns.size() == 1) {
                primaryKeyType = introspectedTable.getPrimaryKeyColumns().get(0).getFullyQualifiedJavaType().getFullyQualifiedName();
            } else {
                primaryKeyType = introspectedTable.getPrimaryKeyType();
            }
            primaryKeyType = primaryKeyType.substring(primaryKeyType.lastIndexOf(".") + 1);

            //添加 extends MybatisBaseMapper
            interfaze.addSuperInterface(calculateSuperInterface(introspectedTable.getBaseRecordType(), introspectedTable.getExampleType(), primaryKeyType));

            //方法不需要
            interfaze.getMethods().clear();
            //注解清理
            interfaze.getAnnotations().clear();
            //引入清理
            interfaze.getImportedTypes().clear();

            //添加com.study.security.dao.BaseMapper
            interfaze.addImportedType(new FullyQualifiedJavaType("com.study.mybatis.dao.BaseMapper"));

            return true;
        } else {
            throw new RuntimeException("未找到主键类型");
        }
    }


    private FullyQualifiedJavaType calculateSuperInterface(String baseRecordType, String exampleType, String primaryType) {
        StringBuilder fullQualifiedClassNameBuilder = new StringBuilder("BaseMapper<");
        fullQualifiedClassNameBuilder.append(baseRecordType).append(", ");
        fullQualifiedClassNameBuilder.append(exampleType).append(", ");
        fullQualifiedClassNameBuilder.append(primaryType).append(">");
        return new FullyQualifiedJavaType(fullQualifiedClassNameBuilder.toString());
    }
}

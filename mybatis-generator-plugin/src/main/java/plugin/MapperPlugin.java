package plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import util.IntrospectedTableUtil;

import java.util.List;

public class MapperPlugin extends PluginAdapter {

    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if(introspectedTable.hasPrimaryKeyColumns()) {
            //获取主键类型
            String primaryKeyType;
            if(IntrospectedTableUtil.isUnionKeyTable(introspectedTable)) {
                primaryKeyType = introspectedTable.getPrimaryKeyType();
            } else {
                primaryKeyType = introspectedTable.getPrimaryKeyColumns().get(0).getFullyQualifiedJavaType().getFullyQualifiedName();
            }
            //引入清理
            interfaze.getImportedTypes().clear();
            //继承接口
            setSuperInterfaceAndNessaryImport(interfaze, introspectedTable.getBaseRecordType(), introspectedTable.getExampleType(), primaryKeyType);
            //方法不需要
            interfaze.getMethods().clear();
            //注解清理
            interfaze.getAnnotations().clear();
            return true;
        } else {
            throw new RuntimeException("未找到主键类型");
        }
    }


    private void setSuperInterfaceAndNessaryImport(Interface interfaze, String baseRecordType, String exampleType, String primaryType) {
        StringBuilder fullQualifiedClassNameBuilder = new StringBuilder("BaseMapper<");
        fullQualifiedClassNameBuilder.append(baseRecordType).append(", ");
        fullQualifiedClassNameBuilder.append(exampleType).append(", ");
        fullQualifiedClassNameBuilder.append(primaryType).append(">");
        interfaze.addSuperInterface(new FullyQualifiedJavaType(fullQualifiedClassNameBuilder.toString()));

        //添加com.study.security.dao.BaseMapper
        interfaze.addImportedType(new FullyQualifiedJavaType("cn.t.base.mybatis.dao.BaseMapper"));
        interfaze.addImportedType(new FullyQualifiedJavaType(baseRecordType));
        interfaze.addImportedType(new FullyQualifiedJavaType(exampleType));
        interfaze.addImportedType(new FullyQualifiedJavaType(primaryType));
    }
}

package generator;

import cn.t.util.common.CollectionUtil;
import cn.t.util.common.StringUtil;
import constants.FieldConstants;
import generator.table.KeyDescriptor;
import mybatis3.ExtendedIntrospectedTableMyBatis3Impl;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import util.IntrospectedTableUtil;
import util.JavaModelGeneratorUtil;

import java.util.*;
import java.util.stream.Collectors;

import static org.mybatis.generator.internal.util.JavaBeansUtil.*;

/**
 * 联合键生成器
 *
 * @author <a href="mailto:yangjian@ifenxi.com">研发部-杨建</a>
 * @version V1.0
 * @since 2021-02-19 20:59
 **/
public class UnionKeyJavaGenerator extends AbstractJavaGenerator {
    @Override
    public List<CompilationUnit> getCompilationUnits() {
        if(introspectedTable instanceof ExtendedIntrospectedTableMyBatis3Impl) {
            ExtendedIntrospectedTableMyBatis3Impl extendedIntrospectedTable = (ExtendedIntrospectedTableMyBatis3Impl)introspectedTable;
            Map<String, List<KeyDescriptor>> keyDescriptorMap = extendedIntrospectedTable.getKeyDescriptorMap();
            Map<String, List<KeyDescriptor>> keyDescriptorMapToUse = new HashMap<>();
            keyDescriptorMap.forEach((key, value) -> {
                //联合索引且不为主键
                if(value.size() > 1 && !"PRIMARY".equalsIgnoreCase(key)) {
                    keyDescriptorMapToUse.put(key, value);
                }
            });
            if(keyDescriptorMapToUse.size() > 0) {
                List<CompilationUnit> answer = new ArrayList<>();
                keyDescriptorMapToUse.forEach((key, value) -> {
                    value.sort(Comparator.comparingInt(KeyDescriptor::getIndexSeq));
                    List<String> columnList = value.stream().map(KeyDescriptor::getColumnName).collect(Collectors.toList());
                    String snakeString = CollectionUtil.join(columnList, "_");
                    String topLevelClassQualifiedName = JavaModelGeneratorUtil.getUnionKeyClassQualifiedName(extendedIntrospectedTable.getBaseRecordType() + StringUtil.snakeToCamel(snakeString, false),
                        IntrospectedTableUtil.unionKeyClassSeparate(context.getJavaModelGeneratorConfiguration()));

                    TopLevelClass topLevelClass = new TopLevelClass(topLevelClassQualifiedName);
                    topLevelClass.setVisibility(JavaVisibility.PUBLIC);

                    topLevelClass.addSuperInterface(FieldConstants.SERIALIZABLE);
                    topLevelClass.addImportedType(FieldConstants.SERIALIZABLE);

                    //serialVersionUID属性设置
                    topLevelClass.getFields().add(0, JavaModelGeneratorUtil.generateSerialVersionUidField());

                    value.forEach(kd -> {
                        Plugin plugins = context.getPlugins();
                        for (IntrospectedColumn introspectedColumn : introspectedTable.getBaseColumns()) {
                            if(introspectedColumn.getActualColumnName().equalsIgnoreCase(kd.getColumnName())) {
                                Field field = getJavaBeansField(introspectedColumn, context, introspectedTable);
                                if (plugins.modelFieldGenerated(field, topLevelClass,
                                    introspectedColumn, introspectedTable,
                                    Plugin.ModelClassType.PRIMARY_KEY)) {
                                    topLevelClass.addField(field);
                                    topLevelClass.addImportedType(field.getType());
                                }

                                Method method = getJavaBeansGetter(introspectedColumn, context, introspectedTable);
                                if (plugins.modelGetterMethodGenerated(method, topLevelClass,
                                    introspectedColumn, introspectedTable,
                                    Plugin.ModelClassType.PRIMARY_KEY)) {
                                    topLevelClass.addMethod(method);
                                }

                                if (!introspectedTable.isImmutable()) {
                                    method = getJavaBeansSetter(introspectedColumn, context, introspectedTable);
                                    if (plugins.modelSetterMethodGenerated(method, topLevelClass,
                                        introspectedColumn, introspectedTable,
                                        Plugin.ModelClassType.PRIMARY_KEY)) {
                                        topLevelClass.addMethod(method);
                                    }
                                }
                                break;
                            }
                        }
                    });
                    extendedIntrospectedTable.setUnionKeyJavaTypeName(key, JavaModelGeneratorUtil.getSimpleClassName(topLevelClassQualifiedName));
                    answer.add(topLevelClass);
                });
                return answer;
            }
        }
        return Collections.emptyList();
    }
}

package generator;

import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.mybatis3.model.BaseRecordGenerator;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.util.List;

/**
 * 实体类生成器扩展
 * */
public class ExtendedBaseRecordGenerator extends BaseRecordGenerator {

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        List<CompilationUnit> compilationUnitList = super.getCompilationUnits();
        setUnionKeyAsField((TopLevelClass)compilationUnitList.get(0));
        return compilationUnitList;
    }

    /**
     * 如果是联合主键则取消实体类继承关系，将主键类添加到实体类中的字段中
     * */
    private void setUnionKeyAsField(TopLevelClass topLevelClass) {
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            String rootClass = getRootClass();
            if (rootClass != null) {
                FullyQualifiedJavaType superClass = new FullyQualifiedJavaType(rootClass);
                FullyQualifiedJavaType unionKeyClass = topLevelClass.getSuperClass();
                topLevelClass.setSuperClass(superClass);

                //添加主键为属性属性
                Field unionKeyField = new Field("unionKey", unionKeyClass);
                unionKeyField.setVisibility(JavaVisibility.PRIVATE);
                topLevelClass.addField(unionKeyField);
                topLevelClass.addImportedType(rootClass);

                //getter
                Method getter = new Method();
                getter.setVisibility(JavaVisibility.PUBLIC);
                getter.setReturnType(unionKeyClass);
                getter.setName(JavaBeansUtil.getGetterMethodName(unionKeyField.getName(), unionKeyClass));

                StringBuilder getterBuilder = new StringBuilder();
                getterBuilder.append("return ");
                getterBuilder.append(unionKeyField.getName());
                getterBuilder.append(';');
                getter.addBodyLine(getterBuilder.toString());
                topLevelClass.addMethod(getter);


                //setter
                Method setter = new Method();
                setter.setVisibility(JavaVisibility.PUBLIC);
                setter.setReturnType(unionKeyClass);
                setter.setName(JavaBeansUtil.getSetterMethodName(unionKeyField.getName()));
                StringBuilder setterBuilder = new StringBuilder();
                setterBuilder.append("return ");
                setterBuilder.append(unionKeyField.getName());
                setterBuilder.append(';');
                setter.addBodyLine(setterBuilder.toString());
                topLevelClass.addMethod(setter);

            }
        }
    }
}

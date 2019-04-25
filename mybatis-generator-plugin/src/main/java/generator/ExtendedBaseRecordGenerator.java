package generator;

import constants.FieldConstants;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.RootClassInfo;
import org.mybatis.generator.codegen.mybatis3.model.BaseRecordGenerator;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.mybatis.generator.internal.util.JavaBeansUtil.*;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * 实体类生成器扩展，被ExtendedIntrospectedTableMyBatis3Impl加载
 * */
public class ExtendedBaseRecordGenerator extends BaseRecordGenerator {

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        progressCallback.startTask(getString(
            "Progress.8", table.toString()));
        Plugin plugins = context.getPlugins();
        CommentGenerator commentGenerator = context.getCommentGenerator();

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
            introspectedTable.getBaseRecordType());
        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(topLevelClass);

        FullyQualifiedJavaType superClass = getSuperClass();
        if (superClass != null) {
            topLevelClass.setSuperClass(superClass);
            topLevelClass.addImportedType(superClass);
        }
        setUnionKeyAsField(topLevelClass);

        commentGenerator.addModelClassComment(topLevelClass, introspectedTable);

        List<IntrospectedColumn> introspectedColumns = getColumnsInThisClass();

        if (introspectedTable.isConstructorBased()) {
            addParameterizedConstructor(topLevelClass, introspectedTable.getNonBLOBColumns());

            if (includeBLOBColumns()) {
                addParameterizedConstructor(topLevelClass, introspectedTable.getAllColumns());
            }

            if (!introspectedTable.isImmutable()) {
                addDefaultConstructor(topLevelClass);
            }
        }

        String rootClass = getRootClass();
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            if (RootClassInfo.getInstance(rootClass, warnings)
                .containsProperty(introspectedColumn)) {
                continue;
            }

            Field field = getJavaBeansField(introspectedColumn, context, introspectedTable);
            if (plugins.modelFieldGenerated(field, topLevelClass,
                introspectedColumn, introspectedTable,
                Plugin.ModelClassType.BASE_RECORD)) {
                topLevelClass.addField(field);
                topLevelClass.addImportedType(field.getType());
            }

            Method method = getJavaBeansGetter(introspectedColumn, context, introspectedTable);
            if (plugins.modelGetterMethodGenerated(method, topLevelClass,
                introspectedColumn, introspectedTable,
                Plugin.ModelClassType.BASE_RECORD)) {
                topLevelClass.addMethod(method);
            }

            if (!introspectedTable.isImmutable()) {
                method = getJavaBeansSetter(introspectedColumn, context, introspectedTable);
                if (plugins.modelSetterMethodGenerated(method, topLevelClass,
                    introspectedColumn, introspectedTable,
                    Plugin.ModelClassType.BASE_RECORD)) {
                    topLevelClass.addMethod(method);
                }
            }
        }

        List<CompilationUnit> answer = new ArrayList<>();
        if (context.getPlugins().modelBaseRecordClassGenerated(
            topLevelClass, introspectedTable)) {
            answer.add(topLevelClass);
        }
        return answer;
    }

    /**
     * 此处修改了父类的实现从而使实体类不再继承主键类
     * */
    private FullyQualifiedJavaType getSuperClass() {
        String rootClass = getRootClass();
        if (rootClass != null) {
            return new FullyQualifiedJavaType(rootClass);
        }
        return null;
    }

    private List<IntrospectedColumn> getColumnsInThisClass() {
        List<IntrospectedColumn> introspectedColumns;
        if (includePrimaryKeyColumns()) {
            if (includeBLOBColumns()) {
                introspectedColumns = introspectedTable.getAllColumns();
            } else {
                introspectedColumns = introspectedTable.getNonBLOBColumns();
            }
        } else {
            if (includeBLOBColumns()) {
                introspectedColumns = introspectedTable
                    .getNonPrimaryKeyColumns();
            } else {
                introspectedColumns = introspectedTable.getBaseColumns();
            }
        }

        return introspectedColumns;
    }

    private void addParameterizedConstructor(TopLevelClass topLevelClass, List<IntrospectedColumn> constructorColumns) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setConstructor(true);
        method.setName(topLevelClass.getType().getShortName());
        context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);

        for (IntrospectedColumn introspectedColumn : constructorColumns) {
            method.addParameter(new Parameter(introspectedColumn.getFullyQualifiedJavaType(),
                introspectedColumn.getJavaProperty()));
            topLevelClass.addImportedType(introspectedColumn.getFullyQualifiedJavaType());
        }

        StringBuilder sb = new StringBuilder();
        List<String> superColumns = new LinkedList<String>();
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            boolean comma = false;
            sb.append("super("); //$NON-NLS-1$
            for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                if (comma) {
                    sb.append(", "); //$NON-NLS-1$
                } else {
                    comma = true;
                }
                sb.append(introspectedColumn.getJavaProperty());
                superColumns.add(introspectedColumn.getActualColumnName());
            }
            sb.append(");"); //$NON-NLS-1$
            method.addBodyLine(sb.toString());
        }

        for (IntrospectedColumn introspectedColumn : constructorColumns) {
            if (!superColumns.contains(introspectedColumn.getActualColumnName())) {
                sb.setLength(0);
                sb.append("this."); //$NON-NLS-1$
                sb.append(introspectedColumn.getJavaProperty());
                sb.append(" = "); //$NON-NLS-1$
                sb.append(introspectedColumn.getJavaProperty());
                sb.append(';');
                method.addBodyLine(sb.toString());
            }
        }

        topLevelClass.addMethod(method);
    }

    private boolean includePrimaryKeyColumns() {
        return !introspectedTable.getRules().generatePrimaryKeyClass()
            && introspectedTable.hasPrimaryKeyColumns();
    }

    private boolean includeBLOBColumns() {
        return !introspectedTable.getRules().generateRecordWithBLOBsClass()
            && introspectedTable.hasBLOBColumns();
    }

    /**
     * 如果是联合主键则取消实体类继承关系，将主键类添加到实体类中的字段中
     * */
    private void setUnionKeyAsField(TopLevelClass topLevelClass) {
        List<IntrospectedColumn> keyColumns = introspectedTable.getPrimaryKeyColumns();
        if (keyColumns != null && keyColumns.size() > 1) {
            String primaryKeyType = introspectedTable.getPrimaryKeyType();
            if (primaryKeyType != null) {
                FullyQualifiedJavaType unionKeyClass = new FullyQualifiedJavaType(primaryKeyType);

                //添加主键为属性属性
                Field unionKeyField = new Field(FieldConstants.UNION_KEY_PROPERTY_NAME, unionKeyClass);
                unionKeyField.setVisibility(JavaVisibility.PRIVATE);
                topLevelClass.addField(unionKeyField);
                topLevelClass.addImportedType(unionKeyClass);

                //getter
                Method getter = new Method();
                getter.setVisibility(JavaVisibility.PUBLIC);
                getter.setReturnType(unionKeyClass);
                getter.setName(JavaBeansUtil.getGetterMethodName(unionKeyField.getName(), unionKeyClass));

                StringBuilder getterBuilder = new StringBuilder();
                getterBuilder.append("return this.");
                getterBuilder.append(unionKeyField.getName());
                getterBuilder.append(';');
                getter.addBodyLine(getterBuilder.toString());
                topLevelClass.addMethod(getter);

                //setter
                Method setter = new Method();
                setter.setVisibility(JavaVisibility.PUBLIC);
                setter.addParameter(new Parameter(unionKeyClass, unionKeyField.getName()));
                setter.setName(JavaBeansUtil.getSetterMethodName(unionKeyField.getName()));
                StringBuilder setterBuilder = new StringBuilder();
                setterBuilder.append("this."); //$NON-NLS-1$
                setterBuilder.append(unionKeyField.getName());
                setterBuilder.append(" = "); //$NON-NLS-1$
                setterBuilder.append(unionKeyField.getName());
                setterBuilder.append(';');
                setter.addBodyLine(setterBuilder.toString());
                topLevelClass.addMethod(setter);

            }
        }
    }
}

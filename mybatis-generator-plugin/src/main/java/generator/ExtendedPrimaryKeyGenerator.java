package generator;

import constants.PackageConstants;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.RootClassInfo;
import org.mybatis.generator.codegen.mybatis3.model.PrimaryKeyGenerator;

import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.internal.util.JavaBeansUtil.getJavaBeansField;
import static org.mybatis.generator.internal.util.JavaBeansUtil.getJavaBeansGetter;
import static org.mybatis.generator.internal.util.JavaBeansUtil.getJavaBeansSetter;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

public class ExtendedPrimaryKeyGenerator extends PrimaryKeyGenerator {

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        progressCallback.startTask(getString(
            "Progress.7", table.toString())); //$NON-NLS-1$
        Plugin plugins = context.getPlugins();
        CommentGenerator commentGenerator = context.getCommentGenerator();

        String unionKeyType = introspectedTable.getPrimaryKeyType();
        FullyQualifiedJavaType type;

        if(unionKeyClassSeparate()) {
            int shortBaseRecordTypeIndex = unionKeyType.lastIndexOf(".") + 1;
            String shortUnionKeyType = unionKeyType.substring(shortBaseRecordTypeIndex);
            String typeStr = unionKeyType.substring(0, shortBaseRecordTypeIndex).concat(PackageConstants.UNION_KEY_CLASS_PACKAGE).concat(".".concat(shortUnionKeyType));
            type = new FullyQualifiedJavaType(typeStr);
        } else {
            type = new FullyQualifiedJavaType(unionKeyType);
        }

        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(topLevelClass);

        String rootClass = getRootClass();
        if (rootClass != null) {
            topLevelClass.setSuperClass(new FullyQualifiedJavaType(rootClass));
            topLevelClass.addImportedType(topLevelClass.getSuperClass());
        }

        if (introspectedTable.isConstructorBased()) {
            addParameterizedConstructor(topLevelClass);

            if (!introspectedTable.isImmutable()) {
                addDefaultConstructor(topLevelClass);
            }
        }

        commentGenerator.addModelClassComment(topLevelClass, introspectedTable);

        for (IntrospectedColumn introspectedColumn : introspectedTable
            .getPrimaryKeyColumns()) {
            if (RootClassInfo.getInstance(rootClass, warnings)
                .containsProperty(introspectedColumn)) {
                continue;
            }

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
        }

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        if (context.getPlugins().modelPrimaryKeyClassGenerated(
            topLevelClass, introspectedTable)) {
            answer.add(topLevelClass);
        }
        return answer;
    }

    private void addParameterizedConstructor(TopLevelClass topLevelClass) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setConstructor(true);
        method.setName(topLevelClass.getType().getShortName());
        context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);

        StringBuilder sb = new StringBuilder();
        for (IntrospectedColumn introspectedColumn : introspectedTable
            .getPrimaryKeyColumns()) {
            method.addParameter(new Parameter(introspectedColumn.getFullyQualifiedJavaType(),
                introspectedColumn.getJavaProperty()));
            sb.setLength(0);
            sb.append("this."); //$NON-NLS-1$
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" = "); //$NON-NLS-1$
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(';');
            method.addBodyLine(sb.toString());
        }

        topLevelClass.addMethod(method);
    }

    private boolean unionKeyClassSeparate() {
        String str = context.getJavaModelGeneratorConfiguration().getProperty("unionKeyClassSeparate");
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

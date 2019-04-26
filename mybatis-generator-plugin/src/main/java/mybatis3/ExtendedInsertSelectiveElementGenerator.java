package mybatis3;

import constants.FieldConstants;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.InsertSelectiveElementGenerator;
import org.mybatis.generator.config.GeneratedKey;

import java.util.List;

public class ExtendedInsertSelectiveElementGenerator extends InsertSelectiveElementGenerator {

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("insert");

        answer.addAttribute(new Attribute(
            "id", introspectedTable.getInsertSelectiveStatementId()));

        FullyQualifiedJavaType parameterType = introspectedTable.getRules()
            .calculateAllFieldsClass();

        answer.addAttribute(new Attribute("parameterType",
            parameterType.getFullyQualifiedName()));

        context.getCommentGenerator().addComment(answer);

        GeneratedKey gk = introspectedTable.getGeneratedKey();
        if (gk != null) {
            IntrospectedColumn introspectedColumn = introspectedTable
                .getColumn(gk.getColumn());
            // if the column is null, then it's a configuration error. The
            // warning has already been reported
            if (introspectedColumn != null) {
                if (gk.isJdbcStandard()) {
                    answer.addAttribute(new Attribute("useGeneratedKeys", "true"));  //$NON-NLS-2$
                    answer.addAttribute(new Attribute("keyProperty", introspectedColumn.getJavaProperty()));
                    answer.addAttribute(new Attribute("keyColumn", introspectedColumn.getActualColumnName()));
                } else {
                    answer.addElement(getSelectKey(introspectedColumn, gk));
                }
            }
        }

        StringBuilder sb = new StringBuilder();

        sb.append("insert into ");
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        XmlElement insertTrimElement = new XmlElement("trim");
        insertTrimElement.addAttribute(new Attribute("prefix", "("));  //$NON-NLS-2$
        insertTrimElement.addAttribute(new Attribute("suffix", ")"));  //$NON-NLS-2$
        insertTrimElement.addAttribute(new Attribute("suffixOverrides", ","));  //$NON-NLS-2$
        answer.addElement(insertTrimElement);

        XmlElement valuesTrimElement = new XmlElement("trim");
        valuesTrimElement.addAttribute(new Attribute("prefix", "values ("));  //$NON-NLS-2$
        valuesTrimElement.addAttribute(new Attribute("suffix", ")"));  //$NON-NLS-2$
        valuesTrimElement.addAttribute(new Attribute("suffixOverrides", ","));  //$NON-NLS-2$
        answer.addElement(valuesTrimElement);


        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        List<IntrospectedColumn> introspectedColumnList = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns());

        /**
         * 联合主键情况下从introspectedColumnList删除掉相关主键列，并提前最sql拼接处理
         * */
        if(primaryKeyColumns.size() > 1) {
            introspectedColumnList.removeIf(primaryKeyColumns::contains);
            //设置联合主键
            sb.setLength(0);
            sb.append(FieldConstants.UNION_KEY_PROPERTY_NAME);
            sb.append(" != null");
            XmlElement insertNotNullElement = new XmlElement("if");
            insertNotNullElement.addAttribute(new Attribute(
                "test", sb.toString()));

            sb.setLength(0);
            sb.append(FieldConstants.UNION_KEY_PROPERTY_NAME);
            sb.append(" != null");
            XmlElement valuesNotNullElement = new XmlElement("if");
            valuesNotNullElement.addAttribute(new Attribute(
                "test", sb.toString()));

            for(IntrospectedColumn keyColumn: primaryKeyColumns) {
                sb.setLength(0);
                sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(keyColumn));
                sb.append(',');
                insertNotNullElement.addElement(new TextElement(sb.toString()));

                sb.setLength(0);
                sb.append(keyColumn.getJavaProperty(FieldConstants.UNION_KEY_PROPERTY_NAME.concat(".")));
                sb.append(',');
                valuesNotNullElement.addElement(new TextElement(sb.toString()));
            }
            insertTrimElement.addElement(insertNotNullElement);
            valuesTrimElement.addElement(valuesNotNullElement);
        }
        for (IntrospectedColumn introspectedColumn : introspectedColumnList) {
            if (introspectedColumn.isSequenceColumn()
                || introspectedColumn.getFullyQualifiedJavaType().isPrimitive()) {
                // if it is a sequence column, it is not optional
                // This is required for MyBatis3 because MyBatis3 parses
                // and calculates the SQL before executing the selectKey

                // if it is primitive, we cannot do a null check
                sb.setLength(0);
                sb.append(MyBatis3FormattingUtilities
                    .getEscapedColumnName(introspectedColumn));
                sb.append(',');
                insertTrimElement.addElement(new TextElement(sb.toString()));

                sb.setLength(0);
                sb.append(MyBatis3FormattingUtilities
                    .getParameterClause(introspectedColumn));
                sb.append(',');
                valuesTrimElement.addElement(new TextElement(sb.toString()));

                continue;
            }

            sb.setLength(0);
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" != null");
            XmlElement insertNotNullElement = new XmlElement("if");
            insertNotNullElement.addAttribute(new Attribute(
                "test", sb.toString()));

            sb.setLength(0);
            sb.append(MyBatis3FormattingUtilities
                .getEscapedColumnName(introspectedColumn));
            sb.append(',');
            insertNotNullElement.addElement(new TextElement(sb.toString()));
            insertTrimElement.addElement(insertNotNullElement);

            sb.setLength(0);
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" != null");
            XmlElement valuesNotNullElement = new XmlElement("if");
            valuesNotNullElement.addAttribute(new Attribute(
                "test", sb.toString()));

            sb.setLength(0);
            sb.append(MyBatis3FormattingUtilities
                .getParameterClause(introspectedColumn));
            sb.append(',');
            valuesNotNullElement.addElement(new TextElement(sb.toString()));
            valuesTrimElement.addElement(valuesNotNullElement);
        }

        if (context.getPlugins().sqlMapInsertSelectiveElementGenerated(
            answer, introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}

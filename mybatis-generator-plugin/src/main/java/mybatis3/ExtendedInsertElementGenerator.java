package mybatis3;

import constants.FieldConstants;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.InsertElementGenerator;
import org.mybatis.generator.config.GeneratedKey;

import java.util.ArrayList;
import java.util.List;

public class ExtendedInsertElementGenerator extends InsertElementGenerator {

    private boolean isSimple;


    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("insert");

        answer.addAttribute(new Attribute(
            "id", introspectedTable.getInsertStatementId()));

        FullyQualifiedJavaType parameterType;
        if (isSimple) {
            parameterType = new FullyQualifiedJavaType(
                introspectedTable.getBaseRecordType());
        } else {
            parameterType = introspectedTable.getRules()
                .calculateAllFieldsClass();
        }

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
                    answer.addAttribute(new Attribute(
                        "useGeneratedKeys", "true")); //$NON-NLS-2$
                    answer.addAttribute(new Attribute(
                        "keyProperty", introspectedColumn.getJavaProperty()));
                    answer.addAttribute(new Attribute(
                        "keyColumn", introspectedColumn.getActualColumnName()));
                } else {
                    answer.addElement(getSelectKey(introspectedColumn, gk));
                }
            }
        }

        StringBuilder insertClause = new StringBuilder();

        insertClause.append("insert into ");
        insertClause.append(introspectedTable
            .getFullyQualifiedTableNameAtRuntime());
        insertClause.append(" (");

        StringBuilder valuesClause = new StringBuilder();
        valuesClause.append("values (");

        List<String> valuesClauses = new ArrayList<>();
        List<IntrospectedColumn> columns = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns());
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        for (int i = 0; i < columns.size(); i++) {
            IntrospectedColumn introspectedColumn = columns.get(i);

            insertClause.append(MyBatis3FormattingUtilities
                .getEscapedColumnName(introspectedColumn));

            /**
             * 联合主键情况下拼接内联主键属性前缀
             * */
            if(primaryKeyColumns.size() > 1 && primaryKeyColumns.contains(introspectedColumn)) {
                for(IntrospectedColumn keyColumn: primaryKeyColumns) {
                    if(keyColumn.getActualColumnName().equals(introspectedColumn.getActualColumnName())) {
                        String prefix = FieldConstants.UNION_KEY_PROPERTY_NAME.concat(".");
                        valuesClause.append(MyBatis3FormattingUtilities
                            .getParameterClause(introspectedColumn, prefix));
                        break;
                    }
                }
            } else {
                valuesClause.append(MyBatis3FormattingUtilities
                    .getParameterClause(introspectedColumn));
            }
            if (i + 1 < columns.size()) {
                insertClause.append(", ");
                valuesClause.append(", ");
            }

            if (valuesClause.length() > 80) {
                answer.addElement(new TextElement(insertClause.toString()));
                insertClause.setLength(0);
                OutputUtilities.xmlIndent(insertClause, 1);

                valuesClauses.add(valuesClause.toString());
                valuesClause.setLength(0);
                OutputUtilities.xmlIndent(valuesClause, 1);
            }
        }

        insertClause.append(')');
        answer.addElement(new TextElement(insertClause.toString()));

        valuesClause.append(')');
        valuesClauses.add(valuesClause.toString());

        for (String clause : valuesClauses) {
            answer.addElement(new TextElement(clause));
        }

        if (context.getPlugins().sqlMapInsertElementGenerated(answer,
            introspectedTable)) {
            parentElement.addElement(answer);
        }
    }

    public ExtendedInsertElementGenerator(boolean isSimple) {
        super(isSimple);
        this.isSimple = isSimple;
    }


}

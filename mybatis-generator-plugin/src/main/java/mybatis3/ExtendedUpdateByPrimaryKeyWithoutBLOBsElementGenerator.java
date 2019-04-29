package mybatis3;

import constants.FieldConstants;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.UpdateByPrimaryKeyWithoutBLOBsElementGenerator;
import util.IntrospectedTableUtil;

import java.util.Iterator;

public class ExtendedUpdateByPrimaryKeyWithoutBLOBsElementGenerator extends UpdateByPrimaryKeyWithoutBLOBsElementGenerator {

    private boolean isSimple;

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("update");

        answer.addAttribute(new Attribute(
            "id", introspectedTable.getUpdateByPrimaryKeyStatementId()));
        answer.addAttribute(new Attribute("parameterType",
            introspectedTable.getBaseRecordType()));

        context.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();
        sb.append("update ");
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        // set up for first column
        sb.setLength(0);
        sb.append("set ");

        Iterator<IntrospectedColumn> iter;
        if (isSimple) {
            iter = ListUtilities.removeGeneratedAlwaysColumns(introspectedTable.getNonPrimaryKeyColumns()).iterator();
        } else {
            iter = ListUtilities.removeGeneratedAlwaysColumns(introspectedTable.getBaseColumns()).iterator();
        }
        while (iter.hasNext()) {
            IntrospectedColumn introspectedColumn = iter.next();

            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));

            if (iter.hasNext()) {
                sb.append(',');
            }

            answer.addElement(new TextElement(sb.toString()));

            // set up for the next column
            if (iter.hasNext()) {
                sb.setLength(0);
                OutputUtilities.xmlIndent(sb, 1);
            }
        }

        boolean and = false;
        boolean isUnionKey = IntrospectedTableUtil.isUnionKeyTable(introspectedTable);
        for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
            sb.setLength(0);
            if (and) {
                sb.append("  and ");
            } else {
                sb.append("where ");
                and = true;
            }
            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            if(isUnionKey) {
                sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, FieldConstants.UNION_KEY_PROPERTY_NAME.concat(".")));
            } else {
                sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            }
            answer.addElement(new TextElement(sb.toString()));
        }
        if (context.getPlugins()
            .sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(answer,
                introspectedTable)) {
            parentElement.addElement(answer);
        }
    }

    public ExtendedUpdateByPrimaryKeyWithoutBLOBsElementGenerator(boolean isSimple) {
        super(isSimple);
        this.isSimple = isSimple;
    }
}

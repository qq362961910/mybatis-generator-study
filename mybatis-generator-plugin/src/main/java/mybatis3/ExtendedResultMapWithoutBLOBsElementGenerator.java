package mybatis3;


import cn.t.util.common.CollectionUtil;
import constants.FieldConstants;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.ResultMapWithoutBLOBsElementGenerator;
import util.IntrospectedTableUtil;

import java.util.List;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

public class ExtendedResultMapWithoutBLOBsElementGenerator extends ResultMapWithoutBLOBsElementGenerator {

    private boolean isSimple;
    private XmlElement parentElement;

    @Override
    public void addElements(XmlElement parentElement) {
        this.parentElement = parentElement;

        XmlElement answer = new XmlElement("resultMap");
        answer.addAttribute(new Attribute("id", introspectedTable.getBaseResultMapId()));

        String returnType;
        if (isSimple) {
            returnType = introspectedTable.getBaseRecordType();
        } else {
            if (introspectedTable.getRules().generateBaseRecordClass()) {
                returnType = introspectedTable.getBaseRecordType();
            } else {
                returnType = introspectedTable.getPrimaryKeyType();
            }
        }
        answer.addAttribute(new Attribute("type", returnType));
        context.getCommentGenerator().addComment(answer);
        if (introspectedTable.isConstructorBased()) {
            addResultMapConstructorElements(answer);
        } else {
            addResultMapElements(answer);
        }

        if (context.getPlugins().sqlMapResultMapWithoutBLOBsElementGenerated(
            answer, introspectedTable)) {
            parentElement.addElement(answer);
        }
    }

    private void addResultMapElements(XmlElement answer) {
        List<IntrospectedColumn> keyColumns = introspectedTable.getPrimaryKeyColumns();
        XmlElement unionKeyResult = null;
        //联合主键情况
        if(CollectionUtil.isSizeEqualAndGreaterThan(keyColumns, 2)) {
            //生成主键映射map
            addUnionKeyResultMap(parentElement);
            //BaseResultMap关联主键Map
            XmlElement resultElement = new XmlElement("association");
            resultElement.addAttribute(new Attribute("property", FieldConstants.UNION_KEY_PROPERTY_NAME));
            String keyMapId = IntrospectedTableUtil.getUnionKeyMapId(introspectedTable);
            resultElement.addAttribute(new Attribute("resultMap", keyMapId));
            unionKeyResult = resultElement;
        } else {
            for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                XmlElement resultElement = new XmlElement("id");
                resultElement.addAttribute(new Attribute("column", MyBatis3FormattingUtilities.getRenamedColumnNameForResultMap(introspectedColumn)));
                resultElement.addAttribute(new Attribute("property", introspectedColumn.getJavaProperty()));
                resultElement.addAttribute(new Attribute("jdbcType", introspectedColumn.getJdbcTypeName()));
                if (stringHasValue(introspectedColumn.getTypeHandler())) {
                    resultElement.addAttribute(new Attribute("typeHandler", introspectedColumn.getTypeHandler()));
                }
                answer.addElement(resultElement);
            }
        }
        List<IntrospectedColumn> columns;
        if (isSimple) {
            columns = introspectedTable.getNonPrimaryKeyColumns();
        } else {
            columns = introspectedTable.getBaseColumns();
        }
        addBaseColumnResultToMap(columns, answer);
        if(unionKeyResult != null) {
            answer.addElement(unionKeyResult);
        }
    }

    private void addResultMapConstructorElements(XmlElement answer) {
        XmlElement constructor = new XmlElement("constructor");

        for (IntrospectedColumn introspectedColumn : introspectedTable
            .getPrimaryKeyColumns()) {
            XmlElement resultElement = new XmlElement("idArg");

            resultElement
                .addAttribute(new Attribute(
                    "column", MyBatis3FormattingUtilities.getRenamedColumnNameForResultMap(introspectedColumn)));
            resultElement.addAttribute(new Attribute("jdbcType",
                introspectedColumn.getJdbcTypeName()));
            resultElement.addAttribute(new Attribute("javaType",
                introspectedColumn.getFullyQualifiedJavaType()
                    .getFullyQualifiedName()));

            if (stringHasValue(introspectedColumn.getTypeHandler())) {
                resultElement.addAttribute(new Attribute(
                    "typeHandler", introspectedColumn.getTypeHandler()));
            }

            constructor.addElement(resultElement);
        }

        List<IntrospectedColumn> columns;
        if (isSimple) {
            columns = introspectedTable.getNonPrimaryKeyColumns();
        } else {
            columns = introspectedTable.getBaseColumns();
        }
        for (IntrospectedColumn introspectedColumn : columns) {
            XmlElement resultElement = new XmlElement("arg");

            resultElement
                .addAttribute(new Attribute(
                    "column", MyBatis3FormattingUtilities.getRenamedColumnNameForResultMap(introspectedColumn)));
            resultElement.addAttribute(new Attribute("jdbcType",
                introspectedColumn.getJdbcTypeName()));
            resultElement.addAttribute(new Attribute("javaType",
                introspectedColumn.getFullyQualifiedJavaType()
                    .getFullyQualifiedName()));

            if (stringHasValue(introspectedColumn.getTypeHandler())) {
                resultElement.addAttribute(new Attribute(
                    "typeHandler", introspectedColumn.getTypeHandler()));
            }

            constructor.addElement(resultElement);
        }

        answer.addElement(constructor);
    }


    /**
     * 添加UnionKeyMap
     * */
    private void addUnionKeyResultMap(XmlElement parentElement) {
        List<IntrospectedColumn> keyColumns = introspectedTable.getPrimaryKeyColumns();
        if(introspectedTable instanceof ExtendedIntrospectedTableMyBatis3Impl) {
            XmlElement answer = new XmlElement("resultMap");
            answer.addAttribute(new Attribute("id", IntrospectedTableUtil.getUnionKeyMapId(introspectedTable)));
            answer.addAttribute(new Attribute("type", introspectedTable.getPrimaryKeyType()));
            addBaseColumnResultToMap(keyColumns, answer);
            parentElement.addElement(answer);
        }
    }


    /**
     * 参考了父类addResultMapElements方法中处理非主键类型的列
     * 抽取出来的
     * */
    private void addBaseColumnResultToMap(List<IntrospectedColumn> columns, XmlElement parent) {
        for (IntrospectedColumn introspectedColumn : columns) {
            XmlElement resultElement = new XmlElement("result");

            resultElement
                .addAttribute(new Attribute(
                    "column", MyBatis3FormattingUtilities.getRenamedColumnNameForResultMap(introspectedColumn)));
            resultElement.addAttribute(new Attribute(
                "property", introspectedColumn.getJavaProperty()));
            resultElement.addAttribute(new Attribute("jdbcType",
                introspectedColumn.getJdbcTypeName()));

            if (stringHasValue(introspectedColumn.getTypeHandler())) {
                resultElement.addAttribute(new Attribute(
                    "typeHandler", introspectedColumn.getTypeHandler()));
            }
            parent.addElement(resultElement);
        }
    }


    public ExtendedResultMapWithoutBLOBsElementGenerator(boolean isSimple) {
        super(isSimple);
        this.isSimple = isSimple;
    }
}

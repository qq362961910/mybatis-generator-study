package mybatis3;

import generator.table.KeyDescriptor;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.SelectByPrimaryKeyElementGenerator;
import util.XmlMapperUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生成 union key select|update|delete
 * @author <a href="mailto:yangjian@ifenxi.com">研发部-杨建</a>
 * @version V1.0
 * @since 2021-02-20 12:18
 **/
public class UnionKeyOperationElementGenerator extends SelectByPrimaryKeyElementGenerator {
    @Override
    public void addElements(XmlElement parentElement) {
        if(introspectedTable instanceof ExtendedIntrospectedTableMyBatis3Impl) {
            ExtendedIntrospectedTableMyBatis3Impl introspectedTableMyBatis3 = (ExtendedIntrospectedTableMyBatis3Impl)introspectedTable;
            Map<String, List<KeyDescriptor>> keyDescriptorMap = introspectedTableMyBatis3.getKeyDescriptorMap();
            Map<String, List<KeyDescriptor>> keyDescriptorMapToUse = new HashMap<>();
            keyDescriptorMap.forEach((key, value) -> {
                //联合索引且不为主键
                if(value.size() > 1 && !"PRIMARY".equalsIgnoreCase(key)) {
                    keyDescriptorMapToUse.put(key, value);
                }
            });
            if(keyDescriptorMapToUse.size() == 0) {
                return;
            }
            keyDescriptorMapToUse.forEach((indexName, keyDescriptorList) -> {
                //selectByUnionKeyId
                XmlElement answer = new XmlElement("select");
                String unionKeyJavaTypeName = introspectedTableMyBatis3.getUnionKeyJavaTypeName(indexName);
                String selectByUnionKeyId = XmlMapperUtil.getSelectByUnionKeyId(unionKeyJavaTypeName);
                answer.addAttribute(new Attribute("id", selectByUnionKeyId));
                if (introspectedTable.getRules().generateResultMapWithBLOBs()) {
                    answer.addAttribute(new Attribute("resultMap",
                        introspectedTable.getResultMapWithBLOBsId()));
                } else {
                    answer.addAttribute(new Attribute("resultMap",
                        introspectedTable.getBaseResultMapId()));
                }
                answer.addAttribute(new Attribute("parameterType", unionKeyJavaTypeName));
                context.getCommentGenerator().addComment(answer);
                //select
                answer.addElement(new TextElement("select"));
                //include
                answer.addElement(getBaseColumnListElement());
                if (introspectedTable.hasBLOBColumns()) {
                    answer.addElement(new TextElement(","));
                    answer.addElement(getBlobColumnListElement());
                }
                //from table
                answer.addElement(new TextElement("from " + introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime()));

                XmlElement where = new XmlElement("where");
                answer.addElement(where);
                StringBuilder sb = new StringBuilder();
                for(KeyDescriptor kd: keyDescriptorList) {
                    for (IntrospectedColumn introspectedColumn : introspectedTable.getBaseColumns()) {
                        if(introspectedColumn.getActualColumnName().equalsIgnoreCase(kd.getColumnName())) {

                            sb.setLength(0);
                            sb.append(introspectedColumn.getJavaProperty());
                            sb.append(" != null");
                            XmlElement isNotNullElement = new XmlElement("if");
                            isNotNullElement.addAttribute(new Attribute("test", sb.toString()));
                            where.addElement(isNotNullElement);

                            sb.setLength(0);
                            sb.append("  and ");
                            sb.append(MyBatis3FormattingUtilities
                                .getAliasedEscapedColumnName(introspectedColumn));
                            sb.append(" = ");
                            sb.append(MyBatis3FormattingUtilities
                                .getParameterClause(introspectedColumn));
                            isNotNullElement.addElement(new TextElement(sb.toString()));
                            break;
                        }
                    }
                }
                parentElement.addElement(answer);
            });
        }
    }
}

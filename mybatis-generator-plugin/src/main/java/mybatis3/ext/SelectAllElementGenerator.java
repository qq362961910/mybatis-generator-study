package mybatis3.ext;

import constants.XmlMapConstants;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

public class SelectAllElementGenerator extends AbstractXmlElementGenerator {

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("select");
        answer.addAttribute(new Attribute("id", XmlMapConstants.EXT_SELECT_ALL));
        answer.addAttribute(new Attribute("resultMap", introspectedTable.getBaseResultMapId()));
        answer.addAttribute(new Attribute("statementType", "STATEMENT"));
        answer.addElement(new TextElement("select "));
        answer.addElement(getBaseColumnListElement());
        answer.addElement(new TextElement("from ".concat(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime())));
        parentElement.addElement(answer);
    }
}

package generator;

import mybatis3.ExtendedInsertElementGenerator;
import mybatis3.ExtendedInsertSelectiveElementGenerator;
import mybatis3.ExtendedResultMapWithoutBLOBsElementGenerator;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.XMLMapperGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

/**
 * Xml文件生成器扩展，被ExtendedJavaMapperGenerator创建
 * */
public class ExtendedXMLMapperGenerator extends XMLMapperGenerator {


    @Override
    protected void addInsertElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateInsert()) {
            if (introspectedTable.getPrimaryKeyColumns().size() > 1) {
                AbstractXmlElementGenerator elementGenerator = new ExtendedInsertElementGenerator(false);
                initializeAndExecuteGenerator(elementGenerator, parentElement);
            }
        }
    }

    @Override
    protected void addInsertSelectiveElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateInsertSelective()) {
            if (introspectedTable.getPrimaryKeyColumns().size() > 1) {
                AbstractXmlElementGenerator elementGenerator = new ExtendedInsertSelectiveElementGenerator();
                initializeAndExecuteGenerator(elementGenerator, parentElement);
            }
        }
    }

    @Override
    protected void addResultMapWithoutBLOBsElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateBaseResultMap()) {
            AbstractXmlElementGenerator elementGenerator = new ExtendedResultMapWithoutBLOBsElementGenerator(false);
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }


}

package generator;

import mybatis3.*;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.XMLMapperGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.*;
import util.IntrospectedTableUtil;

/**
 * Xml文件生成器扩展，被ExtendedJavaMapperGenerator创建
 * */
public class ExtendedXMLMapperGenerator extends XMLMapperGenerator {


    @Override
    protected XmlElement getSqlMapElement() {
        if(simpleRecordClassName()) {
            introspectedTable.setPrimaryKeyType(simplyClassName(introspectedTable.getPrimaryKeyType()));
            introspectedTable.setBaseRecordType(simplyClassName(introspectedTable.getBaseRecordType()));
            introspectedTable.setExampleType(simplyClassName(introspectedTable.getExampleType()));
        }
        return super.getSqlMapElement();
    }

    @Override
    protected void addInsertElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateInsert()) {
            AbstractXmlElementGenerator elementGenerator = (IntrospectedTableUtil.isUnionKeyTable(introspectedTable) ? new ExtendedInsertElementGenerator(false) : new InsertElementGenerator(false));
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    @Override
    protected void addInsertSelectiveElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateInsertSelective()) {
            AbstractXmlElementGenerator elementGenerator = (IntrospectedTableUtil.isUnionKeyTable(introspectedTable) ? new ExtendedInsertSelectiveElementGenerator() : new InsertSelectiveElementGenerator());
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    @Override
    protected void addResultMapWithoutBLOBsElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateBaseResultMap()) {
            AbstractXmlElementGenerator elementGenerator = (IntrospectedTableUtil.isUnionKeyTable(introspectedTable) ? new ExtendedResultMapWithoutBLOBsElementGenerator(false) : new ResultMapWithoutBLOBsElementGenerator(false));
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    @Override
    protected void addUpdateByPrimaryKeyWithoutBLOBsElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateUpdateByPrimaryKeyWithoutBLOBs()) {
            AbstractXmlElementGenerator elementGenerator = (IntrospectedTableUtil.isUnionKeyTable(introspectedTable) ? new ExtendedUpdateByPrimaryKeyWithoutBLOBsElementGenerator(false) : new UpdateByPrimaryKeyWithoutBLOBsElementGenerator(false));
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    @Override
    protected void addUpdateByPrimaryKeySelectiveElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateUpdateByPrimaryKeySelective()) {
            AbstractXmlElementGenerator elementGenerator = (IntrospectedTableUtil.isUnionKeyTable(introspectedTable) ? new ExtendedUpdateByPrimaryKeySelectiveElementGenerator() : new UpdateByPrimaryKeySelectiveElementGenerator());
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    private boolean simpleRecordClassName() {
        String simpleRecordClassNameStr = (String)introspectedTable.getContext().getSqlMapGeneratorConfiguration().getProperties().get("simpleRecordClassName");
        if(simpleRecordClassNameStr == null || simpleRecordClassNameStr.trim().length() == 0) {
            return false;
        }
        try {
            return Boolean.valueOf(simpleRecordClassNameStr);
        } catch (Exception e) {
            return false;
        }
    }

    private String simplyClassName(String fullClassName) {
        int simplyClassNameIndex = fullClassName.lastIndexOf(".") + 1;
        if(simplyClassNameIndex == -1) {
            return fullClassName;
        }
        return fullClassName.substring(simplyClassNameIndex);
    }
}

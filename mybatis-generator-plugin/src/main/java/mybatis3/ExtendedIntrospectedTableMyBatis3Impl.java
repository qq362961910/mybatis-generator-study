package mybatis3;

import constants.PackageConstants;
import constants.XmlMapConstants;
import enums.ExtendedIntrospectedTableInternalAttribute;
import generator.ExtendedBaseRecordGenerator;
import generator.ExtendedExampleGenerator;
import generator.ExtendedPrimaryKeyGenerator;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3Impl;
import org.mybatis.generator.codegen.mybatis3.model.BaseRecordGenerator;
import org.mybatis.generator.codegen.mybatis3.model.ExampleGenerator;
import org.mybatis.generator.codegen.mybatis3.model.PrimaryKeyGenerator;
import util.IntrospectedTableUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtendedIntrospectedTableMyBatis3Impl extends IntrospectedTableMyBatis3Impl {

    private final Map<ExtendedIntrospectedTableInternalAttribute, Object> extendedInternalAttributes = new HashMap<>();

    @Override
    protected void calculateJavaModelGenerators(List<String> warnings, ProgressCallback progressCallback) {
        super.calculateJavaModelGenerators(warnings, progressCallback);
        if (getRules().generateBaseRecordClass()) {
            //替换默认添加的BaseRecordGenerator
            javaModelGenerators.removeIf(javaGenerator -> javaGenerator instanceof BaseRecordGenerator);
            //add baseRecordGenerator
            AbstractJavaGenerator baseRecordGenerator = new ExtendedBaseRecordGenerator();
            initializeAbstractGenerator(baseRecordGenerator, warnings, progressCallback);
            javaModelGenerators.add(baseRecordGenerator);
        }
        if (getRules().generateExampleClass()) {
            javaModelGenerators.removeIf(javaGenerator -> javaGenerator instanceof ExampleGenerator);
            //替换默认添加的ExampleGenerator
            AbstractJavaGenerator extendedExampleGenerator = new ExtendedExampleGenerator();
            initializeAbstractGenerator(extendedExampleGenerator, warnings, progressCallback);
            javaModelGenerators.add(extendedExampleGenerator);
        }
        if (getRules().generatePrimaryKeyClass()) {
            if(IntrospectedTableUtil.isUnionKeyTable(this)) {
                javaModelGenerators.removeIf(javaGenerator -> javaGenerator instanceof PrimaryKeyGenerator);
                //替换默认添加的PrimaryKeyGenerator
                AbstractJavaGenerator javaGenerator = new ExtendedPrimaryKeyGenerator();
                initializeAbstractGenerator(javaGenerator, warnings,
                    progressCallback);
                javaModelGenerators.add(javaGenerator);
            }
        }
        if (getRules().generateRecordWithBLOBsClass()) {
            //do nothing now
        }
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    protected void calculateModelAttributes() {
        String pakkage = calculateJavaModelPackage();

        StringBuilder sb = new StringBuilder();
        sb.append(pakkage);
        sb.append('.');
        if(IntrospectedTableUtil.unionKeyClassSeparate(context.getJavaModelGeneratorConfiguration())) {
            sb.append(PackageConstants.UNION_KEY_CLASS_PACKAGE);
            sb.append('.');
        }
        sb.append(fullyQualifiedTable.getDomainObjectName());
        sb.append("Key");
        setPrimaryKeyType(sb.toString());

        sb.setLength(0);
        sb.append(pakkage);
        sb.append('.');
        sb.append(fullyQualifiedTable.getDomainObjectName());
        setBaseRecordType(sb.toString());

        sb.setLength(0);
        sb.append(pakkage);
        sb.append('.');
        sb.append(fullyQualifiedTable.getDomainObjectName());
        sb.append("WithBLOBs");
        setRecordWithBLOBsType(sb.toString());

        sb.setLength(0);
        sb.append(pakkage);
        sb.append('.');
        if(IntrospectedTableUtil.exampleClassSeparate(context.getJavaModelGeneratorConfiguration())) {
            sb.append(PackageConstants.EXAMPLE_CLASS_PACKAGE);
            sb.append('.');
        }
        sb.append(fullyQualifiedTable.getDomainObjectName());
        sb.append("Example");
        setExampleType(sb.toString());

    }

    @Override
    protected void calculateXmlAttributes() {
        super.calculateXmlAttributes();
        setUnionPrimaryKeyMapId(XmlMapConstants.EXT_UNION_PRIMARY_KEY_MAP);
    }

    public void setUnionPrimaryKeyMapId(String s) {
        extendedInternalAttributes.put(ExtendedIntrospectedTableInternalAttribute.ATTR_UNION_KEY_MAP_ID, s);
    }

    public String getUnionPrimaryKeyMapId() {
        return (String)extendedInternalAttributes.get(ExtendedIntrospectedTableInternalAttribute.ATTR_UNION_KEY_MAP_ID);
    }
}

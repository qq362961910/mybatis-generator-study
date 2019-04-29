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

    private Map<ExtendedIntrospectedTableInternalAttribute, String> extendedInternalAttributes = new HashMap<>();

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

    /**
     * 该代码针对联合主键情况用来修改内IntrospectedColumn#javaType属性名称
     * 如果此处修改会产生一连串的连锁反映不能打开
     * */
    @Override
    public void initialize() {
//        if(IntrospectedTableUtil.isUnionKeyTable(this)) {
//            for(IntrospectedColumn column: primaryKeyColumns) {
//                column.setJavaProperty(FieldConstants.UNION_KEY_PROPERTY_NAME.concat(".").concat(column.getJavaProperty()));
//            }
//        }
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
        setUnionKeyMapId(XmlMapConstants.EXT_UNION_KEY_MAP);
    }

    public void setUnionKeyMapId(String s) {
        extendedInternalAttributes.put(ExtendedIntrospectedTableInternalAttribute.ATTR_UNION_KEY_MAP_ID, s);
    }

    public String getUnionKeyMapId() {
        return extendedInternalAttributes.get(ExtendedIntrospectedTableInternalAttribute.ATTR_UNION_KEY_MAP_ID);
    }

}

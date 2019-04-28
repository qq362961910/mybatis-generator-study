package mybatis3;

import constants.PackageConstants;
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

    protected Map<ExtendedInternalAttribute, String> extendedInternalAttributes = new HashMap<>();

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
//        if(primaryKeyColumns != null && primaryKeyColumns.size() > 1) {
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
        if(unionKeyClassSeparate()) {
            sb.append(PackageConstants.UNION_KEY_CLASS_PACKAGE);
            sb.append('.');
        }
        sb.append(fullyQualifiedTable.getDomainObjectName());
        sb.append("Key"); //$NON-NLS-1$
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
        sb.append("WithBLOBs"); //$NON-NLS-1$
        setRecordWithBLOBsType(sb.toString());

        sb.setLength(0);
        sb.append(pakkage);
        sb.append('.');
        if(exampleClassSeparate()) {
            sb.append(PackageConstants.EXAMPLE_CLASS_PACKAGE);
            sb.append('.');
        }
        sb.append(fullyQualifiedTable.getDomainObjectName());
        sb.append("Example"); //$NON-NLS-1$
        setExampleType(sb.toString());

    }

    @Override
    protected void calculateXmlAttributes() {
        super.calculateXmlAttributes();
        setUnionKeyMapMapId("UnionKeyMap");
    }

    private boolean exampleClassSeparate() {
        String str = context.getJavaModelGeneratorConfiguration().getProperty("exampleClassSeparate");
        if(str == null || str.trim().length() == 0) {
            return false;
        }
        try {
            return Boolean.valueOf(str);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean unionKeyClassSeparate() {
        String str = context.getJavaModelGeneratorConfiguration().getProperty("unionKeyClassSeparate");
        if(str == null || str.trim().length() == 0) {
            return false;
        }
        try {
            return Boolean.valueOf(str);
        } catch (Exception e) {
            return false;
        }
    }

    public void setUnionKeyMapMapId(String s) {
        extendedInternalAttributes.put(ExtendedInternalAttribute.ATTR_UNION_KEY_MAP_ID, s);
    }

    public String getUnionKeyMapMapId() {
        return extendedInternalAttributes.get(ExtendedInternalAttribute.ATTR_UNION_KEY_MAP_ID);
    }

    protected enum ExtendedInternalAttribute {
        ATTR_UNION_KEY_MAP_ID
    }

}

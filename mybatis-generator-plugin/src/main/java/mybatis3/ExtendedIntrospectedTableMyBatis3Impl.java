package mybatis3;

import constants.ClassConstants;
import constants.PackageConstants;
import constants.XmlMapConstants;
import enums.ExtendedIntrospectedTableInternalAttribute;
import generator.ExtendedBaseRecordGenerator;
import generator.ExtendedExampleGenerator;
import generator.ExtendedPrimaryKeyGenerator;
import generator.UnionKeyJavaGenerator;
import generator.table.KeyDescriptor;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3Impl;
import org.mybatis.generator.codegen.mybatis3.model.BaseRecordGenerator;
import org.mybatis.generator.codegen.mybatis3.model.ExampleGenerator;
import org.mybatis.generator.codegen.mybatis3.model.PrimaryKeyGenerator;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.JDBCConnectionFactory;
import util.IntrospectedTableUtil;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
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
        //组合键类生成器
        UnionKeyJavaGenerator unionKeyJavaGenerator = new UnionKeyJavaGenerator();
        initializeAbstractGenerator(unionKeyJavaGenerator, warnings, progressCallback);
        javaModelGenerators.add(unionKeyJavaGenerator);
        if (getRules().generateRecordWithBLOBsClass()) {
            //do nothing now
        }
    }

    @Override
    public void initialize() {
        Context context = getContext();
        JDBCConnectionFactory connectionFactory = new JDBCConnectionFactory(context.getJdbcConnectionConfiguration());
        try (Connection connection = connectionFactory.getConnection()){
            Map<String, List<KeyDescriptor>> keyDescriptorMap = new HashMap<>();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            try (ResultSet rs = databaseMetaData.getIndexInfo(
                fullyQualifiedTable.getIntrospectedCatalog(),
                fullyQualifiedTable.getIntrospectedSchema(),
                fullyQualifiedTable.getIntrospectedTableName(),
                false,
                true)) {
                while (rs.next()) {
                    String indexName = rs.getString("INDEX_NAME");
                    int indexSeq = rs.getInt("ORDINAL_POSITION");
                    String columnName = rs.getString("COLUMN_NAME");
                    KeyDescriptor keyDescriptor = new KeyDescriptor();
                    keyDescriptor.setIndexName(indexName);
                    keyDescriptor.setIndexSeq(indexSeq);
                    keyDescriptor.setColumnName(columnName);
                    List<KeyDescriptor> keyDescriptorList = keyDescriptorMap.get(indexName);
                    if(keyDescriptorList == null) {
                        keyDescriptorList = new ArrayList<>(1);
                        keyDescriptorList.add(keyDescriptor);
                        keyDescriptorMap.put(indexName, keyDescriptorList);
                    } else {
                        keyDescriptorList.add(keyDescriptor);
                    }
                }
            }
            setKeyDescriptorMap(keyDescriptorMap);
        } catch (Exception e) {
            System.out.println("ExtendedIntrospectedTableMyBatis3Impl initialize failed");
        }
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
        sb.append(ClassConstants.UNION_KEY_CLASS_SUFFIX);
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

    public void setKeyDescriptorMap(Map<String, List<KeyDescriptor>> keyDescriptorMap) {
        extendedInternalAttributes.put(ExtendedIntrospectedTableInternalAttribute.KEY_DESCRIPTOR_MAP, keyDescriptorMap);
    }
    @SuppressWarnings("unchecked")
    public Map<String, List<KeyDescriptor>> getKeyDescriptorMap() {
        return (Map<String, List<KeyDescriptor>>)extendedInternalAttributes.get(ExtendedIntrospectedTableInternalAttribute.KEY_DESCRIPTOR_MAP);
    }

    @SuppressWarnings("unchecked")
    public void setUnionKeyJavaTypeName(String unionIndexName, String javaTypeName) {
        Map<String, String> unionKeyJavaTypeNameMap = (Map<String, String>)extendedInternalAttributes.get(ExtendedIntrospectedTableInternalAttribute.UNION_KEY_JAVA_TYPE_NAME_MAP);
        if(unionKeyJavaTypeNameMap == null) {
            unionKeyJavaTypeNameMap = new HashMap<>();
            extendedInternalAttributes.put(ExtendedIntrospectedTableInternalAttribute.UNION_KEY_JAVA_TYPE_NAME_MAP, unionKeyJavaTypeNameMap);
        }
        unionKeyJavaTypeNameMap.put(unionIndexName, javaTypeName);
    }

    @SuppressWarnings("unchecked")
    public String getUnionKeyJavaTypeName(String unionIndexName) {
        Map<String, String> unionKeyJavaTypeNameMap = (Map<String, String>)extendedInternalAttributes.get(ExtendedIntrospectedTableInternalAttribute.UNION_KEY_JAVA_TYPE_NAME_MAP);
        if(unionKeyJavaTypeNameMap == null) {
            return null;
        }
        return unionKeyJavaTypeNameMap.get(unionIndexName);
    }
}

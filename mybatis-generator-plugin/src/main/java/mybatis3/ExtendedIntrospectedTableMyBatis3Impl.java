package mybatis3;

import generator.ExtendedBaseRecordGenerator;
import generator.ExtendedExampleGenerator;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3Impl;
import org.mybatis.generator.codegen.mybatis3.model.BaseRecordGenerator;
import org.mybatis.generator.codegen.mybatis3.model.ExampleGenerator;

import java.util.List;

public class ExtendedIntrospectedTableMyBatis3Impl extends IntrospectedTableMyBatis3Impl {

    @Override
    protected void calculateJavaModelGenerators(List<String> warnings, ProgressCallback progressCallback) {
        super.calculateJavaModelGenerators(warnings, progressCallback);
        if (getRules().generateBaseRecordClass()) {
            //替换默认添加的BaseRecordGenerator
            javaModelGenerators.removeIf(javaGenerator -> javaGenerator instanceof BaseRecordGenerator || javaGenerator instanceof ExampleGenerator);

            //add baseRecordGenerator
            AbstractJavaGenerator baseRecordGenerator = new ExtendedBaseRecordGenerator();
            initializeAbstractGenerator(baseRecordGenerator, warnings, progressCallback);
            javaModelGenerators.add(baseRecordGenerator);

            //add extendedExampleGenerator
            AbstractJavaGenerator extendedExampleGenerator = new ExtendedExampleGenerator();
            initializeAbstractGenerator(extendedExampleGenerator, warnings, progressCallback);
            javaModelGenerators.add(extendedExampleGenerator);
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
}

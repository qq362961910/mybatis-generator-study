package generator;

import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.JavaMapperGenerator;

/**
 * Mapper接口生成器扩展，配置在javaClientGenerator标签
 * */
public class ExtendedJavaMapperGenerator extends JavaMapperGenerator {

    @Override
    public AbstractXmlGenerator getMatchedXMLGenerator() {
        return new ExtendedXMLMapperGenerator();
    }
}

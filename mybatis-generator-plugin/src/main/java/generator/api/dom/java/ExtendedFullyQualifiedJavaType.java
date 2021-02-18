package generator.api.dom.java;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

/**
 * @author <a href="mailto:yangjian@ifenxi.com">研发部-杨建</a>
 * @version V1.0
 * @since 2021-02-18 11:14
 **/
public class ExtendedFullyQualifiedJavaType extends FullyQualifiedJavaType {

    public ExtendedFullyQualifiedJavaType(String fullTypeSpecification) {
        super(fullTypeSpecification);
    }

    public String getFullyQualifiedName() {
        //去除泛型
        String fullyQualifiedName = super.getFullyQualifiedName();
        return fullyQualifiedName.replaceAll("<.+?>", "");
    }
}

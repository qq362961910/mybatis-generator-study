package constants;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

public final class FieldConstants {

    public static final FullyQualifiedJavaType SERIALIZABLE = new FullyQualifiedJavaType("java.io.Serializable");

    public static final String UNION_KEY_PROPERTY_NAME = "unionKey";

    private FieldConstants(){}
}

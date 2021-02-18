package generator.codegen;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.ObjectFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * 106行移除了因泛型带来的异常情况
 * @author <a href="mailto:yangjian@ifenxi.com">研发部-杨建</a>
 * @version V1.0
 * @since 2021-02-08 17:09
 **/
public class ExtendedRootClassInfo {
    private static Map<String, ExtendedRootClassInfo> rootClassInfoMap;

    static {
        rootClassInfoMap = Collections
            .synchronizedMap(new HashMap<>());
    }

    public static ExtendedRootClassInfo getInstance(String className,
                                            List<String> warnings) {
        ExtendedRootClassInfo classInfo = rootClassInfoMap.get(className);
        if (classInfo == null) {
            classInfo = new ExtendedRootClassInfo(className, warnings);
            rootClassInfoMap.put(className, classInfo);
        }

        return classInfo;
    }

    /**
     * Clears the internal map containing root class info.  This method should be called at the beginning of
     * a generation run to clear the cached root class info in case there has been a change.
     * For example, when using the eclipse launcher, the cache would be kept until eclipse
     * was restarted.
     *
     */
    public static void reset() {
        rootClassInfoMap.clear();
    }

    private PropertyDescriptor[] propertyDescriptors;
    private String className;
    private List<String> warnings;
    private boolean genericMode = false;

    private ExtendedRootClassInfo(String className, List<String> warnings) {
        super();
        this.className = className;
        this.warnings = warnings;

        if (className == null) {
            return;
        }

        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(className);
        String nameWithoutGenerics = fqjt.getFullyQualifiedNameWithoutTypeParameters();
        if (!nameWithoutGenerics.equals(className)) {
            genericMode = true;
        }

        try {
            Class<?> clazz = ObjectFactory.externalClassForName(nameWithoutGenerics);
            BeanInfo bi = Introspector.getBeanInfo(clazz);
            propertyDescriptors = bi.getPropertyDescriptors();
        } catch (Exception e) {
            propertyDescriptors = null;
            warnings.add(getString("Warning.20", className)); //$NON-NLS-1$
        }
    }

    public boolean containsProperty(IntrospectedColumn introspectedColumn) {
        if (propertyDescriptors == null) {
            return false;
        }

        boolean found = false;
        String propertyName = introspectedColumn.getJavaProperty();
        String propertyType = introspectedColumn.getFullyQualifiedJavaType()
            .getFullyQualifiedName();

        // get method names from class and check against this column definition.
        // better yet, have a map of method Names. check against it.
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor propertyDescriptor = propertyDescriptors[i];

            if (propertyDescriptor.getName().equals(propertyName)) {
                // property name is in the rootClass...

                // Is it the proper type?
                String introspectedPropertyType = propertyDescriptor.getPropertyType().getName();
                if (genericMode && introspectedPropertyType.equals("java.lang.Object")) { //$NON-NLS-1$
                    // OK - but add a warning
                    warnings.add(getString("Warning.28", //$NON-NLS-1$
                        propertyName, className));
                }/* else if (!introspectedPropertyType.equals(propertyType)) {
                    warnings.add(getString("Warning.21", //$NON-NLS-1$
                        propertyName, className, propertyType));
                    break;
                }*/
                try {
                    if (!propertyDescriptor.getPropertyType().isAssignableFrom(Class.forName(introspectedColumn.getFullyQualifiedJavaType().getFullyQualifiedName()))) {
                        warnings.add(getString("Warning.21", //$NON-NLS-1$
                            propertyName, className, propertyType));
                        break;
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                // Does it have a getter?
                if (propertyDescriptor.getReadMethod() == null) {
                    warnings.add(getString("Warning.22", //$NON-NLS-1$
                        propertyName, className));
                    break;
                }

                // Does it have a setter?
                if (propertyDescriptor.getWriteMethod() == null) {
                    warnings.add(getString("Warning.23", //$NON-NLS-1$
                        propertyName, className));
                    break;
                }

                found = true;
                break;
            }
        }

        return found;
    }
}

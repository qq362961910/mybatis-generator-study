package util;

import cn.t.util.common.CollectionUtil;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;

import java.util.List;

public class IntrospectedTableUtil {

    public static boolean isUnionKeyTable(IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> introspectedColumns = introspectedTable.getPrimaryKeyColumns();
        return !CollectionUtil.isEmpty(introspectedColumns) && introspectedColumns.size() > 1;
    }

}

package generator.table;

/**
 * @author <a href="mailto:yangjian@ifenxi.com">研发部-杨建</a>
 * @version V1.0
 * @since 2021-02-19 20:41
 **/
public class KeyDescriptor {
    private String indexName;
    private String columnName;
    private int indexSeq;

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public int getIndexSeq() {
        return indexSeq;
    }

    public void setIndexSeq(int indexSeq) {
        this.indexSeq = indexSeq;
    }

    @Override
    public String toString() {
        return "KeyDescriptor{" +
            "indexName='" + indexName + '\'' +
            ", columnName='" + columnName + '\'' +
            ", indexSeq=" + indexSeq +
            '}';
    }
}

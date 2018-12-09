package by.bsuir.pandadb.core.model;

public class DBValue {

    private DBField field;

    private Object value;

    public DBField getField() {
        return field;
    }

    public void setField(DBField field) {
        this.field = field;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}

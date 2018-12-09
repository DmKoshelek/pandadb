package by.bsuir.pandadb.core.model;

public class DBRecord {

    private DBTable dbTable;

    private DBValue[] values;

    public DBTable getDbTable() {
        return dbTable;
    }

    public void setDbTable(DBTable dbTable) {
        this.dbTable = dbTable;
    }

    public DBValue[] getValues() {
        return values;
    }

    public void setValues(DBValue[] values) {
        this.values = values;
    }
}

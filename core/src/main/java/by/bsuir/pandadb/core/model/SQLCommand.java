package by.bsuir.pandadb.core.model;

import by.bsuir.pandadb.core.sql.enums.SQLCommandType;

import java.util.List;
import java.util.Map;

public class SQLCommand {

    private SQLCommandType type;

    private String table;

    private String condition;

    private Map<String, String> values;

    private List<DBField> fields;

    private List<String> selectFields;

    private List<String> updateFields;

    public SQLCommandType getType() {
        return type;
    }

    public void setType(SQLCommandType type) {
        this.type = type;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }

    public List<DBField> getFields() {
        return fields;
    }

    public void setFields(List<DBField> fields) {
        this.fields = fields;
    }

    public List<String> getSelectFields() {
        return selectFields;
    }

    public void setSelectFields(List<String> selectFields) {
        this.selectFields = selectFields;
    }

    public List<String> getUpdateFields() {
        return updateFields;
    }

    public void setUpdateFields(List<String> updateFields) {
        this.updateFields = updateFields;
    }
}

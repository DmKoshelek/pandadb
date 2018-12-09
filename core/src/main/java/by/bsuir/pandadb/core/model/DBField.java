package by.bsuir.pandadb.core.model;

import by.bsuir.pandadb.core.types.model.DBType;

import java.io.Serializable;
import java.util.Objects;

public class DBField implements Serializable {

    private DBType type;

    private String name;

    public DBType getType() {
        return type;
    }

    public void setType(DBType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DBField dbField = (DBField) o;
        return Objects.equals(name, dbField.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

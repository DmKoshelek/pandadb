package by.bsuir.pandadb.core.types.model;

import java.io.Serializable;
import java.util.Objects;

public abstract class DBType implements Serializable {

    private String name;
    private int size;

    protected DBType(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DBType dbType = (DBType) o;
        return size == dbType.size &&
                Objects.equals(name, dbType.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, size);
    }
}

package by.bsuir.pandadb.core.model;

import java.io.Serializable;

public class DBTable implements Serializable {

    private String name;

    private DBField[] fields;

    private int size;

    private int[] offsets;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DBField[] getFields() {
        return fields;
    }

    public void setFields(DBField[] fields) {
        this.fields = fields;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int[] getOffsets() {
        return offsets;
    }

    public void setOffsets(int[] offsets) {
        this.offsets = offsets;
    }
}

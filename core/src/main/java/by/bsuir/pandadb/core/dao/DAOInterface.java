package by.bsuir.pandadb.core.dao;

import by.bsuir.pandadb.core.table.Table;

public interface DAOInterface {
    byte[] getByOffset(String tableName, int offset, int length);

    void removeFormEnd(String tableName, int length);

    void replaceByOffset(String tableName, int offset, byte[] data);

    void addToEnd(String tableName, byte[] data);

    long getSize(String tableName);

    void addTable(String tableName, Table table);

    void removeTable(String tableName);
}

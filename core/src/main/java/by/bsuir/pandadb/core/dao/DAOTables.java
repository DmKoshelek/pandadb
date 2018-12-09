package by.bsuir.pandadb.core.dao;

import by.bsuir.pandadb.core.model.DBTable;

import java.util.List;

public interface DAOTables {
    void addTable(DBTable dbTable);

    void removeTable(String tableName);

    DBTable getTable(String tableName);

    DBTable loadTable(String tableName);

    List<DBTable> getAllTables();

    void addAllTables(List<DBTable> tables);
}

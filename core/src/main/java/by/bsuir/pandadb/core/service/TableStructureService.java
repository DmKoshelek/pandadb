package by.bsuir.pandadb.core.service;

import by.bsuir.pandadb.core.model.DBField;
import by.bsuir.pandadb.core.model.DBTable;

public interface TableStructureService {

    void addNewField(DBTable table, DBField field);

    void removeField(DBTable table, DBField field);

    void createTable(DBTable table);

    void removeTable(String tableName);
}

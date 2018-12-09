package by.bsuir.pandadb.core.dao.impl;

import by.bsuir.pandadb.core.dao.DAOTables;
import by.bsuir.pandadb.core.exception.TableNotFoundException;
import by.bsuir.pandadb.core.model.DBTable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DAOTablesImpl implements DAOTables {

    private Map<String, DBTable> tables = new HashMap<>();

    @Override
    public void addTable(DBTable dbTable) {
        tables.put(dbTable.getName(), dbTable);
    }

    @Override
    public void removeTable(String tableName) {
        DBTable result = tables.remove(tableName);
        if (result == null) {
            throw new TableNotFoundException();
        }
    }

    @Override
    public DBTable getTable(String tableName) {
        return tables.get(tableName);
    }

    @Override
    public DBTable loadTable(String tableName) {
        DBTable result = getTable(tableName);
        if (result == null) {
            throw new TableNotFoundException();
        }
        return result;
    }

    @Override
    public List<DBTable> getAllTables() {
        return new ArrayList<>(tables.values());
    }

    @Override
    public void addAllTables(List<DBTable> tables) {
        tables.forEach(this::addTable);
    }
}

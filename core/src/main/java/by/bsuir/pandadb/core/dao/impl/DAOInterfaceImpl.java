package by.bsuir.pandadb.core.dao.impl;

import by.bsuir.pandadb.core.dao.DAOInterface;
import by.bsuir.pandadb.core.exception.TableNotFoundException;
import by.bsuir.pandadb.core.table.Table;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class DAOInterfaceImpl implements DAOInterface {

    private Map<String, Table> tables = new HashMap<>();

    @Override
    public byte[] getByOffset(String tableName, int offset, int length) {
        return getTable(tableName).getByOffset(offset, length);
    }

    @Override
    public void removeFormEnd(String tableName, int length) {
        getTable(tableName).removeFormEnd(length);
    }

    @Override
    public void replaceByOffset(String tableName, int offset, byte[] data) {
        getTable(tableName).replaceByOffset(offset, data);
    }

    @Override
    public void addToEnd(String tableName, byte[] data) {
        getTable(tableName).addToEnd(data);
    }

    @Override
    public long getSize(String tableName) {
        return getTable(tableName).getSize();
    }

    @Override
    public void addTable(String tableName, Table table) {
        tables.put(tableName, table);
    }

    @Override
    public void removeTable(String tableName) {
        Table result = tables.remove(tableName);
        if (result == null) {
            throw new TableNotFoundException();
        }
    }

    private Table getTable(String tableName) {
        Table result = tables.get(tableName);
        if (result == null) {
            throw new TableNotFoundException();
        }
        return result;
    }
}

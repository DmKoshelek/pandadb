package by.bsuir.pandadb.core.service.impl;

import by.bsuir.pandadb.core.dao.DAOInterface;
import by.bsuir.pandadb.core.dao.DAOTables;
import by.bsuir.pandadb.core.exception.ElementNotFoundException;
import by.bsuir.pandadb.core.model.DBField;
import by.bsuir.pandadb.core.model.DBTable;
import by.bsuir.pandadb.core.service.TableStructureService;
import by.bsuir.pandadb.core.table.impl.ArrayTableImpl;
import by.bsuir.pandadb.core.types.model.DBType;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;

@Service
public class TableStructureServiceImpl implements TableStructureService {

    @Resource
    private DAOInterface daoInterface;

    @Resource
    private DAOTables daoTables;

    @Override
    public void addNewField(DBTable table, DBField field) {
        DBField[] fields = Arrays.copyOf(table.getFields(), table.getFields().length + 1);
        fields[fields.length - 1] = field;
        table.setFields(fields);
        recalculateTable(table);
    }

    @Override
    public void removeField(DBTable table, DBField field) {
        int index = ArrayUtils.indexOf(table.getFields(), field);
        if (index == ArrayUtils.INDEX_NOT_FOUND) {
            throw new ElementNotFoundException(field.getName());
        }

        DBField[] fields = Arrays.copyOf(table.getFields(), table.getFields().length - 1);
        System.arraycopy(table.getFields(), index + 1, fields, index, fields.length - index);
        table.setFields(fields);
        recalculateTable(table);
    }

    private void recalculateTable(DBTable table) {
        recalculateSize(table);
        recalculateOffsets(table);
    }

    private void recalculateSize(DBTable table) {
        int result = Arrays.stream(table.getFields()).map(DBField::getType).mapToInt(DBType::getSize).sum();
        table.setSize(result);
    }

    private void recalculateOffsets(DBTable table) {
        int[] offsets = new int[table.getFields().length];
        int sum = 0;
        for (int i = 0; i < offsets.length; i++) {
            offsets[i] = sum;
            sum += table.getFields()[i].getType().getSize();
        }
        table.setOffsets(offsets);
    }

    @Override
    public void createTable(DBTable table) {
        recalculateTable(table);
        daoTables.addTable(table);
        daoInterface.addTable(table.getName(), new ArrayTableImpl());
    }

    @Override
    public void removeTable(String tableName) {
        daoInterface.removeTable(tableName);
        daoTables.removeTable(tableName);
    }
}

package by.bsuir.pandadb.core.facade.impl;

import by.bsuir.pandadb.core.dao.DAOTables;
import by.bsuir.pandadb.core.exception.FieldNotFoundException;
import by.bsuir.pandadb.core.facade.SQLCommandInterpreter;
import by.bsuir.pandadb.core.factory.FactoryTypeSerializers;
import by.bsuir.pandadb.core.model.*;
import by.bsuir.pandadb.core.service.TableAccessService;
import by.bsuir.pandadb.core.service.TableStructureService;
import by.bsuir.pandadb.core.sql.enums.SQLCommandType;
import by.bsuir.pandadb.core.types.model.DBType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SQLCommandInterpreterImpl implements SQLCommandInterpreter {

    private static final String ASTERISK = "*";

    @Resource
    private TableStructureService tableStructureService;

    @Resource
    private TableAccessService tableAccessService;

    @Resource
    private DAOTables daoTables;

    @Resource
    private FactoryTypeSerializers factoryTypeSerializers;

    private final Map<SQLCommandType, Function<SQLCommand, SQLResult>> mappedFunctions = new HashMap<>();

    @PostConstruct
    public void init() {
        mappedFunctions.put(SQLCommandType.CREATE_TABLE, this::createTable);
        mappedFunctions.put(SQLCommandType.DROP_TABLE, this::dropTable);
        mappedFunctions.put(SQLCommandType.SELECT, this::selectFromTable);
        mappedFunctions.put(SQLCommandType.INSERT, this::insertToTable);
        mappedFunctions.put(SQLCommandType.REMOVE, this::deleteFromTable);
        mappedFunctions.put(SQLCommandType.UPDATE, this::updateInTable);
    }

    @Override
    public SQLResult executeCommand(SQLCommand sqlCommand) {
        return mappedFunctions.get(sqlCommand.getType()).apply(sqlCommand);
    }

    private SQLResult createTable(SQLCommand sqlCommand) {
        String newTableName = sqlCommand.getTable();

        if (daoTables.getTable(newTableName) != null) {
            return new SQLResult(SQLResultType.ERROR, "table " + newTableName + " already exist");
        }

        DBTable dbTable = new DBTable();
        dbTable.setFields(sqlCommand.getFields().toArray(new DBField[sqlCommand.getFields().size()]));
        dbTable.setName(newTableName);

        tableStructureService.createTable(dbTable);
        return new SQLResult(SQLResultType.SUCCESS, "table " + newTableName + " has been created");
    }

    private SQLResult dropTable(SQLCommand sqlCommand) {
        String droppedTable = sqlCommand.getTable();

        tableStructureService.removeTable(droppedTable);
        return new SQLResult(SQLResultType.SUCCESS, "table " + droppedTable + " has been removed");
    }

    private SQLResult insertToTable(SQLCommand sqlCommand) {
        String tableName = sqlCommand.getTable();

        DBTable dbTable = daoTables.loadTable(tableName);
        Map<String, DBField> mappedFields = getMappedFields(dbTable);
        DBValue[] dbValues = sqlCommand.getValues().entrySet().stream().map(entry -> {
            DBValue value = new DBValue();
            DBField field = mappedFields.get(entry.getKey());
            if (field == null) {
                throw new FieldNotFoundException(entry.getKey());
            }
            value.setField(field);
            value.setValue(deserializeFromString(entry.getValue(), field.getType()));
            return value;
        }).toArray(DBValue[]::new);

        DBRecord record = new DBRecord();
        record.setDbTable(dbTable);
        record.setValues(dbValues);

        tableAccessService.insertRecords(dbTable, Collections.singletonList(record));
        return new SQLResult(SQLResultType.SUCCESS, "value was inserted to " + tableName);
    }

    private SQLResult selectFromTable(SQLCommand sqlCommand) {
        String tableName = sqlCommand.getTable();

        DBTable dbTable = daoTables.loadTable(tableName);
        Map<String, DBField> mappedFields = getMappedFields(dbTable);
        List<DBRecord> records = tableAccessService.selectRecords(dbTable, sqlCommand.getCondition());

        List<String> fields = sqlCommand.getSelectFields().stream().flatMap(s -> {
            if (s.equals(ASTERISK)) {
                return mappedFields.keySet().stream();
            }
            return Stream.of(s);
        }).collect(Collectors.toList());
        String result = records.stream()
                .map(record -> jsonByRecords(fields, record))
                .collect(Collectors.joining(",", "[", "]"));

        return new SQLResult(SQLResultType.SUCCESS, result);
    }

    private SQLResult deleteFromTable(SQLCommand sqlCommand) {
        String tableName = sqlCommand.getTable();

        DBTable dbTable = daoTables.loadTable(tableName);

        tableAccessService.deleteRecords(dbTable, sqlCommand.getCondition());

        return new SQLResult(SQLResultType.SUCCESS, "rows deleted");
    }

    private SQLResult updateInTable(SQLCommand sqlCommand) {
        String tableName = sqlCommand.getTable();

        DBTable dbTable = daoTables.loadTable(tableName);

        tableAccessService.updateRecords(dbTable, sqlCommand.getCondition(), sqlCommand.getUpdateFields());

        return new SQLResult(SQLResultType.SUCCESS, "rows updated");
    }

    private String jsonByRecords(List<String> fields, DBRecord record) {
        Map<String, DBValue> mappedValues = getMappedValues(record);
        Map<String, String> serializeValue = fields.stream()
                .map(value -> {
                    if (!mappedValues.containsKey(value)) {
                        throw new FieldNotFoundException(value);
                    }
                    return mappedValues.get(value);
                })
                .collect(Collectors.toMap(o -> o.getField().getName(), this::serializeString));

        Gson gson = new GsonBuilder().create();
        return gson.toJson(serializeValue);
    }

    private Object deserializeFromString(String value, DBType dbType) {
        return factoryTypeSerializers.getSerializer(dbType).deserialize(value);
    }

    private String serializeString(DBValue value) {
        return factoryTypeSerializers.getSerializer(value.getField().getType()).serializeString(value.getValue());
    }

    private Map<String, DBField> getMappedFields(DBTable dbTable) {
        return Arrays.stream(dbTable.getFields()).collect(Collectors.toMap(DBField::getName, Function.identity()));
    }

    private Map<String, DBValue> getMappedValues(DBRecord record) {
        return Arrays.stream(record.getValues()).collect(Collectors.toMap(o -> o.getField().getName(), Function.identity()));
    }
}

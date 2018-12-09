package by.bsuir.pandadb.core.service.impl;

import by.bsuir.pandadb.core.dao.DAOInterface;
import by.bsuir.pandadb.core.exception.IncorrectSetValueException;
import by.bsuir.pandadb.core.factory.FactoryTypeSerializers;
import by.bsuir.pandadb.core.model.DBField;
import by.bsuir.pandadb.core.model.DBRecord;
import by.bsuir.pandadb.core.model.DBTable;
import by.bsuir.pandadb.core.model.DBValue;
import by.bsuir.pandadb.core.service.TableAccessService;
import by.bsuir.pandadb.core.service.util.StringExpressionCalculator;
import by.bsuir.pandadb.core.types.serialize.SerializerDBRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Service
public class TableAccessServiceImpl implements TableAccessService {

    @Resource
    private DAOInterface tablesDAO;

    @Resource
    private SerializerDBRecord serializer;

    @Resource
    private FactoryTypeSerializers factoryTypeSerializers;

    @Value("${dao.load.countRecords:10}")
    private int countRecordsForLoadOne = 10;

    @Override
    public List<DBRecord> selectRecords(DBTable table, String condition) {
        return selectRecords(table, condition, Integer.MAX_VALUE);
    }

    @Override
    public List<DBRecord> selectRecords(DBTable table, String condition, int maxCount) {
        int currentOffset = 0;
        List<DBRecord> result = new ArrayList<>();
        List<DBRecord> records = null;
        do {
            records = readRecords(table, countRecordsForLoadOne, currentOffset);
            records.stream().filter(record -> checkRecord(record, condition))
                    .limit(maxCount - result.size())
                    .forEach(result::add);
            currentOffset += countRecordsForLoadOne;
        } while ((records.size() > 0) && (result.size() < maxCount));
        return result;
    }

    @Override
    public void updateRecords(DBTable table, String condition, List<String> newValues) {
        int currentOffset = 0;
        List<DBRecord> result = new ArrayList<>();
        List<DBRecord> records = null;
        do {
            records = readRecords(table, countRecordsForLoadOne, currentOffset);
            for (int i = 0; i < records.size(); i++) {
                DBRecord record = records.get(i);
                if (checkRecord(record, condition)) {
                    setNewValues(record, newValues);
                    updateRecord(record, i + currentOffset);
                }
            }
            currentOffset += countRecordsForLoadOne;
        } while (records.size() > 0);
    }

    @Override
    public void deleteRecords(DBTable table, String condition) {
        int currentOffset = 0;
        List<Integer> deletedIndex = new ArrayList<>();
        List<DBRecord> records = null;
        do {
            records = readRecords(table, countRecordsForLoadOne, currentOffset);
            for (int i = 0; i < records.size(); i++) {
                DBRecord record = records.get(i);
                if (checkRecord(record, condition)) {
                    deletedIndex.add(i);
                }
            }
            currentOffset += countRecordsForLoadOne;
        } while (records.size() > 0);

        deletedIndex.sort((o1, o2) -> o2 - o1);

        int countRecords = (int) (tablesDAO.getSize(table.getName()) / table.getSize());
        int currentLastRecord = countRecords - 1;
        int size = table.getSize();

        for (Integer currentIndex : deletedIndex) {
            if (!currentIndex.equals(currentLastRecord)) {
                byte[] lastData = tablesDAO.getByOffset(table.getName(), currentLastRecord * size, size);
                tablesDAO.replaceByOffset(table.getName(), currentIndex * size, lastData);
            }
            currentLastRecord--;
        }

        currentLastRecord++;

        tablesDAO.removeFormEnd(table.getName(), (countRecords - currentLastRecord) * size);
    }

    @Override
    public void insertRecords(DBTable table, List<DBRecord> records) {
        byte[] data = serializer.serializeDBRecords(records);
        tablesDAO.addToEnd(table.getName(), data);
    }

    private void setNewValues(DBRecord record, List<String> newValuesExpressions) {
        Map<DBField, DBValue> mappedNewValues = newValuesExpressions.stream()
                .map(s -> calculateExpression(record, s))
                .collect(Collectors.toMap(DBValue::getField, Function.identity()));

        DBValue[] currentValues = record.getValues();

        for (int i = 0; i < currentValues.length; i++) {
            DBValue newValue = mappedNewValues.get(currentValues[i].getField());
            if (newValue != null) {
                currentValues[i] = newValue;
            }
        }
    }

    private DBValue calculateExpression(DBRecord record, String expression) {
        if (StringUtils.countMatches(expression, "=") != 1) {
            throw new IncorrectSetValueException(expression);
        }
        String[] splitted = expression.split("=");
        String calulatedExpression = splitted[1];
        String fieldName = splitted[0].trim();

        DBField destinationFiled = Arrays.stream(record.getDbTable().getFields())
                .filter(field -> field.getName().equals(fieldName))
                .findFirst().orElseThrow(IncorrectSetValueException::new);

        Object value = StringExpressionCalculator.calculateExpression(record.getValues(), calulatedExpression);

        DBValue result = new DBValue();
        result.setField(destinationFiled);
        result.setValue(value);
        return result;
    }

    private void updateRecord(DBRecord record, int index) {
        int size = record.getDbTable().getSize();
        byte[] data = serializer.serializeDBRecord(record);
        tablesDAO.replaceByOffset(record.getDbTable().getName(), size * index, data);
    }

    private boolean checkRecord(DBRecord record, String condition) {
        return StringExpressionCalculator.checkCondition(record.getValues(), condition);
    }

    private List<DBRecord> readRecords(DBTable table, int count, int offset) {
        int sizeRecord = table.getSize();
        long sizeTable = tablesDAO.getSize(table.getName());
        byte[] data = tablesDAO.getByOffset(table.getName(),
                (int) min(offset * sizeRecord, sizeTable),
                (int) min(max(sizeTable - offset * sizeRecord, 0), count * sizeRecord));
        return serializer.deserializeDBRecords(data, table);
    }

}

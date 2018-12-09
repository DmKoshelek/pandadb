package by.bsuir.pandadb.core.types.serialize;

import by.bsuir.pandadb.core.exception.NotDesiralizedDataException;
import by.bsuir.pandadb.core.factory.FactoryTypeSerializers;
import by.bsuir.pandadb.core.model.DBField;
import by.bsuir.pandadb.core.model.DBRecord;
import by.bsuir.pandadb.core.model.DBTable;
import by.bsuir.pandadb.core.model.DBValue;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SerializerDBRecord {

    @Resource
    FactoryTypeSerializers factoryTypeSerializers;

    public byte[] serializeDBRecords(List<DBRecord> records) {
        List<byte[]> serializedRecords = records.stream().map(this::serializeDBRecord).collect(Collectors.toList());
        return concatArrays(serializedRecords);
    }

    public byte[] serializeDBRecord(DBRecord record) {
        Map<DBValue, Integer> mappedOffset = new HashMap<>();
        for (int i = 0; i < record.getDbTable().getFields().length; i++) {
            DBValue value = record.getValues()[i];
            int index = ArrayUtils.indexOf(record.getDbTable().getFields(), value.getField());
            mappedOffset.put(value, index);
        }
        List<byte[]> arrays = Arrays.stream(record.getValues())
                .sorted(Comparator.comparingInt(mappedOffset::get))
                .map(this::serializeDBValue).collect(Collectors.toList());
        return concatArrays(arrays);
    }

    private byte[] concatArrays(List<byte[]> arrays) {
        int size = arrays.stream().mapToInt(value -> value.length).sum();
        byte[] serializeData = new byte[size];
        int currentPos = 0;

        for (int i = 0; i < arrays.size(); i++) {
            byte[] source = arrays.get(i);
            System.arraycopy(source, 0, serializeData, currentPos, source.length);
            currentPos += source.length;
        }
        return serializeData;
    }

    private byte[] serializeDBValue(DBValue dbValue) {
        return factoryTypeSerializers.getSerializer(dbValue.getField().getType())
                .serializeByte(dbValue.getValue(), dbValue.getField().getType());
    }

    public List<DBRecord> deserializeDBRecords(byte[] data, DBTable dbTable) {
        List<DBRecord> result = new ArrayList<>();
        int offset = 0;
        int size = dbTable.getSize();
        while (offset < data.length) {
            byte[] subValue = Arrays.copyOfRange(data, offset, offset + size);
            DBRecord record = deserializeDBRecord(subValue, dbTable);
            result.add(record);
            offset += size;
        }
        return result;
    }

    protected DBRecord deserializeDBRecord(byte[] data, DBTable dbTable) {
        if (data.length != dbTable.getSize()) {
            throw new NotDesiralizedDataException();
        }

        DBRecord dbRecord = new DBRecord();
        DBField[] fields = dbTable.getFields();
        List<DBValue> values = new ArrayList<>();
        for (int i = 0; i < fields.length; i++) {
            int offset = dbTable.getOffsets()[i];
            DBField filed = dbTable.getFields()[i];
            int size = filed.getType().getSize();
            byte[] subValue = Arrays.copyOfRange(data, offset, offset + size);
            values.add(deserializeDBValue(subValue, filed));
        }

        dbRecord.setValues(values.toArray(new DBValue[values.size()]));
        dbRecord.setDbTable(dbTable);
        return dbRecord;
    }

    private DBValue deserializeDBValue(byte[] byteValue, DBField field) {
        Object value = factoryTypeSerializers.getSerializer(field.getType())
                .deserialize(byteValue);
        DBValue result = new DBValue();
        result.setValue(value);
        result.setField(field);
        return result;
    }
}

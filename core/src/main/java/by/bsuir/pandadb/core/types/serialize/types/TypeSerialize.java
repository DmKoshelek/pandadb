package by.bsuir.pandadb.core.types.serialize.types;

import by.bsuir.pandadb.core.types.model.DBType;

import java.util.Arrays;

public interface TypeSerialize {

    byte[] serializeByte(Object value);

    default byte[] serializeByte(Object value, DBType dbType) {
        return Arrays.copyOf(serializeByte(value), dbType.getSize());
    }

    Object deserialize(byte[] value);

    String serializeString(Object value);

    Object deserialize(String value);
}

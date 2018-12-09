package by.bsuir.pandadb.core.types.serialize.types;

import by.bsuir.pandadb.core.exception.IncorrectValueException;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

@Component("integerSerializer")
public class IntegerSerializer implements TypeSerialize {

    @Override
    public byte[] serializeByte(Object value) {
        byte[] bytes = new byte[4];
        ByteBuffer.wrap(bytes).putInt((Integer) value);
        return bytes;
    }

    @Override
    public Object deserialize(byte[] value) {
        return ByteBuffer.wrap(value).getInt();
    }

    @Override
    public String serializeString(Object value) {
        return Integer.toString((Integer) value);
    }

    @Override
    public Object deserialize(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IncorrectValueException(value);
        }
    }
}

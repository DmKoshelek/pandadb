package by.bsuir.pandadb.core.types.serialize.types;

import by.bsuir.pandadb.core.exception.IncorrectValueException;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

@Component("doubleSerializer")
public class DoubleSerializer implements TypeSerialize {

    @Override
    public byte[] serializeByte(Object value) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putDouble((Double) value);
        return bytes;
    }

    @Override
    public Object deserialize(byte[] value) {
        return ByteBuffer.wrap(value).getDouble();
    }

    @Override
    public String serializeString(Object value) {
        return Double.toString((Double) value);
    }

    @Override
    public Object deserialize(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IncorrectValueException(value);
        }
    }
}

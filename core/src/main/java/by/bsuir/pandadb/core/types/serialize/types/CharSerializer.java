package by.bsuir.pandadb.core.types.serialize.types;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component("charSerializer")
public class CharSerializer implements TypeSerialize {

    @Override
    public byte[] serializeByte(Object value) {
        return ((String) value).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Object deserialize(byte[] value) {
        int length = ArrayUtils.indexOf(value, (byte) 0);
        return new String(value, 0, length, StandardCharsets.UTF_8);
    }

    @Override
    public String serializeString(Object value) {
        return (String) value;
    }

    @Override
    public Object deserialize(String value) {
        return value;
    }
}

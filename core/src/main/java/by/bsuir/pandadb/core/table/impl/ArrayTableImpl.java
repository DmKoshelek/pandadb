package by.bsuir.pandadb.core.table.impl;

import by.bsuir.pandadb.core.table.Table;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

public class ArrayTableImpl implements Table {

    @Value("{dao.new.ARRAY_SIZE}")
    private int ARRAY_SIZE = 4 * 1024 * 1024;

    private List<byte[]> listArrays = new ArrayList<>();
    private long currentSize = 0;
    private long currentLength = 0;

    public byte[] getByOffset(int offset, int length) {
        byte[] result = new byte[length];

        int startIndex = 0;
        int currentOffset = offset;

        while (length > startIndex) {
            int arrayIndex = (int) (currentOffset / ARRAY_SIZE);
            int currentLastIndex = Math.min((int) ((arrayIndex + 1) * ARRAY_SIZE), length + offset);

            byte[] sourceArray = listArrays.get(arrayIndex);
            int arrayOffset = (int) (currentOffset - arrayIndex * ARRAY_SIZE);
            int arrayLength = currentLastIndex - arrayOffset - arrayIndex * ARRAY_SIZE;
            System.arraycopy(sourceArray, arrayOffset, result, startIndex, arrayLength);
            currentOffset += arrayLength;

            startIndex += arrayLength;
        }

        return result;
    }

    public void removeFormEnd(int length) {
        currentLength -= length;
    }

    public void replaceByOffset(int offset, byte[] data) {
        putByOffset(data, offset);
    }

    public void addToEnd(byte[] data) {
        while (data.length + currentLength > currentSize) {
            addNewArray();
        }

        putByOffset(data, currentLength);
    }

    private void putByOffset(byte[] data, long offset) {
        int startIndex = 0;
        int lastIndex = data.length;
        long currentOffset = offset;

        while (lastIndex > startIndex) {
            int arrayIndex = (int) (currentOffset / ARRAY_SIZE);
            long currentLastIndex = Math.min(((arrayIndex + 1) * ARRAY_SIZE), lastIndex + offset);

            byte[] dest = listArrays.get(arrayIndex);
            int destArrayOffset = (int) (currentOffset - arrayIndex * ARRAY_SIZE);
            int arrayLength = (int) (currentLastIndex - destArrayOffset);

            System.arraycopy(data, startIndex, dest, destArrayOffset, arrayLength);
            currentOffset += (int) currentLastIndex;

            startIndex += (int) currentLastIndex;
        }

        currentLength = Math.max(currentLength, offset + data.length);
    }

    public long getSize() {
        return currentLength;
    }

    private void addNewArray() {
        byte[] newArray = new byte[ARRAY_SIZE];
        listArrays.add(newArray);
        currentSize += ARRAY_SIZE;
    }
}

package by.bsuir.pandadb.core.table;

public interface Table {
    byte[] getByOffset(int offset, int length);

    void removeFormEnd(int length);

    void replaceByOffset(int offset, byte[] data);

    void addToEnd(byte[] data);

    long getSize();
}
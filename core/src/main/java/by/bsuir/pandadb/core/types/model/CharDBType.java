package by.bsuir.pandadb.core.types.model;

public class CharDBType extends DBType {

    private static final int UTF_8_ELEM_SIZE = 4;
    private static final int MAX_SIZE = 65535;

    public CharDBType(int size) {
        super("CHAR", (size + 1) * UTF_8_ELEM_SIZE);
        if ((size <= 0) || (size >= MAX_SIZE)) {
            throw new IllegalArgumentException("Size lower then 0");
        }
    }
}

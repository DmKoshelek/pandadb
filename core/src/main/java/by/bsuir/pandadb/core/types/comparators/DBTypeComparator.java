package by.bsuir.pandadb.core.types.comparators;

import by.bsuir.pandadb.core.model.DBComparedValue;
import by.bsuir.pandadb.core.model.DBValue;

@Deprecated
public interface DBTypeComparator {
    int compare(DBValue first, DBValue second);

    boolean compareByComparedValue(DBValue value, DBComparedValue comparedValue);
}

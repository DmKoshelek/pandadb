package by.bsuir.pandadb.core.factory;

import by.bsuir.pandadb.core.types.comparators.DBTypeComparator;

import java.util.Map;

public class FactoryComparators {

    private Map<String, DBTypeComparator> mapComparators;

    public void setMapComparators(Map<String, DBTypeComparator> mapComparators) {
        this.mapComparators = mapComparators;
    }

    public DBTypeComparator getComparator(String name) {
        return mapComparators.get(name);
    }
}

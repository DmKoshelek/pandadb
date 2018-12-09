package by.bsuir.pandadb.core.factory;

import by.bsuir.pandadb.core.types.model.DBType;
import by.bsuir.pandadb.core.types.serialize.types.TypeSerialize;

import java.util.Map;

public class FactoryTypeSerializers {

    private Map<String, TypeSerialize> mapSerializers;

    public void setMapSerializers(Map<String, TypeSerialize> mapSerializers) {
        this.mapSerializers = mapSerializers;
    }

    public TypeSerialize getSerializer(String name) {
        return mapSerializers.get(name);
    }

    public TypeSerialize getSerializer(DBType type) {
        return mapSerializers.get(type.getName());
    }
}


package by.bsuir.pandadb.core.service;

import by.bsuir.pandadb.core.model.DBRecord;
import by.bsuir.pandadb.core.model.DBTable;

import java.util.List;

public interface TableAccessService {

    List<DBRecord> selectRecords(DBTable table, String condition);

    List<DBRecord> selectRecords(DBTable table, String condition, int maxCount);

    void updateRecords(DBTable table, String condition, List<String> newValues);

    void deleteRecords(DBTable table, String condition);

    void insertRecords(DBTable table, List<DBRecord> records);

}
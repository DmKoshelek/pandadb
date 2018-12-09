package by.bsuir.pandadb.core.service.impl;

import by.bsuir.pandadb.core.model.DBField;
import by.bsuir.pandadb.core.model.DBTable;
import by.bsuir.pandadb.core.types.model.DBType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TableStructureServiceImplTest {

    private TableStructureServiceImpl service = new TableStructureServiceImpl();

    private static final int ADDED_DB_TYPE_SIZE = 5;
    private static final int EXISTED_DB_TYPE_SIZE = 3;

    private static final String ADDED_DB_TYPE_NAME = "addedName";
    private static final String EXISTED_DB_TYPE_NAME = "existedName";

    @Mock
    private DBType existDBType;
    private DBField existDBField;

    @Mock
    private DBType addedDBType;
    private DBField addedDBField;

    private DBTable dbTable = new DBTable();

    @Before
    public void init(){
        int[] offsets = new int[1];
        offsets[0] = 0;

        when(existDBType.getSize()).thenReturn(EXISTED_DB_TYPE_SIZE);
        when(existDBType.getName()).thenReturn(EXISTED_DB_TYPE_NAME);

        when(addedDBType.getSize()).thenReturn(ADDED_DB_TYPE_SIZE);
        when(addedDBType.getName()).thenReturn(ADDED_DB_TYPE_NAME);

        addedDBField = new DBField();
        addedDBField.setType(addedDBType);
        addedDBField.setName(ADDED_DB_TYPE_NAME);

        existDBField = new DBField();
        existDBField.setType(existDBType);
        existDBField.setName(EXISTED_DB_TYPE_NAME);

        dbTable.setFields((DBField[])Arrays.asList(existDBField).toArray());
        dbTable.setSize(EXISTED_DB_TYPE_SIZE);
        dbTable.setOffsets(offsets);
    }

    @Test
    public void shouldAddElemsToTable(){
        service.addNewField(dbTable, addedDBField);

        int[] offsets = new int[2];
        offsets[0] = 0;
        offsets[1] = EXISTED_DB_TYPE_SIZE;
        assertArrayEquals(offsets, dbTable.getOffsets());
        assertEquals(EXISTED_DB_TYPE_SIZE + ADDED_DB_TYPE_SIZE, dbTable.getSize());
        assertEquals(2, dbTable.getFields().length);
    }

    @Test
    public void shouldRemoveElemsFromTable(){
        service.removeField(dbTable, existDBField);

        assertEquals(0, dbTable.getSize());
        assertEquals(0, dbTable.getFields().length);
    }

}
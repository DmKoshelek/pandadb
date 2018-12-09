package by.bsuir.pandadb.core.facade;

import by.bsuir.pandadb.core.model.SQLCommand;
import by.bsuir.pandadb.core.model.SQLResult;

public interface SQLCommandInterpreter {

    SQLResult executeCommand(SQLCommand sqlCommand);
}

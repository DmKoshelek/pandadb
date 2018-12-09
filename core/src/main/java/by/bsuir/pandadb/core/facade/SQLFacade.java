package by.bsuir.pandadb.core.facade;

import by.bsuir.pandadb.core.model.SQLResult;

public interface SQLFacade {
    SQLResult execute(String command);

    SQLResult executeWithoutLog(String command);
}

package by.bsuir.pandadb.core.facade.impl;

import by.bsuir.pandadb.core.PandaDiskWorker;
import by.bsuir.pandadb.core.facade.SQLCommandInterpreter;
import by.bsuir.pandadb.core.facade.SQLFacade;
import by.bsuir.pandadb.core.model.SQLCommand;
import by.bsuir.pandadb.core.model.SQLResult;
import by.bsuir.pandadb.core.model.SQLResultType;
import by.bsuir.pandadb.core.sql.enums.SQLCommandType;
import by.bsuir.pandadb.core.sql.parser.SQLParser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SQLFacadeImpl implements SQLFacade {

    @Resource
    private SQLParser sqlParser;

    @Resource
    private SQLCommandInterpreter sqlCommandInterpreter;

    @Resource
    private PandaDiskWorker pandaDiskWorker;

    @Override
    public SQLResult execute(String stringCommand) {
        String filteredCommand = stringCommand
                .replaceAll("\r", " ")
                .replaceAll("\n", " ");

        SQLCommand command = sqlParser.parse(filteredCommand);
        SQLResult result = sqlCommandInterpreter.executeCommand(command);
        if (isNotIndenpotentCommand(command) && result.getResult() == SQLResultType.SUCCESS) {
            logCommand(filteredCommand);
        }
        return result;
    }

    @Override
    public SQLResult executeWithoutLog(String stringCommand) {
        SQLCommand command = sqlParser.parse(stringCommand);
        return sqlCommandInterpreter.executeCommand(command);
    }

    private boolean isNotIndenpotentCommand(SQLCommand command) {
        return command.getType() != SQLCommandType.SELECT;
    }

    private void logCommand(String s) {
        pandaDiskWorker.saveToLog(s);
    }
}

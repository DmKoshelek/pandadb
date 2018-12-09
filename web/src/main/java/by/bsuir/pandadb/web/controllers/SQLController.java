package by.bsuir.pandadb.web.controllers;

import by.bsuir.pandadb.core.exception.*;
import by.bsuir.pandadb.core.facade.SQLCommandInterpreter;
import by.bsuir.pandadb.core.facade.SQLFacade;
import by.bsuir.pandadb.core.model.SQLCommand;
import by.bsuir.pandadb.core.model.SQLResult;
import by.bsuir.pandadb.core.model.SQLResultType;
import by.bsuir.pandadb.core.sql.parser.SQLParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@ResponseBody
public class SQLController {

    @Resource
    private SQLFacade sqlFacade;

    @RequestMapping(value = "/", params = {"command"})
    public SQLResult sqlCommand(@RequestParam(name = "command") String stringCommand){
        return sqlFacade.execute(stringCommand);
    }

    @ExceptionHandler(TableNotFoundException.class)
    public SQLResult notFoundHandler(TableNotFoundException e){
        return new SQLResult(SQLResultType.ERROR, "table  " + e.getMessage() + " not found");
    }

    @ExceptionHandler(FieldNotFoundException.class)
    public SQLResult notFoundHandler(FieldNotFoundException e){
        return new SQLResult(SQLResultType.ERROR, "field  " + e.getMessage() + " not found");
    }

    @ExceptionHandler(IncorrectValueException.class)
    public SQLResult notFoundHandler(IncorrectValueException e){
        return new SQLResult(SQLResultType.ERROR, "value " + e.getMessage() + " is incorrect");
    }

    @ExceptionHandler(ElementNotFoundException.class)
    public SQLResult notFoundHandler(ElementNotFoundException e){
        return new SQLResult(SQLResultType.ERROR, "element " + e.getMessage() + " not found");
    }

    @ExceptionHandler(IncorrectConditionException.class)
    public SQLResult notFoundHandler(IncorrectConditionException e){
        return new SQLResult(SQLResultType.ERROR, "Condition '" + e.getMessage() + "' is incorrect");
    }

    @ExceptionHandler(IncorrectFieldNameException.class)
    public SQLResult notFoundHandler(IncorrectFieldNameException e){
        return new SQLResult(SQLResultType.ERROR, "Field name '" + e.getMessage() + "' is incorrect");
    }

    @ExceptionHandler(IncorrectSetValueException.class)
    public SQLResult notFoundHandler(IncorrectSetValueException e){
        return new SQLResult(SQLResultType.ERROR, "Set expression '" + e.getMessage() + "' is incorrect");
    }

    @ExceptionHandler(IncorrectTableNameException.class)
    public SQLResult notFoundHandler(IncorrectTableNameException e){
        return new SQLResult(SQLResultType.ERROR, "Table name '" + e.getMessage() + "' is incorrect");
    }

    @ExceptionHandler(IncorrectTypeNameException.class)
    public SQLResult notFoundHandler(IncorrectTypeNameException e){
        return new SQLResult(SQLResultType.ERROR, "Type name '" + e.getMessage() + "' is incorrect");
    }

    @ExceptionHandler(InsertCountValuesException.class)
    public SQLResult notFoundHandler(InsertCountValuesException e){
        return new SQLResult(SQLResultType.ERROR, "Count of insert values is incorrect");
    }

    @ExceptionHandler(NotDesiralizedDataException.class)
    public SQLResult notFoundHandler(NotDesiralizedDataException e){
        return new SQLResult(SQLResultType.ERROR, "Value can't be deserialize");
    }

    @ExceptionHandler(ParseException.class)
    public SQLResult notFoundHandler(ParseException e){
        return new SQLResult(SQLResultType.ERROR, "Value can't be parse");
    }
}

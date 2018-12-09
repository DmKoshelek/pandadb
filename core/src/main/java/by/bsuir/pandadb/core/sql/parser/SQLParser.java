package by.bsuir.pandadb.core.sql.parser;

import by.bsuir.pandadb.core.exception.*;
import by.bsuir.pandadb.core.factory.FactoryTypes;
import by.bsuir.pandadb.core.model.DBField;
import by.bsuir.pandadb.core.model.SQLCommand;
import by.bsuir.pandadb.core.sql.enums.SQLCommandType;
import by.bsuir.pandadb.core.types.model.DBType;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SQLParser {

    private static final Pattern CREATE_DATABASE_PATTERN = Pattern.compile("(?i)^CREATE\\s+DATABASE\\s+(\\S+)$");

    private static final Pattern DROP_DATABASE_PATTERN = Pattern.compile("(?i)^DROP\\s+DATABASE\\s+(\\S+)$");

    private static final Pattern CREATE_TABLE_PATTERN = Pattern.compile("(?i)^CREATE\\s+TABLE\\s+([^(\\s]+)\\s*\\((.*)\\)$");

    private static final Pattern DROP_TABLE_PATTERN = Pattern.compile("(?i)^DROP\\s+TABLE\\s+(\\S+)$");

    private static final Pattern INSERT_INTO_PATTERN =
            Pattern.compile("(?i)^INSERT\\s+INTO\\s+([^(\\s]+)\\s*\\(((?:.(?!\\s*VALUES))+)\\)\\s*VALUES\\s*\\(([^)]+)\\)$");

    private static final Pattern SELECT_FROM_PATTERN =
            Pattern.compile("(?i)^SELECT\\s+((?:.(?!ROM))+)FROM\\s+([^\\s]+)\\s*(?:\\s+WHERE\\s+(.+))?$");

    private static final Pattern DELETE_FROM_PATTERN =
            Pattern.compile("(?i)^DELETE\\s+FROM\\s+([^\\s]+)\\s+WHERE\\s+(.+)$");

    private static final Pattern UPDATE_SET_PATTERN =
            Pattern.compile("(?i)^UPDATE\\s+([^\\s]+)\\s+SET\\s+((?:.(?!HERE))+)WHERE\\s+(.+)$");

    private static final Pattern CORRECT_NAME_PATTER =
            Pattern.compile("^[a-zA-Z](?:\\w)*$");

    private static final String SPLIT_COMMA_PATTER = "(,)(?=(?:[^\']|\'[^\']*\')*$)";
    private static final String SPLIT_EQUAL_PATTER = "(=)(?=(?:[^\']|\'[^\']*\')*$)";
    private static final Pattern QUOTATION_PATTERN = Pattern.compile("^\'(.*)\'$");

    private static final List<Pattern> patters = Arrays.asList(
            CREATE_DATABASE_PATTERN, DROP_DATABASE_PATTERN,
            CREATE_TABLE_PATTERN, DROP_TABLE_PATTERN,
            INSERT_INTO_PATTERN, SELECT_FROM_PATTERN,
            DELETE_FROM_PATTERN, UPDATE_SET_PATTERN);

    private final Map<Pattern, Function<Matcher, SQLCommand>> patternFunctionMap = new HashMap<>();

    @PostConstruct
    public void init() {
        patternFunctionMap.put(CREATE_DATABASE_PATTERN, this::createDatabaseParse);
        patternFunctionMap.put(DROP_DATABASE_PATTERN, this::dropDatabaseParse);
        patternFunctionMap.put(CREATE_TABLE_PATTERN, this::createTableParse);
        patternFunctionMap.put(DROP_TABLE_PATTERN, this::dropTableParse);
        patternFunctionMap.put(INSERT_INTO_PATTERN, this::insertTableParse);
        patternFunctionMap.put(SELECT_FROM_PATTERN, this::selectTableParse);
        patternFunctionMap.put(DELETE_FROM_PATTERN, this::deleteTableParse);
        patternFunctionMap.put(UPDATE_SET_PATTERN, this::updateTableParse);
    }

    @Resource
    private FactoryTypes factoryTypes;

    public SQLCommand parse(String value) {
        String checkedValue = value.trim();
        Matcher matcher = patters.stream()
                .map(pattern -> pattern.matcher(value))
                .filter(Matcher::find)
                .findFirst().orElseThrow(ParseException::new);

        return patternFunctionMap.get(matcher.pattern()).apply(matcher);
    }

    private SQLCommand createDatabaseParse(Matcher matcher) {
        String databaseName = matcher.group(1);
        if (!isCorrectName(databaseName)) {
            throw new IncorrectDatabaseNameException(databaseName);
        }

        SQLCommand command = new SQLCommand();
        command.setType(SQLCommandType.CREATE_DATABASE);
        command.setTable(databaseName);

        return command;
    }

    private SQLCommand dropDatabaseParse(Matcher matcher) {
        String databaseName = matcher.group(1).trim();

        SQLCommand command = new SQLCommand();
        command.setType(SQLCommandType.DROP_DATABASE);
        command.setTable(databaseName);

        return command;
    }

    private SQLCommand createTableParse(Matcher matcher) {
        String tableName = matcher.group(1).trim();

        if (!isCorrectName(tableName)) {
            throw new IncorrectTableNameException(tableName);
        }

        String[] fields = matcher.group(2).trim().split(",");
        List<DBField> dbFields = Arrays.stream(fields)
                .map(String::trim)
                .map(s -> s.split(" ", 2))
                .map(this::createDBField)
                .collect(Collectors.toList());


        SQLCommand command = new SQLCommand();
        command.setTable(tableName);
        command.setType(SQLCommandType.CREATE_TABLE);
        command.setFields(dbFields);

        return command;
    }

    private SQLCommand selectTableParse(Matcher matcher) {
        List<String> fields = Arrays.stream(matcher.group(1).split(","))
                .map(String::trim).collect(Collectors.toList());

        String tableName = matcher.group(2).trim();

        String condition = matcher.group(3);
        if (condition == null) {
            condition = "true";
        }
        condition = condition.trim();

        SQLCommand command = new SQLCommand();
        command.setTable(tableName);
        command.setType(SQLCommandType.SELECT);
        command.setSelectFields(fields);
        command.setCondition(condition);
        return command;
    }

    private SQLCommand deleteTableParse(Matcher matcher) {
        String tableName = matcher.group(1).trim();

        String condition = matcher.group(2).trim();

        SQLCommand command = new SQLCommand();
        command.setTable(tableName);
        command.setType(SQLCommandType.REMOVE);
        command.setCondition(condition);
        return command;
    }

    private SQLCommand insertTableParse(Matcher matcher) {
        String tableName = matcher.group(1).trim();

        String[] fields = matcher.group(2).trim().split(SPLIT_COMMA_PATTER);
        String[] values = matcher.group(3).trim().split(SPLIT_COMMA_PATTER);

        if (fields.length != values.length) {
            throw new InsertCountValuesException();
        }

        Map<String, String> valuesMap = Stream.iterate(0, integer -> integer + 1)
                .limit(fields.length)
                .collect(Collectors.toMap(i -> fields[i].trim(), i -> removeQuotation(values[i].trim())));

        SQLCommand command = new SQLCommand();
        command.setTable(tableName);
        command.setType(SQLCommandType.INSERT);
        command.setValues(valuesMap);
        return command;
    }

    private SQLCommand updateTableParse(Matcher matcher) {
        String tableName = matcher.group(1).trim();

        List<String> values = Arrays.stream(matcher.group(2).trim().split(SPLIT_COMMA_PATTER))
                .collect(Collectors.toList());

        String condition = matcher.group(3).trim();

        SQLCommand command = new SQLCommand();
        command.setTable(tableName);
        command.setType(SQLCommandType.UPDATE);
        command.setUpdateFields(values);
        command.setCondition(condition);
        return command;
    }

    private DBField createDBField(String[] elems) {
        if (elems.length != 2) {
            throw new IncorrectFieldNameException(elems[0]);
        }
        String fieldName = elems[0].trim();
        String fieldType = elems[1].replace(" ", "");

        if (!isCorrectName(fieldName)) {
            throw new IncorrectFieldNameException(elems[0]);
        }

        DBType dbType = factoryTypes.getDBType(fieldType);
        DBField field = new DBField();
        field.setName(fieldName);
        field.setType(dbType);
        return field;
    }

    private SQLCommand dropTableParse(Matcher matcher) {
        String tableName = matcher.group(1).trim();

        SQLCommand command = new SQLCommand();
        command.setType(SQLCommandType.DROP_TABLE);
        command.setTable(tableName);

        return command;
    }

    private boolean isCorrectName(String name) {
        return CORRECT_NAME_PATTER.matcher(name).matches();
    }

    private String removeQuotation(String value) {
        Matcher matcher = QUOTATION_PATTERN.matcher(value);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return value;
    }
}

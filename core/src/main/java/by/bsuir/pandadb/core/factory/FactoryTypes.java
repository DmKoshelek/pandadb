package by.bsuir.pandadb.core.factory;

import by.bsuir.pandadb.core.exception.IncorrectTypeNameException;
import by.bsuir.pandadb.core.types.model.CharDBType;
import by.bsuir.pandadb.core.types.model.DBType;
import by.bsuir.pandadb.core.types.model.IntegerDBType;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FactoryTypes {
    private static final Pattern INT_PATTERN = Pattern.compile("(?i)^int$");

    private static final Pattern DOUBLE_PATTERN = Pattern.compile("(?i)^double$");

    private static final Pattern CHAR_PATTERN = Pattern.compile("(?i)^char\\((\\d+)\\)$");

    private static final List<Pattern> patters = Arrays.asList(
            INT_PATTERN, DOUBLE_PATTERN, CHAR_PATTERN);

    private final Map<Pattern, Function<Matcher, DBType>> patternFunctionMap = new HashMap<>();

    @PostConstruct
    public void init() {
        patternFunctionMap.put(INT_PATTERN, this::createIntType);
        patternFunctionMap.put(DOUBLE_PATTERN, this::createDoubleType);
        patternFunctionMap.put(CHAR_PATTERN, this::createCharType);
    }

    public DBType getDBType(String name) {
        String checkedValue = name.trim();
        Matcher matcher = patters.stream()
                .map(pattern -> pattern.matcher(name))
                .filter(Matcher::find)
                .findFirst().orElseThrow(IncorrectTypeNameException::new);

        return patternFunctionMap.get(matcher.pattern()).apply(matcher);
    }

    private DBType createIntType(Matcher matcher) {
        return new IntegerDBType();
    }

    private DBType createDoubleType(Matcher matcher) {
        return new IntegerDBType();
    }

    private DBType createCharType(Matcher matcher) {
        int size = Integer.parseInt(matcher.group(1));
        return new CharDBType(size);
    }
}

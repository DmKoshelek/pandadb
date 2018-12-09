package by.bsuir.pandadb.core.service.util;

import by.bsuir.pandadb.core.exception.IncorrectConditionException;
import by.bsuir.pandadb.core.exception.IncorrectSetValueException;
import by.bsuir.pandadb.core.model.DBValue;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Arrays;

public final class StringExpressionCalculator {

    private StringExpressionCalculator() {
    }

    public static boolean checkCondition(DBValue[] values, String condition) {
        ScriptEngine jsEndine = new ScriptEngineManager().getEngineByName("js");

        Arrays.stream(values)
                .forEach(value -> jsEndine.put(value.getField().getName(), value.getValue()));
        try {
            return (Boolean) jsEndine.eval(condition);
        } catch (ScriptException e) {
            throw new IncorrectConditionException(condition, e);
        }
    }

    public static Object calculateExpression(DBValue[] values, String expression) {
        ScriptEngine jsEndine = new ScriptEngineManager().getEngineByName("js");

        Arrays.stream(values)
                .forEach(value -> jsEndine.put(value.getField().getName(), value.getValue()));
        try {
            return jsEndine.eval(expression);
        } catch (ScriptException e) {
            throw new IncorrectSetValueException(e);
        }
    }
}

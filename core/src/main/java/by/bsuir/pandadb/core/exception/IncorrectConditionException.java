package by.bsuir.pandadb.core.exception;

public class IncorrectConditionException extends SQLException {

    public IncorrectConditionException() {
    }

    public IncorrectConditionException(String message) {
        super(message);
    }

    public IncorrectConditionException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectConditionException(Throwable cause) {
        super(cause);
    }

    public IncorrectConditionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

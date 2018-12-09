package by.bsuir.pandadb.core.exception;

public class IncorrectSetValueException extends SQLException {
    public IncorrectSetValueException() {
    }

    public IncorrectSetValueException(String message) {
        super(message);
    }

    public IncorrectSetValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectSetValueException(Throwable cause) {
        super(cause);
    }

    public IncorrectSetValueException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

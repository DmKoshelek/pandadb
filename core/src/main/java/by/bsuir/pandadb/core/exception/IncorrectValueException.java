package by.bsuir.pandadb.core.exception;

public class IncorrectValueException extends SQLException {
    public IncorrectValueException() {
    }

    public IncorrectValueException(String message) {
        super(message);
    }

    public IncorrectValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectValueException(Throwable cause) {
        super(cause);
    }

    public IncorrectValueException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

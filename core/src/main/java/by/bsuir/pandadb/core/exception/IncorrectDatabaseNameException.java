package by.bsuir.pandadb.core.exception;

public class IncorrectDatabaseNameException extends IncorrectNameException {
    public IncorrectDatabaseNameException() {
    }

    public IncorrectDatabaseNameException(String message) {
        super(message);
    }

    public IncorrectDatabaseNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectDatabaseNameException(Throwable cause) {
        super(cause);
    }

    public IncorrectDatabaseNameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

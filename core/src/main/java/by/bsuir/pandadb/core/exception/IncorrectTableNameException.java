package by.bsuir.pandadb.core.exception;

public class IncorrectTableNameException extends IncorrectNameException {
    public IncorrectTableNameException() {
        super();
    }

    public IncorrectTableNameException(String message) {
        super(message);
    }

    public IncorrectTableNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectTableNameException(Throwable cause) {
        super(cause);
    }

    public IncorrectTableNameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

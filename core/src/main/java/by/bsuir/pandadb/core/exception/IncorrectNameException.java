package by.bsuir.pandadb.core.exception;

public class IncorrectNameException extends SQLException {
    public IncorrectNameException() {
    }

    public IncorrectNameException(String message) {
        super(message);
    }

    public IncorrectNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectNameException(Throwable cause) {
        super(cause);
    }

    public IncorrectNameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

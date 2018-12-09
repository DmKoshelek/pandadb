package by.bsuir.pandadb.core.exception;

public class IncorrectFieldNameException extends IncorrectNameException {
    public IncorrectFieldNameException() {
    }

    public IncorrectFieldNameException(String message) {
        super(message);
    }

    public IncorrectFieldNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectFieldNameException(Throwable cause) {
        super(cause);
    }

    public IncorrectFieldNameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

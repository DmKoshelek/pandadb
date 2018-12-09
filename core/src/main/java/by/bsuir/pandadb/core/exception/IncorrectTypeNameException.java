package by.bsuir.pandadb.core.exception;

public class IncorrectTypeNameException extends IncorrectNameException {
    public IncorrectTypeNameException() {
    }

    public IncorrectTypeNameException(String message) {
        super(message);
    }

    public IncorrectTypeNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectTypeNameException(Throwable cause) {
        super(cause);
    }

    public IncorrectTypeNameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package by.bsuir.pandadb.core.exception;

public class AlreadyExistTableException extends SQLException {
    public AlreadyExistTableException() {
    }

    public AlreadyExistTableException(String message) {
        super(message);
    }

    public AlreadyExistTableException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyExistTableException(Throwable cause) {
        super(cause);
    }

    public AlreadyExistTableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

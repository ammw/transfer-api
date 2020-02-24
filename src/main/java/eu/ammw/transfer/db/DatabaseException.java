package eu.ammw.transfer.db;

public class DatabaseException extends RuntimeException {
    DatabaseException(String message) {
        super(message);
    }

    DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}

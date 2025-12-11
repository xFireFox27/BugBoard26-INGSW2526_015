package it.unina.frontend.exception;

public class UserServiceException extends RuntimeException {
    private final int statusCode;

    public UserServiceException(String message) {
        super(message);
        this.statusCode = -1;
    }

    public UserServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public UserServiceException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
    }

    public UserServiceException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
package it.unina.frontend.exception;

public class ChangeServiceException extends RuntimeException {
    private final int statusCode;

    public ChangeServiceException(String message) {
        super(message);
        this.statusCode = -1;
    }

    public ChangeServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public ChangeServiceException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
    }

    public ChangeServiceException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}


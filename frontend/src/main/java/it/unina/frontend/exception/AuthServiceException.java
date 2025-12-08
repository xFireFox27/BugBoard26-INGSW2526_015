package it.unina.frontend.exception;

public class AuthServiceException extends Exception {

    private final int statusCode;

    public AuthServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public AuthServiceException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

package it.unina.frontend.exception;

public class IssueServiceException extends Exception {

    private final int statusCode;

    public IssueServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public IssueServiceException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

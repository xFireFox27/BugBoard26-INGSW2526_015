package it.unina.frontend.exception;

public class CommentServiceException extends RuntimeException {
    private final int statusCode;

    public CommentServiceException(String message) {
        super(message);
        this.statusCode = -1;
    }

    public CommentServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public CommentServiceException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
    }

    public CommentServiceException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

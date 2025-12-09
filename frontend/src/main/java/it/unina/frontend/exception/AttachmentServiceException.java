package it.unina.frontend.exception;

public class AttachmentServiceException extends Exception {
    private final int statusCode;

    public AttachmentServiceException(String message) {
        super(message);
        this.statusCode = -1;
    }

    public AttachmentServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public AttachmentServiceException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

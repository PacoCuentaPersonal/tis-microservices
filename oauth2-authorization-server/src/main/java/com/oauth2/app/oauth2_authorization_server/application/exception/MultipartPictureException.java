package com.oauth2.app.oauth2_authorization_server.application.exception;

// Updated import for TypeFileError
import com.oauth2.app.oauth2_authorization_server.application.exception.enums.TypeFileError;

public class MultipartPictureException extends RuntimeException{
    private final TypeFileError errorType;

    public MultipartPictureException(TypeFileError errorType) {
        super(errorType.getDefaultMessage());
        this.errorType = errorType;
    }

    public MultipartPictureException(TypeFileError errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public MultipartPictureException(TypeFileError errorType, Throwable cause) {
        super(errorType.getDefaultMessage(), cause);
        this.errorType = errorType;
    }

    public MultipartPictureException(TypeFileError errorType, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

    public TypeFileError getErrorType() { return errorType; }
}

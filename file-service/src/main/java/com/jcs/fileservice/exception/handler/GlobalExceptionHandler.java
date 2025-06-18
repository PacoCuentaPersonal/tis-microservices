package com.jcs.fileservice.exception.handler;

import com.jcs.fileservice.exception.thrower.ImageNotFoundException;
import com.jcs.fileservice.exception.thrower.ImageProcessingException;
import com.jcs.fileservice.exception.thrower.InvalidImageException;
import com.jcs.response.EnvelopeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<EnvelopeResponse<Void>> handleImageNotFound(ImageNotFoundException ex) {
        log.error("Image not found: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(EnvelopeResponse.error(
                        "Image not found",
                        List.of(ex.getMessage())
                ));
    }

    @ExceptionHandler(InvalidImageException.class)
    public ResponseEntity<EnvelopeResponse<Void>> handleInvalidImage(InvalidImageException ex) {
        log.error("Invalid image: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(EnvelopeResponse.error(
                        "Invalid image",
                        List.of(ex.getMessage())
                ));
    }

    @ExceptionHandler(ImageProcessingException.class)
    public ResponseEntity<EnvelopeResponse<Void>> handleProcessingError(ImageProcessingException ex) {
        log.error("Image processing error", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(EnvelopeResponse.error(
                        "Failed to process image",
                        List.of(ex.getMessage() != null ? ex.getMessage() : "Image processing failed")
                ));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<EnvelopeResponse<Void>> handleMaxSizeExceeded(MaxUploadSizeExceededException ex) {
        log.error("File size exceeded. Max allowed: {}", ex.getMaxUploadSize());

        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(EnvelopeResponse.error(
                        "File too large",
                        List.of(String.format("File size exceeds maximum allowed size of %d bytes", ex.getMaxUploadSize()))
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<EnvelopeResponse<Void>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(fieldName + ": " + errorMessage);
        });

        log.error("Validation failed: {}", errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(EnvelopeResponse.error("Validation failed", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<EnvelopeResponse<Void>> handleGenericError(Exception ex) {
        log.error("Unexpected error", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(EnvelopeResponse.error(
                        "An unexpected error occurred",
                        List.of("Please try again later")
                ));
    }
}
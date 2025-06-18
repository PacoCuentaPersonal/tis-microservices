package com.jcs.fileservice.exception.thrower;

public class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException(String imageId) {
        super("Image not found: " + imageId);
    }
}
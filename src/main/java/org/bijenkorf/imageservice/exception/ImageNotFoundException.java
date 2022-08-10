package org.bijenkorf.imageservice.exception;

public class ImageNotFoundException extends RuntimeException{
    public ImageNotFoundException(final String message) {
        super(message);
    }
}

package org.bijenkorf.imageservice.exception;

public class SourceNotReachableException extends RuntimeException{
    public SourceNotReachableException(final String message) {
        super(message);
    }
}

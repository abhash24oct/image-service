package org.bijenkorf.imageservice.exception;

public class AwsException extends RuntimeException{
    public AwsException(final String message) {
        super(message);
    }
}

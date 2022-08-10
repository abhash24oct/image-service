package org.bijenkorf.imageservice.exception;

import java.util.Date;

public class ErrorMessage {

    private int statusCode;
    private Date timestamp;
    private String message;

    public ErrorMessage(final int statusCode, final Date timestamp,
                        final String message) {
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }
}

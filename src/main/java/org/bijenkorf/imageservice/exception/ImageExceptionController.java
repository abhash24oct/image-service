package org.bijenkorf.imageservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice
public class ImageExceptionController {

    @ExceptionHandler({ImageNotFoundException.class, AwsException.class,
            SourceNotReachableException.class, GenericException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage handleProductImportException(final Exception exception) {
        return new ErrorMessage(HttpStatus.NOT_FOUND.value(),
                new Date(),
                exception.getMessage());
    }
}

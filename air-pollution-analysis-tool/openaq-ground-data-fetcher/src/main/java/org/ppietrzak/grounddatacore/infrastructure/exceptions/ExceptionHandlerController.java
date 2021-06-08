package org.ppietrzak.grounddatacore.infrastructure.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerController
        extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exception(Exception ex) {
        log.error("Exception occured: ", ex);
        ExceptionBody exceptionBody = ExceptionBody.builder()
                .message(ex.getMessage())
                .cause(Optional.ofNullable(ex.getCause()).map(Throwable::getMessage).orElse(""))
                .stackTrace(Arrays.asList(ex.getStackTrace()).stream()
                        .map(stackElement -> MessageFormat.format("[File: {0}, Line: {1}]: {2}#{3}",
                                stackElement.getFileName(), stackElement.getLineNumber(), stackElement.getClassName(), stackElement.getMethodName()))
                        .collect(Collectors.toList()))
                .build();
        return ResponseEntity.of(Optional.of(exceptionBody));
    }
}

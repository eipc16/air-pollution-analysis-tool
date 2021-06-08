package org.ppietrzak.grounddatacore.infrastructure.exceptions;

import java.text.MessageFormat;

public class StationsReindexationFailureException extends RuntimeException {

    private StationsReindexationFailureException() {
        // empty
    }

    public StationsReindexationFailureException(String sourceName, Exception cause) {
        super(MessageFormat.format("Failed initializing indexation of source: {0}", sourceName), cause);
    }

    public StationsReindexationFailureException(String sourceName, String cause) {
        super(MessageFormat.format("Failed initializing indexation of source: {0}. Cause: {0}", sourceName, cause));
    }
}

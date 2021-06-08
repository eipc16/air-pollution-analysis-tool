package org.ppietrzak.grounddatacore.infrastructure.exceptions;

import java.text.MessageFormat;

public class StationsReindexationStatusCheckFailureException extends RuntimeException {

    private String sourceName;

    private StationsReindexationStatusCheckFailureException() {
        // empty
    }

    public StationsReindexationStatusCheckFailureException(String sourceName, Exception exception) {
        super(MessageFormat.format("Failed to check status of indexation for source {0}", sourceName), exception);
        this.sourceName = sourceName;
    }
}

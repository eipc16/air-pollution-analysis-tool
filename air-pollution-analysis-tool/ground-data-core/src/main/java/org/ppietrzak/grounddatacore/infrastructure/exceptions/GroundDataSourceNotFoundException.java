package org.ppietrzak.grounddatacore.infrastructure.exceptions;

import java.text.MessageFormat;

public class GroundDataSourceNotFoundException extends RuntimeException {

    private String sourceName;

    private GroundDataSourceNotFoundException() {
        // empty
    }

    public GroundDataSourceNotFoundException(String sourceName) {
        super(MessageFormat.format("Ground data source with name {0} not found.", sourceName));
        this.sourceName = sourceName;
    }
}

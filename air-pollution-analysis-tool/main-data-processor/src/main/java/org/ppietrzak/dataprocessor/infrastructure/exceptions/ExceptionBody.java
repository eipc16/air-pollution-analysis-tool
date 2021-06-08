package org.ppietrzak.dataprocessor.infrastructure.exceptions;


import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
class ExceptionBody {
    private final String message;
    private final String cause;
    private final List<String> stackTrace;
}

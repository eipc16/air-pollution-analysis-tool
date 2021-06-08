package org.ppietrzak.dataprocessor.config.graphql.types.scalars;

import graphql.language.StringValue;
import graphql.schema.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Configuration
public class DateTimeScalarConfiguration {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Bean
    public GraphQLScalarType dateTimeScalar() {
        return GraphQLScalarType.newScalar()
                .name("OffsetDateTime")
                .description("Java 8 OffsetDateTime as scalar")
                .coercing(new Coercing<OffsetDateTime, String>() {

                    @Override
                    public String serialize(Object o) throws CoercingSerializeException {
                        if (o instanceof OffsetDateTime) {
                            return ((OffsetDateTime) o).format(DATETIME_FORMATTER);
                        }
                        throw new CoercingSerializeException("Expected a LocalDateTime object");
                    }

                    @Override
                    public OffsetDateTime parseValue(Object o) throws CoercingParseValueException {
                        try {
                            if(o instanceof String) {
                                return OffsetDateTime.parse((String) o, DATETIME_FORMATTER);
                            }
                            throw new CoercingParseValueException("Expected a String");
                        } catch (DateTimeParseException exception) {
                            throw new CoercingParseValueException(String.format("Not a valid datetime: '%s'.", o), exception);
                        }
                    }

                    @Override
                    public OffsetDateTime parseLiteral(Object o) throws CoercingParseLiteralException {
                        try {
                            if(o instanceof StringValue) {
                                String value = ((StringValue) o).getValue();
                                return OffsetDateTime.parse(value, DATETIME_FORMATTER);
                            }
                            throw new CoercingParseValueException("Expected a StringValue");
                        } catch (DateTimeParseException exception) {
                            throw new CoercingParseValueException(String.format("Not a valid datetime: '%s'.", o), exception);
                        }
                    }
                })
                .build();
    }
}

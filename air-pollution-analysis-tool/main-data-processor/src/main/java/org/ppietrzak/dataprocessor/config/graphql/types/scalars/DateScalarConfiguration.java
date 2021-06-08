package org.ppietrzak.dataprocessor.config.graphql.types.scalars;

import graphql.language.StringValue;
import graphql.schema.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Configuration
public class DateScalarConfiguration {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Bean
    public GraphQLScalarType dateScalar() {
        return GraphQLScalarType.newScalar()
                .name("Date")
                .description("Java 8 Date as scalar")
                .coercing(new Coercing<LocalDate, String>() {

                    @Override
                    public String serialize(Object o) throws CoercingSerializeException {
                        if (o instanceof LocalDate) {
                            return ((LocalDate) o).format(DATETIME_FORMATTER);
                        }
                        throw new CoercingSerializeException("Expected a LocalDate object");
                    }

                    @Override
                    public LocalDate parseValue(Object o) throws CoercingParseValueException {
                        try {
                            if (o instanceof String) {
                                return LocalDate.parse((String) o, DATETIME_FORMATTER);
                            }
                            throw new CoercingParseValueException("Expected a String");
                        } catch (DateTimeParseException exception) {
                            throw new CoercingParseValueException(String.format("Not a valid date: '%s'.", o), exception);
                        }
                    }

                    @Override
                    public LocalDate parseLiteral(Object o) throws CoercingParseLiteralException {
                        try {
                            if (o instanceof StringValue) {
                                String value = ((StringValue) o).getValue();
                                return LocalDate.parse(value, DATETIME_FORMATTER);
                            }
                            throw new CoercingParseValueException("Expected a StringValue");
                        } catch (DateTimeParseException exception) {
                            throw new CoercingParseValueException(String.format("Not a valid date: '%s'.", o), exception);
                        }
                    }
                })
                .build();
    }
}

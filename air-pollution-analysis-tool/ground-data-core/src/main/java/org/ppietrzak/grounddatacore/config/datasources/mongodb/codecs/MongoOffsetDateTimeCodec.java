package org.ppietrzak.grounddatacore.config.datasources.mongodb.codecs;

import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.springframework.core.convert.converter.Converter;

import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class MongoOffsetDateTimeCodec implements Codec<OffsetDateTime> {

    @Override
    public void encode(
            BsonWriter writer,
            OffsetDateTime value,
            EncoderContext encoderContext) {

        requireNonNull(writer, "writer is null");
        requireNonNull(value, "value is null");
        writer.writeString(value.toString());
    }

    @Override
    public OffsetDateTime decode(
            BsonReader reader,
            DecoderContext decoderContext) {

        requireNonNull(reader, "reader is null");
        return handleDecodeExceptions(
                reader::readString,
                OffsetDateTime::parse
        );
    }

    private static <Value, Result> Result handleDecodeExceptions(
            Supplier<Value> valueSupplier,
            Function<Value, Result> valueConverter) {

        Value value = valueSupplier.get();
        try {
            return valueConverter.apply(value);
        }
        catch (ArithmeticException |
                DateTimeException |
                IllegalArgumentException ex) {

            throw new BsonInvalidOperationException(String.format(
                    "The value %s is not supported", value
            ), ex);
        }
    }

    @Override
    public Class<OffsetDateTime> getEncoderClass() {
        return OffsetDateTime.class;
    }

    public static StringToOffsetDateTimeConverter getConverter() {
        return new StringToOffsetDateTimeConverter();
    }

    private static final class StringToOffsetDateTimeConverter implements Converter<String, OffsetDateTime> {
        @Override
        public OffsetDateTime convert(String source) {
            return source == null ? null : OffsetDateTime.parse(source);
        }
    }
}

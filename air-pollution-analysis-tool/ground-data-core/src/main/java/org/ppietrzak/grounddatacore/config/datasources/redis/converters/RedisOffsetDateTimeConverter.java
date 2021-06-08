package org.ppietrzak.grounddatacore.config.datasources.redis.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Component
public class RedisOffsetDateTimeConverter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public List<Converter<?, ?>> getConverters() {
        return Arrays.asList(new ReadingConverter(), new WritingConverter());
    }

    @org.springframework.data.convert.ReadingConverter
    public static class ReadingConverter implements Converter<byte[], OffsetDateTime> {
        @Override
        public OffsetDateTime convert(final byte[] source) {
            return OffsetDateTime.parse(new String(source), DATE_TIME_FORMATTER);
        }
    }

    @org.springframework.data.convert.WritingConverter
    public static class WritingConverter implements Converter<OffsetDateTime, byte[]> {
        @Override
        public byte[] convert(OffsetDateTime offsetDateTime) {
            return offsetDateTime.format(DATE_TIME_FORMATTER).getBytes();
        }
    }
}
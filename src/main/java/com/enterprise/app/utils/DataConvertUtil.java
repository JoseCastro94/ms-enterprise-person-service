package com.enterprise.app.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.function.Function;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DataConvertUtil {
    private static final DateTimeFormatter[] LOCAL_DATE_TIME_FORMATTERS = {
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        DateTimeFormatter.ofPattern("d/M/yyyy H:mm:ss", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("d/M/yyyy h:mm:ss a", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("dd-MM-yyyy H:mm:ss", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm:ss", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("yyyy/MM/dd H:mm:ss", Locale.ENGLISH)
    };
    private static final DateTimeFormatter[] LOCAL_DATE_FORMATTERS = {
        DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("yyyy/MM/dd", Locale.ENGLISH)
    };

    public static String normalize(String value) {
        return value == null ? null : value.trim();
    }

    public static String toUpperCase(String value) {
        String normalized = normalize(value);
        return normalized == null ? null : normalized.toUpperCase(Locale.ROOT);
    }

    public static String trimToNull(String value) {
        String normalized = normalize(value);
        return normalized == null || normalized.isEmpty() ? null : normalized;
    }

    public static Integer toInteger(String value) {
        return Integer.valueOf(normalize(value));
    }

    public static Long toLong(String value) {
        return Long.valueOf(normalize(value));
    }

    public static BigDecimal toBigDecimal(String value) {
        String normalized = normalize(value).replace(" ", "");
        if (normalized.contains(",") && normalized.contains(".")) {
            normalized = normalized.lastIndexOf(',') > normalized.lastIndexOf('.')
                    ? normalized.replace(".", "").replace(',', '.')
                    : normalized.replace(",", "");
        } else if (normalized.contains(",")) {
            normalized = normalized.replace(',', '.');
        }
        return new BigDecimal(normalized);
    }

    public static Double toDouble(String value) {
        return Double.valueOf(normalize(value));
    }

    public static LocalDateTime toLocalDateTime(String value) {
        String normalized = normalize(value);
        try {
            return OffsetDateTime.parse(normalized, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();
        } catch (DateTimeParseException ignored) {
        }
        for (DateTimeFormatter formatter : LOCAL_DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(normalized, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        for (DateTimeFormatter formatter : LOCAL_DATE_FORMATTERS) {
            try {
                return LocalDate.parse(normalized, formatter).atStartOfDay();
            } catch (DateTimeParseException ignored) {
            }
        }
        throw new DateTimeParseException("Formato de fecha no soportado.", normalized, 0);
    }

    public static <T> T toEnum(String value, Function<String, T> converter) {
        return converter.apply(normalize(value));
    }
}

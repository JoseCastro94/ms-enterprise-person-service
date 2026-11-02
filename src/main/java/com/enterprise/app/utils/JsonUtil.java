package com.enterprise.app.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class JsonUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String serializar(Object valor) {
        try {
            return OBJECT_MAPPER.writeValueAsString(valor);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("No se pudo serializar el valor JSON.", exception);
        }
    }

    public static <T> T deserializar(String json, TypeReference<T> type) {
        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("No se pudo deserializar el valor JSON.", exception);
        }
    }
}

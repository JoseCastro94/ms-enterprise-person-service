package com.enterprise.app.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class JsonUtil {
    private final ObjectMapper objectMapper;

    @Inject
    public JsonUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String serializar(List<String> valores) {
        try {
            return objectMapper.writeValueAsString(valores);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("No se pudo serializar la lista de documentos.", exception);
        }
    }
}

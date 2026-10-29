package com.enterprise.app.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Genero {
    MASCULINO("M"),
    FEMENINO("F"),
    UNSUPPORTED("UNSUPPORTED");

    @JsonValue
    private final String value;

    @JsonCreator
    public static Genero fromValue(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(genero -> genero != UNSUPPORTED)
                .filter(genero -> genero.value.equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElse(UNSUPPORTED);
    }
}

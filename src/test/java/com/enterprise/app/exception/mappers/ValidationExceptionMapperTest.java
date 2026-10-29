package com.enterprise.app.exception.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.enterprise.app.exception.responses.ErrorResponse;
import com.enterprise.app.model.dto.PersonaRequestDto;
import io.quarkus.hibernate.validator.runtime.jaxrs.ResteasyReactiveViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ValidationExceptionMapperTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldReturnStandardErrorResponseForInvalidRequest() {
        PersonaRequestDto request = PersonaRequestDto.builder()
                .numeroDocumento("000351461")
                .build();
        var violations = validator.validate(request);
        var exception = new ResteasyReactiveViolationException(Set.copyOf(violations));

        var response = new ValidationExceptionMapper().toResponse(exception);

        assertEquals(400, response.getStatus());
        ErrorResponse body = (ErrorResponse) response.getEntity();
        assertEquals(400, body.getStatus());
        assertEquals("BAD_REQUEST", body.getCode());
        assertEquals("La solicitud contiene campos inválidos.", body.getMessage());
        assertEquals("idTipoDocumento", body.getErrors().getFirst().getField());
        assertEquals("El tipo de documento es obligatorio.", body.getErrors().getFirst().getMessage());
    }
}

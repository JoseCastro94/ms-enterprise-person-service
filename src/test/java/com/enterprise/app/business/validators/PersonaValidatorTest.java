package com.enterprise.app.business.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.enterprise.app.model.dto.PersonaDto;
import com.enterprise.app.model.enums.Genero;
import jakarta.validation.Validation;
import java.util.List;
import org.junit.jupiter.api.Test;

class PersonaValidatorTest {
    private final PersonaValidator service =
            new PersonaValidator(Validation.buildDefaultValidatorFactory().getValidator());

    @Test
    void shouldKeepFirstDuplicateAndRejectSubsequentRow() {
        var resultado = service.validar(List.of(persona("12345678", 2), persona(" 12345678 ", 3)), "tx-test");

        assertEquals(1, resultado.getPersonasValidas().size());
        assertEquals(1, resultado.getDuplicadosArchivo());
        assertEquals(3, resultado.getErrores().getFirst().getFila());
        assertEquals("DUPLICATE_IN_FILE", resultado.getErrores().getFirst().getCode());
    }

    @Test
    void shouldRejectMissingRequiredValues() {
        PersonaDto persona = persona("", 2);
        persona.setNombres(null);

        var resultado = service.validar(List.of(persona), "tx-test");

        assertEquals(0, resultado.getPersonasValidas().size());
        assertEquals(2, resultado.getErrores().size());
    }

    @Test
    void shouldReportUnsupportedGenderAgainstGeneroField() {
        PersonaDto persona = persona("12345678", 2);
        persona.setGenero(Genero.UNSUPPORTED);

        var resultado = service.validar(List.of(persona), "tx-test");

        assertEquals(1, resultado.getErrores().size());
        assertEquals("genero", resultado.getErrores().getFirst().getField());
        assertEquals("El campo genero debe ser M o F.", resultado.getErrores().getFirst().getMessage());
    }

    private PersonaDto persona(String documento, int fila) {
        return PersonaDto.builder()
                .idTipoDocumento(1L)
                .idEstadoCivil(1L)
                .idDistrito("010101")
                .numeroDocumento(documento)
                .nombres("Ana")
                .apellidoPaterno("Pérez")
                .genero(Genero.fromValue("F"))
                .numeroFila(fila)
                .build();
    }
}

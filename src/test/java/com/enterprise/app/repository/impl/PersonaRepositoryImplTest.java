package com.enterprise.app.repository.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static com.enterprise.app.repository.queries.PersonaQueryTemplate.INSERTAR_PERSONAS;
import com.enterprise.app.model.entity.PersonaEntity;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class PersonaRepositoryImplTest {
    @Test
    void shouldUseOnePreparedInsertStatementForBatchExecution() {
        assertTrue(INSERTAR_PERSONAS.contains("VALUES (@p1, @p2, @p3"));
        assertTrue(INSERTAR_PERSONAS.contains("@p13)"));
        assertFalse(INSERTAR_PERSONAS.contains("OPENJSON"));
    }

    @Test
    void shouldMapAllPersonaColumnsToPreparedQueryTuple() {
        PersonaEntity persona = PersonaEntity.builder()
                .idTipoDocumento(1L)
                .idEstadoCivil(2L)
                .idDistrito("010101")
                .numeroDocumento("12345678")
                .nombres("Ana")
                .apellidoPaterno("Pérez")
                .apellidoMaterno("López")
                .fechaNacimiento(LocalDate.of(1990, 5, 20))
                .genero("F")
                .correo("ana@example.com")
                .telefono("999888777")
                .direccion("Av. Principal")
                .estado(true)
                .build();

        var tuple = PersonaRepositoryImpl.crearTupla(persona);

        assertEquals(13, tuple.size());
        assertEquals(1L, tuple.getLong(0));
        assertEquals("12345678", tuple.getString(3));
        assertEquals("F", tuple.getString(8));
        assertEquals(Boolean.TRUE, tuple.getBoolean(12));
    }
}

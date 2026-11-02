package com.enterprise.app.business.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.enterprise.app.mapper.PersonaMapper;
import com.enterprise.app.business.validators.PersonaValidator;
import com.enterprise.app.model.dto.PersonaDto;
import com.enterprise.app.model.entity.PersonaEntity;
import com.enterprise.app.model.enums.Genero;
import com.enterprise.app.repository.PersonaRepository;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Validation;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class PersonaServiceImplTest {
    @Test
    void shouldQueryExistingDocumentsOnceAndDelegateOnlyNewPeopleToRepositoryBatch() {
        AtomicInteger consultasExistencia = new AtomicInteger();
        AtomicInteger llamadasInsercion = new AtomicInteger();
        AtomicInteger personasParaInsertar = new AtomicInteger();
        PersonaRepository repository = new PersonaRepository() {
            @Override
            public Uni<PersonaEntity> obtenerPorDocumento(Long idTipoDocumento, String numeroDocumento) {
                return Uni.createFrom().nullItem();
            }

            @Override
            public Uni<java.util.Set<String>> obtenerDocumentosExistentes(String documentosJson) {
                consultasExistencia.incrementAndGet();
                assertEquals(250, documentosJson.split(",").length);
                return Uni.createFrom().item(java.util.Set.of("DOC-1"));
            }

            @Override
            public Uni<Integer> guardarPersonasPorLote(List<PersonaEntity> personas) {
                llamadasInsercion.incrementAndGet();
                personasParaInsertar.set(personas.size());
                return Uni.createFrom().item(personas.size());
            }

            @Override
            public Uni<Integer> actualizar(PersonaEntity persona) {
                return Uni.createFrom().item(1);
            }
        };
        PersonaServiceImpl service = new PersonaServiceImpl(
                repository,
                new PersonaValidator(Validation.buildDefaultValidatorFactory().getValidator()),
                new PersonaMapper());
        List<PersonaDto> personas = java.util.stream.IntStream.range(0, 250)
                .mapToObj(index -> persona("DOC-" + index, index + 2))
                .toList();

        var resumen = service.procesarCargaMasiva(personas, "tx-test").await().indefinitely();

        assertEquals(1, consultasExistencia.get());
        assertEquals(1, llamadasInsercion.get());
        assertEquals(249, personasParaInsertar.get());
        assertEquals(249, resumen.getInsertados());
        assertEquals(1, resumen.getExistentes());
    }

    private PersonaDto persona(String numeroDocumento, int fila) {
        return PersonaDto.builder()
                .idTipoDocumento(1L)
                .idEstadoCivil(1L)
                .idDistrito("010101")
                .numeroDocumento(numeroDocumento)
                .nombres("Persona")
                .apellidoPaterno("Prueba")
                .genero(Genero.fromValue("M"))
                .numeroFila(fila)
                .build();
    }
}

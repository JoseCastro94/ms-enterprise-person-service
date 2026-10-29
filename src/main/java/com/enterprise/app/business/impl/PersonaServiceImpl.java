package com.enterprise.app.business.impl;

import com.enterprise.app.business.PersonaService;
import com.enterprise.app.business.validators.PersonaValidator;
import com.enterprise.app.exception.codes.ErrorCodes;
import com.enterprise.app.exception.exceptions.BusinessException;
import com.enterprise.app.mapper.PersonaMapper;
import com.enterprise.app.model.dto.PersonaDto;
import com.enterprise.app.model.dto.PersonaRequestDto;
import com.enterprise.app.model.dto.PersonaResponseDto;
import com.enterprise.app.model.dto.ResumenCargaPersonas;
import com.enterprise.app.model.dto.ResultadoValidacionPersonas;
import com.enterprise.app.model.entity.PersonaEntity;
import com.enterprise.app.repository.PersonaRepository;
import com.enterprise.app.utils.JsonUtil;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class PersonaServiceImpl implements PersonaService {
    private final PersonaRepository repository;
    private final PersonaValidator personaValidator;
    private final PersonaMapper personaMapper;
    private final JsonUtil jsonUtil;

    @Inject
    public PersonaServiceImpl(
            PersonaRepository repository,
            PersonaValidator personaValidator,
            PersonaMapper personaMapper,
            JsonUtil jsonUtil) {
        this.repository = repository;
        this.personaValidator = personaValidator;
        this.personaMapper = personaMapper;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public Uni<PersonaResponseDto> obtenerPorDocumento(PersonaRequestDto request, String transactionId) {
        log.info("Inicio de consulta de persona. transactionId={}, idTipoDocumento={}, numeroDocumento={}",
                transactionId, request.getIdTipoDocumento(), request.getNumeroDocumento());

        return repository.obtenerPorDocumento(
                        request.getIdTipoDocumento(), request.getNumeroDocumento().trim())
                .onItem().ifNull().failWith(() ->
                        new BusinessException("No existe una persona con los datos indicados.", ErrorCodes.NOT_FOUND))
                .map(personaMapper::toResponse)
                .invoke(persona -> log.info(
                        "Consulta de persona finalizada. transactionId={}, idPersona={}",
                        transactionId, persona.getIdPersona()))
                .onFailure().invoke(exception ->
                        log.error("Error al consultar persona. transactionId={}", transactionId, exception));
    }

    @Override
    public Uni<ResumenCargaPersonas> procesarCargaMasiva(List<PersonaDto> personas, String transactionId) {
        long inicio = System.nanoTime();
        log.info("Inicio de procesamiento de carga masiva. transactionId={}, leídos={}",
                transactionId, personas.size());

        return Uni.createFrom().item(() -> personaValidator.validar(personas, transactionId))
                .flatMap(resultado -> procesarPersonasValidadas(personas, resultado, transactionId))
                .invoke(resumen -> log.info(
                        "Carga masiva finalizada. transactionId={}, procesados={}, insertados={}, existentes={}, duplicados={}, rechazados={}",
                        transactionId, resumen.getTotalProcesados(), resumen.getInsertados(),
                        resumen.getExistentes(), resumen.getDuplicadosArchivo(), resumen.getRechazados()))
                .onFailure().invoke(exception ->
                        log.error("Error en procesamiento de carga masiva. transactionId={}", transactionId, exception))
                .onTermination().invoke(() -> log.info(
                        "Tiempo de procesamiento de carga masiva. transactionId={}, duraciónMs={}",
                        transactionId, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - inicio)));
    }

    private Uni<ResumenCargaPersonas> procesarPersonasValidadas(
            List<PersonaDto> personas,
            ResultadoValidacionPersonas resultado,
            String transactionId) {
        List<PersonaDto> validas = resultado.getPersonasValidas();
        List<String> documentos = validas.stream()
                .map(PersonaDto::getNumeroDocumento)
                .distinct()
                .toList();

        return consultarDocumentosExistentes(documentos, transactionId)
                .map(existentes -> filtrarPersonasNuevas(validas, existentes))
                .flatMap(nuevas -> guardarPersonasNuevas(nuevas, transactionId)
                        .map(insertados -> construirResumen(personas, resultado, validas.size(), nuevas.size(),
                                insertados)));
    }

    private Uni<Set<String>> consultarDocumentosExistentes(List<String> documentos, String transactionId) {
        if (documentos.isEmpty()) {
            return Uni.createFrom().item(Set.of());
        }

        log.info("Consulta masiva de documentos iniciada. transactionId={}, documentos={}",
                transactionId, documentos.size());
        return Uni.createFrom().item(() -> jsonUtil.serializar(documentos))
                .flatMap(repository::obtenerDocumentosExistentes)
                .map(documentosEncontrados -> documentosEncontrados.stream()
                        .map(documento -> documento.trim().toUpperCase(Locale.ROOT))
                        .collect(Collectors.toUnmodifiableSet()))
                .invoke(existentes -> log.info(
                        "Consulta masiva de documentos finalizada. transactionId={}, existentes={}",
                        transactionId, existentes.size()));
    }

    private List<PersonaDto> filtrarPersonasNuevas(List<PersonaDto> validas, Set<String> existentes) {
        return validas.stream()
                .filter(persona -> !existentes.contains(
                        persona.getNumeroDocumento().trim().toUpperCase(Locale.ROOT)))
                .toList();
    }

    private Uni<Integer> guardarPersonasNuevas(List<PersonaDto> nuevas, String transactionId) {
        List<PersonaEntity> entidades = nuevas.stream()
                .map(personaMapper::toEntity)
                .toList();
        log.info("Filtrado masivo completado. transactionId={}, nuevos={}", transactionId, entidades.size());
        return repository.guardarPersonasPorLote(entidades)
                .invoke(insertados -> log.info(
                        "Inserción masiva finalizada. transactionId={}, insertados={}",
                        transactionId, insertados));
    }

    private ResumenCargaPersonas construirResumen(
            List<PersonaDto> personas,
            ResultadoValidacionPersonas resultado,
            int cantidadValidas,
            int cantidadNuevas,
            int insertados) {
        int existentes = cantidadValidas - cantidadNuevas;
        return ResumenCargaPersonas.builder()
                .totalProcesados(personas.size())
                .insertados(insertados)
                .existentes(existentes)
                .duplicadosArchivo(resultado.getDuplicadosArchivo())
                .rechazados((int) resultado.getErrores().stream()
                        .map(error -> error.getFila()).distinct().count())
                .errores(resultado.getErrores())
                .build();
    }
}

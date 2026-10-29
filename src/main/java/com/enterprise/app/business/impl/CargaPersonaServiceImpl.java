package com.enterprise.app.business.impl;

import com.enterprise.app.business.PersonaService;
import com.enterprise.app.business.CargaPersonaService;
import com.enterprise.app.business.validators.ArchivoPersonaValidator;
import com.enterprise.app.model.dto.PersonaDto;
import com.enterprise.app.model.dto.ResumenCargaPersonas;
import com.enterprise.app.utils.excel.ExcelPersonaExtractor;
import io.quarkus.vertx.VertxContextSupport;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.multipart.FileUpload;

@Slf4j
@ApplicationScoped
public class CargaPersonaServiceImpl implements CargaPersonaService {
    private final ExcelPersonaExtractor extractor;
    private final PersonaService personaService;
    private final ArchivoPersonaValidator archivoValidator;

    @Inject
    public CargaPersonaServiceImpl(
            ExcelPersonaExtractor extractor,
            PersonaService personaService,
            ArchivoPersonaValidator archivoValidator) {
        this.extractor = extractor;
        this.personaService = personaService;
        this.archivoValidator = archivoValidator;
    }

    public Uni<ResumenCargaPersonas> procesarCargaMasiva(FileUpload archivo, String transactionId) {
        return VertxContextSupport.executeBlocking(() -> extraerPersonas(archivo, transactionId))
                .flatMap(personas -> personaService.procesarCargaMasiva(personas, transactionId))
                .invoke(resumen -> log.info("Carga de personas finalizada. transactionId={}, archivo={}",
                        transactionId, archivo.fileName()))
                .onFailure().invoke(exception ->
                        log.error("Error al procesar archivo Excel. transactionId={}", transactionId, exception));
    }

    private List<PersonaDto> extraerPersonas(FileUpload archivo, String transactionId) {
        archivoValidator.validar(archivo);
        log.info("Inicio de carga masiva de personas. transactionId={}, tamañoBytes={}",
                transactionId, archivo.size());
        return extractor.extraer(archivo.uploadedFile(), transactionId);
    }
}

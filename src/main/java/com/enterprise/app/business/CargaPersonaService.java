package com.enterprise.app.business;

import com.enterprise.app.model.dto.ResumenCargaPersonas;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.multipart.FileUpload;

public interface CargaPersonaService {
    Uni<ResumenCargaPersonas> procesarCargaMasiva(FileUpload archivo, String transactionId);
}

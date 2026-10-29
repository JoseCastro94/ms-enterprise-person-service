package com.enterprise.app.business.validators;

import com.enterprise.app.config.ApplicationProperties;
import com.enterprise.app.exception.exceptions.BusinessException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.nio.file.Files;
import java.util.Locale;
import org.jboss.resteasy.reactive.multipart.FileUpload;

@ApplicationScoped
public class ArchivoPersonaValidator {
    private final ApplicationProperties properties;

    @Inject
    public ArchivoPersonaValidator(ApplicationProperties properties) {
        this.properties = properties;
    }

    public void validar(FileUpload archivo) {
        if (archivo == null) {
            throw new BusinessException("Debe adjuntar un archivo Excel en el campo archivo.");
        }

        String nombre = archivo.fileName() == null ? "" : archivo.fileName().toLowerCase(Locale.ROOT);

        if (!nombre.endsWith(".xlsx") && !nombre.endsWith(".xls")) {
            throw new BusinessException("El archivo debe tener extensión .xlsx o .xls.");
        }

        if (archivo.size() <= 0 || archivo.size() > properties.archivo().maximoBytes()) {
            throw new BusinessException("El archivo está vacío o supera el tamaño máximo permitido.");
        }

        if (!Files.isReadable(archivo.uploadedFile())) {
            throw new BusinessException("No se puede leer el archivo recibido.");
        }
    }
}

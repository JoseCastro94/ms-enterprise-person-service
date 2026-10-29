package com.enterprise.app.exception.mappers;

import static com.enterprise.app.constants.ConstantsApp.ZONA_LIMA;

import com.enterprise.app.context.TransactionLogContext;
import com.enterprise.app.exception.codes.ErrorCodes;
import com.enterprise.app.exception.responses.ErrorDetalle;
import com.enterprise.app.exception.responses.ErrorResponse;
import io.quarkus.hibernate.validator.runtime.jaxrs.ResteasyReactiveViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ResteasyReactiveViolationException> {
    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(ResteasyReactiveViolationException exception) {
        List<ErrorDetalle> errores = exception.getConstraintViolations().stream()
                .map(violacion -> ErrorDetalle.builder()
                        .field(nombreCampo(violacion.getPropertyPath().toString()))
                        .message(violacion.getMessage())
                        .build())
                .sorted(Comparator.comparing(ErrorDetalle::getField))
                .toList();

        String transactionId = TransactionLogContext.getOrCreateTransactionId();
        log.warn("Solicitud rechazada por validación. transactionId={}, errores={}",
                transactionId, errores.size());

        ErrorResponse body = ErrorResponse.builder()
                .timestamp(OffsetDateTime.now(ZONA_LIMA))
                .status(Response.Status.BAD_REQUEST.getStatusCode())
                .code(ErrorCodes.BAD_REQUEST.getValue())
                .message("La solicitud contiene campos inválidos.")
                .path(path())
                .errors(errores)
                .build();
        return Response.status(Response.Status.BAD_REQUEST).entity(body).build();
    }

    private String nombreCampo(String propertyPath) {
        int ultimoSeparador = propertyPath.lastIndexOf('.');
        return ultimoSeparador < 0 ? propertyPath : propertyPath.substring(ultimoSeparador + 1);
    }

    private String path() {
        String path = uriInfo == null ? null : uriInfo.getPath();
        if (path == null || path.isBlank() || path.startsWith("/")) {
            return path;
        }
        return "/" + path;
    }
}

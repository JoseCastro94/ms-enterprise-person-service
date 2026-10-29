package com.enterprise.app.exception.mappers;

import static com.enterprise.app.constants.ConstantsApp.ZONA_LIMA;

import com.enterprise.app.context.TransactionLogContext;
import com.enterprise.app.exception.codes.ErrorCodes;
import com.enterprise.app.exception.exceptions.BusinessException;
import com.enterprise.app.exception.responses.ErrorResponse;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {
    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Exception exception) {
        int status;
        String code;
        String message;
        if (exception instanceof BusinessException businessException) {
            ErrorCodes errorCode = businessException.getCode();
            status = switch (errorCode) {
                case BAD_REQUEST -> Response.Status.BAD_REQUEST.getStatusCode();
                case NOT_FOUND -> Response.Status.NOT_FOUND.getStatusCode();
                case ERROR -> Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
            };
            code = errorCode.getValue();
            message = businessException.getMessage();
        } else if (exception instanceof WebApplicationException webException) {
            status = webException.getResponse().getStatus();
            code = status >= 500 ? ErrorCodes.ERROR.getValue() : ErrorCodes.BAD_REQUEST.getValue();
            message = status == 404 ? "El recurso solicitado no existe." : "La solicitud no pudo procesarse.";
        } else {
            status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
            code = ErrorCodes.ERROR.getValue();
            message = "Ocurrió un error interno al procesar la solicitud.";
        }

        String transactionId = TransactionLogContext.getOrCreateTransactionId();
        if (status >= 500) {
            log.error("Error al procesar solicitud. transactionId={}", transactionId, exception);
        } else {
            log.warn("Solicitud rechazada. transactionId={}, status={}, code={}", transactionId, status, code);
        }
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(OffsetDateTime.now(ZONA_LIMA))
                .status(status)
                .code(code)
                .message(message)
                .path(path())
                .errors(List.of())
                .build();
        return Response.status(status).entity(body).build();
    }

    private String path() {
        String path = uriInfo == null ? null : uriInfo.getPath();
        if (path == null || path.isBlank() || path.startsWith("/")) {
            return path;
        }
        return "/" + path;
    }
}

package com.enterprise.app.resource;

import static com.enterprise.app.constants.ConstantsApp.ID_TRANSACTION;
import static com.enterprise.app.constants.ConstantsApp.ZONA_LIMA;

import com.enterprise.app.business.UbigeoService;
import com.enterprise.app.model.dto.DepartamentoDto;
import com.enterprise.app.model.dto.DistritoDto;
import com.enterprise.app.model.dto.ProvinciaDto;
import com.enterprise.app.model.dto.ResponseDto;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.OffsetDateTime;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/ubigeo")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Ubigeo", description = "Consultas de departamentos, provincias y distritos.")
public class UbigeoResource {
    private final UbigeoService ubigeoService;

    @Inject
    public UbigeoResource(UbigeoService ubigeoService) {
        this.ubigeoService = ubigeoService;
    }

    @GET
    @Path("/departamentos")
    @Operation(summary = "Listar departamentos")
    public Uni<Response> obtenerDepartamentos(@Context ContainerRequestContext requestContext) {
        String transactionId = transactionId(requestContext);
        return ubigeoService.obtenerDepartamentos(transactionId)
                .map(departamentos -> Response.ok(respuesta(
                        departamentos, "Departamentos consultados correctamente.")).build());
    }

    @GET
    @Path("/departamentos/{idDepartamento}/provincias")
    @Operation(summary = "Listar provincias por departamento")
    public Uni<Response> obtenerProvincias(
            @PathParam("idDepartamento") String idDepartamento,
            @Context ContainerRequestContext requestContext) {
        String transactionId = transactionId(requestContext);
        return ubigeoService.obtenerProvincias(idDepartamento, transactionId)
                .map(provincias -> Response.ok(respuesta(
                        provincias, "Provincias consultadas correctamente.")).build());
    }

    @GET
    @Path("/provincias/{idProvincia}/distritos")
    @Operation(summary = "Listar distritos por provincia")
    public Uni<Response> obtenerDistritos(
            @PathParam("idProvincia") String idProvincia,
            @Context ContainerRequestContext requestContext) {
        String transactionId = transactionId(requestContext);
        return ubigeoService.obtenerDistritos(idProvincia, transactionId)
                .map(distritos -> Response.ok(respuesta(
                        distritos, "Distritos consultados correctamente.")).build());
    }

    private static String transactionId(ContainerRequestContext requestContext) {
        Object id = requestContext.getProperty(ID_TRANSACTION);
        return id == null ? "unknown" : id.toString();
    }

    private static <T> ResponseDto<List<T>> respuesta(List<T> data, String message) {
        return ResponseDto.<List<T>>builder()
                .timestamp(OffsetDateTime.now(ZONA_LIMA))
                .status(Response.Status.OK.getStatusCode())
                .message(message)
                .data(data)
                .build();
    }
}

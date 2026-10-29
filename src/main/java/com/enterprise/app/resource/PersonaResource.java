package com.enterprise.app.resource;

import static com.enterprise.app.constants.ConstantsApp.ID_TRANSACTION;
import static com.enterprise.app.constants.ConstantsApp.ZONA_LIMA;

import com.enterprise.app.business.CargaPersonaService;
import com.enterprise.app.business.PersonaService;
import com.enterprise.app.model.dto.PersonaRequestDto;
import com.enterprise.app.model.dto.PersonaResponseDto;
import com.enterprise.app.model.dto.ResumenCargaPersonas;
import com.enterprise.app.model.dto.ResponseDto;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.OffsetDateTime;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

@Path("/api/v1/personas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Personas", description = "Consulta de personas y carga masiva desde Excel.")
public class PersonaResource {
    private final PersonaService personaService;
    private final CargaPersonaService cargaService;

    @Inject
    public PersonaResource(PersonaService personaService, CargaPersonaService cargaService) {
        this.personaService = personaService;
        this.cargaService = cargaService;
    }

    @POST
    @Path("/consultar")
    @Operation(summary = "Consultar persona por tipo y número de documento")
    @APIResponse(responseCode = "200", description = "Persona encontrada.",
            content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    @APIResponse(responseCode = "400", description = "Solicitud inválida.")
    @APIResponse(responseCode = "404", description = "Persona no encontrada.")
    public Uni<Response> obtenerPorDocumento(
            @Valid PersonaRequestDto request,
            @Context ContainerRequestContext requestContext) {
        Object id = requestContext.getProperty(ID_TRANSACTION);
        String transactionId = id == null ? "unknown" : id.toString();
        return personaService.obtenerPorDocumento(request, transactionId)
                .map(persona -> Response.ok(ResponseDto.<PersonaResponseDto>builder()
                        .timestamp(OffsetDateTime.now(ZONA_LIMA))
                        .status(Response.Status.OK.getStatusCode())
                        .message("Persona consultada correctamente.")
                        .data(persona)
                        .build()).build());
    }

    @POST
    @Path("/carga-masiva")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(summary = "Procesar carga masiva de personas desde Excel")
    @APIResponse(responseCode = "202", description = "Carga procesada.",
            content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    @APIResponse(responseCode = "400", description = "Archivo o estructura inválida.")
    @APIResponse(responseCode = "500", description = "Error al procesar la carga.")
    public Uni<Response> procesarCargaMasiva(
            @RestForm("archivo") FileUpload archivo,
            @Context ContainerRequestContext requestContext) {
                
        Object id = requestContext.getProperty(ID_TRANSACTION);
        String transactionId = id == null ? "unknown" : id.toString();

        return cargaService.procesarCargaMasiva(archivo, transactionId)
                .map(resumen -> Response.accepted(ResponseDto.<ResumenCargaPersonas>builder()
                        .timestamp(OffsetDateTime.now(ZONA_LIMA))
                        .status(Response.Status.ACCEPTED.getStatusCode())
                        .message("Carga procesada correctamente.")
                        .data(resumen)
                        .build()).build());
    }
}

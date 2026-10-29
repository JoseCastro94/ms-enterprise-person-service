package com.enterprise.app.mapper;

import com.enterprise.app.model.dto.PersonaDto;
import com.enterprise.app.model.dto.PersonaResponseDto;
import com.enterprise.app.model.entity.PersonaEntity;
import com.enterprise.app.model.enums.Genero;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PersonaMapper {
    public PersonaResponseDto toResponse(PersonaEntity persona) {
        return PersonaResponseDto.builder()
                .idPersona(persona.getIdPersona())
                .tipoDocumento(persona.getTipoDocumento().getCodigo())
                .descripcionTipoDocumento(persona.getTipoDocumento().getDescripcion())
                .numeroDocumento(persona.getNumeroDocumento())
                .nombres(persona.getNombres())
                .apellidoPaterno(persona.getApellidoPaterno())
                .apellidoMaterno(persona.getApellidoMaterno())
                .fechaNacimiento(persona.getFechaNacimiento())
                .genero(genero(persona.getGenero()))
                .correo(persona.getCorreo())
                .telefono(persona.getTelefono())
                .direccion(persona.getDireccion())
                .estadoCivil(persona.getEstadoCivil().getDescripcion())
                .departamento(persona.getDistrito().getDepartamento().getNombre())
                .provincia(persona.getDistrito().getProvincia().getNombre())
                .distrito(persona.getDistrito().getNombre())
                .estado(persona.getEstado())
                .fechaRegistro(persona.getFechaRegistro())
                .build();
    }

    public PersonaEntity toEntity(PersonaDto persona) {
        return PersonaEntity.builder()
                .idTipoDocumento(persona.getIdTipoDocumento())
                .idEstadoCivil(persona.getIdEstadoCivil())
                .idDistrito(persona.getIdDistrito())
                .numeroDocumento(persona.getNumeroDocumento())
                .nombres(persona.getNombres())
                .apellidoPaterno(persona.getApellidoPaterno())
                .apellidoMaterno(persona.getApellidoMaterno())
                .fechaNacimiento(persona.getFechaNacimiento())
                .genero(persona.getGenero() == null ? null : persona.getGenero().getValue())
                .correo(persona.getCorreo())
                .telefono(persona.getTelefono())
                .direccion(persona.getDireccion())
                .estado(persona.getEstado() == null || persona.getEstado())
                .build();
    }

    private String genero(String genero) {
        if (genero == null) {
            return null;
        }
        return switch (Genero.fromValue(genero)) {
            case MASCULINO -> "Masculino";
            case FEMENINO -> "Femenino";
            case UNSUPPORTED -> genero;
        };
    }
}

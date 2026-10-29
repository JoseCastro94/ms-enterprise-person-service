package com.enterprise.app.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonaResponseDto {
    private Long idPersona;
    private String tipoDocumento;
    private String descripcionTipoDocumento;
    private String numeroDocumento;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private LocalDate fechaNacimiento;
    private String genero;
    private String correo;
    private String telefono;
    private String direccion;
    private String estadoCivil;
    private String departamento;
    private String provincia;
    private String distrito;
    private Boolean estado;
    private LocalDateTime fechaRegistro;
}

package com.enterprise.app.model.dto;

import com.enterprise.app.model.enums.Genero;
import com.enterprise.app.business.validators.GeneroValido;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
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
public class PersonaDto {
    @NotNull(message = "El campo idTipoDocumento es obligatorio.")
    @Positive(message = "El campo idTipoDocumento debe ser mayor que cero.")
    private Long idTipoDocumento;

    @NotNull(message = "El campo idEstadoCivil es obligatorio.")
    @Positive(message = "El campo idEstadoCivil debe ser mayor que cero.")
    private Long idEstadoCivil;

    @NotBlank(message = "El campo idDistrito es obligatorio.")
    @Pattern(regexp = "[A-Za-z0-9]{6}", message = "El campo idDistrito debe tener 6 caracteres.")
    private String idDistrito;

    @NotBlank(message = "El campo numeroDocumento es obligatorio.")
    @Size(max = 20, message = "El campo numeroDocumento no puede superar 20 caracteres.")
    private String numeroDocumento;

    @NotBlank(message = "El campo nombres es obligatorio.")
    @Size(max = 150, message = "El campo nombres no puede superar 150 caracteres.")
    private String nombres;

    @NotBlank(message = "El campo apellidoPaterno es obligatorio.")
    @Size(max = 100, message = "El campo apellidoPaterno no puede superar 100 caracteres.")
    private String apellidoPaterno;

    @Size(max = 100, message = "El campo apellidoMaterno no puede superar 100 caracteres.")
    private String apellidoMaterno;

    private LocalDate fechaNacimiento;

    @NotNull(message = "El campo genero es obligatorio.")
    @GeneroValido
    private Genero genero;

    @Size(max = 150, message = "El campo correo no puede superar 150 caracteres.")
    private String correo;

    @Size(max = 30, message = "El campo telefono no puede superar 30 caracteres.")
    private String telefono;

    @Size(max = 300, message = "El campo direccion no puede superar 300 caracteres.")
    private String direccion;

    private Boolean estado;

    private int numeroFila;
}

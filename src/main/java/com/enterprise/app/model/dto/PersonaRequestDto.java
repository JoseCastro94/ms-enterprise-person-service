package com.enterprise.app.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
public class PersonaRequestDto {
    @NotNull(message = "El tipo de documento es obligatorio.")
    @Positive(message = "El tipo de documento debe ser mayor que cero.")
    private Long idTipoDocumento;
    
    @NotBlank(message = "El número de documento es obligatorio.")
    @Size(max = 20, message = "El número de documento no puede superar 20 caracteres.")
    private String numeroDocumento;
}

package com.enterprise.app.model.dto;

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
public class DistritoDto {
    private String idDistrito;
    private String idProvincia;
    private String idDepartamento;
    private String nombre;
}

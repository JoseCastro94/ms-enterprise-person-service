package com.enterprise.app.model.dto;

import com.enterprise.app.exception.responses.ErrorFilaDto;
import java.util.List;
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
public class ResultadoValidacionPersonas {
    private List<PersonaDto> personasValidas;
    private List<ErrorFilaDto> errores;
    private int duplicadosArchivo;
}

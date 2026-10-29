package com.enterprise.app.business;

import com.enterprise.app.model.dto.PersonaDto;
import com.enterprise.app.model.dto.PersonaRequestDto;
import com.enterprise.app.model.dto.PersonaResponseDto;
import com.enterprise.app.model.dto.ResumenCargaPersonas;
import io.smallrye.mutiny.Uni;
import java.util.List;

public interface PersonaService {
    Uni<PersonaResponseDto> obtenerPorDocumento(PersonaRequestDto request, String transactionId);

    Uni<ResumenCargaPersonas> procesarCargaMasiva(List<PersonaDto> personas, String transactionId);
}

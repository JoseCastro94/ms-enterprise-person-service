package com.enterprise.app.repository;

import com.enterprise.app.model.entity.PersonaEntity;
import io.smallrye.mutiny.Uni;
import java.util.List;
import java.util.Set;

public interface PersonaRepository {
    Uni<PersonaEntity> obtenerPorDocumento(Long idTipoDocumento, String numeroDocumento);

    Uni<Set<String>> obtenerDocumentosExistentes(String documentosJson);

    Uni<Integer> guardarPersonasPorLote(List<PersonaEntity> personas);

    Uni<Integer> actualizar(PersonaEntity persona);
}

package com.enterprise.app.repository;

import com.enterprise.app.model.entity.DepartamentoEntity;
import com.enterprise.app.model.entity.DistritoEntity;
import com.enterprise.app.model.entity.ProvinciaEntity;
import io.smallrye.mutiny.Uni;
import java.util.List;

public interface UbigeoRepository {
    Uni<List<DepartamentoEntity>> obtenerDepartamentos();

    Uni<List<ProvinciaEntity>> obtenerProvincias(String idDepartamento);

    Uni<List<DistritoEntity>> obtenerDistritos(String idProvincia);
}

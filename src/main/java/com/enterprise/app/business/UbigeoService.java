package com.enterprise.app.business;

import com.enterprise.app.model.dto.DepartamentoDto;
import com.enterprise.app.model.dto.DistritoDto;
import com.enterprise.app.model.dto.ProvinciaDto;
import io.smallrye.mutiny.Uni;
import java.util.List;

public interface UbigeoService {
    Uni<List<DepartamentoDto>> obtenerDepartamentos(String transactionId);

    Uni<List<ProvinciaDto>> obtenerProvincias(String idDepartamento, String transactionId);

    Uni<List<DistritoDto>> obtenerDistritos(String idProvincia, String transactionId);
}

package com.enterprise.app.repository.queries;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UbigeoQueryTemplate {
    public static final String OBTENER_DEPARTAMENTOS = """
            SELECT d FROM DepartamentoEntity d
            ORDER BY d.nombre
            """;

    public static final String OBTENER_PROVINCIAS_POR_DEPARTAMENTO = """
            SELECT p FROM ProvinciaEntity p
            WHERE p.idDepartamento = ?1
            ORDER BY p.nombre
            """;

    public static final String OBTENER_DISTRITOS_POR_PROVINCIA = """
            SELECT d FROM DistritoEntity d
            WHERE d.idProvincia = ?1
            ORDER BY d.nombre
            """;
}

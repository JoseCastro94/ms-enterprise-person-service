package com.enterprise.app.constants;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RedisKeys {

    public static final String DEPARTAMENTOS = "departamentos:all";

    public static String provincias(String idDepartamento) {
        return "provincias:" + idDepartamento;
    }

    public static String distritos(String idProvincia) {
        return "distritos:" + idProvincia;
    }
}

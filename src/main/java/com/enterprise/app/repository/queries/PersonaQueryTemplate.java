package com.enterprise.app.repository.queries;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PersonaQueryTemplate {
    public static final String OBTENER_PERSONA_POR_DOCUMENTO = """
            SELECT p FROM PersonaEntity p
            JOIN FETCH p.tipoDocumento
            JOIN FETCH p.estadoCivil
            JOIN FETCH p.distrito d
            JOIN FETCH d.provincia
            JOIN FETCH d.departamento
            WHERE p.tipoDocumento.idTipoDocumento = ?1 AND p.numeroDocumento = ?2
            """;

    public static final String OBTENER_DOCUMENTOS_EXISTENTES = """
            SELECT p.numero_documento
            FROM dbo.persona p
            WHERE p.numero_documento IN (SELECT [value] FROM OPENJSON(?1))
            """;

    public static final String INSERTAR_PERSONAS = """
            INSERT INTO dbo.persona
                (id_tipo_documento, id_estado_civil, id_distrito, numero_documento,
                 nombres, apellido_paterno, apellido_materno, fecha_nacimiento, genero,
                 correo, telefono, direccion, estado)
            VALUES (@p1, @p2, @p3, @p4, @p5, @p6, @p7, @p8, @p9, @p10, @p11, @p12, @p13)
            """;

    public static final String ACTUALIZAR_PERSONA = """
            UPDATE dbo.persona
            SET id_tipo_documento = ?1,
                id_estado_civil = ?2,
                id_distrito = ?3,
                numero_documento = ?4,
                nombres = ?5,
                apellido_paterno = ?6,
                apellido_materno = ?7,
                fecha_nacimiento = ?8,
                genero = ?9,
                correo = ?10,
                telefono = ?11,
                direccion = ?12,
                estado = ?13,
                fecha_actualizacion = SYSDATETIME()
            WHERE id_persona = ?14
            """;
}

# ms-enterprise-person-service

Microservicio Java 21 y Quarkus con Hibernate Reactive, Mutiny y el cliente reactivo MSSQL para SQL Server. Conserva la estructura de paquetes, `transactionId`, formato de respuestas, manejo de excepciones y estilo de logs de `ms-transaccion-batch`.

## Configuración

Variables disponibles:

- `DB_REACTIVE_URL`: URL de conexión reactiva SQL Server (predeterminado `sqlserver://localhost:1433/db_persona?encrypt=true&trustServerCertificate=true`).
- `DB_USERNAME` / `DB_PASSWORD`: credenciales de SQL Server.
- `REDIS_URL`: URL de conexión de Redis, por ejemplo `redis://localhost:6379`.
- `PERSONAS_TAMANO_LOTE`: filas por ejecución batch (predeterminado `100`).
- `PERSONAS_ARCHIVO_MAXIMO_BYTES`: tamaño máximo de Excel (20 MiB predeterminado).
- `CACHE_UBIGEO_ENABLED` / `CACHE_UBIGEO_TTL`: habilita la caché de catálogos ubigeo y define su TTL en segundos (por defecto `true` y `1800`).

## Ejecución

```powershell
.\mvnw.cmd quarkus:dev
```

Swagger UI: `/q/swagger-ui/`. OpenAPI: `/q/openapi`.

## Operaciones

- `POST /api/v1/personas/carga-masiva`: multipart con campo `archivo`.
- `POST /api/v1/personas/consultar`: recibe `idTipoDocumento` y `numeroDocumento`, devuelve la persona con descripciones de catálogos.
- `GET /api/v1/ubigeo/departamentos`: lista departamentos.
- `GET /api/v1/ubigeo/departamentos/{idDepartamento}/provincias`: lista provincias por departamento.
- `GET /api/v1/ubigeo/provincias/{idProvincia}/distritos`: lista distritos por provincia.

Las consultas ubigeo usan Cache Aside: Redis guarda las listas serializadas con TTL configurable y, ante un miss, el servicio consulta SQL Server. La configuración de conexión Redis se proporciona mediante `REDIS_URL`; las claves están centralizadas en `RedisKeys`.

El Excel debe contener la hoja `Personas` y la fila de cabecera, exactamente en este orden: `id_tipo_documento`, `nro_documento`, `nombres`, `apellido_paterno`, `apellido_materno`, `fecha_nacimiento`, `correo`, `genero`, `direccion`, `telefono`, `id_estado_civil`, `id_distrito`. Los documentos existentes se consultan en una sola consulta masiva antes de filtrar en memoria. Las personas nuevas se insertan con sentencias multivalor en lotes de hasta 161 filas para respetar el máximo de parámetros de SQL Server. La consulta utiliza `OPENJSON`, por lo que SQL Server debe tener nivel de compatibilidad 130 o superior.

### Persistencia

La persistencia utiliza `Mutiny.SessionFactory` y el cliente reactivo MSSQL; el repositorio administra el tamaño de lote, el límite de parámetros de SQL Server, las inserciones multivalor y la transacción reactiva. El servicio compone las operaciones con `Uni`, valida datos, serializa la lista documental para `OPENJSON` y filtra en memoria. La lectura de Excel con Apache POI se ejecuta en un worker mediante `VertxContextSupport.executeBlocking`, sin bloquear el event loop. Los contratos están en `repository`, sus implementaciones en `repository.impl` y las consultas en `repository.querys`.

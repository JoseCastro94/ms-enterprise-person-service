package com.enterprise.app.business.impl;

import com.enterprise.app.business.UbigeoService;
import com.enterprise.app.config.CacheProperties;
import com.enterprise.app.constants.RedisKeys;
import com.enterprise.app.model.dto.DepartamentoDto;
import com.enterprise.app.model.dto.DistritoDto;
import com.enterprise.app.model.dto.ProvinciaDto;
import com.enterprise.app.mapper.UbigeoMapper;
import com.enterprise.app.repository.RedisRepository;
import com.enterprise.app.repository.UbigeoRepository;
import com.enterprise.app.utils.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class UbigeoServiceImpl implements UbigeoService {
    private static final TypeReference<List<DepartamentoDto>> DEPARTAMENTOS_TYPE = new TypeReference<>() { };
    private static final TypeReference<List<ProvinciaDto>> PROVINCIAS_TYPE = new TypeReference<>() { };
    private static final TypeReference<List<DistritoDto>> DISTRITOS_TYPE = new TypeReference<>() { };

    private final UbigeoRepository repository;
    private final RedisRepository redisRepository;
    private final CacheProperties cacheProperties;

    @Inject
    public UbigeoServiceImpl(
            UbigeoRepository repository,
            RedisRepository redisRepository,
            CacheProperties cacheProperties) {
        this.repository = repository;
        this.redisRepository = redisRepository;
        this.cacheProperties = cacheProperties;
    }

    @Override
    public Uni<List<DepartamentoDto>> obtenerDepartamentos(String transactionId) {
        String key = RedisKeys.DEPARTAMENTOS;
        if (!cacheProperties.ubigeo().enabled()) {
            log.info("[{}] CACHE DESHABILITADA -> {}", transactionId, key);
            return repository.obtenerDepartamentos()
                    .map(UbigeoMapper::toDepartamentoDtoList)
                    .onFailure().invoke(error ->
                            log.error("[{}] Error al consultar departamentos -> {}",
                                    transactionId, key, error));
        }

        return redisRepository.obtener(key, transactionId)
                .flatMap(json -> {
                    if (json != null) {
                        log.info("[{}] CACHE HIT -> {}", transactionId, key);
                        return Uni.createFrom().item(JsonUtil.deserializar(json, DEPARTAMENTOS_TYPE));
                    }

                    log.info("[{}] CACHE MISS -> {}", transactionId, key);
                    log.info("[{}] Consultando SQL Server -> {}", transactionId, key);
                    return repository.obtenerDepartamentos()
                            .map(UbigeoMapper::toDepartamentoDtoList)
                            .flatMap(departamentos -> Uni.createFrom()
                                    .item(() -> JsonUtil.serializar(departamentos))
                                    .flatMap(jsonCache -> redisRepository.guardar(
                                            key, jsonCache, transactionId))
                                    .replaceWith(departamentos));
                })
                .onFailure().invoke(error ->
                        log.error("[{}] Error al consultar departamentos -> {}",
                                transactionId, key, error));
    }

    @Override
    public Uni<List<ProvinciaDto>> obtenerProvincias(String idDepartamento, String transactionId) {
        String id = idDepartamento.trim();
        String key = RedisKeys.provincias(id);
        if (!cacheProperties.ubigeo().enabled()) {
            log.info("[{}] CACHE DESHABILITADA -> {}", transactionId, key);
            return repository.obtenerProvincias(id)
                    .map(UbigeoMapper::toProvinciaDtoList)
                    .onFailure().invoke(error ->
                            log.error("[{}] Error al consultar provincias -> {}",
                                    transactionId, key, error));
        }

        return redisRepository.obtener(key, transactionId)
                .flatMap(json -> {
                    if (json != null) {
                        log.info("[{}] CACHE HIT -> {}", transactionId, key);
                        return Uni.createFrom().item(JsonUtil.deserializar(json, PROVINCIAS_TYPE));
                    }

                    log.info("[{}] CACHE MISS -> {}", transactionId, key);
                    log.info("[{}] Consultando SQL Server -> {}", transactionId, key);
                    return repository.obtenerProvincias(id)
                            .map(UbigeoMapper::toProvinciaDtoList)
                            .flatMap(provincias -> Uni.createFrom()
                                    .item(() -> JsonUtil.serializar(provincias))
                                    .flatMap(jsonCache -> redisRepository.guardar(
                                            key, jsonCache, transactionId))
                                    .replaceWith(provincias));
                })
                .onFailure().invoke(error ->
                        log.error("[{}] Error al consultar provincias -> {}",
                                transactionId, key, error));
    }

    @Override
    public Uni<List<DistritoDto>> obtenerDistritos(String idProvincia, String transactionId) {
        String id = idProvincia.trim();
        String key = RedisKeys.distritos(id);
        if (!cacheProperties.ubigeo().enabled()) {
            log.info("[{}] CACHE DESHABILITADA -> {}", transactionId, key);
            return repository.obtenerDistritos(id)
                    .map(UbigeoMapper::toDistritoDtoList)
                    .onFailure().invoke(error ->
                            log.error("[{}] Error al consultar distritos -> {}",
                                    transactionId, key, error));
        }

        return redisRepository.obtener(key, transactionId)
                .flatMap(json -> {
                    if (json != null) {
                        log.info("[{}] CACHE HIT -> {}", transactionId, key);
                        return Uni.createFrom().item(JsonUtil.deserializar(json, DISTRITOS_TYPE));
                    }

                    log.info("[{}] CACHE MISS -> {}", transactionId, key);
                    log.info("[{}] Consultando SQL Server -> {}", transactionId, key);
                    return repository.obtenerDistritos(id)
                            .map(UbigeoMapper::toDistritoDtoList)
                            .flatMap(distritos -> Uni.createFrom()
                                    .item(() -> JsonUtil.serializar(distritos))
                                    .flatMap(jsonCache -> redisRepository.guardar(
                                            key, jsonCache, transactionId))
                                    .replaceWith(distritos));
                })
                .onFailure().invoke(error ->
                        log.error("[{}] Error al consultar distritos -> {}",
                                transactionId, key, error));
    }
}

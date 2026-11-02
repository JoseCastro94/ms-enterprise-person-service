package com.enterprise.app.repository.impl;

import com.enterprise.app.config.CacheProperties;
import com.enterprise.app.repository.RedisRepository;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.value.ReactiveValueCommands;
import io.quarkus.redis.datasource.value.SetArgs;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class RedisRepositoryImpl implements RedisRepository {

    private final ReactiveValueCommands<String, String> values;
    private final ReactiveRedisDataSource redisDataSource;
    private final CacheProperties cacheProperties;

    @Inject
    public RedisRepositoryImpl(
            ReactiveRedisDataSource redisDataSource,
            CacheProperties cacheProperties) {
        this.redisDataSource = redisDataSource;
        this.values = redisDataSource.value(String.class);
        this.cacheProperties = cacheProperties;
    }

    @Override
    public Uni<String> obtener(String key, String transactionId) {

        return values.get(key)
                .invoke(valor -> {
                    if (valor == null) {
                        log.info(
                                "[{}] CACHE MISS REDIS -> {}",
                                transactionId,
                                key);
                    }
                })
                .onFailure()
                .invoke(error ->
                        log.warn(
                                "[{}] Redis no disponible. Se utilizara SQL -> {}",
                                transactionId,
                                key))
                .onFailure()
                .recoverWithNull();
    }

    @Override
    public Uni<Void> guardar(
            String key,
            String value,
            String transactionId) {

        long ttlSeconds = cacheProperties.ubigeo().ttl();

        if (ttlSeconds < 1) {
            log.warn(
                    "[{}] TTL inválido configurado para Redis: {}",
                    transactionId,
                    ttlSeconds);
            return Uni.createFrom().voidItem();
        }

        return values.set(key, value, new SetArgs().ex(ttlSeconds))
                .invoke(() -> {
                    log.info("[{}] Guardado Redis -> {}", transactionId, key);
                    log.info(
                            "[{}] TTL aplicado: {} segundos -> {}",
                            transactionId,
                            ttlSeconds,
                            key);
                })
                .onFailure()
                .recoverWithNull()
                .invoke(() -> log.warn(
                        "[{}] Redis no disponible. No fue posible guardar la clave  -> {}",
                        transactionId,
                        key));
    }

    @Override
    public Uni<Void> eliminar(
            String key,
            String transactionId) {

        return redisDataSource.key()
                .del(key)
                .replaceWithVoid()
                .invoke(() ->
                        log.info(
                                "[{}] Clave Redis eliminada -> {}",
                                transactionId,
                                key))
                .onFailure()
                .recoverWithNull()
                .invoke(() ->
                        log.warn(
                                "[{}] No fue posible eliminar la clave Redis -> {}",
                                transactionId,
                                key));
    }
}
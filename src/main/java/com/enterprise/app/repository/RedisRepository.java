package com.enterprise.app.repository;

import io.smallrye.mutiny.Uni;

public interface RedisRepository {
    Uni<String> obtener(String key, String transactionId);

    Uni<Void> guardar(String key, String value, String transactionId);

    Uni<Void> eliminar(String key, String transactionId);
}

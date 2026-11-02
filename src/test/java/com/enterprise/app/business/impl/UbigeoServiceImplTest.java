package com.enterprise.app.business.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.enterprise.app.config.CacheProperties;
import com.enterprise.app.model.dto.DepartamentoDto;
import com.enterprise.app.model.entity.DepartamentoEntity;
import com.enterprise.app.model.entity.DistritoEntity;
import com.enterprise.app.model.entity.ProvinciaEntity;
import com.enterprise.app.repository.RedisRepository;
import com.enterprise.app.repository.UbigeoRepository;
import com.enterprise.app.utils.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.smallrye.mutiny.Uni;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class UbigeoServiceImplTest {
    @Test
    void shouldMapRepositoryResultAndSaveItOnCacheMiss() {
        FakeRedisRepository redisRepository = new FakeRedisRepository();
        AtomicInteger consultasRepository = new AtomicInteger();
        UbigeoRepository repository = new UbigeoRepositoryStub(consultasRepository);
        UbigeoServiceImpl service = new UbigeoServiceImpl(
                repository, redisRepository, cacheProperties(true));

        List<DepartamentoDto> resultado = service.obtenerDepartamentos("tx-miss")
                .await().indefinitely();

        assertEquals(1, consultasRepository.get());
        assertEquals("01", resultado.getFirst().getIdDepartamento());
        assertEquals("Lima", resultado.getFirst().getNombre());
        assertEquals(1, redisRepository.escrituras.get());
        List<DepartamentoDto> guardados = JsonUtil.deserializar(
                redisRepository.valores.get("departamentos:all"),
                new TypeReference<>() { });
        assertEquals(resultado.getFirst().getNombre(), guardados.getFirst().getNombre());
    }

    @Test
    void shouldReturnCachedResultWithoutConsultingRepository() {
        FakeRedisRepository redisRepository = new FakeRedisRepository();
        redisRepository.valores.put("departamentos:all",
                JsonUtil.serializar(List.of(new DepartamentoDto("01", "Lima"))));
        redisRepository.escrituras.set(0);
        AtomicInteger consultasRepository = new AtomicInteger();
        UbigeoServiceImpl service = new UbigeoServiceImpl(
                new UbigeoRepositoryStub(consultasRepository), redisRepository, cacheProperties(true));

        List<DepartamentoDto> resultado = service.obtenerDepartamentos("tx-hit")
                .await().indefinitely();

        assertEquals(0, consultasRepository.get());
        assertEquals("Lima", resultado.getFirst().getNombre());
        assertEquals(0, redisRepository.escrituras.get());
        assertEquals(1, redisRepository.lecturas.get());
    }

    @Test
    void shouldBypassRedisWhenCachingIsDisabled() {
        FakeRedisRepository redisRepository = new FakeRedisRepository();
        AtomicInteger consultasRepository = new AtomicInteger();
        UbigeoServiceImpl service = new UbigeoServiceImpl(
                new UbigeoRepositoryStub(consultasRepository), redisRepository, cacheProperties(false));

        List<DepartamentoDto> resultado = service.obtenerDepartamentos("tx-disabled")
                .await().indefinitely();

        assertEquals(1, consultasRepository.get());
        assertEquals("Lima", resultado.getFirst().getNombre());
        assertEquals(0, redisRepository.lecturas.get());
        assertEquals(0, redisRepository.escrituras.get());
    }

    private CacheProperties cacheProperties(boolean enabled) {
        return () -> new CacheProperties.Ubigeo() {
            @Override
            public boolean enabled() {
                return enabled;
            }

            @Override
            public long ttl() {
                return 1800;
            }
        };
    }

    private static class UbigeoRepositoryStub implements UbigeoRepository {
        private final AtomicInteger consultas;

        private UbigeoRepositoryStub(AtomicInteger consultas) {
            this.consultas = consultas;
        }

        @Override
        public Uni<List<DepartamentoEntity>> obtenerDepartamentos() {
            consultas.incrementAndGet();
            return Uni.createFrom().item(List.of(new DepartamentoEntity("01", "Lima", null)));
        }

        @Override
        public Uni<List<ProvinciaEntity>> obtenerProvincias(String idDepartamento) {
            return Uni.createFrom().item(List.of());
        }

        @Override
        public Uni<List<DistritoEntity>> obtenerDistritos(String idProvincia) {
            return Uni.createFrom().item(List.of());
        }
    }

    private static class FakeRedisRepository implements RedisRepository {
        private final Map<String, String> valores = new HashMap<>();
        private final AtomicInteger lecturas = new AtomicInteger();
        private final AtomicInteger escrituras = new AtomicInteger();

        @Override
        public Uni<String> obtener(String key, String transactionId) {
            lecturas.incrementAndGet();
            return Uni.createFrom().item(valores.get(key));
        }

        @Override
        public Uni<Void> guardar(String key, String value, String transactionId) {
            escrituras.incrementAndGet();
            valores.put(key, value);
            return Uni.createFrom().voidItem();
        }

        @Override
        public Uni<Void> eliminar(String key, String transactionId) {
            valores.remove(key);
            return Uni.createFrom().voidItem();
        }
    }
}

package com.enterprise.app.repository.impl;

import static com.enterprise.app.repository.queries.PersonaQueryTemplate.ACTUALIZAR_PERSONA;
import static com.enterprise.app.repository.queries.PersonaQueryTemplate.INSERTAR_PERSONAS;
import static com.enterprise.app.repository.queries.PersonaQueryTemplate.OBTENER_DOCUMENTOS_EXISTENTES;
import static com.enterprise.app.repository.queries.PersonaQueryTemplate.OBTENER_PERSONA_POR_DOCUMENTO;

import com.enterprise.app.config.ApplicationProperties;
import com.enterprise.app.model.entity.PersonaEntity;
import com.enterprise.app.repository.PersonaRepository;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.SqlConnection;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.reactive.mutiny.Mutiny;

@ApplicationScoped
public class PersonaRepositoryImpl implements PersonaRepository {
    private final Mutiny.SessionFactory sessionFactory;
    private final Pool reactivePool;
    private final ApplicationProperties properties;

    @Inject
    public PersonaRepositoryImpl(
            Mutiny.SessionFactory sessionFactory,
            Pool reactivePool,
            ApplicationProperties properties) {
        this.sessionFactory = sessionFactory;
        this.reactivePool = reactivePool;
        this.properties = properties;
    }

    @Override
    public Uni<PersonaEntity> obtenerPorDocumento(Long idTipoDocumento, String numeroDocumento) {
        return sessionFactory.withSession(session -> session
                .createQuery(OBTENER_PERSONA_POR_DOCUMENTO, PersonaEntity.class)
                .setParameter(1, idTipoDocumento)
                .setParameter(2, numeroDocumento)
                .getSingleResultOrNull());
    }

    @Override
    public Uni<Set<String>> obtenerDocumentosExistentes(String documentosJson) {
        return sessionFactory.withSession(session -> session
                .createNativeQuery(OBTENER_DOCUMENTOS_EXISTENTES, String.class)
                .setParameter(1, documentosJson)
                .getResultList()
                .map(Set::copyOf));
    }

    @Override
    public Uni<Integer> guardarPersonasPorLote(List<PersonaEntity> personas) {
        if (personas.isEmpty()) {
            return Uni.createFrom().item(0);
        }

        int tamanoLote = properties.batch().tamanoLote();
        if (tamanoLote < 1) {
            return Uni.createFrom().failure(new IllegalStateException(
                    "personas.batch.tamano-lote debe ser mayor que cero."));
        }

        List<List<PersonaEntity>> lotes = particionar(personas, tamanoLote);
        return reactivePool.withTransaction(connection -> Multi.createFrom().iterable(lotes)
                .onItem().transformToUniAndConcatenate(lote -> insertarLote(connection, lote))
                .collect().with(Collectors.summingInt(Integer::intValue)));
    }

    @Override
    public Uni<Integer> actualizar(PersonaEntity persona) {
        return sessionFactory.withTransaction((session, transaction) -> {
            var query = session.createNativeQuery(ACTUALIZAR_PERSONA);
            bindPersona(query, persona, 1);
            return query.setParameter(14, persona.getIdPersona())
                    .executeUpdate();
        });
    }

    private Uni<Integer> insertarLote(SqlConnection connection, List<PersonaEntity> lote) {
        List<Tuple> batch = lote.stream()
                .map(PersonaRepositoryImpl::crearTupla)
                .toList();
        return connection.preparedQuery(INSERTAR_PERSONAS)
                .executeBatch(batch)
                .map(ignored -> lote.size());
    }

    private int bindPersona(Mutiny.Query<?> query, PersonaEntity persona, int indiceParametro) {
        query.setParameter(indiceParametro++, persona.getIdTipoDocumento());
        query.setParameter(indiceParametro++, persona.getIdEstadoCivil());
        query.setParameter(indiceParametro++, persona.getIdDistrito());
        query.setParameter(indiceParametro++, persona.getNumeroDocumento());
        query.setParameter(indiceParametro++, persona.getNombres());
        query.setParameter(indiceParametro++, persona.getApellidoPaterno());
        query.setParameter(indiceParametro++, persona.getApellidoMaterno());
        query.setParameter(indiceParametro++, persona.getFechaNacimiento());
        query.setParameter(indiceParametro++, persona.getGenero());
        query.setParameter(indiceParametro++, persona.getCorreo());
        query.setParameter(indiceParametro++, persona.getTelefono());
        query.setParameter(indiceParametro++, persona.getDireccion());
        query.setParameter(indiceParametro++, persona.getEstado());
        return indiceParametro;
    }

    static Tuple crearTupla(PersonaEntity persona) {
        return Tuple.tuple()
                .addLong(persona.getIdTipoDocumento())
                .addLong(persona.getIdEstadoCivil())
                .addString(persona.getIdDistrito())
                .addString(persona.getNumeroDocumento())
                .addString(persona.getNombres())
                .addString(persona.getApellidoPaterno())
                .addString(persona.getApellidoMaterno())
                .addLocalDate(persona.getFechaNacimiento())
                .addString(persona.getGenero())
                .addString(persona.getCorreo())
                .addString(persona.getTelefono())
                .addString(persona.getDireccion())
                .addBoolean(persona.getEstado());
    }

    private List<List<PersonaEntity>> particionar(List<PersonaEntity> personas, int tamanoLote) {
        List<List<PersonaEntity>> lotes = new ArrayList<>();
        for (int inicio = 0; inicio < personas.size(); inicio += tamanoLote) {
            lotes.add(personas.subList(inicio, Math.min(inicio + tamanoLote, personas.size())));
        }
        return lotes;
    }

}

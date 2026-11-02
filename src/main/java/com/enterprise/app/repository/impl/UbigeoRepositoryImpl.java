package com.enterprise.app.repository.impl;

import static com.enterprise.app.repository.queries.UbigeoQueryTemplate.OBTENER_DEPARTAMENTOS;
import static com.enterprise.app.repository.queries.UbigeoQueryTemplate.OBTENER_DISTRITOS_POR_PROVINCIA;
import static com.enterprise.app.repository.queries.UbigeoQueryTemplate.OBTENER_PROVINCIAS_POR_DEPARTAMENTO;

import com.enterprise.app.model.entity.DepartamentoEntity;
import com.enterprise.app.model.entity.DistritoEntity;
import com.enterprise.app.model.entity.ProvinciaEntity;
import com.enterprise.app.repository.UbigeoRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import org.hibernate.reactive.mutiny.Mutiny;

@ApplicationScoped
public class UbigeoRepositoryImpl implements UbigeoRepository {
    private final Mutiny.SessionFactory sessionFactory;

    @Inject
    public UbigeoRepositoryImpl(Mutiny.SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Uni<List<DepartamentoEntity>> obtenerDepartamentos() {
        return sessionFactory.withSession(session -> session
                .createQuery(OBTENER_DEPARTAMENTOS, DepartamentoEntity.class)
                .getResultList());
    }

    @Override
    public Uni<List<ProvinciaEntity>> obtenerProvincias(String idDepartamento) {
        return sessionFactory.withSession(session -> session
                .createQuery(OBTENER_PROVINCIAS_POR_DEPARTAMENTO, ProvinciaEntity.class)
                .setParameter(1, idDepartamento)
                .getResultList());
    }

    @Override
    public Uni<List<DistritoEntity>> obtenerDistritos(String idProvincia) {
        return sessionFactory.withSession(session -> session
                .createQuery(OBTENER_DISTRITOS_POR_PROVINCIA, DistritoEntity.class)
                .setParameter(1, idProvincia)
                .getResultList());
    }
}

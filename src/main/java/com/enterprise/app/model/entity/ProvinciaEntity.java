package com.enterprise.app.model.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "provincia")
public class ProvinciaEntity {
    @Id
    @Column(name = "id_provincia", columnDefinition = "CHAR(4)")
    private String idProvincia;

    @Column(name = "id_departamento", columnDefinition = "CHAR(2)")
    private String idDepartamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_departamento", insertable = false, updatable = false)
    private DepartamentoEntity departamento;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "fecha_registro", insertable = false, updatable = false)
    private LocalDateTime fechaRegistro;
}

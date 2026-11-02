package com.enterprise.app.mapper;

import com.enterprise.app.model.dto.DepartamentoDto;
import com.enterprise.app.model.dto.DistritoDto;
import com.enterprise.app.model.dto.ProvinciaDto;
import com.enterprise.app.model.entity.DepartamentoEntity;
import com.enterprise.app.model.entity.DistritoEntity;
import com.enterprise.app.model.entity.ProvinciaEntity;
import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class UbigeoMapper {
    public static DepartamentoDto toDepartamentoDto(DepartamentoEntity entity) {
        return DepartamentoDto.builder()
                .idDepartamento(entity.getIdDepartamento().trim())
                .nombre(entity.getNombre())
                .build();
    }

    public static List<DepartamentoDto> toDepartamentoDtoList(List<DepartamentoEntity> entities) {
        return entities.stream().map(UbigeoMapper::toDepartamentoDto).toList();
    }

    public static ProvinciaDto toProvinciaDto(ProvinciaEntity entity) {
        return ProvinciaDto.builder()
                .idProvincia(entity.getIdProvincia().trim())
                .idDepartamento(entity.getIdDepartamento().trim())
                .nombre(entity.getNombre())
                .build();
    }

    public static List<ProvinciaDto> toProvinciaDtoList(List<ProvinciaEntity> entities) {
        return entities.stream().map(UbigeoMapper::toProvinciaDto).toList();
    }

    public static DistritoDto toDistritoDto(DistritoEntity entity) {
        return DistritoDto.builder()
                .idDistrito(entity.getIdDistrito().trim())
                .idProvincia(entity.getIdProvincia().trim())
                .idDepartamento(entity.getIdDepartamento().trim())
                .nombre(entity.getNombre())
                .build();
    }

    public static List<DistritoDto> toDistritoDtoList(List<DistritoEntity> entities) {
        return entities.stream().map(UbigeoMapper::toDistritoDto).toList();
    }
}

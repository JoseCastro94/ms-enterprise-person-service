package com.enterprise.app.business.validators;

import com.enterprise.app.exception.responses.ErrorFilaDto;
import com.enterprise.app.model.dto.PersonaDto;
import com.enterprise.app.model.dto.ResultadoValidacionPersonas;
import com.enterprise.app.model.enums.CodigoValidacion;
import com.enterprise.app.utils.DataConvertUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class PersonaValidator {
    private final Validator validator;

    @Inject
    public PersonaValidator(Validator validator) {
        this.validator = validator;
    }

    public ResultadoValidacionPersonas validar(List<PersonaDto> personas, String transactionId) {
        Set<String> documentos = new HashSet<>();
        List<PersonaDto> validas = new ArrayList<>();
        List<ErrorFilaDto> errores = new ArrayList<>();
        int duplicados = 0;

        for (PersonaDto persona : personas) {
            List<ErrorFilaDto> erroresPersona = validarPersona(persona);
            String documentoNormalizado = DataConvertUtil.toUpperCase(persona.getNumeroDocumento());
            boolean primeraOcurrencia = documentoNormalizado == null
                    || documentoNormalizado.isBlank()
                    || documentos.add(documentoNormalizado);

            if (!primeraOcurrencia) {
                erroresPersona.add(error(persona, "numero_documento",
                        "El número de documento está repetido en el archivo.",
                        CodigoValidacion.DUPLICATE_IN_FILE));
                duplicados++;
            }

            if (erroresPersona.isEmpty() && primeraOcurrencia) {
                normalizarCampos(persona);
                validas.add(persona);
            }
            errores.addAll(erroresPersona);
        }

        log.info("Validación de personas finalizada. transactionId={}, válidas={}, rechazadas={}, duplicadosArchivo={}",
                transactionId, validas.size(), errores.stream().map(ErrorFilaDto::getFila).distinct().count(),
                duplicados);

        return ResultadoValidacionPersonas.builder()
                .personasValidas(validas)
                .errores(errores)
                .duplicadosArchivo(duplicados)
                .build();
    }

    private List<ErrorFilaDto> validarPersona(PersonaDto persona) {
        return new ArrayList<>(validator.validate(persona).stream()
                .sorted(Comparator.comparing(violation -> violation.getPropertyPath().toString()))
                .map(violation -> error(
                        persona,
                        campo(violation.getPropertyPath().toString()),
                        violation.getMessage(),
                        codigo(violation)))
                .toList());
    }

    private CodigoValidacion codigo(ConstraintViolation<PersonaDto> violation) {
        Class<?> tipoAnotacion = violation.getConstraintDescriptor()
                .getAnnotation()
                .annotationType();

        if (tipoAnotacion == NotBlank.class || tipoAnotacion == NotNull.class) {
            return CodigoValidacion.NOT_BLANK;
        }

        if (tipoAnotacion == Size.class) {
            return CodigoValidacion.MAX_LENGTH;
        }

        if (tipoAnotacion == Positive.class
                || tipoAnotacion == Pattern.class
                || tipoAnotacion == GeneroValido.class) {
            return CodigoValidacion.INVALID_VALUE;
        }

        throw new IllegalStateException("Constraint Jakarta no soportada: " + tipoAnotacion.getName());
    }

    private String campo(String campo) {
        int ultimoSeparador = campo.lastIndexOf('.');
        String nombrePropiedad = ultimoSeparador < 0 ? campo : campo.substring(ultimoSeparador + 1);
        return nombrePropiedad.replaceAll("([a-z0-9])([A-Z])", "$1_$2")
                .toLowerCase(Locale.ROOT);
    }

    private void normalizarCampos(PersonaDto persona) {
        persona.setNumeroDocumento(DataConvertUtil.normalize(persona.getNumeroDocumento()));
        persona.setNombres(DataConvertUtil.normalize(persona.getNombres()));
        persona.setApellidoPaterno(DataConvertUtil.normalize(persona.getApellidoPaterno()));
        persona.setApellidoMaterno(DataConvertUtil.trimToNull(persona.getApellidoMaterno()));
        persona.setIdDistrito(DataConvertUtil.normalize(persona.getIdDistrito()));
        persona.setCorreo(DataConvertUtil.trimToNull(persona.getCorreo()));
        persona.setTelefono(DataConvertUtil.trimToNull(persona.getTelefono()));
        persona.setDireccion(DataConvertUtil.trimToNull(persona.getDireccion()));
    }

    private ErrorFilaDto error(PersonaDto persona, String campo, String mensaje, CodigoValidacion codigo) {
        return ErrorFilaDto.builder()
                .fila(persona.getNumeroFila())
                .field(campo)
                .message(mensaje)
                .code(codigo.name())
                .build();
    }
}

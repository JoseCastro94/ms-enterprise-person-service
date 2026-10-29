package com.enterprise.app.utils.excel;

import com.enterprise.app.exception.codes.ErrorCodes;
import com.enterprise.app.exception.exceptions.BusinessException;
import com.enterprise.app.model.dto.PersonaDto;
import com.enterprise.app.model.enums.Genero;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

@Slf4j
@ApplicationScoped
public class ExcelPersonaExtractor {
    private final String NOMBRE_HOJA_PERSONAS = "Personas";
    private final int COLUMNAS_PERSONA = 12;

    public List<PersonaDto> extraer(Path archivo, String transactionId) {
        try (InputStream inputStream = Files.newInputStream(archivo);
                Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheet(NOMBRE_HOJA_PERSONAS);
            if (sheet == null || sheet.getRow(0) == null) {
                throw new BusinessException(
                        "El archivo debe contener la hoja Personas y una fila de cabecera.");
            }

            List<PersonaDto> personas = new ArrayList<>();
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (!ExcelCellReader.isEmpty(row, COLUMNAS_PERSONA)) {
                    personas.add(toDto(row, rowIndex + 1));
                }
            }

            log.info("Extracción del Excel finalizada. transactionId={}, registros={}",
                    transactionId, personas.size());
            return personas;
        } catch (BusinessException exception) {
            throw exception;
        } catch (IOException | EncryptedDocumentException | POIXMLException exception) {
            log.error("No se pudo leer el archivo Excel. transactionId={}", transactionId, exception);
            throw new BusinessException(
                    "No se pudo interpretar el archivo Excel.", ErrorCodes.ERROR, exception);
        }
    }

    private PersonaDto toDto(Row row, int numeroFila) {
        return PersonaDto.builder()
                .idTipoDocumento(ExcelCellReader.toLong(row.getCell(0)))
                .numeroDocumento(ExcelCellReader.toString(row.getCell(1)))
                .nombres(ExcelCellReader.toString(row.getCell(2)))
                .apellidoPaterno(ExcelCellReader.toString(row.getCell(3)))
                .apellidoMaterno(ExcelCellReader.toString(row.getCell(4)))
                .fechaNacimiento(ExcelCellReader.toLocalDate(row, 5))
                .correo(ExcelCellReader.toString(row.getCell(6)))
                .genero(Genero.fromValue(ExcelCellReader.toString(row.getCell(7))))
                .direccion(ExcelCellReader.toString(row.getCell(8)))
                .telefono(ExcelCellReader.toString(row.getCell(9)))
                .idEstadoCivil(ExcelCellReader.toLong(row.getCell(10)))
                .idDistrito(ExcelCellReader.toString(row.getCell(11)))
                .estado(Boolean.TRUE)
                .numeroFila(numeroFila)
                .build();
    }
}

package com.enterprise.app.utils.excel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.enterprise.app.model.enums.Genero;
import java.io.OutputStream;
import java.nio.file.Path;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ExcelPersonaExtractorTest {
    @TempDir
    Path tempDirectory;

    @Test
    void shouldReadSampleSpanishHeadersAndPreserveDocument() throws Exception {
        Path excel = tempDirectory.resolve("personas.xlsx");
        try (XSSFWorkbook workbook = new XSSFWorkbook();
                OutputStream output = java.nio.file.Files.newOutputStream(excel)) {
            var sheet = workbook.createSheet("Personas");
            String[] cabeceras = {
                "id_tipo_documento", "nro_documento", "nombres", "apellido_paterno",
                "apellido_materno", "fecha_nacimiento", "correo", "genero", "direccion",
                "telefono", "id_estado_civil", "id_distrito"
            };
            var header = sheet.createRow(0);
            for (int i = 0; i < cabeceras.length; i++) {
                header.createCell(i).setCellValue(cabeceras[i]);
            }
            var row = sheet.createRow(1);
            row.createCell(0).setCellValue(1);
            row.createCell(1).setCellValue("00123456");
            row.createCell(2).setCellValue("Ana");
            row.createCell(3).setCellValue("Pérez");
            row.createCell(4).setCellValue("López");
            row.createCell(5).setCellValue("20/05/1990");
            row.createCell(6).setCellValue("ana@example.com");
            row.createCell(7).setCellValue("F");
            row.createCell(8).setCellValue("Av. Principal 123");
            row.createCell(9).setCellValue("999888777");
            row.createCell(10).setCellValue(1);
            row.createCell(11).setCellValue("010101");
            workbook.write(output);
        }

        var personas = new ExcelPersonaExtractor().extraer(excel, "tx-test");

        assertEquals(1, personas.size());
        assertEquals(1L, personas.getFirst().getIdTipoDocumento());
        assertEquals("00123456", personas.getFirst().getNumeroDocumento());
        assertEquals("Ana", personas.getFirst().getNombres());
        assertEquals("Pérez", personas.getFirst().getApellidoPaterno());
        assertEquals("López", personas.getFirst().getApellidoMaterno());
        assertEquals(java.time.LocalDate.of(1990, 5, 20), personas.getFirst().getFechaNacimiento());
        assertEquals("ana@example.com", personas.getFirst().getCorreo());
        assertEquals(Genero.FEMENINO, personas.getFirst().getGenero());
        assertEquals("Av. Principal 123", personas.getFirst().getDireccion());
        assertEquals("999888777", personas.getFirst().getTelefono());
        assertEquals(1L, personas.getFirst().getIdEstadoCivil());
        assertEquals("010101", personas.getFirst().getIdDistrito());
        assertEquals(2, personas.getFirst().getNumeroFila());
    }

    @Test
    void shouldNotValidateHeaderNames() throws Exception {
        Path excel = tempDirectory.resolve("personas-orden-invalido.xlsx");
        try (XSSFWorkbook workbook = new XSSFWorkbook();
                OutputStream output = java.nio.file.Files.newOutputStream(excel)) {
            var sheet = workbook.createSheet("Personas");
            var header = sheet.createRow(0);
            header.createCell(0).setCellValue("nro_documento");
            header.createCell(1).setCellValue("id_tipo_documento");
            workbook.write(output);
        }

        var personas = new ExcelPersonaExtractor().extraer(excel, "tx-test");
        assertEquals(0, personas.size());
    }
}

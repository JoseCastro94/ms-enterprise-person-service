package com.enterprise.app.utils.excel;

import com.enterprise.app.utils.DataConvertUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.function.Function;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

@Slf4j
@UtilityClass
public class ExcelCellReader {
    public static String toString(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        CellType type = cell.getCellType() == CellType.FORMULA
                ? cell.getCachedFormulaResultType()
                : cell.getCellType();
        if (type == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue()).stripTrailingZeros().toPlainString();
        }
        return DataConvertUtil.trimToNull(cell.toString());
    }

    public static Integer toInteger(Cell cell) {
        return convert(cell, DataConvertUtil::toInteger);
    }

    public static Long toLong(Cell cell) {
        return convert(cell, DataConvertUtil::toLong);
    }

    public static BigDecimal toBigDecimal(Cell cell) {
        return convert(cell, DataConvertUtil::toBigDecimal);
    }

    public static Double toDouble(Cell cell) {
        return convert(cell, DataConvertUtil::toDouble);
    }

    public static LocalDateTime toDate(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue();
        }

        return convertDateCell(cell, columnIndex);
    }

    public static LocalDate toLocalDate(Row row, int columnIndex) {
        LocalDateTime dateTime = toDate(row, columnIndex);
        return dateTime == null ? null : dateTime.toLocalDate();
    }

    public static <T> T toEnum(Cell cell, Function<String, T> converter) {
        return convert(cell, value -> DataConvertUtil.toEnum(value, converter));
    }

    public static <T> T toEnum(Cell cell, Function<String, T> converter, T defaultValue) {
        return toString(cell) == null ? defaultValue : toEnum(cell, converter);
    }

    public static boolean isEmpty(Row row, int numberOfColumns) {
        if (row == null) {
            return true;
        }
        for (int index = 0; index < numberOfColumns; index++) {
            if (toString(row.getCell(index)) != null) {
                return false;
            }
        }
        return true;
    }

    private LocalDateTime convertDateCell(Cell cell, int columnIndex) {
        String value = toString(cell);
        if (value == null) {
            return null;
        }
        try {
            return DataConvertUtil.toLocalDateTime(value);
        } catch (DateTimeParseException exception) {
            log.error("No se pudo interpretar la fecha Excel '{}' en la columna {}", value, columnIndex);
            return null;
        }
    }

    private <T> T convert(Cell cell, Function<String, T> converter) {
        String value = toString(cell);
        if (value == null) {
            return null;
        }
        try {
            return converter.apply(value);
        } catch (RuntimeException exception) {
            log.warn("No se pudo convertir el valor '{}' del Excel.", value);
            return null;
        }
    }
}

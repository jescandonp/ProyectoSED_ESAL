package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.CampoObligatoriedad;
import co.gov.bogota.sed.esal.dto.DiccionarioImportResultDto;
import co.gov.bogota.sed.esal.repository.CampoObligatoriedadRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de importación del diccionario de campos desde Base excel.xlsx.
 *
 * Estructura del archivo:
 *   - Fila 1: encabezado (TÍTULOS, vacío, vacío) — se omite
 *   - Filas 2-118: datos (117 registros)
 *     - Columna 0 (A): nombre del campo
 *     - Columna 1 (B): obligatoriedad — "OBLIGACIÓN" u "OPCIONAL"
 *     - Columna 2 (C): nota/contexto (puede estar vacía)
 *
 * Normalización:
 *   - "OBLIGACIÓN" → obligatorio = true
 *   - "OPCIONAL"   → obligatorio = false
 *
 * La operación es idempotente: elimina registros existentes antes de recargar.
 */
@Service
@Transactional
public class DiccionarioImportService {

    private static final String VALOR_OBLIGACION = "OBLIGACIÓN";
    private static final String VALOR_OPCIONAL    = "OPCIONAL";

    private final CampoObligatoriedadRepository repository;

    public DiccionarioImportService(CampoObligatoriedadRepository repository) {
        this.repository = repository;
    }

    /**
     * Lee el Excel desde el InputStream dado y persiste los campos.
     * Si ya existen registros, los elimina y recarga (idempotente).
     *
     * @param excelStream InputStream del archivo Base excel.xlsx
     * @return resumen de la importación
     * @throws IOException si el stream no puede leerse como XLSX
     */
    public DiccionarioImportResultDto importar(InputStream excelStream) throws IOException {
        DiccionarioImportResultDto resultado = new DiccionarioImportResultDto();
        List<String> advertencias = new ArrayList<>();

        // Limpiar registros existentes para garantizar idempotencia
        repository.deleteAll();

        try (Workbook workbook = new XSSFWorkbook(excelStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            List<CampoObligatoriedad> campos = new ArrayList<>();
            int orden = 0;

            // Iterar desde la fila 1 (índice 1) para saltar el encabezado (fila 0)
            int lastRowNum = sheet.getLastRowNum();
            for (int rowIdx = 1; rowIdx <= lastRowNum; rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) {
                    continue;
                }

                String nombreCampo = getCellText(row, 0);
                String obligatoriedad = getCellText(row, 1);
                String nota = getCellText(row, 2);

                // Omitir filas sin nombre de campo
                if (nombreCampo == null || nombreCampo.trim().isEmpty()) {
                    advertencias.add("Fila " + (rowIdx + 1) + ": nombre de campo vacío, omitida.");
                    continue;
                }

                // Normalizar obligatoriedad
                Boolean obligatorio;
                if (VALOR_OBLIGACION.equalsIgnoreCase(obligatoriedad.trim())) {
                    obligatorio = Boolean.TRUE;
                } else if (VALOR_OPCIONAL.equalsIgnoreCase(obligatoriedad.trim())) {
                    obligatorio = Boolean.FALSE;
                } else {
                    advertencias.add("Fila " + (rowIdx + 1) + ": valor de obligatoriedad desconocido '"
                            + obligatoriedad + "', se asume OPCIONAL.");
                    obligatorio = Boolean.FALSE;
                }

                orden++;
                CampoObligatoriedad campo = new CampoObligatoriedad();
                campo.setNombreCampo(nombreCampo.trim());
                campo.setObligatorio(obligatorio);
                campo.setNota(nota != null && !nota.trim().isEmpty() ? nota.trim() : null);
                campo.setOrden(orden);
                // seccion y contexto: en este archivo no hay columna de sección explícita;
                // se usa la nota como contexto cuando está presente
                campo.setContexto(nota != null && !nota.trim().isEmpty() ? nota.trim() : null);

                campos.add(campo);
            }

            // Persistir todos los campos
            repository.saveAll(campos);

            // Calcular conteos
            long obligatorios = campos.stream().filter(c -> Boolean.TRUE.equals(c.getObligatorio())).count();
            long opcionales   = campos.stream().filter(c -> Boolean.FALSE.equals(c.getObligatorio())).count();

            resultado.setTotalLeidos(campos.size() + advertencias.size());
            resultado.setTotalPersistidos(campos.size());
            resultado.setTotalObligatorios((int) obligatorios);
            resultado.setTotalOpcionales((int) opcionales);
            resultado.setAdvertencias(advertencias);
        }

        return resultado;
    }

    /**
     * Extrae el texto de una celda de forma segura, independientemente del tipo.
     */
    private String getCellText(Row row, int colIdx) {
        Cell cell = row.getCell(colIdx);
        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            double val = cell.getNumericCellValue();
            if (val == Math.floor(val)) {
                return String.valueOf((long) val);
            }
            return String.valueOf(val);
        }
        if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        }
        return cell.toString();
    }
}

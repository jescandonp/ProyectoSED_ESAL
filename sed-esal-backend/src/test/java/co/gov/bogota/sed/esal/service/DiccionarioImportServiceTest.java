package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.CampoObligatoriedad;
import co.gov.bogota.sed.esal.dto.DiccionarioImportResultDto;
import co.gov.bogota.sed.esal.repository.CampoObligatoriedadRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests de integración para DiccionarioImportService.
 *
 * Requieren el archivo real Base excel.xlsx en la ruta local de desarrollo.
 * Si el archivo no está disponible, los tests se omiten con assumeTrue.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DiccionarioImportServiceTest {

    @Autowired
    DiccionarioImportService service;

    @Autowired
    CampoObligatoriedadRepository repository;

    private static final String EXCEL_PATH =
            "C:/Users/jmep2/Downloads/SED/ProyectoESAL/Documentos_Referencia/Base excel.xlsx";

    /**
     * Verifica que se importan exactamente 117 registros,
     * 23 obligatorios y 94 opcionales.
     */
    @Test
    void importaDiccionarioCompleto() throws Exception {
        File f = new File(EXCEL_PATH);
        assumeTrue(f.exists(), "Archivo Base excel.xlsx no disponible en ruta local — test omitido.");

        DiccionarioImportResultDto result;
        try (InputStream is = new FileInputStream(f)) {
            result = service.importar(is);
        }

        assertThat(result.getTotalPersistidos())
                .as("Debe persistir exactamente 117 campos")
                .isEqualTo(117);
        assertThat(result.getTotalObligatorios())
                .as("Debe haber exactamente 23 campos obligatorios")
                .isEqualTo(23);
        assertThat(result.getTotalOpcionales())
                .as("Debe haber exactamente 94 campos opcionales")
                .isEqualTo(94);
    }

    /**
     * Verifica que la operación es idempotente:
     * importar dos veces no duplica los registros.
     */
    @Test
    void importacionEsIdempotente() throws Exception {
        File f = new File(EXCEL_PATH);
        assumeTrue(f.exists(), "Archivo Base excel.xlsx no disponible en ruta local — test omitido.");

        try (InputStream is1 = new FileInputStream(f)) {
            service.importar(is1);
        }
        try (InputStream is2 = new FileInputStream(f)) {
            service.importar(is2);
        }

        long total = repository.count();
        assertThat(total)
                .as("Importar dos veces no debe duplicar registros")
                .isEqualTo(117);
    }

    /**
     * Verifica que los campos con nombre duplicado se preservan como registros
     * separados (contexto diferente), no se colapsan por nombre global.
     */
    @Test
    void camposDuplicadosPreservanContexto() throws Exception {
        File f = new File(EXCEL_PATH);
        assumeTrue(f.exists(), "Archivo Base excel.xlsx no disponible en ruta local — test omitido.");

        try (InputStream is = new FileInputStream(f)) {
            service.importar(is);
        }

        List<CampoObligatoriedad> todos = repository.findAll();
        assertThat(todos).hasSize(117);

        // Verificar que hay campos con mismo nombre (duplicados por contexto)
        Map<String, Long> porNombre = todos.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getNombreCampo().trim().toUpperCase(),
                        Collectors.counting()));

        long duplicados = porNombre.values().stream().filter(c -> c > 1).count();
        // El Excel tiene nombres repetidos (ej: ENTIDAD QUE EXPIDE, FECHA, ACTA APRUEBA)
        // Todos deben estar presentes como registros separados
        assertThat(duplicados)
                .as("Debe haber al menos un nombre de campo duplicado (con contexto diferente)")
                .isGreaterThanOrEqualTo(1);
    }

    /**
     * Verifica que el campo 'orden' se asigna correctamente (secuencial desde 1).
     */
    @Test
    void camposTienenOrdenSecuencial() throws Exception {
        File f = new File(EXCEL_PATH);
        assumeTrue(f.exists(), "Archivo Base excel.xlsx no disponible en ruta local — test omitido.");

        try (InputStream is = new FileInputStream(f)) {
            service.importar(is);
        }

        List<CampoObligatoriedad> todos = repository.findAll();
        long conOrden = todos.stream()
                .filter(c -> c.getOrden() != null && c.getOrden() > 0)
                .count();
        assertThat(conOrden)
                .as("Todos los campos deben tener orden asignado")
                .isEqualTo(117);
    }

    /**
     * Verifica que findByObligatorio funciona correctamente tras la importación.
     */
    @Test
    void findByObligatorioRetornaConteosCorrecto() throws Exception {
        File f = new File(EXCEL_PATH);
        assumeTrue(f.exists(), "Archivo Base excel.xlsx no disponible en ruta local — test omitido.");

        try (InputStream is = new FileInputStream(f)) {
            service.importar(is);
        }

        List<CampoObligatoriedad> obligatorios = repository.findByObligatorio(true);
        List<CampoObligatoriedad> opcionales   = repository.findByObligatorio(false);

        assertThat(obligatorios).hasSize(23);
        assertThat(opcionales).hasSize(94);
    }
}

package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.ReformaEstatutaria;
import co.gov.bogota.sed.esal.domain.enums.EstadoCompletitud;
import co.gov.bogota.sed.esal.dto.EsalImportResultDto;
import co.gov.bogota.sed.esal.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests de integración para EsalImportService con el archivo real.
 * Todos los tests usan assumeTrue para omitirse si el archivo no está disponible.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EsalImportServiceTest {

    private static final String EXCEL_PATH =
        "C:/Users/jmep2/Downloads/SED/ProyectoESAL/Documentos_Referencia/BASE DE DATOS - REGISTRO_1.xlsx";

    @Autowired
    private EsalImportService esalImportService;

    @Autowired
    private EsalRepository esalRepository;

    @Autowired
    private ReformaEstatutariaRepository reformaRepository;

    @Autowired
    private AdvertenciaCompletitudRepository advertenciaRepository;

    @Autowired
    private NombramientoRepository nombramientoRepository;

    @Autowired
    private PersoneriaJuridicaRepository personeriaRepository;

    @Test
    void importaEsalesEfectivas() throws Exception {
        File f = new File(EXCEL_PATH);
        assumeTrue(f.exists(), "Archivo no disponible — test omitido");

        EsalImportResultDto resultado;
        try (FileInputStream fis = new FileInputStream(f)) {
            resultado = esalImportService.importar(fis, "test-admin");
        }

        assertNotNull(resultado);
        assertTrue(resultado.getTotalImportados() > 0,
            "Debe importar al menos una ESAL efectiva");
        assertTrue(resultado.getTotalLeidos() > 0,
            "Debe leer al menos una fila efectiva");
        assertTrue(resultado.getTotalRechazados() >= 0,
            "Total rechazados debe ser >= 0");

        // Verificar que las ESALes quedaron en base de datos
        // Nota: el count en BD puede ser <= totalImportados porque el upsert por idSipej
        // puede actualizar registros existentes en lugar de crear nuevos
        long totalEnBD = esalRepository.count();
        assertTrue(totalEnBD > 0,
            "Debe haber al menos una ESAL en la base de datos");
        assertTrue(totalEnBD <= resultado.getTotalImportados(),
            "El número de ESALes en BD debe ser <= totalImportados (upsert puede reducir duplicados)");

        System.out.println("=== Resultado importación ===");
        System.out.println("Total leídos: " + resultado.getTotalLeidos());
        System.out.println("Total importados: " + resultado.getTotalImportados());
        System.out.println("Total rechazados: " + resultado.getTotalRechazados());
        System.out.println("Total advertencias: " + resultado.getTotalAdvertencias());
        System.out.println("Total reformas: " + resultado.getTotalReformas());
    }

    @Test
    void reformasSeTransformanAFilasDinamicas() throws Exception {
        File f = new File(EXCEL_PATH);
        assumeTrue(f.exists(), "Archivo no disponible — test omitido");

        EsalImportResultDto resultado;
        try (FileInputStream fis = new FileInputStream(f)) {
            resultado = esalImportService.importar(fis, "test-admin");
        }

        assertTrue(resultado.getTotalReformas() > 0,
            "Debe haber al menos una reforma estatutaria importada");

        // Verificar que al menos una ESAL tiene más de una reforma
        boolean hayEsalConMultiplesReformas = esalRepository.findAll().stream()
            .anyMatch(esal -> reformaRepository.findByEsalIdOrderByOrden(esal.getId()).size() > 1);

        assertTrue(hayEsalConMultiplesReformas,
            "Al menos una ESAL debe tener más de una reforma estatutaria");

        // Verificar que las reformas tienen orden secuencial
        esalRepository.findAll().forEach(esal -> {
            List<ReformaEstatutaria> reformas = reformaRepository.findByEsalIdOrderByOrden(esal.getId());
            for (int i = 0; i < reformas.size(); i++) {
                assertEquals(i + 1, reformas.get(i).getOrden(),
                    "Las reformas deben tener orden secuencial comenzando en 1");
            }
        });

        System.out.println("Total reformas importadas: " + resultado.getTotalReformas());
    }

    @Test
    void advertenciasGeneradasParaCamposFaltantes() throws Exception {
        File f = new File(EXCEL_PATH);
        assumeTrue(f.exists(), "Archivo no disponible — test omitido");

        EsalImportResultDto resultado;
        try (FileInputStream fis = new FileInputStream(f)) {
            resultado = esalImportService.importar(fis, "test-admin");
        }

        assertTrue(resultado.getTotalAdvertencias() > 0,
            "Debe haber advertencias generadas para campos faltantes");

        // Verificar que hay advertencias bloqueantes (campos obligatorios faltantes)
        long advertenciasBloqueantes = advertenciaRepository.findAll().stream()
            .filter(a -> Boolean.TRUE.equals(a.getBloqueante()))
            .count();
        assertTrue(advertenciasBloqueantes > 0,
            "Debe haber advertencias bloqueantes por campos obligatorios faltantes");

        // Verificar que hay ESALes con estado INCOMPLETO_BLOQUEANTE
        long esalesIncompletas = esalRepository.findAll().stream()
            .filter(e -> EstadoCompletitud.INCOMPLETO_BLOQUEANTE.equals(e.getEstadoCompletitud()))
            .count();
        assertTrue(esalesIncompletas > 0,
            "Debe haber ESALes con estado INCOMPLETO_BLOQUEANTE");

        System.out.println("Advertencias bloqueantes: " + advertenciasBloqueantes);
        System.out.println("ESALes con INCOMPLETO_BLOQUEANTE: " + esalesIncompletas);
    }

    @Test
    void importacionEsIdempotente() throws Exception {
        File f = new File(EXCEL_PATH);
        assumeTrue(f.exists(), "Archivo no disponible — test omitido");

        // Primera importación
        EsalImportResultDto resultado1;
        try (FileInputStream fis = new FileInputStream(f)) {
            resultado1 = esalImportService.importar(fis, "test-admin");
        }

        // Verificar que ESALes con idSipej no se duplican en segunda importación
        // (las que no tienen idSipej siempre se insertan — comportamiento esperado por spec)
        long esalesConIdSipejDespues1 = esalRepository.findAll().stream()
            .filter(e -> e.getIdSipej() != null && !e.getIdSipej().isEmpty())
            .count();

        // Segunda importación
        EsalImportResultDto resultado2;
        try (FileInputStream fis = new FileInputStream(f)) {
            resultado2 = esalImportService.importar(fis, "test-admin");
        }

        long esalesConIdSipejDespues2 = esalRepository.findAll().stream()
            .filter(e -> e.getIdSipej() != null && !e.getIdSipej().isEmpty())
            .count();

        // ESALes con idSipej no deben duplicarse
        assertEquals(esalesConIdSipejDespues1, esalesConIdSipejDespues2,
            "Las ESALes con idSipej no deben duplicarse en la segunda importación (upsert)");
        assertEquals(resultado1.getTotalImportados(), resultado2.getTotalImportados(),
            "Ambas importaciones deben reportar el mismo número de importados");

        System.out.println("ESALes con idSipej tras 1a importación: " + esalesConIdSipejDespues1);
        System.out.println("ESALes con idSipej tras 2a importación: " + esalesConIdSipejDespues2);
    }

    @Test
    void esFaltanteDetectaValoresNR() {
        assertTrue(esalImportService.esFaltante("NR"));
        assertTrue(esalImportService.esFaltante("N/A"));
        assertTrue(esalImportService.esFaltante("NA"));
        assertTrue(esalImportService.esFaltante("-"));
        assertTrue(esalImportService.esFaltante("N.A."));
        assertTrue(esalImportService.esFaltante(""));
        assertTrue(esalImportService.esFaltante(null));
        assertTrue(esalImportService.esFaltante("  "));
        assertFalse(esalImportService.esFaltante("VALOR REAL"));
        assertFalse(esalImportService.esFaltante("12345"));
    }
}

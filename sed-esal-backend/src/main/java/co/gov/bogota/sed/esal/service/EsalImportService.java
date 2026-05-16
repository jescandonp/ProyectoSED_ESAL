package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.*;
import co.gov.bogota.sed.esal.domain.enums.*;
import co.gov.bogota.sed.esal.dto.EsalImportResultDto;
import co.gov.bogota.sed.esal.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de importación histórica de ESALes desde BASE DE DATOS - REGISTRO_1.xlsx.
 *
 * Estructura del Excel (índices 0-based):
 *   Fila 0 (row 1): secciones — INFORMACION PRINCIPAL (col0), CONSTITUCION Y REFORMAS (col8),
 *                               NOMBRAMIENTOS (col50), ORGANO DE ADMINISTRACION (col95),
 *                               ESTADO DE LIQUIDACION (col105), CANCELACION (col107)
 *   Fila 1 (row 2): encabezados de columna
 *   Filas 2+ (row 3+): datos
 *
 * Columnas clave (0-based):
 *   0=No., 1=NOMBRE, 2=ID SIPEJ, 3=NIT, 4=DOMICILIO, 5=CORREO ELECTRONICO,
 *   6=TERMINO DE DURACION, 7=OBJETO SOCIAL,
 *   8=INSCRIPCION, 9=FECHA DE INSCRIPCION, 10=ENTIDAD QUE INSCRIBIO,
 *   11=RECONOCIMIENTO DE PERSONERIA JURIDICA, 12=FECHA RECONOCIMIENTO, 13=ENTIDAD QUE EXPIDE,
 *   14=RESOLUCION ACLARATORIA, 15=FECHA RESOLUCION ACLARATORIA, 16=ENTIDAD QUE EXPIDE,
 *   Reformas: 1a(17-25), 2a(26-28), 3a(29-34), 4a(35-37), 5a(38-40), 6a(41-43), 7a(44-46), 8a(47-49)
 *   50=REPRESENTANTE LEGAL, 51=TIPO DOC, 52=NUM DOC, 53=ACTA APRUEBA, 54=FECHA,
 *   55-59=SUPLENTE 1, 60-64=SUPLENTE 2, 65-69=SUPLENTE 3,
 *   70=FACULTADES Y LIMITACIONES RL,
 *   71=REVISORIA FISCAL (NIT), 72=NIT, 73=ACTA APRUEBA, 74=FECHA,
 *   75=REVISOR FISCAL PRINCIPAL, 76=TIPO DOC, 77=NUM DOC, 78=TARJETA PROF, 79=ACTA, 80=FECHA,
 *   81=ACTA ACLARATORIA, 82=FECHA,
 *   83=REVISOR FISCAL SUPLENTE, 84=TIPO DOC, 85=NUM DOC, 86=TARJETA PROF, 87=ACTA, 88=FECHA,
 *   89=TESORERO, 90=TIPO DOC, 91=NUM DOC, 92=ACTA APRUEBA, 93=FECHA, 94=FACULTADES TESORERO,
 *   95=ORGANO, 96=MIEMBRO, 97=CARGO, 98=TIPO DOC, 99=NUM DOC, 100=ACTA APRUEBA, 101=FECHA,
 *   102=ACTA ACLARATORIA, 103=FECHA, 104=FACULTADES ORGANO,
 *   105=ACTA LIQUIDACION, 106=FECHA LIQUIDACION,
 *   107=RESOLUCION CANCELACION, 108=FECHA CANCELACION, 109=MOTIVO CANCELACION
 */
@Service
@Transactional
public class EsalImportService {

    // Índices de columna (0-based)
    private static final int COL_NOMBRE = 1;
    private static final int COL_ID_SIPEJ = 2;
    private static final int COL_NIT = 3;
    private static final int COL_DOMICILIO = 4;
    private static final int COL_CORREO = 5;
    private static final int COL_TERMINO = 6;
    private static final int COL_OBJETO = 7;

    // Personería jurídica
    private static final int COL_INSCRIPCION = 8;
    private static final int COL_FECHA_INSCRIPCION = 9;
    private static final int COL_ENTIDAD_INSCRIBIO = 10;
    private static final int COL_RECONOCIMIENTO_PJ = 11;
    private static final int COL_FECHA_RECONOCIMIENTO_PJ = 12;
    private static final int COL_ENTIDAD_EXPIDE_PJ = 13;
    private static final int COL_RESOLUCION_ACLARATORIA_PJ = 14;
    private static final int COL_FECHA_RESOLUCION_ACLARATORIA_PJ = 15;
    private static final int COL_ENTIDAD_EXPIDE_ACLARATORIA_PJ = 16;

    // Reformas estatutarias (cada reforma: numero_acto, fecha, entidad_expide)
    // 1a reforma: cols 17,18,19 + aclaratoria 20,21,22 + adicionada 23,24,25
    // 2a reforma: cols 26,27,28
    // 3a reforma: cols 29,30,31 + aclaratoria 32,33,34
    // 4a reforma: cols 35,36,37
    // 5a reforma: cols 38,39,40
    // 6a reforma: cols 41,42,43
    // 7a reforma: cols 44,45,46
    // 8a reforma: cols 47,48,49
    private static final int[][] REFORMA_COLS = {
        {17, 18, 19},  // 1a: numero_acto, fecha, entidad
        {26, 27, 28},  // 2a
        {29, 30, 31},  // 3a
        {35, 36, 37},  // 4a
        {38, 39, 40},  // 5a
        {41, 42, 43},  // 6a
        {44, 45, 46},  // 7a
        {47, 48, 49}   // 8a
    };

    // Representante legal
    private static final int COL_RL_NOMBRE = 50;
    private static final int COL_RL_TIPO_DOC = 51;
    private static final int COL_RL_NUM_DOC = 52;
    private static final int COL_RL_ACTA = 53;
    private static final int COL_RL_FECHA = 54;

    // Representantes legales suplentes (3 suplentes)
    private static final int[][] SUPLENTE_COLS = {
        {55, 56, 57, 58, 59},  // suplente 1: nombre, tipo_doc, num_doc, acta, fecha
        {60, 61, 62, 63, 64},  // suplente 2
        {65, 66, 67, 68, 69}   // suplente 3
    };

    private static final int COL_RL_FACULTADES = 70;

    // Revisor fiscal principal
    private static final int COL_RF_PRINCIPAL_NOMBRE = 75;
    private static final int COL_RF_PRINCIPAL_TIPO_DOC = 76;
    private static final int COL_RF_PRINCIPAL_NUM_DOC = 77;
    private static final int COL_RF_PRINCIPAL_TARJETA = 78;
    private static final int COL_RF_PRINCIPAL_ACTA = 79;
    private static final int COL_RF_PRINCIPAL_FECHA = 80;

    // Revisor fiscal suplente
    private static final int COL_RF_SUPLENTE_NOMBRE = 83;
    private static final int COL_RF_SUPLENTE_TIPO_DOC = 84;
    private static final int COL_RF_SUPLENTE_NUM_DOC = 85;
    private static final int COL_RF_SUPLENTE_TARJETA = 86;
    private static final int COL_RF_SUPLENTE_ACTA = 87;
    private static final int COL_RF_SUPLENTE_FECHA = 88;

    // Tesorero
    private static final int COL_TESORERO_NOMBRE = 89;
    private static final int COL_TESORERO_TIPO_DOC = 90;
    private static final int COL_TESORERO_NUM_DOC = 91;
    private static final int COL_TESORERO_ACTA = 92;
    private static final int COL_TESORERO_FECHA = 93;
    private static final int COL_TESORERO_FACULTADES = 94;

    // Órgano de administración
    private static final int COL_ORGANO = 95;
    private static final int COL_ORGANO_MIEMBRO = 96;
    private static final int COL_ORGANO_CARGO = 97;
    private static final int COL_ORGANO_TIPO_DOC = 98;
    private static final int COL_ORGANO_NUM_DOC = 99;
    private static final int COL_ORGANO_ACTA = 100;
    private static final int COL_ORGANO_FECHA = 101;
    private static final int COL_ORGANO_ACTA_ACLARATORIA = 102;
    private static final int COL_ORGANO_FECHA_ACLARATORIA = 103;
    private static final int COL_ORGANO_FACULTADES = 104;

    // Estado de liquidación
    private static final int COL_LIQUIDACION_ACTA = 105;
    private static final int COL_LIQUIDACION_FECHA = 106;

    // Cancelación
    private static final int COL_CANCELACION_RESOLUCION = 107;
    private static final int COL_CANCELACION_FECHA = 108;
    private static final int COL_CANCELACION_MOTIVO = 109;


    private final EsalRepository esalRepository;
    private final PersoneriaJuridicaRepository personeriaRepository;
    private final ReformaEstatutariaRepository reformaRepository;
    private final NombramientoRepository nombramientoRepository;
    private final OrganoAdministracionRepository organoRepository;
    private final AdvertenciaCompletitudRepository advertenciaRepository;
    private final ActuacionAdministrativaRepository actuacionRepository;

    public EsalImportService(
            EsalRepository esalRepository,
            PersoneriaJuridicaRepository personeriaRepository,
            ReformaEstatutariaRepository reformaRepository,
            NombramientoRepository nombramientoRepository,
            OrganoAdministracionRepository organoRepository,
            AdvertenciaCompletitudRepository advertenciaRepository,
            ActuacionAdministrativaRepository actuacionRepository) {
        this.esalRepository = esalRepository;
        this.personeriaRepository = personeriaRepository;
        this.reformaRepository = reformaRepository;
        this.nombramientoRepository = nombramientoRepository;
        this.organoRepository = organoRepository;
        this.advertenciaRepository = advertenciaRepository;
        this.actuacionRepository = actuacionRepository;
    }

    /**
     * Importa ESALes desde el InputStream del archivo Excel.
     * Estrategia: upsert por idSipej (si existe, actualiza; si no, inserta).
     * Las advertencias se regeneran en cada importación.
     *
     * @param excelStream  InputStream del archivo BASE DE DATOS - REGISTRO_1.xlsx
     * @param importadoPor usuario que ejecuta la importación
     * @return resumen de la importación
     */
    public EsalImportResultDto importar(InputStream excelStream, String importadoPor) throws IOException {
        EsalImportResultDto resultado = new EsalImportResultDto();
        resultado.setFechaImportacion(LocalDateTime.now());
        resultado.setImportadoPor(importadoPor);

        List<String> mensajesAdvertencia = new ArrayList<>();
        int totalLeidos = 0;
        int totalImportados = 0;
        int totalRechazados = 0;
        int totalAdvertencias = 0;
        int totalReformas = 0;

        try (Workbook workbook = new XSSFWorkbook(excelStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            int lastRow = sheet.getLastRowNum();

            // Iterar desde fila 2 (índice 2) — fila 0 es secciones, fila 1 es encabezados
            for (int rowIdx = 2; rowIdx <= lastRow; rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) continue;

                String nombre = getCellText(row, COL_NOMBRE);
                String idSipej = getCellText(row, COL_ID_SIPEJ);

                // Solo importar filas con NOMBRE o ID SIPEJ
                boolean tieneNombre = !esFaltante(nombre);
                boolean tieneIdSipej = !esFaltante(idSipej);

                if (!tieneNombre && !tieneIdSipej) {
                    totalRechazados++;
                    continue;
                }

                totalLeidos++;

                // Upsert por idSipej
                Esal esal;
                boolean esNueva = false;
                if (tieneIdSipej) {
                    Optional<Esal> existente = esalRepository.findByIdSipej(idSipej.trim());
                    if (existente.isPresent()) {
                        esal = existente.get();
                        esal.setUpdatedAt(LocalDateTime.now());
                        esal.setUpdatedBy(importadoPor);
                        // Limpiar advertencias previas para regenerar
                        advertenciaRepository.findByEsalId(esal.getId())
                            .forEach(a -> advertenciaRepository.delete(a));
                    } else {
                        esal = new Esal();
                        esal.setCreatedAt(LocalDateTime.now());
                        esal.setCreatedBy(importadoPor);
                        esNueva = true;
                    }
                } else {
                    // Sin idSipej: siempre insertar
                    esal = new Esal();
                    esal.setCreatedAt(LocalDateTime.now());
                    esal.setCreatedBy(importadoPor);
                    esNueva = true;
                }

                // Datos básicos
                esal.setNombre(tieneNombre ? nombre.trim().substring(0, Math.min(nombre.trim().length(), 500)) : "(SIN NOMBRE)");
                esal.setIdSipej(tieneIdSipej ? idSipej.trim().substring(0, Math.min(idSipej.trim().length(), 100)) : null);
                esal.setNit(normalizarTruncado(getCellText(row, COL_NIT), 50));
                esal.setDomicilio(normalizarTruncado(getCellText(row, COL_DOMICILIO), 500));
                esal.setCorreoElectronico(normalizarTruncado(getCellText(row, COL_CORREO), 255));
                esal.setTerminoDuracion(normalizarTruncado(getCellText(row, COL_TERMINO), 255));
                esal.setObjetoSocial(normalizar(getCellText(row, COL_OBJETO)));
                esal.setEstado(EstadoEsal.ACTIVO);
                esal.setEstadoCompletitud(EstadoCompletitud.INCOMPLETO_BLOQUEANTE);

                esal = esalRepository.save(esal);
                Long esalId = esal.getId();

                // Si es actualización, limpiar datos relacionados para regenerar
                if (!esNueva) {
                    personeriaRepository.findByEsalId(esalId)
                        .forEach(p -> personeriaRepository.delete(p));
                    reformaRepository.findByEsalIdOrderByOrden(esalId)
                        .forEach(r -> reformaRepository.delete(r));
                    nombramientoRepository.findByEsalId(esalId)
                        .forEach(n -> nombramientoRepository.delete(n));
                    organoRepository.findByEsalId(esalId)
                        .forEach(o -> organoRepository.delete(o));
                }

                // Personería jurídica
                PersoneriaJuridica pj = new PersoneriaJuridica();
                pj.setEsalId(esalId);
                pj.setInscripcion(normalizarTruncado(getCellText(row, COL_INSCRIPCION), 500));
                pj.setFechaInscripcion(parseFecha(getCellText(row, COL_FECHA_INSCRIPCION)));
                pj.setEntidadQueInscribio(normalizarTruncado(getCellText(row, COL_ENTIDAD_INSCRIBIO), 500));
                pj.setReconocimientoPersoneriaJuridica(normalizarTruncado(getCellText(row, COL_RECONOCIMIENTO_PJ), 500));
                pj.setFechaReconocimientoPersoneriaJuridica(parseFecha(getCellText(row, COL_FECHA_RECONOCIMIENTO_PJ)));
                pj.setEntidadQueExpide(normalizarTruncado(getCellText(row, COL_ENTIDAD_EXPIDE_PJ), 500));
                personeriaRepository.save(pj);

                // Reformas estatutarias
                int ordenReforma = 0;
                for (int[] cols : REFORMA_COLS) {
                    String numeroActo = normalizarTruncado(getCellText(row, cols[0]), 255);
                    String fechaActoStr = getCellText(row, cols[1]);
                    String entidadExpide = normalizarTruncado(getCellText(row, cols[2]), 500);

                    if (numeroActo != null || !esFaltante(fechaActoStr) || entidadExpide != null) {
                        ordenReforma++;
                        ReformaEstatutaria reforma = new ReformaEstatutaria();
                        reforma.setEsalId(esalId);
                        reforma.setOrden(ordenReforma);
                        reforma.setNumeroActo(numeroActo);
                        reforma.setFechaActo(parseFecha(fechaActoStr));
                        reforma.setEntidadQueExpide(entidadExpide);
                        reformaRepository.save(reforma);
                        totalReformas++;
                    }
                }

                // Representante legal principal
                String rlNombre = normalizar(getCellText(row, COL_RL_NOMBRE));
                if (rlNombre != null) {
                    Nombramiento rl = new Nombramiento();
                    rl.setEsalId(esalId);
                    rl.setTipoNombramiento(TipoNombramiento.REPRESENTANTE_LEGAL);
                    rl.setNombre(rlNombre.length() > 500 ? rlNombre.substring(0, 500) : rlNombre);
                    rl.setTipoDocumento(normalizar(getCellText(row, COL_RL_TIPO_DOC)));
                    rl.setNumeroDocumento(normalizar(getCellText(row, COL_RL_NUM_DOC)));
                    rl.setActaAprueba(normalizar(getCellText(row, COL_RL_ACTA)));
                    rl.setFechaActa(parseFecha(getCellText(row, COL_RL_FECHA)));
                    rl.setFacultadesLimitaciones(normalizarTruncado(getCellText(row, COL_RL_FACULTADES), 1000));
                    rl.setVigente(Boolean.TRUE);
                    nombramientoRepository.save(rl);
                }

                // Representantes legales suplentes
                for (int[] cols : SUPLENTE_COLS) {
                    String sNombre = normalizar(getCellText(row, cols[0]));
                    if (sNombre != null) {
                        Nombramiento suplente = new Nombramiento();
                        suplente.setEsalId(esalId);
                        suplente.setTipoNombramiento(TipoNombramiento.REPRESENTANTE_LEGAL_SUPLENTE);
                        suplente.setNombre(sNombre);
                        suplente.setTipoDocumento(normalizar(getCellText(row, cols[1])));
                        suplente.setNumeroDocumento(normalizar(getCellText(row, cols[2])));
                        suplente.setActaAprueba(normalizar(getCellText(row, cols[3])));
                        suplente.setFechaActa(parseFecha(getCellText(row, cols[4])));
                        suplente.setVigente(Boolean.TRUE);
                        nombramientoRepository.save(suplente);
                    }
                }

                // Revisor fiscal principal
                String rfPNombre = normalizar(getCellText(row, COL_RF_PRINCIPAL_NOMBRE));
                if (rfPNombre != null) {
                    Nombramiento rfP = new Nombramiento();
                    rfP.setEsalId(esalId);
                    rfP.setTipoNombramiento(TipoNombramiento.REVISOR_FISCAL_PRINCIPAL);
                    rfP.setNombre(rfPNombre);
                    rfP.setTipoDocumento(normalizar(getCellText(row, COL_RF_PRINCIPAL_TIPO_DOC)));
                    rfP.setNumeroDocumento(normalizar(getCellText(row, COL_RF_PRINCIPAL_NUM_DOC)));
                    rfP.setTarjetaProfesional(normalizar(getCellText(row, COL_RF_PRINCIPAL_TARJETA)));
                    rfP.setActaAprueba(normalizar(getCellText(row, COL_RF_PRINCIPAL_ACTA)));
                    rfP.setFechaActa(parseFecha(getCellText(row, COL_RF_PRINCIPAL_FECHA)));
                    rfP.setVigente(Boolean.TRUE);
                    nombramientoRepository.save(rfP);
                }

                // Revisor fiscal suplente
                String rfSNombre = normalizar(getCellText(row, COL_RF_SUPLENTE_NOMBRE));
                if (rfSNombre != null) {
                    Nombramiento rfS = new Nombramiento();
                    rfS.setEsalId(esalId);
                    rfS.setTipoNombramiento(TipoNombramiento.REVISOR_FISCAL_SUPLENTE);
                    rfS.setNombre(rfSNombre);
                    rfS.setTipoDocumento(normalizar(getCellText(row, COL_RF_SUPLENTE_TIPO_DOC)));
                    rfS.setNumeroDocumento(normalizar(getCellText(row, COL_RF_SUPLENTE_NUM_DOC)));
                    rfS.setTarjetaProfesional(normalizar(getCellText(row, COL_RF_SUPLENTE_TARJETA)));
                    rfS.setActaAprueba(normalizar(getCellText(row, COL_RF_SUPLENTE_ACTA)));
                    rfS.setFechaActa(parseFecha(getCellText(row, COL_RF_SUPLENTE_FECHA)));
                    rfS.setVigente(Boolean.TRUE);
                    nombramientoRepository.save(rfS);
                }

                // Tesorero
                String tesoreroNombre = normalizar(getCellText(row, COL_TESORERO_NOMBRE));
                if (tesoreroNombre != null) {
                    Nombramiento tesorero = new Nombramiento();
                    tesorero.setEsalId(esalId);
                    tesorero.setTipoNombramiento(TipoNombramiento.TESORERO);
                    tesorero.setNombre(tesoreroNombre);
                    tesorero.setTipoDocumento(normalizar(getCellText(row, COL_TESORERO_TIPO_DOC)));
                    tesorero.setNumeroDocumento(normalizar(getCellText(row, COL_TESORERO_NUM_DOC)));
                    tesorero.setActaAprueba(normalizar(getCellText(row, COL_TESORERO_ACTA)));
                    tesorero.setFechaActa(parseFecha(getCellText(row, COL_TESORERO_FECHA)));
                    tesorero.setFacultadesLimitaciones(normalizarTruncado(getCellText(row, COL_TESORERO_FACULTADES), 1000));
                    tesorero.setVigente(Boolean.TRUE);
                    nombramientoRepository.save(tesorero);
                }

                // Órgano de administración
                String organoNombre = normalizar(getCellText(row, COL_ORGANO));
                String organoMiembro = normalizar(getCellText(row, COL_ORGANO_MIEMBRO));
                if (organoNombre != null || organoMiembro != null) {
                    OrganoAdministracion organo = new OrganoAdministracion();
                    organo.setEsalId(esalId);
                    organo.setOrgano(organoNombre);
                    organo.setMiembro(organoMiembro);
                    organo.setCargo(normalizar(getCellText(row, COL_ORGANO_CARGO)));
                    organo.setTipoDocumento(normalizar(getCellText(row, COL_ORGANO_TIPO_DOC)));
                    organo.setNumeroDocumento(normalizar(getCellText(row, COL_ORGANO_NUM_DOC)));
                    organo.setActaAprueba(normalizar(getCellText(row, COL_ORGANO_ACTA)));
                    organo.setFechaActa(parseFecha(getCellText(row, COL_ORGANO_FECHA)));
                    organo.setActaAclaratoria(normalizar(getCellText(row, COL_ORGANO_ACTA_ACLARATORIA)));
                    organo.setFechaActaAclaratoria(parseFecha(getCellText(row, COL_ORGANO_FECHA_ACLARATORIA)));
                    organo.setFacultadesLimitaciones(normalizarTruncado(getCellText(row, COL_ORGANO_FACULTADES), 1000));
                    organoRepository.save(organo);
                }

                // Actuaciones administrativas (liquidación y cancelación)
                String actaLiquidacion = normalizar(getCellText(row, COL_LIQUIDACION_ACTA));
                String fechaLiquidacionStr = getCellText(row, COL_LIQUIDACION_FECHA);
                if (actaLiquidacion != null || !esFaltante(fechaLiquidacionStr)) {
                    ActuacionAdministrativa liquidacion = new ActuacionAdministrativa();
                    liquidacion.setEsalId(esalId);
                    liquidacion.setTipoActuacion(TipoActuacion.LIQUIDACION);
                    liquidacion.setActa(actaLiquidacion);
                    liquidacion.setFechaActa(parseFecha(fechaLiquidacionStr));
                    actuacionRepository.save(liquidacion);
                    esal.setEstado(EstadoEsal.EN_LIQUIDACION);
                    esalRepository.save(esal);
                }

                String resolucionCancelacion = normalizar(getCellText(row, COL_CANCELACION_RESOLUCION));
                String fechaCancelacionStr = getCellText(row, COL_CANCELACION_FECHA);
                String motivoCancelacion = normalizar(getCellText(row, COL_CANCELACION_MOTIVO));
                if (resolucionCancelacion != null || !esFaltante(fechaCancelacionStr)) {
                    ActuacionAdministrativa cancelacion = new ActuacionAdministrativa();
                    cancelacion.setEsalId(esalId);
                    cancelacion.setTipoActuacion(TipoActuacion.CANCELACION);
                    cancelacion.setResolucion(resolucionCancelacion);
                    cancelacion.setFechaResolucion(parseFecha(fechaCancelacionStr));
                    cancelacion.setMotivo(normalizarTruncado(motivoCancelacion, 1000));
                    actuacionRepository.save(cancelacion);
                    esal.setEstado(EstadoEsal.CANCELADO);
                    esalRepository.save(esal);
                }

                // Generar advertencias de completitud
                List<AdvertenciaCompletitud> advertencias = generarAdvertencias(esal, row, rowIdx + 1);
                advertencias.forEach(a -> advertenciaRepository.save(a));
                totalAdvertencias += advertencias.size();

                // Calcular estado de completitud
                boolean tieneBloqueante = advertencias.stream().anyMatch(a -> Boolean.TRUE.equals(a.getBloqueante()));
                boolean tieneNoBloqueante = advertencias.stream().anyMatch(a -> Boolean.FALSE.equals(a.getBloqueante()));
                if (tieneBloqueante) {
                    esal.setEstadoCompletitud(EstadoCompletitud.INCOMPLETO_BLOQUEANTE);
                } else if (tieneNoBloqueante) {
                    esal.setEstadoCompletitud(EstadoCompletitud.INCOMPLETO_NO_BLOQUEANTE);
                } else {
                    esal.setEstadoCompletitud(EstadoCompletitud.LISTO_PARA_CERTIFICAR);
                }
                esalRepository.save(esal);

                totalImportados++;
            }
        }

        resultado.setTotalLeidos(totalLeidos);
        resultado.setTotalImportados(totalImportados);
        resultado.setTotalRechazados(totalRechazados);
        resultado.setTotalAdvertencias(totalAdvertencias);
        resultado.setTotalReformas(totalReformas);
        resultado.setAdvertencias(mensajesAdvertencia);

        return resultado;
    }


    /**
     * Genera advertencias de completitud para una ESAL importada.
     */
    private List<AdvertenciaCompletitud> generarAdvertencias(Esal esal, Row row, int numeroFila) {
        List<AdvertenciaCompletitud> advertencias = new ArrayList<>();
        Long esalId = esal.getId();
        LocalDateTime ahora = LocalDateTime.now();

        // Campos obligatorios bloqueantes
        if (esFaltante(getCellText(row, COL_NOMBRE))) {
            advertencias.add(crearAdvertencia(esalId, "INFORMACION PRINCIPAL", "NOMBRE",
                "Fila " + numeroFila + ": NOMBRE es obligatorio y está faltante.", true, ahora));
        }
        if (esFaltante(getCellText(row, COL_ID_SIPEJ))) {
            advertencias.add(crearAdvertencia(esalId, "INFORMACION PRINCIPAL", "ID SIPEJ",
                "Fila " + numeroFila + ": ID SIPEJ es obligatorio y está faltante o es NR.", true, ahora));
        }
        if (esFaltante(getCellText(row, COL_DOMICILIO))) {
            advertencias.add(crearAdvertencia(esalId, "INFORMACION PRINCIPAL", "DOMICILIO",
                "Fila " + numeroFila + ": DOMICILIO es obligatorio y está faltante.", true, ahora));
        }
        if (esFaltante(getCellText(row, COL_CORREO))) {
            advertencias.add(crearAdvertencia(esalId, "INFORMACION PRINCIPAL", "CORREO ELECTRONICO",
                "Fila " + numeroFila + ": CORREO ELECTRONICO es obligatorio y está faltante.", true, ahora));
        }
        if (esFaltante(getCellText(row, COL_TERMINO))) {
            advertencias.add(crearAdvertencia(esalId, "INFORMACION PRINCIPAL", "TERMINO DE DURACION",
                "Fila " + numeroFila + ": TERMINO DE DURACION es obligatorio y está faltante.", true, ahora));
        }
        if (esFaltante(getCellText(row, COL_OBJETO))) {
            advertencias.add(crearAdvertencia(esalId, "INFORMACION PRINCIPAL", "OBJETO SOCIAL",
                "Fila " + numeroFila + ": OBJETO SOCIAL es obligatorio y está faltante.", true, ahora));
        }
        if (esFaltante(getCellText(row, COL_RECONOCIMIENTO_PJ))) {
            advertencias.add(crearAdvertencia(esalId, "CONSTITUCION Y REFORMAS",
                "RECONOCIMIENTO DE PERSONERIA JURIDICA",
                "Fila " + numeroFila + ": Reconocimiento de personería jurídica es obligatorio y está faltante.",
                true, ahora));
        }
        if (esFaltante(getCellText(row, COL_FECHA_RECONOCIMIENTO_PJ))) {
            advertencias.add(crearAdvertencia(esalId, "CONSTITUCION Y REFORMAS",
                "FECHA RECONOCIMIENTO PERSONERIA JURIDICA",
                "Fila " + numeroFila + ": Fecha de reconocimiento de personería jurídica es obligatoria y está faltante.",
                true, ahora));
        }
        if (esFaltante(getCellText(row, COL_ENTIDAD_EXPIDE_PJ))) {
            advertencias.add(crearAdvertencia(esalId, "CONSTITUCION Y REFORMAS", "ENTIDAD QUE EXPIDE",
                "Fila " + numeroFila + ": Entidad que expide personería jurídica es obligatoria y está faltante.",
                true, ahora));
        }
        if (esFaltante(getCellText(row, COL_RL_NOMBRE))) {
            advertencias.add(crearAdvertencia(esalId, "NOMBRAMIENTOS", "REPRESENTANTE LEGAL",
                "Fila " + numeroFila + ": Nombre del representante legal es obligatorio y está faltante.",
                true, ahora));
        }
        if (esFaltante(getCellText(row, COL_RL_NUM_DOC))) {
            advertencias.add(crearAdvertencia(esalId, "NOMBRAMIENTOS", "NUMERO DE DOCUMENTO RL",
                "Fila " + numeroFila + ": Documento del representante legal es obligatorio y está faltante.",
                true, ahora));
        }
        if (esFaltante(getCellText(row, COL_RL_ACTA))) {
            advertencias.add(crearAdvertencia(esalId, "NOMBRAMIENTOS", "ACTA APRUEBA RL",
                "Fila " + numeroFila + ": Acta que aprueba representante legal es obligatoria y está faltante.",
                true, ahora));
        }
        if (esFaltante(getCellText(row, COL_RL_FECHA))) {
            advertencias.add(crearAdvertencia(esalId, "NOMBRAMIENTOS", "FECHA ACTA RL",
                "Fila " + numeroFila + ": Fecha del acta del representante legal es obligatoria y está faltante.",
                true, ahora));
        }
        if (esFaltante(getCellText(row, COL_RL_FACULTADES))) {
            advertencias.add(crearAdvertencia(esalId, "NOMBRAMIENTOS", "FACULTADES Y LIMITACIONES RL",
                "Fila " + numeroFila + ": Facultades/limitaciones del representante legal son obligatorias y están faltantes.",
                true, ahora));
        }

        // Órgano de administración — advertencia bloqueante si no hay ningún miembro
        String organoVal = getCellText(row, COL_ORGANO);
        String miembroVal = getCellText(row, COL_ORGANO_MIEMBRO);
        if (esFaltante(organoVal) && esFaltante(miembroVal)) {
            advertencias.add(crearAdvertencia(esalId, "ORGANO DE ADMINISTRACION", "ORGANO/MIEMBRO",
                "Fila " + numeroFila + ": Órgano de administración no tiene miembros registrados.",
                true, ahora));
        }

        // Campos opcionales — advertencia no bloqueante
        if (esFaltante(getCellText(row, COL_NIT))) {
            advertencias.add(crearAdvertencia(esalId, "INFORMACION PRINCIPAL", "NIT",
                "Fila " + numeroFila + ": NIT no informado.", false, ahora));
        }
        if (esFaltante(getCellText(row, COL_INSCRIPCION))) {
            advertencias.add(crearAdvertencia(esalId, "CONSTITUCION Y REFORMAS", "INSCRIPCION",
                "Fila " + numeroFila + ": Inscripción no informada.", false, ahora));
        }

        return advertencias;
    }

    private AdvertenciaCompletitud crearAdvertencia(Long esalId, String seccion, String campo,
            String mensaje, boolean bloqueante, LocalDateTime ahora) {
        AdvertenciaCompletitud adv = new AdvertenciaCompletitud();
        adv.setEsalId(esalId);
        adv.setSeccion(seccion);
        adv.setCampo(campo);
        adv.setMensaje(mensaje);
        adv.setBloqueante(bloqueante);
        adv.setTipo(bloqueante ? TipoAdvertencia.CAMPO_OBLIGATORIO_FALTANTE : TipoAdvertencia.ADVERTENCIA_HISTORICA);
        adv.setCreatedAt(ahora);
        return adv;
    }

    /**
     * Retorna true si el valor se considera faltante: null, vacío, NR, N/A, NA, -, N.A.
     */
    public boolean esFaltante(String valor) {
        if (valor == null || valor.trim().isEmpty()) return true;
        String v = valor.trim().toUpperCase();
        return v.equals("NR") || v.equals("N/A") || v.equals("NA") || v.equals("-") || v.equals("N.A.")
            || v.equals("N.R.") || v.equals("S/I") || v.equals("S.I.");
    }

    /**
     * Normaliza un valor: retorna null si es faltante, o el valor recortado.
     */
    private String normalizar(String valor) {
        return esFaltante(valor) ? null : valor.trim();
    }

    /**
     * Normaliza un valor truncando a maxLen caracteres si es necesario.
     */
    private String normalizarTruncado(String valor, int maxLen) {
        String v = normalizar(valor);
        if (v == null) return null;
        return v.length() > maxLen ? v.substring(0, maxLen) : v;
    }

    /**
     * Parsea una fecha en formatos comunes del Excel colombiano.
     * Retorna null si el valor es faltante o no parseable.
     */
    private LocalDate parseFecha(String valor) {
        if (esFaltante(valor)) return null;
        String v = valor.trim();
        // Formatos comunes
        String[] formatos = {
            "dd/MM/yyyy", "d/MM/yyyy", "dd/M/yyyy", "d/M/yyyy",
            "dd-MM-yyyy", "d-MM-yyyy",
            "yyyy-MM-dd",
            "dd/MM/yy", "d/MM/yy"
        };
        for (String fmt : formatos) {
            try {
                return LocalDate.parse(v, DateTimeFormatter.ofPattern(fmt));
            } catch (DateTimeParseException e) {
                // intentar siguiente formato
            }
        }
        return null;
    }

    /**
     * Extrae el texto de una celda de forma segura.
     */
    private String getCellText(Row row, int colIdx) {
        Cell cell = row.getCell(colIdx);
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    LocalDate date = cell.getLocalDateTimeCellValue().toLocalDate();
                    return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                }
                double val = cell.getNumericCellValue();
                if (val == Math.floor(val) && !Double.isInfinite(val)) {
                    return String.valueOf((long) val);
                }
                return String.valueOf(val);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return cell.toString();
        }
    }
}

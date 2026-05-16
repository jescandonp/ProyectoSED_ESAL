package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.Certificado;
import co.gov.bogota.sed.esal.domain.Firmante;
import co.gov.bogota.sed.esal.domain.enums.EstadoCertificado;
import co.gov.bogota.sed.esal.dto.CertificadoDto;
import co.gov.bogota.sed.esal.dto.PreviewCertificadoDto;
import co.gov.bogota.sed.esal.repository.CertificadoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Orquesta el flujo completo de expedición de un certificado:
 * 1. Valida preview (I2).
 * 2. Resuelve firmante vigente.
 * 3. Reserva número único (transaccional).
 * 4. Genera PDF.
 * 5. Calcula hash SHA-256.
 * 6. Almacena PDF.
 * 7. Registra Certificado GENERADO.
 * 8. Registra auditoría.
 */
@Service
public class GeneracionService {

    private static final String PLANTILLA_VERSION = "I3-v1";

    private final PreviewService previewService;
    private final FirmanteService firmanteService;
    private final NumeracionService numeracionService;
    private final CertificadoPdfService pdfService;
    private final AlmacenamientoService almacenamientoService;
    private final CertificadoRepository certificadoRepository;
    private final AuditoriaService auditoriaService;

    public GeneracionService(PreviewService previewService,
                             FirmanteService firmanteService,
                             NumeracionService numeracionService,
                             CertificadoPdfService pdfService,
                             AlmacenamientoService almacenamientoService,
                             CertificadoRepository certificadoRepository,
                             AuditoriaService auditoriaService) {
        this.previewService = previewService;
        this.firmanteService = firmanteService;
        this.numeracionService = numeracionService;
        this.pdfService = pdfService;
        this.almacenamientoService = almacenamientoService;
        this.certificadoRepository = certificadoRepository;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public CertificadoDto generar(Long esalId) {
        String usuario = obtenerUsuario();
        String rol     = auditoriaService.obtenerRolActual();

        auditoriaService.registrar(usuario, rol,
                AuditoriaAcciones.CERTIFICADO_GENERACION_SOLICITADA,
                AuditoriaAcciones.ENTIDAD_ESAL, esalId, null,
                AuditoriaAcciones.RESULTADO_EXITO, null);

        // 1. Validar preview
        Authentication authCtx = SecurityContextHolder.getContext().getAuthentication();
        PreviewCertificadoDto preview;
        try {
            preview = previewService.obtenerPreview(esalId, authCtx);
        } catch (ResponseStatusException e) {
            registrarBloqueado(esalId, usuario, rol, "Preview no disponible: " + e.getReason());
            throw e;
        }

        if (!Boolean.TRUE.equals(preview.getGeneracionHabilitada())) {
            String motivo = construirMotivoBloqueo(preview);
            registrarBloqueado(esalId, usuario, rol, motivo);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "No se puede generar el certificado. " + motivo);
        }

        // 2. Firmante vigente
        LocalDate hoy = LocalDate.now();
        Firmante firmante;
        try {
            firmante = firmanteService.resolverFirmanteVigente(hoy);
        } catch (ResponseStatusException e) {
            registrarFallido(esalId, null, usuario, rol, e.getReason());
            throw e;
        }

        // 3. Número único (transaccional — saveAndFlush dentro)
        String numero = numeracionService.reservarSiguienteNumero();
        LocalDateTime fechaExp = LocalDateTime.now();

        // 4 y 5. Generar PDF y calcular hash
        byte[] pdfBytes;
        String hash;
        try {
            pdfBytes = pdfService.generar(preview, numero, firmante, fechaExp);
            hash = sha256Hex(pdfBytes);
        } catch (Exception e) {
            registrarFallido(esalId, numero, usuario, rol, "Error generando PDF: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error técnico al generar el PDF del certificado.", e);
        }

        // 6. Almacenar PDF
        String nombreArchivo = construirNombreArchivo(numero, preview.getIdSipej());
        String rutaPdf;
        try {
            rutaPdf = almacenamientoService.guardar(esalId, nombreArchivo,
                    new ByteArrayInputStream(pdfBytes), pdfBytes.length);
        } catch (IOException e) {
            registrarFallido(esalId, numero, usuario, rol, "Error almacenando PDF: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error técnico al almacenar el PDF del certificado.", e);
        }

        // 7. Persistir registro
        Certificado cert = new Certificado();
        cert.setEsalId(esalId);
        cert.setIdSipej(preview.getIdSipej());
        cert.setNit(preview.getNit());
        cert.setNumeroCertificado(numero);
        cert.setEstadoCertificado(EstadoCertificado.GENERADO);
        cert.setVersionDatos(preview.getVersionDatos() != null
                ? preview.getVersionDatos().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
        cert.setFechaExpedicion(fechaExp);
        cert.setFirmanteId(firmante.getId());
        cert.setFirmanteNombre(firmante.getNombre());
        cert.setFirmanteCargo(firmante.getCargo());
        cert.setPlantillaVersion(PLANTILLA_VERSION);
        cert.setHashSha256(hash);
        cert.setRutaPdf(rutaPdf);
        cert.setNombreArchivo(nombreArchivo);
        cert.setContentType("application/pdf");
        cert.setTamanoBytes((long) pdfBytes.length);
        cert.setCreatedAt(fechaExp);
        cert.setCreatedBy(usuario);
        certificadoRepository.save(cert);

        // 8. Auditoría
        auditoriaService.registrar(usuario, rol,
                AuditoriaAcciones.CERTIFICADO_GENERADO,
                AuditoriaAcciones.ENTIDAD_CERTIFICADO, cert.getId(),
                preview.getIdSipej(), AuditoriaAcciones.RESULTADO_EXITO,
                "Número: " + numero + " | Hash: " + hash + " | Firmante: " + firmante.getNombre());

        return toDto(cert);
    }

    @Transactional(readOnly = true)
    public CertificadoDto obtener(Long certificadoId) {
        Certificado cert = buscarOFallar(certificadoId);
        return toDto(cert);
    }

    @Transactional(readOnly = true)
    public List<CertificadoDto> historialPorEsal(Long esalId) {
        return certificadoRepository.findByEsalIdOrderByCreatedAtDesc(esalId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Descarga el PDF del certificado. Valida hash antes de entregar.
     */
    @Transactional(readOnly = true)
    public byte[] descargar(Long certificadoId) {
        String usuario = obtenerUsuario();
        String rol     = auditoriaService.obtenerRolActual();
        Certificado cert = buscarOFallar(certificadoId);

        if (cert.getEstadoCertificado() != EstadoCertificado.GENERADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El certificado no está en estado GENERADO y no puede descargarse.");
        }

        byte[] bytes;
        try {
            bytes = Files.readAllBytes(Paths.get(cert.getRutaPdf()));
        } catch (IOException e) {
            auditoriaService.registrar(usuario, rol,
                    AuditoriaAcciones.CERTIFICADO_DESCARGADO,
                    AuditoriaAcciones.ENTIDAD_CERTIFICADO, certificadoId,
                    cert.getIdSipej(), AuditoriaAcciones.RESULTADO_ERROR,
                    "Error leyendo archivo: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se puede leer el PDF del certificado.");
        }

        // Validar integridad
        String hashActual = sha256Hex(bytes);
        if (!hashActual.equals(cert.getHashSha256())) {
            auditoriaService.registrar(usuario, rol,
                    AuditoriaAcciones.CERTIFICADO_DESCARGADO,
                    AuditoriaAcciones.ENTIDAD_CERTIFICADO, certificadoId,
                    cert.getIdSipej(), AuditoriaAcciones.RESULTADO_ERROR,
                    "Hash inconsistente. Esperado: " + cert.getHashSha256() + " | Actual: " + hashActual);
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El PDF del certificado está corrupto (hash inconsistente). " +
                    "Contacte al administrador del sistema.");
        }

        auditoriaService.registrar(usuario, rol,
                AuditoriaAcciones.CERTIFICADO_DESCARGADO,
                AuditoriaAcciones.ENTIDAD_CERTIFICADO, certificadoId,
                cert.getIdSipej(), AuditoriaAcciones.RESULTADO_EXITO,
                "Número: " + cert.getNumeroCertificado());

        return bytes;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void registrarBloqueado(Long esalId, String usuario, String rol, String motivo) {
        auditoriaService.registrar(usuario, rol,
                AuditoriaAcciones.CERTIFICADO_BLOQUEADO,
                AuditoriaAcciones.ENTIDAD_ESAL, esalId, null,
                AuditoriaAcciones.RESULTADO_ERROR, motivo);
    }

    private void registrarFallido(Long esalId, String numero, String usuario, String rol, String error) {
        Certificado fallido = new Certificado();
        fallido.setEsalId(esalId);
        fallido.setNumeroCertificado(numero != null ? numero : null);
        fallido.setEstadoCertificado(EstadoCertificado.FALLIDO);
        fallido.setErrorDetalle(error);
        fallido.setCreatedAt(LocalDateTime.now());
        fallido.setCreatedBy(usuario);
        certificadoRepository.save(fallido);

        auditoriaService.registrar(usuario, rol,
                AuditoriaAcciones.CERTIFICADO_GENERACION_FALLIDA,
                AuditoriaAcciones.ENTIDAD_CERTIFICADO, fallido.getId(), null,
                AuditoriaAcciones.RESULTADO_ERROR, error);
    }

    private String construirMotivoBloqueo(PreviewCertificadoDto preview) {
        if (preview.getBloqueos() != null && !preview.getBloqueos().isEmpty()) {
            return "Campos obligatorios faltantes: " +
                    preview.getBloqueos().stream()
                            .map(b -> b.getSeccion() + " / " + b.getCampo())
                            .collect(Collectors.joining(", "));
        }
        return "La generación está bloqueada por el estado de la ESAL.";
    }

    private String construirNombreArchivo(String numero, String idSipej) {
        String safe = numero.replaceAll("[^A-Za-z0-9\\-]", "_");
        return "CERT_" + safe + ".pdf";
    }

    private String sha256Hex(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error calculando hash SHA-256", e);
        }
    }

    private Certificado buscarOFallar(Long id) {
        return certificadoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Certificado no encontrado: " + id));
    }

    private CertificadoDto toDto(Certificado c) {
        CertificadoDto dto = new CertificadoDto();
        dto.setId(c.getId());
        dto.setEsalId(c.getEsalId());
        dto.setIdSipej(c.getIdSipej());
        dto.setNit(c.getNit());
        dto.setNumeroCertificado(c.getNumeroCertificado());
        dto.setEstadoCertificado(c.getEstadoCertificado());
        dto.setVersionDatos(c.getVersionDatos());
        dto.setFechaExpedicion(c.getFechaExpedicion());
        dto.setFirmanteNombre(c.getFirmanteNombre());
        dto.setFirmanteCargo(c.getFirmanteCargo());
        dto.setPlantillaVersion(c.getPlantillaVersion());
        dto.setHashSha256(c.getHashSha256());
        dto.setNombreArchivo(c.getNombreArchivo());
        dto.setTamanoBytes(c.getTamanoBytes());
        dto.setErrorDetalle(c.getErrorDetalle());
        dto.setDownloadUrl("/api/certificados/" + c.getId() + "/descargar");
        dto.setCreatedAt(c.getCreatedAt());
        dto.setCreatedBy(c.getCreatedBy());
        return dto;
    }

    private String obtenerUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "sistema";
    }
}

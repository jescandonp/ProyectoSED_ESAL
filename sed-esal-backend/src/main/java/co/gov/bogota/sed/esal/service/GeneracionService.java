package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.Certificado;
import co.gov.bogota.sed.esal.domain.Firmante;
import co.gov.bogota.sed.esal.domain.enums.EstadoCertificado;
import co.gov.bogota.sed.esal.dto.CertificadoDto;
import co.gov.bogota.sed.esal.dto.PreviewCertificadoDto;
import co.gov.bogota.sed.esal.repository.CertificadoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GeneracionService {

    private final PreviewService previewService;
    private final NumeracionService numeracionService;
    private final FirmanteService firmanteService;
    private final CertificadoPdfService pdfService;
    private final AlmacenamientoService almacenamientoService;
    private final CertificadoRepository certificadoRepository;
    private final AuditoriaService auditoriaService;

    public GeneracionService(PreviewService previewService,
                             NumeracionService numeracionService,
                             FirmanteService firmanteService,
                             CertificadoPdfService pdfService,
                             AlmacenamientoService almacenamientoService,
                             CertificadoRepository certificadoRepository,
                             AuditoriaService auditoriaService) {
        this.previewService       = previewService;
        this.numeracionService    = numeracionService;
        this.firmanteService      = firmanteService;
        this.pdfService           = pdfService;
        this.almacenamientoService = almacenamientoService;
        this.certificadoRepository = certificadoRepository;
        this.auditoriaService     = auditoriaService;
    }

    @Transactional
    public CertificadoDto generar(Long esalId, String usuario) {
        auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.CERTIFICADO_GENERACION_SOLICITADA, AuditoriaAcciones.ENTIDAD_ESAL,
                esalId, null, null, null);

        // 1. Obtener y validar preview
        PreviewCertificadoDto preview = previewService.obtenerPreview(esalId, usuario);

        if (Boolean.FALSE.equals(preview.getGeneracionHabilitada())) {
            Certificado bloqueado = registrarEstado(esalId, preview, EstadoCertificado.BLOQUEADO,
                    null, null, null, null,
                    "Generacion bloqueada: " + formatBloqueos(preview), usuario);
            auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                    AuditoriaAcciones.CERTIFICADO_BLOQUEADO, AuditoriaAcciones.ENTIDAD_CERTIFICADO,
                    bloqueado.getId(), preview.getIdSipej(), AuditoriaAcciones.RESULTADO_ERROR,
                    "Bloqueos: " + formatBloqueos(preview));
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "La generacion del certificado esta bloqueada: " + formatBloqueos(preview));
        }

        LocalDateTime ahora = LocalDateTime.now();
        LocalDate hoy = ahora.toLocalDate();

        // 2. Resolver firmante vigente
        Firmante firmante = firmanteService.resolverVigente(hoy);

        // 3. Reservar numero (REQUIRES_NEW para evitar duplicados)
        String numero = numeracionService.reservarSiguienteNumero(usuario);

        // 4. Generar PDF
        byte[] pdfBytes;
        try {
            pdfBytes = pdfService.generar(preview, numero, firmante.getNombre(), firmante.getCargo(), ahora);
        } catch (Exception e) {
            Certificado fallido = registrarEstado(esalId, preview, EstadoCertificado.FALLIDO,
                    numero, firmante, ahora, null, "Error al generar PDF: " + e.getMessage(), usuario);
            auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                    AuditoriaAcciones.CERTIFICADO_GENERACION_FALLIDA, AuditoriaAcciones.ENTIDAD_CERTIFICADO,
                    fallido.getId(), preview.getIdSipej(), AuditoriaAcciones.RESULTADO_ERROR, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error tecnico al generar el PDF del certificado.");
        }

        // 5. Calcular hash SHA-256
        String hash = calcularHash(pdfBytes);

        // 6. Almacenar PDF
        String nombreArchivo = numero.replace("/", "-") + ".pdf";
        String rutaPdf;
        try {
            rutaPdf = almacenamientoService.guardar(esalId, nombreArchivo,
                    new ByteArrayInputStream(pdfBytes), pdfBytes.length);
        } catch (Exception e) {
            Certificado fallido = registrarEstado(esalId, preview, EstadoCertificado.FALLIDO,
                    numero, firmante, ahora, hash, "Error al almacenar PDF: " + e.getMessage(), usuario);
            auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                    AuditoriaAcciones.CERTIFICADO_GENERACION_FALLIDA, AuditoriaAcciones.ENTIDAD_CERTIFICADO,
                    fallido.getId(), preview.getIdSipej(), AuditoriaAcciones.RESULTADO_ERROR, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error tecnico al almacenar el certificado generado.");
        }

        // 7. Persistir certificado GENERADO
        Certificado cert = new Certificado();
        cert.setEsalId(esalId);
        cert.setIdSipej(preview.getIdSipej());
        cert.setNit(preview.getNit());
        cert.setNumeroCertificado(numero);
        cert.setEstadoCertificado(EstadoCertificado.GENERADO);
        cert.setVersionDatos(preview.getVersionDatos());
        cert.setFechaExpedicion(ahora);
        cert.setFirmanteId(firmante.getId());
        cert.setFirmanteNombre(firmante.getNombre());
        cert.setFirmanteCargo(firmante.getCargo());
        cert.setPlantillaVersion(CertificadoPdfService.VERSION_PLANTILLA);
        cert.setHashSha256(hash);
        cert.setRutaPdf(rutaPdf);
        cert.setNombreArchivo(nombreArchivo);
        cert.setContentType("application/pdf");
        cert.setTamanoBytes((long) pdfBytes.length);
        cert.setCreatedAt(ahora);
        cert.setCreatedBy(usuario);
        Certificado saved = certificadoRepository.save(cert);

        auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.CERTIFICADO_GENERADO, AuditoriaAcciones.ENTIDAD_CERTIFICADO,
                saved.getId(), preview.getIdSipej(), AuditoriaAcciones.RESULTADO_EXITO,
                "Numero: " + numero + " | Hash: " + hash);

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public CertificadoDto obtener(Long certificadoId) {
        Certificado cert = certificadoRepository.findById(certificadoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificado no encontrado."));
        return toDto(cert);
    }

    @Transactional(readOnly = true)
    public List<CertificadoDto> listarPorEsal(Long esalId) {
        return certificadoRepository.findByEsalIdOrderByCreatedAtDesc(esalId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public byte[] descargar(Long certificadoId, String usuario) {
        Certificado cert = certificadoRepository.findById(certificadoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificado no encontrado."));

        if (cert.getEstadoCertificado() != EstadoCertificado.GENERADO || cert.getRutaPdf() == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "El certificado no esta disponible para descarga.");
        }

        try {
            java.nio.file.Path path = java.nio.file.Paths.get(cert.getRutaPdf());
            byte[] bytes = java.nio.file.Files.readAllBytes(path);

            // Validar integridad
            String hashActual = calcularHash(bytes);
            if (!hashActual.equals(cert.getHashSha256())) {
                auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                        AuditoriaAcciones.CERTIFICADO_DESCARGADO, AuditoriaAcciones.ENTIDAD_CERTIFICADO,
                        cert.getId(), cert.getIdSipej(), AuditoriaAcciones.RESULTADO_ERROR,
                        "Hash inconsistente al descargar certificado " + certificadoId);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Integridad del certificado comprometida. Contacte al administrador.");
            }

            auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                    AuditoriaAcciones.CERTIFICADO_DESCARGADO, AuditoriaAcciones.ENTIDAD_CERTIFICADO,
                    cert.getId(), cert.getIdSipej(), AuditoriaAcciones.RESULTADO_EXITO,
                    "Hash: " + cert.getHashSha256());
            return bytes;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al leer el archivo del certificado.");
        }
    }

    private Certificado registrarEstado(Long esalId, PreviewCertificadoDto preview,
                                         EstadoCertificado estado, String numero,
                                         Firmante firmante, LocalDateTime ahora,
                                         String hash, String errorDetalle, String usuario) {
        Certificado c = new Certificado();
        c.setEsalId(esalId);
        c.setIdSipej(preview.getIdSipej());
        c.setNit(preview.getNit());
        c.setNumeroCertificado(numero);
        c.setEstadoCertificado(estado);
        c.setVersionDatos(preview.getVersionDatos());
        c.setFechaExpedicion(ahora);
        if (firmante != null) {
            c.setFirmanteId(firmante.getId());
            c.setFirmanteNombre(firmante.getNombre());
            c.setFirmanteCargo(firmante.getCargo());
        }
        c.setHashSha256(hash);
        c.setErrorDetalle(errorDetalle);
        c.setCreatedAt(LocalDateTime.now());
        c.setCreatedBy(usuario);
        return certificadoRepository.save(c);
    }

    private String calcularHash(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(bytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error calculando hash SHA-256", e);
        }
    }

    private String formatBloqueos(PreviewCertificadoDto preview) {
        if (preview.getBloqueos() == null || preview.getBloqueos().isEmpty()) return "sin detalle";
        return preview.getBloqueos().stream()
                .map(b -> b.getCampo() + ": " + b.getMensaje())
                .collect(Collectors.joining("; "));
    }

    private CertificadoDto toDto(Certificado c) {
        CertificadoDto dto = new CertificadoDto();
        dto.setCertificadoId(c.getId());
        dto.setEsalId(c.getEsalId());
        dto.setIdSipej(c.getIdSipej());
        dto.setNit(c.getNit());
        dto.setNumeroCertificado(c.getNumeroCertificado());
        dto.setEstadoCertificado(c.getEstadoCertificado());
        dto.setFechaExpedicion(c.getFechaExpedicion());
        dto.setVersionDatos(c.getVersionDatos());
        dto.setFirmanteNombre(c.getFirmanteNombre());
        dto.setFirmanteCargo(c.getFirmanteCargo());
        dto.setPlantillaVersion(c.getPlantillaVersion());
        dto.setHashSha256(c.getHashSha256());
        dto.setNombreArchivo(c.getNombreArchivo());
        dto.setTamanoBytes(c.getTamanoBytes());
        dto.setErrorDetalle(c.getErrorDetalle());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setCreatedBy(c.getCreatedBy());
        return dto;
    }
}

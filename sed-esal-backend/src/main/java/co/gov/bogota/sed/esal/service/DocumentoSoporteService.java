package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.DocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.EstadoValidacionDocumento;
import co.gov.bogota.sed.esal.domain.enums.SubtipoDocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.TipoDocumentoSoporte;
import co.gov.bogota.sed.esal.dto.DocumentoSoporteDto;
import co.gov.bogota.sed.esal.repository.DocumentoSoporteRepository;
import co.gov.bogota.sed.esal.repository.EsalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de lógica de negocio para documentos soporte de ESALes.
 *
 * Reglas de negocio (spec I1 sección 5.7):
 * - Solo se acepta {@code application/pdf} — cualquier otro contentType lanza 400.
 * - El almacenamiento pasa por {@link AlmacenamientoService} (abstraído).
 * - El documento queda asociado a la ESAL con {@code esalId}.
 * - El estado inicial de validación es {@code PENDIENTE}.
 */
@Service
@Transactional
public class DocumentoSoporteService {

    private static final String CONTENT_TYPE_PDF = "application/pdf";
    private static final long TAMANO_MAXIMO_PDF = 10L * 1024L * 1024L;

    private final EsalRepository esalRepository;
    private final DocumentoSoporteRepository documentoRepository;
    private final AlmacenamientoService almacenamientoService;
    private final AuditoriaService auditoriaService;

    public DocumentoSoporteService(
            EsalRepository esalRepository,
            DocumentoSoporteRepository documentoRepository,
            AlmacenamientoService almacenamientoService,
            AuditoriaService auditoriaService) {
        this.esalRepository = esalRepository;
        this.documentoRepository = documentoRepository;
        this.almacenamientoService = almacenamientoService;
        this.auditoriaService = auditoriaService;
    }

    /**
     * Registra un documento soporte para una ESAL.
     *
     * Pasos:
     * 1. Valida que la ESAL existe — lanza 404 si no.
     * 2. Valida que el contentType es {@code application/pdf} — lanza 400 si no.
     * 3. Guarda el archivo vía {@link AlmacenamientoService}.
     * 4. Crea y persiste la entidad {@link DocumentoSoporte}.
     * 5. Retorna el {@link DocumentoSoporteDto}.
     *
     * @param esalId        ID de la ESAL
     * @param nombreArchivo nombre original del archivo
     * @param contentType   tipo MIME del archivo
     * @param tamanoBytes   tamaño del archivo en bytes
     * @param contenido     stream con el contenido del archivo
     * @param tipoProceso   tipo de proceso asociado (opcional)
     * @param tipoDocumento tipo de documento (opcional)
     * @param registradoPor usuario que registra el documento
     * @return DTO con los datos del documento registrado
     * @throws IOException              si ocurre un error de I/O al guardar el archivo
     * @throws ResponseStatusException  404 si la ESAL no existe; 400 si el contentType no es PDF
     */
    public DocumentoSoporteDto registrar(Long esalId,
                                          String nombreArchivo,
                                          String contentType,
                                          long tamanoBytes,
                                          InputStream contenido,
                                          String tipoProceso,
                                          String tipoDocumento,
                                          String registradoPor) throws IOException {
        return registrarLegacy(esalId, nombreArchivo, contentType, tamanoBytes, contenido,
                tipoProceso, tipoDocumento, registradoPor);
    }

    public DocumentoSoporteDto registrar(Long esalId,
                                          String nombreArchivo,
                                          String contentType,
                                          long tamanoBytes,
                                          InputStream contenido,
                                          String tipoDocumento,
                                          String subtipoDocumento,
                                          String referencia,
                                          LocalDate fechaActo,
                                          String observacion,
                                          String registradoPor) throws IOException {
        // 1. Validar que la ESAL existe
        esalRepository.findById(esalId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "ESAL no encontrada con id: " + esalId));

        validarArchivoPdf(contentType, tamanoBytes);
        TipoDocumentoSoporte tipo = parseTipo(tipoDocumento);
        SubtipoDocumentoSoporte subtipo = parseSubtipo(subtipoDocumento);
        validarCatalogo(tipo, subtipo);
        validarMetadatos(referencia, fechaActo);

        // 3. Guardar archivo vía servicio de almacenamiento abstraído
        String rutaAlmacenamiento = almacenamientoService.guardar(
                esalId, nombreArchivo, contenido, tamanoBytes);

        documentoRepository.findFirstByEsalIdAndTipoDocumentalAndSubtipoDocumentalAndVigenteTrue(esalId, tipo, subtipo)
                .ifPresent(anterior -> {
                    anterior.setVigente(Boolean.FALSE);
                    documentoRepository.save(anterior);
                    auditoriaService.registrar(registradoPor, auditoriaService.obtenerRolActual(),
                            AuditoriaAcciones.DOCUMENTO_SOPORTE_VIGENCIA_REEMPLAZADA,
                            AuditoriaAcciones.ENTIDAD_DOCUMENTO,
                            anterior.getId(), null,
                            AuditoriaAcciones.RESULTADO_EXITO,
                            "ESAL: " + esalId + ", tipo: " + tipo + ", subtipo: " + subtipo);
                });

        // 4. Crear y persistir DocumentoSoporte
        DocumentoSoporte documento = new DocumentoSoporte();
        documento.setEsalId(esalId);
        documento.setNombreArchivo(nombreArchivo != null ? nombreArchivo : "documento.pdf");
        documento.setContentType(contentType);
        documento.setTamanoBytes(tamanoBytes);
        documento.setRutaAlmacenamiento(rutaAlmacenamiento);
        documento.setTipoDocumento(tipoDocumento);
        documento.setTipoDocumental(tipo);
        documento.setSubtipoDocumental(subtipo);
        documento.setReferenciaActo(referencia.trim());
        documento.setFechaActo(fechaActo);
        documento.setObservacion(observacion == null || observacion.trim().isEmpty() ? null : observacion.trim());
        documento.setVigente(Boolean.TRUE);
        documento.setEstadoValidacion(EstadoValidacionDocumento.PENDIENTE);
        documento.setCreatedAt(LocalDateTime.now());
        documento.setCreatedBy(registradoPor);

        documento = documentoRepository.save(documento);

        auditoriaService.registrar(registradoPor, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.DOCUMENTO_SOPORTE_CREADO,
                AuditoriaAcciones.ENTIDAD_DOCUMENTO,
                documento.getId(), null,
                AuditoriaAcciones.RESULTADO_EXITO,
                "ESAL: " + esalId + ", tipo: " + tipo + ", archivo: " + nombreArchivo);

        // 5. Retornar DTO
        return toDto(documento);
    }

    public static class DocumentoDescarga {
        private final String nombreArchivo;
        private final String contentType;
        private final byte[] contenido;

        public DocumentoDescarga(String nombreArchivo, String contentType, byte[] contenido) {
            this.nombreArchivo = nombreArchivo;
            this.contentType = contentType;
            this.contenido = contenido;
        }

        public String getNombreArchivo() { return nombreArchivo; }
        public String getContentType() { return contentType; }
        public byte[] getContenido() { return contenido; }
    }

    @Transactional(readOnly = true)
    public DocumentoDescarga descargar(Long esalId, Long documentoId, String usuario) throws IOException {
        DocumentoSoporte documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Documento no encontrado con id: " + documentoId));
        if (!esalId.equals(documento.getEsalId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Documento no encontrado con id: " + documentoId);
        }
        byte[] contenido = almacenamientoService.leer(documento.getRutaAlmacenamiento());
        auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.DOCUMENTO_SOPORTE_DESCARGADO,
                AuditoriaAcciones.ENTIDAD_DOCUMENTO,
                documento.getId(), null,
                AuditoriaAcciones.RESULTADO_EXITO,
                "ESAL: " + esalId + ", tipo: " + documento.getTipoDocumental());
        return new DocumentoDescarga(documento.getNombreArchivo(), documento.getContentType(), contenido);
    }

    private DocumentoSoporteDto registrarLegacy(Long esalId,
                                                String nombreArchivo,
                                                String contentType,
                                                long tamanoBytes,
                                                InputStream contenido,
                                                String tipoProceso,
                                                String tipoDocumento,
                                                String registradoPor) throws IOException {
        esalRepository.findById(esalId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "ESAL no encontrada con id: " + esalId));

        validarContentTypePdf(contentType);

        String rutaAlmacenamiento = almacenamientoService.guardar(
                esalId, nombreArchivo, contenido, tamanoBytes);

        DocumentoSoporte documento = new DocumentoSoporte();
        documento.setEsalId(esalId);
        documento.setNombreArchivo(nombreArchivo != null ? nombreArchivo : "documento.pdf");
        documento.setContentType(contentType);
        documento.setTamanoBytes(tamanoBytes);
        documento.setRutaAlmacenamiento(rutaAlmacenamiento);
        documento.setTipoProceso(tipoProceso);
        documento.setTipoDocumento(tipoDocumento);
        documento.setVigente(Boolean.TRUE);
        documento.setEstadoValidacion(EstadoValidacionDocumento.PENDIENTE);
        documento.setCreatedAt(LocalDateTime.now());
        documento.setCreatedBy(registradoPor);

        documento = documentoRepository.save(documento);

        auditoriaService.registrar(registradoPor, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.REGISTRAR_DOCUMENTO,
                AuditoriaAcciones.ENTIDAD_DOCUMENTO,
                documento.getId(), null,
                AuditoriaAcciones.RESULTADO_EXITO,
                "ESAL: " + esalId + ", archivo: " + nombreArchivo);

        return toDto(documento);
    }

    /**
     * Lista los documentos soporte de una ESAL.
     *
     * @param esalId ID de la ESAL
     * @return lista de DTOs con los documentos de la ESAL
     * @throws ResponseStatusException 404 si la ESAL no existe
     */
    @Transactional(readOnly = true)
    public List<DocumentoSoporteDto> listar(Long esalId) {
        // Validar que la ESAL existe
        esalRepository.findById(esalId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "ESAL no encontrada con id: " + esalId));

        return documentoRepository.findByEsalIdOrderByVigenteDescCreatedAtDesc(esalId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Helper de mapeo
    // -------------------------------------------------------------------------

    private DocumentoSoporteDto toDto(DocumentoSoporte doc) {
        DocumentoSoporteDto dto = new DocumentoSoporteDto();
        dto.setId(doc.getId());
        dto.setEsalId(doc.getEsalId());
        dto.setTipoProceso(doc.getTipoProceso());
        dto.setTipoDocumento(doc.getTipoDocumento());
        dto.setNombreArchivo(doc.getNombreArchivo());
        dto.setContentType(doc.getContentType());
        dto.setTamanoBytes(doc.getTamanoBytes());
        dto.setTipoDocumental(doc.getTipoDocumental());
        dto.setSubtipoDocumental(doc.getSubtipoDocumental());
        dto.setReferenciaActo(doc.getReferenciaActo());
        dto.setFechaActo(doc.getFechaActo());
        dto.setObservacion(doc.getObservacion());
        dto.setVigente(Boolean.TRUE.equals(doc.getVigente()));
        dto.setEstadoValidacion(doc.getEstadoValidacion());
        dto.setCreatedAt(doc.getCreatedAt());
        dto.setCreatedBy(doc.getCreatedBy());
        return dto;
    }

    private void validarArchivoPdf(String contentType, long tamanoBytes) {
        validarContentTypePdf(contentType);
        if (tamanoBytes > TAMANO_MAXIMO_PDF) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El documento PDF no puede superar 10 MB.");
        }
    }

    private void validarContentTypePdf(String contentType) {
        if (!CONTENT_TYPE_PDF.equalsIgnoreCase(contentType)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solo se acepta application/pdf. ContentType recibido: " + contentType);
        }
    }

    private TipoDocumentoSoporte parseTipo(String tipoDocumento) {
        if (tipoDocumento == null || tipoDocumento.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'tipoDocumento' es obligatorio.");
        }
        try {
            return TipoDocumentoSoporte.valueOf(tipoDocumento.trim());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo documental no permitido: " + tipoDocumento);
        }
    }

    private SubtipoDocumentoSoporte parseSubtipo(String subtipoDocumento) {
        if (subtipoDocumento == null || subtipoDocumento.trim().isEmpty()) {
            return null;
        }
        try {
            return SubtipoDocumentoSoporte.valueOf(subtipoDocumento.trim());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subtipo documental no permitido: " + subtipoDocumento);
        }
    }

    private void validarCatalogo(TipoDocumentoSoporte tipo, SubtipoDocumentoSoporte subtipo) {
        if (TipoDocumentoSoporte.CREACION_FORMACION.equals(tipo) || TipoDocumentoSoporte.DIGNATARIOS.equals(tipo)) {
            if (subtipo != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El subtipo solo aplica para LIQUIDACION o CANCELACION.");
            }
            return;
        }
        if (TipoDocumentoSoporte.LIQUIDACION.equals(tipo)) {
            if (!SubtipoDocumentoSoporte.TRAMITE_CANCELACION_VOLUNTARIA.equals(subtipo)
                    && !SubtipoDocumentoSoporte.TERMINO_DURACION.equals(subtipo)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "LIQUIDACION requiere subtipo TRAMITE_CANCELACION_VOLUNTARIA o TERMINO_DURACION.");
            }
            return;
        }
        if (TipoDocumentoSoporte.CANCELACION.equals(tipo)) {
            if (!SubtipoDocumentoSoporte.CANCELACION_VOLUNTARIA.equals(subtipo)
                    && !SubtipoDocumentoSoporte.ORDEN_AUTORIDAD.equals(subtipo)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "CANCELACION requiere subtipo CANCELACION_VOLUNTARIA u ORDEN_AUTORIDAD.");
            }
        }
    }

    private void validarMetadatos(String referencia, LocalDate fechaActo) {
        if (referencia == null || referencia.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'referencia' es obligatorio.");
        }
        if (fechaActo == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'fechaActo' es obligatorio.");
        }
    }
}

package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.DocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.EstadoValidacionDocumento;
import co.gov.bogota.sed.esal.dto.DocumentoSoporteDto;
import co.gov.bogota.sed.esal.repository.DocumentoSoporteRepository;
import co.gov.bogota.sed.esal.repository.EsalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
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
        // 1. Validar que la ESAL existe
        esalRepository.findById(esalId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "ESAL no encontrada con id: " + esalId));

        // 2. Validar contentType — solo se acepta application/pdf
        if (!CONTENT_TYPE_PDF.equalsIgnoreCase(contentType)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solo se acepta application/pdf. ContentType recibido: " + contentType);
        }

        // 3. Guardar archivo vía servicio de almacenamiento abstraído
        String rutaAlmacenamiento = almacenamientoService.guardar(
                esalId, nombreArchivo, contenido, tamanoBytes);

        // 4. Crear y persistir DocumentoSoporte
        DocumentoSoporte documento = new DocumentoSoporte();
        documento.setEsalId(esalId);
        documento.setNombreArchivo(nombreArchivo != null ? nombreArchivo : "documento.pdf");
        documento.setContentType(contentType);
        documento.setTamanoBytes(tamanoBytes);
        documento.setRutaAlmacenamiento(rutaAlmacenamiento);
        documento.setTipoProceso(tipoProceso);
        documento.setTipoDocumento(tipoDocumento);
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

        // 5. Retornar DTO
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

        return documentoRepository.findByEsalId(esalId)
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
        dto.setEstadoValidacion(doc.getEstadoValidacion());
        dto.setCreatedAt(doc.getCreatedAt());
        dto.setCreatedBy(doc.getCreatedBy());
        return dto;
    }
}

package co.gov.bogota.sed.esal.domain;

import co.gov.bogota.sed.esal.domain.enums.EstadoValidacionDocumento;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Documento soporte asociado a una ESAL.
 */
@Entity
@Table(schema = "SED_ESAL", name = "ESAL_DOCUMENTO_SOPORTE")
public class DocumentoSoporte {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esal_documento_seq")
    @SequenceGenerator(name = "esal_documento_seq", sequenceName = "ESAL_DOCUMENTO_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ESAL_ID", nullable = false)
    private Long esalId;

    @Column(name = "TIPO_PROCESO", length = 255)
    private String tipoProceso;

    @Column(name = "TIPO_DOCUMENTO", length = 255)
    private String tipoDocumento;

    @Column(name = "NOMBRE_ARCHIVO", nullable = false, length = 500)
    private String nombreArchivo;

    @Column(name = "CONTENT_TYPE", nullable = false, length = 100)
    private String contentType;

    @Column(name = "TAMANO_BYTES")
    private Long tamanoBytes;

    @Column(name = "RUTA_ALMACENAMIENTO", length = 1000)
    private String rutaAlmacenamiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_VALIDACION", length = 20)
    private EstadoValidacionDocumento estadoValidacion = EstadoValidacionDocumento.PENDIENTE;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY", length = 255)
    private String createdBy;

    public DocumentoSoporte() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEsalId() { return esalId; }
    public void setEsalId(Long esalId) { this.esalId = esalId; }

    public String getTipoProceso() { return tipoProceso; }
    public void setTipoProceso(String tipoProceso) { this.tipoProceso = tipoProceso; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Long getTamanoBytes() { return tamanoBytes; }
    public void setTamanoBytes(Long tamanoBytes) { this.tamanoBytes = tamanoBytes; }

    public String getRutaAlmacenamiento() { return rutaAlmacenamiento; }
    public void setRutaAlmacenamiento(String rutaAlmacenamiento) { this.rutaAlmacenamiento = rutaAlmacenamiento; }

    public EstadoValidacionDocumento getEstadoValidacion() { return estadoValidacion; }
    public void setEstadoValidacion(EstadoValidacionDocumento estadoValidacion) { this.estadoValidacion = estadoValidacion; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}

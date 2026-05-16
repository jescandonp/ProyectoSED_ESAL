package co.gov.bogota.sed.esal.dto;

import co.gov.bogota.sed.esal.domain.enums.EstadoValidacionDocumento;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para DocumentoSoporte.
 * Expone los campos del documento sin exponer la entidad JPA directamente.
 */
public class DocumentoSoporteDto {

    private Long id;
    private Long esalId;
    private String tipoProceso;
    private String tipoDocumento;
    private String nombreArchivo;
    private String contentType;
    private Long tamanoBytes;
    private EstadoValidacionDocumento estadoValidacion;
    private LocalDateTime createdAt;
    private String createdBy;

    public DocumentoSoporteDto() {
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

    public EstadoValidacionDocumento getEstadoValidacion() { return estadoValidacion; }
    public void setEstadoValidacion(EstadoValidacionDocumento estadoValidacion) {
        this.estadoValidacion = estadoValidacion;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}

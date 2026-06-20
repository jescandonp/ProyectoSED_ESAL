package co.gov.bogota.sed.esal.dto;

import co.gov.bogota.sed.esal.domain.enums.EstadoValidacionDocumento;
import co.gov.bogota.sed.esal.domain.enums.SubtipoDocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.TipoDocumentoSoporte;

import java.time.LocalDate;
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
    private TipoDocumentoSoporte tipoDocumental;
    private SubtipoDocumentoSoporte subtipoDocumental;
    private String referenciaActo;
    private LocalDate fechaActo;
    private String observacion;
    private boolean vigente;
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

    public TipoDocumentoSoporte getTipoDocumental() { return tipoDocumental; }
    public void setTipoDocumental(TipoDocumentoSoporte tipoDocumental) { this.tipoDocumental = tipoDocumental; }

    public SubtipoDocumentoSoporte getSubtipoDocumental() { return subtipoDocumental; }
    public void setSubtipoDocumental(SubtipoDocumentoSoporte subtipoDocumental) { this.subtipoDocumental = subtipoDocumental; }

    public String getReferenciaActo() { return referenciaActo; }
    public void setReferenciaActo(String referenciaActo) { this.referenciaActo = referenciaActo; }

    public LocalDate getFechaActo() { return fechaActo; }
    public void setFechaActo(LocalDate fechaActo) { this.fechaActo = fechaActo; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public boolean isVigente() { return vigente; }
    public void setVigente(boolean vigente) { this.vigente = vigente; }

    public EstadoValidacionDocumento getEstadoValidacion() { return estadoValidacion; }
    public void setEstadoValidacion(EstadoValidacionDocumento estadoValidacion) {
        this.estadoValidacion = estadoValidacion;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}

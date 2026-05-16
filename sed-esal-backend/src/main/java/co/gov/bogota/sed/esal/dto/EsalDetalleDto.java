package co.gov.bogota.sed.esal.dto;

import co.gov.bogota.sed.esal.domain.enums.EstadoCompletitud;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;

import java.time.LocalDateTime;

/**
 * DTO de detalle completo para una ESAL.
 */
public class EsalDetalleDto {

    private Long id;
    private String nombre;
    private String idSipej;
    private String nit;
    private String domicilio;
    private String correoElectronico;
    private String terminoDuracion;
    private String objetoSocial;
    private EstadoEsal estado;
    private EstadoCompletitud estadoCompletitud;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    public EsalDetalleDto() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getIdSipej() { return idSipej; }
    public void setIdSipej(String idSipej) { this.idSipej = idSipej; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }

    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getTerminoDuracion() { return terminoDuracion; }
    public void setTerminoDuracion(String terminoDuracion) { this.terminoDuracion = terminoDuracion; }

    public String getObjetoSocial() { return objetoSocial; }
    public void setObjetoSocial(String objetoSocial) { this.objetoSocial = objetoSocial; }

    public EstadoEsal getEstado() { return estado; }
    public void setEstado(EstadoEsal estado) { this.estado = estado; }

    public EstadoCompletitud getEstadoCompletitud() { return estadoCompletitud; }
    public void setEstadoCompletitud(EstadoCompletitud estadoCompletitud) {
        this.estadoCompletitud = estadoCompletitud;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}

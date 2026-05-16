package co.gov.bogota.sed.esal.dto;

import co.gov.bogota.sed.esal.domain.enums.EstadoCompletitud;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;

import java.time.LocalDateTime;

/**
 * DTO de resumen para listados de ESAL.
 */
public class EsalResumenDto {

    private Long id;
    private String nombre;
    private String idSipej;
    private String nit;
    private String domicilio;
    private EstadoEsal estado;
    private EstadoCompletitud estadoCompletitud;
    private LocalDateTime createdAt;

    public EsalResumenDto() {
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

    public EstadoEsal getEstado() { return estado; }
    public void setEstado(EstadoEsal estado) { this.estado = estado; }

    public EstadoCompletitud getEstadoCompletitud() { return estadoCompletitud; }
    public void setEstadoCompletitud(EstadoCompletitud estadoCompletitud) {
        this.estadoCompletitud = estadoCompletitud;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

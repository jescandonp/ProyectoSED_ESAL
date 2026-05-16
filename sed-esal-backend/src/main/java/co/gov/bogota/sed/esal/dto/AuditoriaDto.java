package co.gov.bogota.sed.esal.dto;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para registros de auditoría.
 */
public class AuditoriaDto {

    private Long id;
    private String usuario;
    private String rol;
    private String accion;
    private String entidad;
    private Long entidadId;
    private String idSipej;
    private String resultado;
    private String detalle;
    private LocalDateTime createdAt;

    public AuditoriaDto() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }

    public String getEntidad() { return entidad; }
    public void setEntidad(String entidad) { this.entidad = entidad; }

    public Long getEntidadId() { return entidadId; }
    public void setEntidadId(Long entidadId) { this.entidadId = entidadId; }

    public String getIdSipej() { return idSipej; }
    public void setIdSipej(String idSipej) { this.idSipej = idSipej; }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }

    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

package co.gov.bogota.sed.esal.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Registro de auditoría de acciones sobre el sistema.
 */
@Entity
@Table(schema = "SED_ESAL", name = "ESAL_AUDITORIA")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esal_auditoria_seq")
    @SequenceGenerator(name = "esal_auditoria_seq", sequenceName = "ESAL_AUDITORIA_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USUARIO", nullable = false, length = 255)
    private String usuario;

    @Column(name = "ROL", length = 100)
    private String rol;

    @Column(name = "ACCION", nullable = false, length = 255)
    private String accion;

    @Column(name = "ENTIDAD", length = 255)
    private String entidad;

    @Column(name = "ENTIDAD_ID")
    private Long entidadId;

    @Column(name = "ID_SIPEJ", length = 100)
    private String idSipej;

    @Column(name = "RESULTADO", length = 100)
    private String resultado;

    @Column(name = "DETALLE", length = 2000)
    private String detalle;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    public Auditoria() {
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

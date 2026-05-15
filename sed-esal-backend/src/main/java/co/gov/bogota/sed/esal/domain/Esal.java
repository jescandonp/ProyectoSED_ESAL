package co.gov.bogota.sed.esal.domain;

import co.gov.bogota.sed.esal.domain.enums.EstadoCompletitud;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad raíz del sistema. Representa una Entidad Sin Ánimo de Lucro (ESAL).
 */
@Entity
@Table(schema = "SED_ESAL", name = "ESAL_ESAL")
public class Esal {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esal_esal_seq")
    @SequenceGenerator(name = "esal_esal_seq", sequenceName = "ESAL_ESAL_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 500)
    private String nombre;

    @Column(name = "ID_SIPEJ", unique = true, length = 100)
    private String idSipej;

    @Column(name = "NIT", length = 50)
    private String nit;

    @Column(name = "DOMICILIO", length = 500)
    private String domicilio;

    @Column(name = "CORREO_ELECTRONICO", length = 255)
    private String correoElectronico;

    @Column(name = "TERMINO_DURACION", length = 255)
    private String terminoDuracion;

    @Lob
    @Column(name = "OBJETO_SOCIAL")
    private String objetoSocial;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 30)
    private EstadoEsal estado = EstadoEsal.ACTIVO;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_COMPLETITUD", nullable = false, length = 40)
    private EstadoCompletitud estadoCompletitud = EstadoCompletitud.INCOMPLETO_BLOQUEANTE;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY", length = 255)
    private String createdBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "UPDATED_BY", length = 255)
    private String updatedBy;

    // -------------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------------

    public Esal() {
    }

    // -------------------------------------------------------------------------
    // Getters y Setters
    // -------------------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIdSipej() {
        return idSipej;
    }

    public void setIdSipej(String idSipej) {
        this.idSipej = idSipej;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getTerminoDuracion() {
        return terminoDuracion;
    }

    public void setTerminoDuracion(String terminoDuracion) {
        this.terminoDuracion = terminoDuracion;
    }

    public String getObjetoSocial() {
        return objetoSocial;
    }

    public void setObjetoSocial(String objetoSocial) {
        this.objetoSocial = objetoSocial;
    }

    public EstadoEsal getEstado() {
        return estado;
    }

    public void setEstado(EstadoEsal estado) {
        this.estado = estado;
    }

    public EstadoCompletitud getEstadoCompletitud() {
        return estadoCompletitud;
    }

    public void setEstadoCompletitud(EstadoCompletitud estadoCompletitud) {
        this.estadoCompletitud = estadoCompletitud;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}

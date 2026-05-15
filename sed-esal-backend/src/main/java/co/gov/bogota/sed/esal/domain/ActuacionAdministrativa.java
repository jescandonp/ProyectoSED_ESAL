package co.gov.bogota.sed.esal.domain;

import co.gov.bogota.sed.esal.domain.enums.TipoActuacion;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Actuación administrativa (suspensión, liquidación, cancelación) sobre una ESAL.
 */
@Entity
@Table(schema = "SED_ESAL", name = "ESAL_ACTUACION_ADMINISTRATIVA")
public class ActuacionAdministrativa {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esal_actuacion_seq")
    @SequenceGenerator(name = "esal_actuacion_seq", sequenceName = "ESAL_ACTUACION_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ESAL_ID", nullable = false)
    private Long esalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_ACTUACION", nullable = false, length = 30)
    private TipoActuacion tipoActuacion;

    @Column(name = "ACTA", length = 255)
    private String acta;

    @Column(name = "FECHA_ACTA")
    private LocalDate fechaActa;

    @Column(name = "RESOLUCION", length = 255)
    private String resolucion;

    @Column(name = "FECHA_RESOLUCION")
    private LocalDate fechaResolucion;

    @Column(name = "MOTIVO", length = 1000)
    private String motivo;

    @Column(name = "TIEMPO_SUSPENSION", length = 255)
    private String tiempoSuspension;

    @Column(name = "FECHA_INICIO")
    private LocalDate fechaInicio;

    @Column(name = "FECHA_FIN")
    private LocalDate fechaFin;

    // -------------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------------

    public ActuacionAdministrativa() {
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

    public Long getEsalId() {
        return esalId;
    }

    public void setEsalId(Long esalId) {
        this.esalId = esalId;
    }

    public TipoActuacion getTipoActuacion() {
        return tipoActuacion;
    }

    public void setTipoActuacion(TipoActuacion tipoActuacion) {
        this.tipoActuacion = tipoActuacion;
    }

    public String getActa() {
        return acta;
    }

    public void setActa(String acta) {
        this.acta = acta;
    }

    public LocalDate getFechaActa() {
        return fechaActa;
    }

    public void setFechaActa(LocalDate fechaActa) {
        this.fechaActa = fechaActa;
    }

    public String getResolucion() {
        return resolucion;
    }

    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }

    public LocalDate getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(LocalDate fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getTiempoSuspension() {
        return tiempoSuspension;
    }

    public void setTiempoSuspension(String tiempoSuspension) {
        this.tiempoSuspension = tiempoSuspension;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }
}

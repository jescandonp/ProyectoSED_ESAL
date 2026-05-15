package co.gov.bogota.sed.esal.domain;

import co.gov.bogota.sed.esal.domain.enums.TipoNombramiento;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Nombramiento de representante, revisor fiscal, tesorero o dignatario de una ESAL.
 */
@Entity
@Table(schema = "SED_ESAL", name = "ESAL_NOMBRAMIENTO")
public class Nombramiento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esal_nombramiento_seq")
    @SequenceGenerator(name = "esal_nombramiento_seq", sequenceName = "ESAL_NOMBRAMIENTO_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ESAL_ID", nullable = false)
    private Long esalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_NOMBRAMIENTO", nullable = false, length = 50)
    private TipoNombramiento tipoNombramiento;

    @Column(name = "NOMBRE", length = 500)
    private String nombre;

    @Column(name = "TIPO_DOCUMENTO", length = 50)
    private String tipoDocumento;

    @Column(name = "NUMERO_DOCUMENTO", length = 100)
    private String numeroDocumento;

    @Column(name = "CARGO", length = 255)
    private String cargo;

    @Column(name = "ACTA_APRUEBA", length = 255)
    private String actaAprueba;

    @Column(name = "FECHA_ACTA")
    private LocalDate fechaActa;

    @Column(name = "TARJETA_PROFESIONAL", length = 255)
    private String tarjetaProfesional;

    @Column(name = "FACULTADES_LIMITACIONES", length = 1000)
    private String facultadesLimitaciones;

    @Column(name = "VIGENTE")
    private Boolean vigente = Boolean.TRUE;

    // -------------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------------

    public Nombramiento() {
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

    public TipoNombramiento getTipoNombramiento() {
        return tipoNombramiento;
    }

    public void setTipoNombramiento(TipoNombramiento tipoNombramiento) {
        this.tipoNombramiento = tipoNombramiento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getActaAprueba() {
        return actaAprueba;
    }

    public void setActaAprueba(String actaAprueba) {
        this.actaAprueba = actaAprueba;
    }

    public LocalDate getFechaActa() {
        return fechaActa;
    }

    public void setFechaActa(LocalDate fechaActa) {
        this.fechaActa = fechaActa;
    }

    public String getTarjetaProfesional() {
        return tarjetaProfesional;
    }

    public void setTarjetaProfesional(String tarjetaProfesional) {
        this.tarjetaProfesional = tarjetaProfesional;
    }

    public String getFacultadesLimitaciones() {
        return facultadesLimitaciones;
    }

    public void setFacultadesLimitaciones(String facultadesLimitaciones) {
        this.facultadesLimitaciones = facultadesLimitaciones;
    }

    public Boolean getVigente() {
        return vigente;
    }

    public void setVigente(Boolean vigente) {
        this.vigente = vigente;
    }
}

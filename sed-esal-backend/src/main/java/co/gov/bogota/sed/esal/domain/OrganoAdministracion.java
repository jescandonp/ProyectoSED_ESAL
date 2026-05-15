package co.gov.bogota.sed.esal.domain;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Miembro del órgano de administración de una ESAL.
 */
@Entity
@Table(schema = "SED_ESAL", name = "ESAL_ORGANO_ADMINISTRACION")
public class OrganoAdministracion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esal_organo_seq")
    @SequenceGenerator(name = "esal_organo_seq", sequenceName = "ESAL_ORGANO_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ESAL_ID", nullable = false)
    private Long esalId;

    @Column(name = "ORGANO", length = 255)
    private String organo;

    @Column(name = "MIEMBRO", length = 500)
    private String miembro;

    @Column(name = "CARGO", length = 255)
    private String cargo;

    @Column(name = "TIPO_DOCUMENTO", length = 50)
    private String tipoDocumento;

    @Column(name = "NUMERO_DOCUMENTO", length = 100)
    private String numeroDocumento;

    @Column(name = "ACTA_APRUEBA", length = 255)
    private String actaAprueba;

    @Column(name = "FECHA_ACTA")
    private LocalDate fechaActa;

    @Column(name = "ACTA_ACLARATORIA", length = 255)
    private String actaAclaratoria;

    @Column(name = "FECHA_ACTA_ACLARATORIA")
    private LocalDate fechaActaAclaratoria;

    @Column(name = "FACULTADES_LIMITACIONES", length = 1000)
    private String facultadesLimitaciones;

    // -------------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------------

    public OrganoAdministracion() {
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

    public String getOrgano() {
        return organo;
    }

    public void setOrgano(String organo) {
        this.organo = organo;
    }

    public String getMiembro() {
        return miembro;
    }

    public void setMiembro(String miembro) {
        this.miembro = miembro;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
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

    public String getActaAclaratoria() {
        return actaAclaratoria;
    }

    public void setActaAclaratoria(String actaAclaratoria) {
        this.actaAclaratoria = actaAclaratoria;
    }

    public LocalDate getFechaActaAclaratoria() {
        return fechaActaAclaratoria;
    }

    public void setFechaActaAclaratoria(LocalDate fechaActaAclaratoria) {
        this.fechaActaAclaratoria = fechaActaAclaratoria;
    }

    public String getFacultadesLimitaciones() {
        return facultadesLimitaciones;
    }

    public void setFacultadesLimitaciones(String facultadesLimitaciones) {
        this.facultadesLimitaciones = facultadesLimitaciones;
    }
}

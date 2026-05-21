package co.gov.bogota.sed.esal.dto;

import java.time.LocalDate;

public class OrganoAdministracionDto {

    private Long id;
    private Long esalId;
    private String organo;
    private String miembro;
    private String cargo;
    private String tipoDocumento;
    private String numeroDocumento;
    private String actaAprueba;
    private LocalDate fechaActa;
    private String actaAclaratoria;
    private LocalDate fechaActaAclaratoria;
    private String facultadesLimitaciones;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEsalId() { return esalId; }
    public void setEsalId(Long esalId) { this.esalId = esalId; }

    public String getOrgano() { return organo; }
    public void setOrgano(String organo) { this.organo = organo; }

    public String getMiembro() { return miembro; }
    public void setMiembro(String miembro) { this.miembro = miembro; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getActaAprueba() { return actaAprueba; }
    public void setActaAprueba(String actaAprueba) { this.actaAprueba = actaAprueba; }

    public LocalDate getFechaActa() { return fechaActa; }
    public void setFechaActa(LocalDate fechaActa) { this.fechaActa = fechaActa; }

    public String getActaAclaratoria() { return actaAclaratoria; }
    public void setActaAclaratoria(String actaAclaratoria) { this.actaAclaratoria = actaAclaratoria; }

    public LocalDate getFechaActaAclaratoria() { return fechaActaAclaratoria; }
    public void setFechaActaAclaratoria(LocalDate fechaActaAclaratoria) {
        this.fechaActaAclaratoria = fechaActaAclaratoria;
    }

    public String getFacultadesLimitaciones() { return facultadesLimitaciones; }
    public void setFacultadesLimitaciones(String facultadesLimitaciones) {
        this.facultadesLimitaciones = facultadesLimitaciones;
    }
}

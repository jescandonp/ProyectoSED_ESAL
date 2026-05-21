package co.gov.bogota.sed.esal.dto;

import co.gov.bogota.sed.esal.domain.enums.TipoNombramiento;

import java.time.LocalDate;

public class NombramientoDto {

    private Long id;
    private Long esalId;
    private TipoNombramiento tipoNombramiento;
    private String nombre;
    private String tipoDocumento;
    private String numeroDocumento;
    private String cargo;
    private String actaAprueba;
    private LocalDate fechaActa;
    private String tarjetaProfesional;
    private String facultadesLimitaciones;
    private Boolean vigente;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEsalId() { return esalId; }
    public void setEsalId(Long esalId) { this.esalId = esalId; }

    public TipoNombramiento getTipoNombramiento() { return tipoNombramiento; }
    public void setTipoNombramiento(TipoNombramiento tipoNombramiento) {
        this.tipoNombramiento = tipoNombramiento;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getActaAprueba() { return actaAprueba; }
    public void setActaAprueba(String actaAprueba) { this.actaAprueba = actaAprueba; }

    public LocalDate getFechaActa() { return fechaActa; }
    public void setFechaActa(LocalDate fechaActa) { this.fechaActa = fechaActa; }

    public String getTarjetaProfesional() { return tarjetaProfesional; }
    public void setTarjetaProfesional(String tarjetaProfesional) {
        this.tarjetaProfesional = tarjetaProfesional;
    }

    public String getFacultadesLimitaciones() { return facultadesLimitaciones; }
    public void setFacultadesLimitaciones(String facultadesLimitaciones) {
        this.facultadesLimitaciones = facultadesLimitaciones;
    }

    public Boolean getVigente() { return vigente; }
    public void setVigente(Boolean vigente) { this.vigente = vigente; }
}

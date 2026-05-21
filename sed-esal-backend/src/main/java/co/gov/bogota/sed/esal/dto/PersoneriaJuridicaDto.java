package co.gov.bogota.sed.esal.dto;

import java.time.LocalDate;

public class PersoneriaJuridicaDto {

    private Long id;
    private Long esalId;
    private String reconocimientoPersoneriaJuridica;
    private LocalDate fechaReconocimientoPersoneriaJuridica;
    private String entidadQueExpide;
    private String inscripcion;
    private LocalDate fechaInscripcion;
    private String entidadQueInscribio;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEsalId() { return esalId; }
    public void setEsalId(Long esalId) { this.esalId = esalId; }

    public String getReconocimientoPersoneriaJuridica() { return reconocimientoPersoneriaJuridica; }
    public void setReconocimientoPersoneriaJuridica(String reconocimientoPersoneriaJuridica) {
        this.reconocimientoPersoneriaJuridica = reconocimientoPersoneriaJuridica;
    }

    public LocalDate getFechaReconocimientoPersoneriaJuridica() {
        return fechaReconocimientoPersoneriaJuridica;
    }
    public void setFechaReconocimientoPersoneriaJuridica(LocalDate fechaReconocimientoPersoneriaJuridica) {
        this.fechaReconocimientoPersoneriaJuridica = fechaReconocimientoPersoneriaJuridica;
    }

    public String getEntidadQueExpide() { return entidadQueExpide; }
    public void setEntidadQueExpide(String entidadQueExpide) { this.entidadQueExpide = entidadQueExpide; }

    public String getInscripcion() { return inscripcion; }
    public void setInscripcion(String inscripcion) { this.inscripcion = inscripcion; }

    public LocalDate getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(LocalDate fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }

    public String getEntidadQueInscribio() { return entidadQueInscribio; }
    public void setEntidadQueInscribio(String entidadQueInscribio) { this.entidadQueInscribio = entidadQueInscribio; }
}

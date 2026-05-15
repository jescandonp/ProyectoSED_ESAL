package co.gov.bogota.sed.esal.domain;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Datos de personería jurídica de una ESAL.
 */
@Entity
@Table(schema = "SED_ESAL", name = "ESAL_PERSONERIA_JURIDICA")
public class PersoneriaJuridica {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esal_personeria_seq")
    @SequenceGenerator(name = "esal_personeria_seq", sequenceName = "ESAL_PERSONERIA_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ESAL_ID", nullable = false)
    private Long esalId;

    @Column(name = "RECONOCIMIENTO_PERSONERIA_JURIDICA", length = 500)
    private String reconocimientoPersoneriaJuridica;

    @Column(name = "FECHA_RECONOCIMIENTO_PERSONERIA_JURIDICA")
    private LocalDate fechaReconocimientoPersoneriaJuridica;

    @Column(name = "ENTIDAD_QUE_EXPIDE", length = 500)
    private String entidadQueExpide;

    @Column(name = "INSCRIPCION", length = 500)
    private String inscripcion;

    @Column(name = "FECHA_INSCRIPCION")
    private LocalDate fechaInscripcion;

    @Column(name = "ENTIDAD_QUE_INSCRIBIO", length = 500)
    private String entidadQueInscribio;

    // -------------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------------

    public PersoneriaJuridica() {
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

    public String getReconocimientoPersoneriaJuridica() {
        return reconocimientoPersoneriaJuridica;
    }

    public void setReconocimientoPersoneriaJuridica(String reconocimientoPersoneriaJuridica) {
        this.reconocimientoPersoneriaJuridica = reconocimientoPersoneriaJuridica;
    }

    public LocalDate getFechaReconocimientoPersoneriaJuridica() {
        return fechaReconocimientoPersoneriaJuridica;
    }

    public void setFechaReconocimientoPersoneriaJuridica(LocalDate fechaReconocimientoPersoneriaJuridica) {
        this.fechaReconocimientoPersoneriaJuridica = fechaReconocimientoPersoneriaJuridica;
    }

    public String getEntidadQueExpide() {
        return entidadQueExpide;
    }

    public void setEntidadQueExpide(String entidadQueExpide) {
        this.entidadQueExpide = entidadQueExpide;
    }

    public String getInscripcion() {
        return inscripcion;
    }

    public void setInscripcion(String inscripcion) {
        this.inscripcion = inscripcion;
    }

    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(LocalDate fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public String getEntidadQueInscribio() {
        return entidadQueInscribio;
    }

    public void setEntidadQueInscribio(String entidadQueInscribio) {
        this.entidadQueInscribio = entidadQueInscribio;
    }
}

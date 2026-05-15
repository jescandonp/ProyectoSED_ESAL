package co.gov.bogota.sed.esal.domain;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Reforma estatutaria de una ESAL. Lista dinámica 1:N.
 */
@Entity
@Table(schema = "SED_ESAL", name = "ESAL_REFORMA_ESTATUTARIA")
public class ReformaEstatutaria {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esal_reforma_seq")
    @SequenceGenerator(name = "esal_reforma_seq", sequenceName = "ESAL_REFORMA_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ESAL_ID", nullable = false)
    private Long esalId;

    @Column(name = "ORDEN")
    private Integer orden;

    @Column(name = "TIPO_ACTO", length = 255)
    private String tipoActo;

    @Column(name = "NUMERO_ACTO", length = 255)
    private String numeroActo;

    @Column(name = "FECHA_ACTO")
    private LocalDate fechaActo;

    @Column(name = "ENTIDAD_QUE_EXPIDE", length = 500)
    private String entidadQueExpide;

    @Column(name = "DESCRIPCION", length = 1000)
    private String descripcion;

    // -------------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------------

    public ReformaEstatutaria() {
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

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public String getTipoActo() {
        return tipoActo;
    }

    public void setTipoActo(String tipoActo) {
        this.tipoActo = tipoActo;
    }

    public String getNumeroActo() {
        return numeroActo;
    }

    public void setNumeroActo(String numeroActo) {
        this.numeroActo = numeroActo;
    }

    public LocalDate getFechaActo() {
        return fechaActo;
    }

    public void setFechaActo(LocalDate fechaActo) {
        this.fechaActo = fechaActo;
    }

    public String getEntidadQueExpide() {
        return entidadQueExpide;
    }

    public void setEntidadQueExpide(String entidadQueExpide) {
        this.entidadQueExpide = entidadQueExpide;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}

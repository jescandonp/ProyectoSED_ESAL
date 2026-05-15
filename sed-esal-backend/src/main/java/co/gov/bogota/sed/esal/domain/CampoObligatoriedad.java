package co.gov.bogota.sed.esal.domain;

import javax.persistence.*;

/**
 * Diccionario de obligatoriedad de campos (cargado desde Base excel.xlsx).
 */
@Entity
@Table(schema = "SED_ESAL", name = "ESAL_CAMPO_OBLIGATORIEDAD")
public class CampoObligatoriedad {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esal_campo_seq")
    @SequenceGenerator(name = "esal_campo_seq", sequenceName = "ESAL_CAMPO_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOMBRE_CAMPO", nullable = false, length = 255)
    private String nombreCampo;

    @Column(name = "SECCION", length = 255)
    private String seccion;

    @Column(name = "CONTEXTO", length = 500)
    private String contexto;

    @Column(name = "OBLIGATORIO", nullable = false)
    private Boolean obligatorio;

    @Column(name = "NOTA", length = 1000)
    private String nota;

    @Column(name = "ORDEN")
    private Integer orden;

    public CampoObligatoriedad() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreCampo() { return nombreCampo; }
    public void setNombreCampo(String nombreCampo) { this.nombreCampo = nombreCampo; }

    public String getSeccion() { return seccion; }
    public void setSeccion(String seccion) { this.seccion = seccion; }

    public String getContexto() { return contexto; }
    public void setContexto(String contexto) { this.contexto = contexto; }

    public Boolean getObligatorio() { return obligatorio; }
    public void setObligatorio(Boolean obligatorio) { this.obligatorio = obligatorio; }

    public String getNota() { return nota; }
    public void setNota(String nota) { this.nota = nota; }

    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
}

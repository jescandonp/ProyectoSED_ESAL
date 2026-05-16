package co.gov.bogota.sed.esal.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(schema = "SED_ESAL", name = "ESAL_NUMERACION_CERTIFICADO")
public class NumeracionCertificado {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esal_numeracion_seq")
    @SequenceGenerator(name = "esal_numeracion_seq", sequenceName = "ESAL_NUMERACION_CERT_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PREFIJO", nullable = false, length = 20)
    private String prefijo;

    @Column(name = "ANIO", nullable = false)
    private Integer anio;

    @Column(name = "ULTIMO_CONSECUTIVO", nullable = false)
    private Long ultimoConsecutivo = 0L;

    @Column(name = "ACTIVO", nullable = false)
    private Boolean activo = true;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    public NumeracionCertificado() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPrefijo() { return prefijo; }
    public void setPrefijo(String prefijo) { this.prefijo = prefijo; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public Long getUltimoConsecutivo() { return ultimoConsecutivo; }
    public void setUltimoConsecutivo(Long ultimoConsecutivo) { this.ultimoConsecutivo = ultimoConsecutivo; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

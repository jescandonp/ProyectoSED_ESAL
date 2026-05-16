package co.gov.bogota.sed.esal.domain;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(schema = "SED_ESAL", name = "ESAL_FIRMANTE")
public class Firmante {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esal_firmante_seq")
    @SequenceGenerator(name = "esal_firmante_seq", sequenceName = "ESAL_FIRMANTE_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 255)
    private String nombre;

    @Column(name = "CARGO", nullable = false, length = 255)
    private String cargo;

    @Column(name = "DEPENDENCIA", length = 255)
    private String dependencia;

    @Column(name = "FECHA_INICIO_VIGENCIA", nullable = false)
    private LocalDate fechaInicioVigencia;

    @Column(name = "FECHA_FIN_VIGENCIA")
    private LocalDate fechaFinVigencia;

    @Column(name = "ACTIVO", nullable = false)
    private Boolean activo = true;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY", length = 255)
    private String createdBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "UPDATED_BY", length = 255)
    private String updatedBy;

    public Firmante() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getDependencia() { return dependencia; }
    public void setDependencia(String dependencia) { this.dependencia = dependencia; }

    public LocalDate getFechaInicioVigencia() { return fechaInicioVigencia; }
    public void setFechaInicioVigencia(LocalDate fechaInicioVigencia) { this.fechaInicioVigencia = fechaInicioVigencia; }

    public LocalDate getFechaFinVigencia() { return fechaFinVigencia; }
    public void setFechaFinVigencia(LocalDate fechaFinVigencia) { this.fechaFinVigencia = fechaFinVigencia; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}

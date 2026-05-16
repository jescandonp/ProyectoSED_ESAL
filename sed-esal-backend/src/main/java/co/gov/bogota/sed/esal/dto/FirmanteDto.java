package co.gov.bogota.sed.esal.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FirmanteDto {
    private Long id;
    private String nombre;
    private String cargo;
    private String dependencia;
    private LocalDate fechaInicioVigencia;
    private LocalDate fechaFinVigencia;
    private Boolean activo;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;

    public FirmanteDto() {}

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
}

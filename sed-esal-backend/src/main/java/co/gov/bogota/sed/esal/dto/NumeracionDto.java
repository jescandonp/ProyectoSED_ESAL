package co.gov.bogota.sed.esal.dto;

import java.time.LocalDateTime;

public class NumeracionDto {
    private Long id;
    private String prefijo;
    private Integer anio;
    private Long ultimoConsecutivo;
    private boolean activo;
    private LocalDateTime updatedAt;

    public NumeracionDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPrefijo() { return prefijo; }
    public void setPrefijo(String prefijo) { this.prefijo = prefijo; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public Long getUltimoConsecutivo() { return ultimoConsecutivo; }
    public void setUltimoConsecutivo(Long ultimoConsecutivo) { this.ultimoConsecutivo = ultimoConsecutivo; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

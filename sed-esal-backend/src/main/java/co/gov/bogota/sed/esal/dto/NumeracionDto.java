package co.gov.bogota.sed.esal.dto;

import java.time.LocalDateTime;

public class NumeracionDto {
    private Long id;
    private String prefijo;
    private Integer anio;
    private Long ultimoConsecutivo;
    private Boolean activo;
    private String proximoNumero;
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

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public String getProximoNumero() { return proximoNumero; }
    public void setProximoNumero(String proximoNumero) { this.proximoNumero = proximoNumero; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

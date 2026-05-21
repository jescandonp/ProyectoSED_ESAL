package co.gov.bogota.sed.esal.dto;

import java.time.LocalDate;

public class CancelacionEsalDto {

    private String resolucion;
    private LocalDate fechaResolucion;
    private String motivo;

    public String getResolucion() { return resolucion; }
    public void setResolucion(String resolucion) { this.resolucion = resolucion; }

    public LocalDate getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDate fechaResolucion) { this.fechaResolucion = fechaResolucion; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}

package co.gov.bogota.sed.esal.dto;

import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;

public class ReactivacionEsalDto {

    private EstadoEsal estadoDestino;
    private String motivo;

    public EstadoEsal getEstadoDestino() { return estadoDestino; }
    public void setEstadoDestino(EstadoEsal estadoDestino) { this.estadoDestino = estadoDestino; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}

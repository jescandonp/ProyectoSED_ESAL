package co.gov.bogota.sed.esal.dto;

import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;

/**
 * DTO para cambiar el estado de una ESAL.
 */
public class CambiarEstadoDto {

    private EstadoEsal estado;

    public CambiarEstadoDto() {
    }

    public EstadoEsal getEstado() { return estado; }
    public void setEstado(EstadoEsal estado) { this.estado = estado; }
}

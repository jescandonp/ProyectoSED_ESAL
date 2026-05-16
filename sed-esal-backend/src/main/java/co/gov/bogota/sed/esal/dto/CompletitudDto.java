package co.gov.bogota.sed.esal.dto;

import co.gov.bogota.sed.esal.domain.enums.EstadoCompletitud;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;

import java.util.List;

/**
 * DTO con el resultado del cálculo de completitud de una ESAL.
 */
public class CompletitudDto {

    private Long esalId;
    private String idSipej;
    private String nombre;
    private EstadoEsal estado;
    private EstadoCompletitud estadoCompletitud;
    private int totalAdvertencias;
    private int advertenciasBloqueantes;
    private int advertenciasNoBloqueantes;
    private List<AdvertenciaItemDto> advertencias;

    public CompletitudDto() {
    }

    public Long getEsalId() {
        return esalId;
    }

    public void setEsalId(Long esalId) {
        this.esalId = esalId;
    }

    public String getIdSipej() {
        return idSipej;
    }

    public void setIdSipej(String idSipej) {
        this.idSipej = idSipej;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public EstadoEsal getEstado() {
        return estado;
    }

    public void setEstado(EstadoEsal estado) {
        this.estado = estado;
    }

    public EstadoCompletitud getEstadoCompletitud() {
        return estadoCompletitud;
    }

    public void setEstadoCompletitud(EstadoCompletitud estadoCompletitud) {
        this.estadoCompletitud = estadoCompletitud;
    }

    public int getTotalAdvertencias() {
        return totalAdvertencias;
    }

    public void setTotalAdvertencias(int totalAdvertencias) {
        this.totalAdvertencias = totalAdvertencias;
    }

    public int getAdvertenciasBloqueantes() {
        return advertenciasBloqueantes;
    }

    public void setAdvertenciasBloqueantes(int advertenciasBloqueantes) {
        this.advertenciasBloqueantes = advertenciasBloqueantes;
    }

    public int getAdvertenciasNoBloqueantes() {
        return advertenciasNoBloqueantes;
    }

    public void setAdvertenciasNoBloqueantes(int advertenciasNoBloqueantes) {
        this.advertenciasNoBloqueantes = advertenciasNoBloqueantes;
    }

    public List<AdvertenciaItemDto> getAdvertencias() {
        return advertencias;
    }

    public void setAdvertencias(List<AdvertenciaItemDto> advertencias) {
        this.advertencias = advertencias;
    }

    // -------------------------------------------------------------------------
    // Inner DTO
    // -------------------------------------------------------------------------

    public static class AdvertenciaItemDto {

        private String seccion;
        private String campo;
        private String tipo;
        private boolean bloqueante;
        private String mensaje;

        public AdvertenciaItemDto() {
        }

        public String getSeccion() {
            return seccion;
        }

        public void setSeccion(String seccion) {
            this.seccion = seccion;
        }

        public String getCampo() {
            return campo;
        }

        public void setCampo(String campo) {
            this.campo = campo;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public boolean isBloqueante() {
            return bloqueante;
        }

        public void setBloqueante(boolean bloqueante) {
            this.bloqueante = bloqueante;
        }

        public String getMensaje() {
            return mensaje;
        }

        public void setMensaje(String mensaje) {
            this.mensaje = mensaje;
        }
    }
}

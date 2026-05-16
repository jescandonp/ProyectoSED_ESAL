package co.gov.bogota.sed.esal.dto;

import co.gov.bogota.sed.esal.domain.enums.EstadoCompletitud;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;

import java.time.LocalDateTime;
import java.util.List;

public class PreviewCertificadoDto {

    private Long esalId;
    private String idSipej;
    private String nit;
    private String nombre;
    private EstadoEsal estado;
    private EstadoCompletitud estadoCompletitud;
    private LocalDateTime versionDatos;
    private Boolean generacionHabilitada;
    private String alertaEstado;
    private List<SeccionPreviewDto> secciones;
    private List<String> advertencias;
    private List<BloqueoDto> bloqueos;

    public Long getEsalId() { return esalId; }
    public void setEsalId(Long esalId) { this.esalId = esalId; }

    public String getIdSipej() { return idSipej; }
    public void setIdSipej(String idSipej) { this.idSipej = idSipej; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public EstadoEsal getEstado() { return estado; }
    public void setEstado(EstadoEsal estado) { this.estado = estado; }

    public EstadoCompletitud getEstadoCompletitud() { return estadoCompletitud; }
    public void setEstadoCompletitud(EstadoCompletitud estadoCompletitud) { this.estadoCompletitud = estadoCompletitud; }

    public LocalDateTime getVersionDatos() { return versionDatos; }
    public void setVersionDatos(LocalDateTime versionDatos) { this.versionDatos = versionDatos; }

    public Boolean getGeneracionHabilitada() { return generacionHabilitada; }
    public void setGeneracionHabilitada(Boolean generacionHabilitada) { this.generacionHabilitada = generacionHabilitada; }

    public String getAlertaEstado() { return alertaEstado; }
    public void setAlertaEstado(String alertaEstado) { this.alertaEstado = alertaEstado; }

    public List<SeccionPreviewDto> getSecciones() { return secciones; }
    public void setSecciones(List<SeccionPreviewDto> secciones) { this.secciones = secciones; }

    public List<String> getAdvertencias() { return advertencias; }
    public void setAdvertencias(List<String> advertencias) { this.advertencias = advertencias; }

    public List<BloqueoDto> getBloqueos() { return bloqueos; }
    public void setBloqueos(List<BloqueoDto> bloqueos) { this.bloqueos = bloqueos; }

    public static class SeccionPreviewDto {
        private String nombre;
        private List<CampoPreviewDto> campos;

        public SeccionPreviewDto() {
        }

        public SeccionPreviewDto(String nombre, List<CampoPreviewDto> campos) {
            this.nombre = nombre;
            this.campos = campos;
        }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public List<CampoPreviewDto> getCampos() { return campos; }
        public void setCampos(List<CampoPreviewDto> campos) { this.campos = campos; }
    }

    public static class CampoPreviewDto {
        private String etiqueta;
        private String valor;
        private Boolean faltante;
        private Boolean obligatorio;
        private Boolean origenHistorico;

        public CampoPreviewDto() {
        }

        public CampoPreviewDto(String etiqueta, String valor, Boolean faltante,
                               Boolean obligatorio, Boolean origenHistorico) {
            this.etiqueta = etiqueta;
            this.valor = valor;
            this.faltante = faltante;
            this.obligatorio = obligatorio;
            this.origenHistorico = origenHistorico;
        }

        public String getEtiqueta() { return etiqueta; }
        public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }
        public String getValor() { return valor; }
        public void setValor(String valor) { this.valor = valor; }
        public Boolean getFaltante() { return faltante; }
        public void setFaltante(Boolean faltante) { this.faltante = faltante; }
        public Boolean getObligatorio() { return obligatorio; }
        public void setObligatorio(Boolean obligatorio) { this.obligatorio = obligatorio; }
        public Boolean getOrigenHistorico() { return origenHistorico; }
        public void setOrigenHistorico(Boolean origenHistorico) { this.origenHistorico = origenHistorico; }
    }
}

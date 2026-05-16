package co.gov.bogota.sed.esal.dto;

public class BloqueoDto {

    private String seccion;
    private String campo;
    private String tipo;
    private String mensaje;
    private Boolean origenHistorico;

    public BloqueoDto() {
    }

    public BloqueoDto(String seccion, String campo, String tipo, String mensaje, Boolean origenHistorico) {
        this.seccion = seccion;
        this.campo = campo;
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.origenHistorico = origenHistorico;
    }

    public String getSeccion() { return seccion; }
    public void setSeccion(String seccion) { this.seccion = seccion; }

    public String getCampo() { return campo; }
    public void setCampo(String campo) { this.campo = campo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public Boolean getOrigenHistorico() { return origenHistorico; }
    public void setOrigenHistorico(Boolean origenHistorico) { this.origenHistorico = origenHistorico; }
}

package co.gov.bogota.sed.esal.domain.enums;

public enum CertificadoPlantilla {
    EYRL_DEFAULT("I10-EYRL-DEFAULT-v1"),
    EYRL_SUSPENDIDA("I10-EYRL-SUSPENDIDA-v1"),
    EYRL_LIQUIDACION_TRAMITE_CANCELACION_VOLUNTARIA("I10-EYRL-LIQUIDACION-TRAMITE-v1"),
    EYRL_LIQUIDACION_TERMINO_DURACION("I10-EYRL-LIQUIDACION-TERMINO-v1"),
    EYRL_CANCELADA_VOLUNTARIAMENTE("I10-EYRL-CANCELADA-VOLUNTARIA-v1"),
    EYRL_CANCELADA_ORDEN_AUTORIDAD("I10-EYRL-CANCELADA-AUTORIDAD-v1");

    private final String version;

    CertificadoPlantilla(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}

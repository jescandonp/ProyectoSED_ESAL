package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.DocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.CertificadoPlantilla;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.domain.enums.SubtipoDocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.TipoDocumentoSoporte;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class CertificadoTemplateSelectorTest {

    private final CertificadoTemplateSelector selector = new CertificadoTemplateSelector();

    @Test
    void seleccionar_suspendida_retornaPlantillaSuspendida() {
        assertThat(selector.seleccionar(EstadoEsal.SUSPENDIDO, Collections.emptyList()))
                .isEqualTo(CertificadoPlantilla.EYRL_SUSPENDIDA);
    }

    @Test
    void seleccionar_enLiquidacionConTramiteCancelacionVoluntaria_retornaPlantillaLiquidacionTramite() {
        assertThat(selector.seleccionar(EstadoEsal.EN_LIQUIDACION,
                Collections.singletonList(documento(TipoDocumentoSoporte.LIQUIDACION,
                        SubtipoDocumentoSoporte.TRAMITE_CANCELACION_VOLUNTARIA, true))))
                .isEqualTo(CertificadoPlantilla.EYRL_LIQUIDACION_TRAMITE_CANCELACION_VOLUNTARIA);
    }

    @Test
    void seleccionar_enLiquidacionConTerminoDuracion_retornaPlantillaLiquidacionTermino() {
        assertThat(selector.seleccionar(EstadoEsal.EN_LIQUIDACION,
                Collections.singletonList(documento(TipoDocumentoSoporte.LIQUIDACION,
                        SubtipoDocumentoSoporte.TERMINO_DURACION, true))))
                .isEqualTo(CertificadoPlantilla.EYRL_LIQUIDACION_TERMINO_DURACION);
    }

    @Test
    void seleccionar_canceladaVoluntariamente_retornaPlantillaCanceladaVoluntaria() {
        assertThat(selector.seleccionar(EstadoEsal.CANCELADO,
                Collections.singletonList(documento(TipoDocumentoSoporte.CANCELACION,
                        SubtipoDocumentoSoporte.CANCELACION_VOLUNTARIA, true))))
                .isEqualTo(CertificadoPlantilla.EYRL_CANCELADA_VOLUNTARIAMENTE);
    }

    @Test
    void seleccionar_canceladaPorOrdenAutoridad_retornaPlantillaCanceladaAutoridad() {
        assertThat(selector.seleccionar(EstadoEsal.CANCELADO,
                Collections.singletonList(documento(TipoDocumentoSoporte.CANCELACION,
                        SubtipoDocumentoSoporte.ORDEN_AUTORIDAD, true))))
                .isEqualTo(CertificadoPlantilla.EYRL_CANCELADA_ORDEN_AUTORIDAD);
    }

    @Test
    void seleccionar_activaSinReglaEspecial_retornaDefault() {
        assertThat(selector.seleccionar(EstadoEsal.ACTIVO, Collections.emptyList()))
                .isEqualTo(CertificadoPlantilla.EYRL_DEFAULT);
    }

    @Test
    void seleccionar_ignoraDocumentosNoVigentes() {
        assertThat(selector.seleccionar(EstadoEsal.CANCELADO,
                Collections.singletonList(documento(TipoDocumentoSoporte.CANCELACION,
                        SubtipoDocumentoSoporte.CANCELACION_VOLUNTARIA, false))))
                .isEqualTo(CertificadoPlantilla.EYRL_DEFAULT);
    }

    @Test
    void seleccionar_prefiereDocumentoVigenteCompatibleEntreHistoricos() {
        DocumentoSoporte historico = documento(TipoDocumentoSoporte.CANCELACION,
                SubtipoDocumentoSoporte.CANCELACION_VOLUNTARIA, false);
        DocumentoSoporte vigente = documento(TipoDocumentoSoporte.CANCELACION,
                SubtipoDocumentoSoporte.ORDEN_AUTORIDAD, true);

        assertThat(selector.seleccionar(EstadoEsal.CANCELADO, Arrays.asList(historico, vigente)))
                .isEqualTo(CertificadoPlantilla.EYRL_CANCELADA_ORDEN_AUTORIDAD);
    }

    private DocumentoSoporte documento(TipoDocumentoSoporte tipo,
                                       SubtipoDocumentoSoporte subtipo,
                                       boolean vigente) {
        DocumentoSoporte documento = new DocumentoSoporte();
        documento.setTipoDocumental(tipo);
        documento.setSubtipoDocumental(subtipo);
        documento.setVigente(vigente);
        return documento;
    }
}

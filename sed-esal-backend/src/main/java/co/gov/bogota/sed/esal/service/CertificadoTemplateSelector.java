package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.DocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.CertificadoPlantilla;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.domain.enums.SubtipoDocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.TipoDocumentoSoporte;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CertificadoTemplateSelector {

    public CertificadoPlantilla seleccionar(EstadoEsal estado, List<DocumentoSoporte> documentos) {
        if (EstadoEsal.SUSPENDIDO.equals(estado)) {
            return CertificadoPlantilla.EYRL_SUSPENDIDA;
        }
        List<DocumentoSoporte> documentosSeguros = documentos == null ? Collections.emptyList() : documentos;
        if (EstadoEsal.EN_LIQUIDACION.equals(estado)) {
            if (existeVigente(documentosSeguros, TipoDocumentoSoporte.LIQUIDACION,
                    SubtipoDocumentoSoporte.TRAMITE_CANCELACION_VOLUNTARIA)) {
                return CertificadoPlantilla.EYRL_LIQUIDACION_TRAMITE_CANCELACION_VOLUNTARIA;
            }
            if (existeVigente(documentosSeguros, TipoDocumentoSoporte.LIQUIDACION,
                    SubtipoDocumentoSoporte.TERMINO_DURACION)) {
                return CertificadoPlantilla.EYRL_LIQUIDACION_TERMINO_DURACION;
            }
        }
        if (EstadoEsal.CANCELADO.equals(estado)) {
            if (existeVigente(documentosSeguros, TipoDocumentoSoporte.CANCELACION,
                    SubtipoDocumentoSoporte.CANCELACION_VOLUNTARIA)) {
                return CertificadoPlantilla.EYRL_CANCELADA_VOLUNTARIAMENTE;
            }
            if (existeVigente(documentosSeguros, TipoDocumentoSoporte.CANCELACION,
                    SubtipoDocumentoSoporte.ORDEN_AUTORIDAD)) {
                return CertificadoPlantilla.EYRL_CANCELADA_ORDEN_AUTORIDAD;
            }
        }
        return CertificadoPlantilla.EYRL_DEFAULT;
    }

    private boolean existeVigente(List<DocumentoSoporte> documentos,
                                  TipoDocumentoSoporte tipo,
                                  SubtipoDocumentoSoporte subtipo) {
        return documentos.stream()
                .anyMatch(documento -> Boolean.TRUE.equals(documento.getVigente())
                        && tipo.equals(documento.getTipoDocumental())
                        && subtipo.equals(documento.getSubtipoDocumental()));
    }
}

package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.ActuacionAdministrativa;
import co.gov.bogota.sed.esal.domain.Esal;
import co.gov.bogota.sed.esal.domain.Nombramiento;
import co.gov.bogota.sed.esal.domain.OrganoAdministracion;
import co.gov.bogota.sed.esal.domain.PersoneriaJuridica;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.domain.enums.TipoActuacion;
import co.gov.bogota.sed.esal.domain.enums.TipoNombramiento;
import co.gov.bogota.sed.esal.dto.CertificadoNarrativoDto;
import co.gov.bogota.sed.esal.dto.CertificadoNarrativoDto.MiembroDto;
import co.gov.bogota.sed.esal.repository.ActuacionAdministrativaRepository;
import co.gov.bogota.sed.esal.repository.EsalRepository;
import co.gov.bogota.sed.esal.repository.NombramientoRepository;
import co.gov.bogota.sed.esal.repository.OrganoAdministracionRepository;
import co.gov.bogota.sed.esal.repository.PersoneriaJuridicaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CertificadoAssembler {

    private final EsalRepository esalRepository;
    private final PersoneriaJuridicaRepository personeriaRepository;
    private final NombramientoRepository nombramientoRepository;
    private final OrganoAdministracionRepository organoRepository;
    private final ActuacionAdministrativaRepository actuacionRepository;

    public CertificadoAssembler(EsalRepository esalRepository,
                                PersoneriaJuridicaRepository personeriaRepository,
                                NombramientoRepository nombramientoRepository,
                                OrganoAdministracionRepository organoRepository,
                                ActuacionAdministrativaRepository actuacionRepository) {
        this.esalRepository = esalRepository;
        this.personeriaRepository = personeriaRepository;
        this.nombramientoRepository = nombramientoRepository;
        this.organoRepository = organoRepository;
        this.actuacionRepository = actuacionRepository;
    }

    public CertificadoNarrativoDto ensamblar(Long esalId) {
        Esal esal = esalRepository.findById(esalId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "ESAL no encontrada con id: " + esalId));

        List<PersoneriaJuridica> personerias = personeriaRepository.findByEsalId(esalId);
        List<Nombramiento> nombramientos = nombramientoRepository.findByEsalId(esalId);
        List<OrganoAdministracion> organos = organoRepository.findByEsalId(esalId);
        List<ActuacionAdministrativa> actuaciones = actuacionRepository.findByEsalId(esalId);

        CertificadoNarrativoDto dto = new CertificadoNarrativoDto();
        dto.setNombre(esal.getNombre());
        dto.setIdSipej(esal.getIdSipej());
        dto.setNit(esal.getNit());
        dto.setDomicilio(esal.getDomicilio());
        dto.setCorreoElectronico(esal.getCorreoElectronico());
        dto.setTerminoDuracion(esal.getTerminoDuracion());
        dto.setObjetoSocial(esal.getObjetoSocial());
        dto.setEstado(esal.getEstado());
        dto.setAlertaEstado(alertaEstado(esal.getEstado(), actuaciones));

        PersoneriaJuridica pj = personerias.isEmpty() ? null : personerias.get(0);
        if (pj != null) {
            dto.setResolucionPersoneria(pj.getReconocimientoPersoneriaJuridica());
            dto.setFechaResolucion(pj.getFechaReconocimientoPersoneriaJuridica());
            dto.setEntidadQueExpide(pj.getEntidadQueExpide());
            dto.setInscripcion(pj.getInscripcion());
            dto.setFechaInscripcion(pj.getFechaInscripcion());
        }

        List<Nombramiento> representantes = nombramientos.stream()
                .filter(this::esVigente)
                .filter(n -> TipoNombramiento.REPRESENTANTE_LEGAL.equals(n.getTipoNombramiento())
                        || TipoNombramiento.REPRESENTANTE_LEGAL_SUPLENTE.equals(n.getTipoNombramiento()))
                .sorted(Comparator.comparingInt(this::ordenRepresentante))
                .collect(Collectors.toList());

        dto.setRepresentantesLegales(representantes.stream()
                .map(this::mapearNombramiento)
                .collect(Collectors.toList()));
        dto.setFacultadesRepresentante(representantes.stream()
                .filter(n -> TipoNombramiento.REPRESENTANTE_LEGAL.equals(n.getTipoNombramiento()))
                .map(Nombramiento::getFacultadesLimitaciones)
                .filter(v -> v != null && !v.trim().isEmpty())
                .findFirst()
                .orElse(null));

        dto.setMiembrosJunta(mapearOrganos(organos, "JUNTA"));
        dto.setMiembrosAsamblea(mapearOrganos(organos, "ASAMBLEA"));
        dto.setRevisoresFiscales(nombramientos.stream()
                .filter(this::esVigente)
                .filter(n -> TipoNombramiento.REVISOR_FISCAL_PRINCIPAL.equals(n.getTipoNombramiento())
                        || TipoNombramiento.REVISOR_FISCAL_SUPLENTE.equals(n.getTipoNombramiento()))
                .sorted(Comparator.comparingInt(this::ordenRevisor))
                .map(this::mapearNombramiento)
                .collect(Collectors.toList()));

        return dto;
    }

    private boolean esVigente(Nombramiento nombramiento) {
        return Boolean.TRUE.equals(nombramiento.getVigente());
    }

    private int ordenRepresentante(Nombramiento nombramiento) {
        return TipoNombramiento.REPRESENTANTE_LEGAL.equals(nombramiento.getTipoNombramiento()) ? 0 : 1;
    }

    private int ordenRevisor(Nombramiento nombramiento) {
        return TipoNombramiento.REVISOR_FISCAL_PRINCIPAL.equals(nombramiento.getTipoNombramiento()) ? 0 : 1;
    }

    private List<MiembroDto> mapearOrganos(List<OrganoAdministracion> organos, String textoOrgano) {
        if (organos == null || organos.isEmpty()) {
            return new ArrayList<>();
        }
        return organos.stream()
                .filter(o -> contiene(o.getOrgano(), textoOrgano))
                .map(this::mapearOrgano)
                .collect(Collectors.toList());
    }

    private boolean contiene(String valor, String esperado) {
        return valor != null && valor.toUpperCase(Locale.ROOT).contains(esperado);
    }

    private MiembroDto mapearNombramiento(Nombramiento nombramiento) {
        MiembroDto dto = new MiembroDto();
        dto.setNombre(nombramiento.getNombre());
        dto.setTipoDocumento(nombramiento.getTipoDocumento());
        dto.setNumeroDocumento(nombramiento.getNumeroDocumento());
        dto.setCargo(nombramiento.getCargo());
        dto.setActaNombramiento(nombramiento.getActaAprueba());
        dto.setRadicadoSed(null);
        return dto;
    }

    private MiembroDto mapearOrgano(OrganoAdministracion organo) {
        MiembroDto dto = new MiembroDto();
        dto.setNombre(organo.getMiembro());
        dto.setTipoDocumento(organo.getTipoDocumento());
        dto.setNumeroDocumento(organo.getNumeroDocumento());
        dto.setCargo(organo.getCargo());
        dto.setActaNombramiento(organo.getActaAprueba());
        dto.setRadicadoSed(null);
        return dto;
    }

    private String alertaEstado(EstadoEsal estado, List<ActuacionAdministrativa> actuaciones) {
        if (EstadoEsal.SUSPENDIDO.equals(estado) && tieneActuacion(actuaciones, TipoActuacion.SUSPENSION)) {
            return "ESAL en estado SUSPENDIDO. El certificado reflejara esta situacion.";
        }
        if (EstadoEsal.EN_LIQUIDACION.equals(estado) && tieneActuacion(actuaciones, TipoActuacion.LIQUIDACION)) {
            return "ESAL en estado EN_LIQUIDACION. El certificado reflejara esta situacion.";
        }
        if (EstadoEsal.CANCELADO.equals(estado) && tieneActuacion(actuaciones, TipoActuacion.CANCELACION)) {
            return "ESAL en estado CANCELADO. El certificado reflejara esta situacion.";
        }
        return null;
    }

    private boolean tieneActuacion(List<ActuacionAdministrativa> actuaciones, TipoActuacion tipo) {
        return actuaciones != null && actuaciones.stream()
                .anyMatch(a -> tipo.equals(a.getTipoActuacion()));
    }
}

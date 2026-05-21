package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.Esal;
import co.gov.bogota.sed.esal.domain.PersoneriaJuridica;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.dto.EsalInformacionPrincipalDto;
import co.gov.bogota.sed.esal.dto.MantenimientoEsalDto;
import co.gov.bogota.sed.esal.dto.PersoneriaJuridicaDto;
import co.gov.bogota.sed.esal.repository.EsalRepository;
import co.gov.bogota.sed.esal.repository.PersoneriaJuridicaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class EsalMaintenanceService {

    private final EsalRepository esalRepository;
    private final PersoneriaJuridicaRepository personeriaRepository;
    private final CompletitudService completitudService;
    private final AuditoriaService auditoriaService;

    public EsalMaintenanceService(EsalRepository esalRepository,
                                  PersoneriaJuridicaRepository personeriaRepository,
                                  CompletitudService completitudService,
                                  AuditoriaService auditoriaService) {
        this.esalRepository = esalRepository;
        this.personeriaRepository = personeriaRepository;
        this.completitudService = completitudService;
        this.auditoriaService = auditoriaService;
    }

    public MantenimientoEsalDto crear(EsalInformacionPrincipalDto dto, String usuario) {
        validarNombre(dto);
        validarIdSipejUnico(dto.getIdSipej(), null);

        Esal esal = new Esal();
        aplicarInformacionPrincipal(esal, dto, true);
        esal.setCreatedAt(LocalDateTime.now());
        esal.setCreatedBy(usuario);
        Esal saved = esalRepository.save(esal);

        auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.ESAL_CREADA,
                AuditoriaAcciones.ENTIDAD_ESAL,
                saved.getId(), saved.getIdSipej(),
                AuditoriaAcciones.RESULTADO_EXITO, null);

        completitudService.calcular(saved.getId());
        return obtenerMantenimiento(saved.getId());
    }

    @Transactional(readOnly = true)
    public MantenimientoEsalDto obtenerMantenimiento(Long esalId) {
        Esal esal = obtenerEsal(esalId);
        MantenimientoEsalDto dto = new MantenimientoEsalDto();
        dto.setId(esal.getId());
        dto.setInformacionPrincipal(toInformacionPrincipalDto(esal));
        dto.setPersoneriaJuridica(obtenerPersoneria(esal.getId()));
        return dto;
    }

    public MantenimientoEsalDto actualizarInformacionPrincipal(Long esalId,
                                                               EsalInformacionPrincipalDto dto,
                                                               String usuario) {
        Esal esal = obtenerEsal(esalId);
        validarIdSipejUnico(dto.getIdSipej(), esal.getId());
        aplicarInformacionPrincipal(esal, dto, false);
        esal.setUpdatedAt(LocalDateTime.now());
        esal.setUpdatedBy(usuario);
        Esal saved = esalRepository.save(esal);

        auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.ESAL_INFORMACION_PRINCIPAL_ACTUALIZADA,
                AuditoriaAcciones.ENTIDAD_ESAL,
                saved.getId(), saved.getIdSipej(),
                AuditoriaAcciones.RESULTADO_EXITO, null);

        completitudService.calcular(saved.getId());
        return obtenerMantenimiento(saved.getId());
    }

    public MantenimientoEsalDto guardarPersoneriaJuridica(Long esalId,
                                                          PersoneriaJuridicaDto dto,
                                                          String usuario) {
        Esal esal = obtenerEsal(esalId);
        PersoneriaJuridica personeria = buscarPersoneriaEntidad(esalId);
        if (personeria == null) {
            personeria = new PersoneriaJuridica();
            personeria.setEsalId(esalId);
        }
        personeria.setReconocimientoPersoneriaJuridica(dto.getReconocimientoPersoneriaJuridica());
        personeria.setFechaReconocimientoPersoneriaJuridica(dto.getFechaReconocimientoPersoneriaJuridica());
        personeria.setEntidadQueExpide(dto.getEntidadQueExpide());
        personeria.setInscripcion(dto.getInscripcion());
        personeria.setFechaInscripcion(dto.getFechaInscripcion());
        personeria.setEntidadQueInscribio(dto.getEntidadQueInscribio());
        personeriaRepository.save(personeria);

        esal.setUpdatedAt(LocalDateTime.now());
        esal.setUpdatedBy(usuario);
        esalRepository.save(esal);

        auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.ESAL_PERSONERIA_ACTUALIZADA,
                AuditoriaAcciones.ENTIDAD_ESAL,
                esal.getId(), esal.getIdSipej(),
                AuditoriaAcciones.RESULTADO_EXITO, null);

        completitudService.calcular(esalId);
        return obtenerMantenimiento(esalId);
    }

    private void aplicarInformacionPrincipal(Esal esal, EsalInformacionPrincipalDto dto, boolean crear) {
        if (dto.getNombre() != null) {
            esal.setNombre(dto.getNombre().trim());
        }
        if (dto.getIdSipej() != null) {
            esal.setIdSipej(dto.getIdSipej());
        }
        if (dto.getNit() != null) {
            esal.setNit(dto.getNit());
        }
        if (dto.getDomicilio() != null) {
            esal.setDomicilio(dto.getDomicilio());
        }
        if (dto.getCorreoElectronico() != null) {
            esal.setCorreoElectronico(dto.getCorreoElectronico());
        }
        if (dto.getTerminoDuracion() != null) {
            esal.setTerminoDuracion(dto.getTerminoDuracion());
        }
        if (dto.getObjetoSocial() != null) {
            esal.setObjetoSocial(dto.getObjetoSocial());
        }
        if (crear && dto.getEstado() != null) {
            esal.setEstado(dto.getEstado());
        } else if (!crear && dto.getEstado() != null && !EstadoEsal.CANCELADO.equals(dto.getEstado())) {
            esal.setEstado(dto.getEstado());
        }
    }

    private void validarNombre(EsalInformacionPrincipalDto dto) {
        if (dto == null || dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'nombre' es obligatorio.");
        }
    }

    private void validarIdSipejUnico(String idSipej, Long esalActualId) {
        if (idSipej == null || idSipej.trim().isEmpty()) {
            return;
        }
        esalRepository.findByIdSipej(idSipej).ifPresent(existente -> {
            if (esalActualId == null || !existente.getId().equals(esalActualId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID SIPEJ ya existe.");
            }
        });
    }

    private Esal obtenerEsal(Long esalId) {
        return esalRepository.findById(esalId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "ESAL no encontrada con id: " + esalId));
    }

    private PersoneriaJuridica buscarPersoneriaEntidad(Long esalId) {
        List<PersoneriaJuridica> registros = personeriaRepository.findByEsalId(esalId);
        return registros.isEmpty() ? null : registros.get(0);
    }

    private PersoneriaJuridicaDto obtenerPersoneria(Long esalId) {
        PersoneriaJuridica personeria = buscarPersoneriaEntidad(esalId);
        return personeria == null ? null : toPersoneriaDto(personeria);
    }

    private EsalInformacionPrincipalDto toInformacionPrincipalDto(Esal esal) {
        EsalInformacionPrincipalDto dto = new EsalInformacionPrincipalDto();
        dto.setId(esal.getId());
        dto.setNombre(esal.getNombre());
        dto.setIdSipej(esal.getIdSipej());
        dto.setNit(esal.getNit());
        dto.setDomicilio(esal.getDomicilio());
        dto.setCorreoElectronico(esal.getCorreoElectronico());
        dto.setTerminoDuracion(esal.getTerminoDuracion());
        dto.setObjetoSocial(esal.getObjetoSocial());
        dto.setEstado(esal.getEstado());
        dto.setEstadoCompletitud(esal.getEstadoCompletitud());
        return dto;
    }

    private PersoneriaJuridicaDto toPersoneriaDto(PersoneriaJuridica personeria) {
        PersoneriaJuridicaDto dto = new PersoneriaJuridicaDto();
        dto.setId(personeria.getId());
        dto.setEsalId(personeria.getEsalId());
        dto.setReconocimientoPersoneriaJuridica(personeria.getReconocimientoPersoneriaJuridica());
        dto.setFechaReconocimientoPersoneriaJuridica(personeria.getFechaReconocimientoPersoneriaJuridica());
        dto.setEntidadQueExpide(personeria.getEntidadQueExpide());
        dto.setInscripcion(personeria.getInscripcion());
        dto.setFechaInscripcion(personeria.getFechaInscripcion());
        dto.setEntidadQueInscribio(personeria.getEntidadQueInscribio());
        return dto;
    }
}

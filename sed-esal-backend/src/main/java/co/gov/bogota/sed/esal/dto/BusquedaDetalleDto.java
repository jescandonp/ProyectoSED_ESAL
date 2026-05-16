package co.gov.bogota.sed.esal.dto;

import co.gov.bogota.sed.esal.domain.enums.EstadoCompletitud;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.domain.enums.TipoActuacion;
import co.gov.bogota.sed.esal.domain.enums.TipoNombramiento;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class BusquedaDetalleDto {

    private Long esalId;
    private String nombre;
    private String idSipej;
    private String nit;
    private String domicilio;
    private String correoElectronico;
    private String terminoDuracion;
    private String objetoSocial;
    private EstadoEsal estado;
    private EstadoCompletitud estadoCompletitud;
    private PersoneriaSeccionDto personeria;
    private List<ReformaDto> reformas;
    private List<NombramientoDto> nombramientos;
    private List<OrganoDto> organos;
    private List<ActuacionDto> actuaciones;
    private List<DocumentoSoporteDto> documentos;
    private CompletitudDto completitud;
    private LocalDateTime updatedAt;

    public Long getEsalId() { return esalId; }
    public void setEsalId(Long esalId) { this.esalId = esalId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getIdSipej() { return idSipej; }
    public void setIdSipej(String idSipej) { this.idSipej = idSipej; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }

    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

    public String getTerminoDuracion() { return terminoDuracion; }
    public void setTerminoDuracion(String terminoDuracion) { this.terminoDuracion = terminoDuracion; }

    public String getObjetoSocial() { return objetoSocial; }
    public void setObjetoSocial(String objetoSocial) { this.objetoSocial = objetoSocial; }

    public EstadoEsal getEstado() { return estado; }
    public void setEstado(EstadoEsal estado) { this.estado = estado; }

    public EstadoCompletitud getEstadoCompletitud() { return estadoCompletitud; }
    public void setEstadoCompletitud(EstadoCompletitud estadoCompletitud) { this.estadoCompletitud = estadoCompletitud; }

    public PersoneriaSeccionDto getPersoneria() { return personeria; }
    public void setPersoneria(PersoneriaSeccionDto personeria) { this.personeria = personeria; }

    public List<ReformaDto> getReformas() { return reformas; }
    public void setReformas(List<ReformaDto> reformas) { this.reformas = reformas; }

    public List<NombramientoDto> getNombramientos() { return nombramientos; }
    public void setNombramientos(List<NombramientoDto> nombramientos) { this.nombramientos = nombramientos; }

    public List<OrganoDto> getOrganos() { return organos; }
    public void setOrganos(List<OrganoDto> organos) { this.organos = organos; }

    public List<ActuacionDto> getActuaciones() { return actuaciones; }
    public void setActuaciones(List<ActuacionDto> actuaciones) { this.actuaciones = actuaciones; }

    public List<DocumentoSoporteDto> getDocumentos() { return documentos; }
    public void setDocumentos(List<DocumentoSoporteDto> documentos) { this.documentos = documentos; }

    public CompletitudDto getCompletitud() { return completitud; }
    public void setCompletitud(CompletitudDto completitud) { this.completitud = completitud; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static class PersoneriaSeccionDto {
        private Long id;
        private String reconocimientoPersoneriaJuridica;
        private LocalDate fechaReconocimientoPersoneriaJuridica;
        private String entidadQueExpide;
        private String inscripcion;
        private LocalDate fechaInscripcion;
        private String entidadQueInscribio;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getReconocimientoPersoneriaJuridica() { return reconocimientoPersoneriaJuridica; }
        public void setReconocimientoPersoneriaJuridica(String reconocimientoPersoneriaJuridica) { this.reconocimientoPersoneriaJuridica = reconocimientoPersoneriaJuridica; }
        public LocalDate getFechaReconocimientoPersoneriaJuridica() { return fechaReconocimientoPersoneriaJuridica; }
        public void setFechaReconocimientoPersoneriaJuridica(LocalDate fechaReconocimientoPersoneriaJuridica) { this.fechaReconocimientoPersoneriaJuridica = fechaReconocimientoPersoneriaJuridica; }
        public String getEntidadQueExpide() { return entidadQueExpide; }
        public void setEntidadQueExpide(String entidadQueExpide) { this.entidadQueExpide = entidadQueExpide; }
        public String getInscripcion() { return inscripcion; }
        public void setInscripcion(String inscripcion) { this.inscripcion = inscripcion; }
        public LocalDate getFechaInscripcion() { return fechaInscripcion; }
        public void setFechaInscripcion(LocalDate fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }
        public String getEntidadQueInscribio() { return entidadQueInscribio; }
        public void setEntidadQueInscribio(String entidadQueInscribio) { this.entidadQueInscribio = entidadQueInscribio; }
    }

    public static class ReformaDto {
        private Long id;
        private Integer orden;
        private String tipoActo;
        private String numeroActo;
        private LocalDate fechaActo;
        private String entidadQueExpide;
        private String descripcion;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Integer getOrden() { return orden; }
        public void setOrden(Integer orden) { this.orden = orden; }
        public String getTipoActo() { return tipoActo; }
        public void setTipoActo(String tipoActo) { this.tipoActo = tipoActo; }
        public String getNumeroActo() { return numeroActo; }
        public void setNumeroActo(String numeroActo) { this.numeroActo = numeroActo; }
        public LocalDate getFechaActo() { return fechaActo; }
        public void setFechaActo(LocalDate fechaActo) { this.fechaActo = fechaActo; }
        public String getEntidadQueExpide() { return entidadQueExpide; }
        public void setEntidadQueExpide(String entidadQueExpide) { this.entidadQueExpide = entidadQueExpide; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    }

    public static class NombramientoDto {
        private Long id;
        private TipoNombramiento tipoNombramiento;
        private String nombre;
        private String tipoDocumento;
        private String numeroDocumento;
        private String cargo;
        private String actaAprueba;
        private LocalDate fechaActa;
        private String tarjetaProfesional;
        private String facultadesLimitaciones;
        private Boolean vigente;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public TipoNombramiento getTipoNombramiento() { return tipoNombramiento; }
        public void setTipoNombramiento(TipoNombramiento tipoNombramiento) { this.tipoNombramiento = tipoNombramiento; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getTipoDocumento() { return tipoDocumento; }
        public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
        public String getNumeroDocumento() { return numeroDocumento; }
        public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
        public String getCargo() { return cargo; }
        public void setCargo(String cargo) { this.cargo = cargo; }
        public String getActaAprueba() { return actaAprueba; }
        public void setActaAprueba(String actaAprueba) { this.actaAprueba = actaAprueba; }
        public LocalDate getFechaActa() { return fechaActa; }
        public void setFechaActa(LocalDate fechaActa) { this.fechaActa = fechaActa; }
        public String getTarjetaProfesional() { return tarjetaProfesional; }
        public void setTarjetaProfesional(String tarjetaProfesional) { this.tarjetaProfesional = tarjetaProfesional; }
        public String getFacultadesLimitaciones() { return facultadesLimitaciones; }
        public void setFacultadesLimitaciones(String facultadesLimitaciones) { this.facultadesLimitaciones = facultadesLimitaciones; }
        public Boolean getVigente() { return vigente; }
        public void setVigente(Boolean vigente) { this.vigente = vigente; }
    }

    public static class OrganoDto {
        private Long id;
        private String organo;
        private String miembro;
        private String cargo;
        private String tipoDocumento;
        private String numeroDocumento;
        private String actaAprueba;
        private LocalDate fechaActa;
        private String actaAclaratoria;
        private LocalDate fechaActaAclaratoria;
        private String facultadesLimitaciones;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getOrgano() { return organo; }
        public void setOrgano(String organo) { this.organo = organo; }
        public String getMiembro() { return miembro; }
        public void setMiembro(String miembro) { this.miembro = miembro; }
        public String getCargo() { return cargo; }
        public void setCargo(String cargo) { this.cargo = cargo; }
        public String getTipoDocumento() { return tipoDocumento; }
        public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
        public String getNumeroDocumento() { return numeroDocumento; }
        public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
        public String getActaAprueba() { return actaAprueba; }
        public void setActaAprueba(String actaAprueba) { this.actaAprueba = actaAprueba; }
        public LocalDate getFechaActa() { return fechaActa; }
        public void setFechaActa(LocalDate fechaActa) { this.fechaActa = fechaActa; }
        public String getActaAclaratoria() { return actaAclaratoria; }
        public void setActaAclaratoria(String actaAclaratoria) { this.actaAclaratoria = actaAclaratoria; }
        public LocalDate getFechaActaAclaratoria() { return fechaActaAclaratoria; }
        public void setFechaActaAclaratoria(LocalDate fechaActaAclaratoria) { this.fechaActaAclaratoria = fechaActaAclaratoria; }
        public String getFacultadesLimitaciones() { return facultadesLimitaciones; }
        public void setFacultadesLimitaciones(String facultadesLimitaciones) { this.facultadesLimitaciones = facultadesLimitaciones; }
    }

    public static class ActuacionDto {
        private Long id;
        private TipoActuacion tipoActuacion;
        private String acta;
        private LocalDate fechaActa;
        private String resolucion;
        private LocalDate fechaResolucion;
        private String motivo;
        private String tiempoSuspension;
        private LocalDate fechaInicio;
        private LocalDate fechaFin;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public TipoActuacion getTipoActuacion() { return tipoActuacion; }
        public void setTipoActuacion(TipoActuacion tipoActuacion) { this.tipoActuacion = tipoActuacion; }
        public String getActa() { return acta; }
        public void setActa(String acta) { this.acta = acta; }
        public LocalDate getFechaActa() { return fechaActa; }
        public void setFechaActa(LocalDate fechaActa) { this.fechaActa = fechaActa; }
        public String getResolucion() { return resolucion; }
        public void setResolucion(String resolucion) { this.resolucion = resolucion; }
        public LocalDate getFechaResolucion() { return fechaResolucion; }
        public void setFechaResolucion(LocalDate fechaResolucion) { this.fechaResolucion = fechaResolucion; }
        public String getMotivo() { return motivo; }
        public void setMotivo(String motivo) { this.motivo = motivo; }
        public String getTiempoSuspension() { return tiempoSuspension; }
        public void setTiempoSuspension(String tiempoSuspension) { this.tiempoSuspension = tiempoSuspension; }
        public LocalDate getFechaInicio() { return fechaInicio; }
        public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
        public LocalDate getFechaFin() { return fechaFin; }
        public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    }
}

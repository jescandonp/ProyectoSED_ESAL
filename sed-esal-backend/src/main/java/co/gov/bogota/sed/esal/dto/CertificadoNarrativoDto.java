package co.gov.bogota.sed.esal.dto;

import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;

import java.time.LocalDate;
import java.util.List;

public class CertificadoNarrativoDto {

    private String nombre;
    private String idSipej;
    private String nit;
    private String domicilio;
    private String correoElectronico;
    private String terminoDuracion;
    private String objetoSocial;
    private EstadoEsal estado;
    private String alertaEstado;

    // Personeria juridica
    private String resolucionPersoneria;
    private LocalDate fechaResolucion;
    private String entidadQueExpide;
    private String inscripcion;
    private LocalDate fechaInscripcion;

    // Organos
    private List<MiembroDto> representantesLegales;
    private String facultadesRepresentante;
    private List<MiembroDto> miembrosJunta;
    private List<MiembroDto> miembrosAsamblea;
    private List<MiembroDto> revisoresFiscales;

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

    public String getAlertaEstado() { return alertaEstado; }
    public void setAlertaEstado(String alertaEstado) { this.alertaEstado = alertaEstado; }

    public String getResolucionPersoneria() { return resolucionPersoneria; }
    public void setResolucionPersoneria(String resolucionPersoneria) { this.resolucionPersoneria = resolucionPersoneria; }

    public LocalDate getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDate fechaResolucion) { this.fechaResolucion = fechaResolucion; }

    public String getEntidadQueExpide() { return entidadQueExpide; }
    public void setEntidadQueExpide(String entidadQueExpide) { this.entidadQueExpide = entidadQueExpide; }

    public String getInscripcion() { return inscripcion; }
    public void setInscripcion(String inscripcion) { this.inscripcion = inscripcion; }

    public LocalDate getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(LocalDate fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }

    public List<MiembroDto> getRepresentantesLegales() { return representantesLegales; }
    public void setRepresentantesLegales(List<MiembroDto> representantesLegales) { this.representantesLegales = representantesLegales; }

    public String getFacultadesRepresentante() { return facultadesRepresentante; }
    public void setFacultadesRepresentante(String facultadesRepresentante) { this.facultadesRepresentante = facultadesRepresentante; }

    public List<MiembroDto> getMiembrosJunta() { return miembrosJunta; }
    public void setMiembrosJunta(List<MiembroDto> miembrosJunta) { this.miembrosJunta = miembrosJunta; }

    public List<MiembroDto> getMiembrosAsamblea() { return miembrosAsamblea; }
    public void setMiembrosAsamblea(List<MiembroDto> miembrosAsamblea) { this.miembrosAsamblea = miembrosAsamblea; }

    public List<MiembroDto> getRevisoresFiscales() { return revisoresFiscales; }
    public void setRevisoresFiscales(List<MiembroDto> revisoresFiscales) { this.revisoresFiscales = revisoresFiscales; }

    public static class MiembroDto {
        private String nombre;
        private String tipoDocumento;
        private String numeroDocumento;
        private String cargo;
        private String actaNombramiento;
        private String radicadoSed;

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public String getTipoDocumento() { return tipoDocumento; }
        public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

        public String getNumeroDocumento() { return numeroDocumento; }
        public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

        public String getCargo() { return cargo; }
        public void setCargo(String cargo) { this.cargo = cargo; }

        public String getActaNombramiento() { return actaNombramiento; }
        public void setActaNombramiento(String actaNombramiento) { this.actaNombramiento = actaNombramiento; }

        public String getRadicadoSed() { return radicadoSed; }
        public void setRadicadoSed(String radicadoSed) { this.radicadoSed = radicadoSed; }
    }
}

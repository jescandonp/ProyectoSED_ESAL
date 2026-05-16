package co.gov.bogota.sed.esal.dto;

/**
 * DTO para crear una nueva ESAL.
 */
public class EsalCreateDto {

    private String nombre;
    private String idSipej;
    private String nit;
    private String domicilio;
    private String correoElectronico;
    private String terminoDuracion;
    private String objetoSocial;

    public EsalCreateDto() {
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getIdSipej() { return idSipej; }
    public void setIdSipej(String idSipej) { this.idSipej = idSipej; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }

    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getTerminoDuracion() { return terminoDuracion; }
    public void setTerminoDuracion(String terminoDuracion) { this.terminoDuracion = terminoDuracion; }

    public String getObjetoSocial() { return objetoSocial; }
    public void setObjetoSocial(String objetoSocial) { this.objetoSocial = objetoSocial; }
}

package co.gov.bogota.sed.esal.dto;

public class PermisosDto {

    private String rol;
    private boolean puedeAdministrar;
    private boolean puedeBuscar;
    private boolean puedeGenerarCertificado;
    private boolean puedeDescargarCertificado;
    private boolean puedeConsultarAuditoria;

    public PermisosDto() {}

    public static PermisosDto paraAdministrador() {
        PermisosDto p = new PermisosDto();
        p.rol = "ADMINISTRADOR";
        p.puedeAdministrar = true;
        p.puedeBuscar = true;
        p.puedeGenerarCertificado = true;
        p.puedeDescargarCertificado = true;
        p.puedeConsultarAuditoria = true;
        return p;
    }

    public static PermisosDto paraExpedidor() {
        PermisosDto p = new PermisosDto();
        p.rol = "EXPEDIDOR";
        p.puedeAdministrar = false;
        p.puedeBuscar = true;
        p.puedeGenerarCertificado = true;
        p.puedeDescargarCertificado = true;
        p.puedeConsultarAuditoria = false;
        return p;
    }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public boolean isPuedeAdministrar() { return puedeAdministrar; }
    public void setPuedeAdministrar(boolean puedeAdministrar) { this.puedeAdministrar = puedeAdministrar; }

    public boolean isPuedeBuscar() { return puedeBuscar; }
    public void setPuedeBuscar(boolean puedeBuscar) { this.puedeBuscar = puedeBuscar; }

    public boolean isPuedeGenerarCertificado() { return puedeGenerarCertificado; }
    public void setPuedeGenerarCertificado(boolean puedeGenerarCertificado) { this.puedeGenerarCertificado = puedeGenerarCertificado; }

    public boolean isPuedeDescargarCertificado() { return puedeDescargarCertificado; }
    public void setPuedeDescargarCertificado(boolean puedeDescargarCertificado) { this.puedeDescargarCertificado = puedeDescargarCertificado; }

    public boolean isPuedeConsultarAuditoria() { return puedeConsultarAuditoria; }
    public void setPuedeConsultarAuditoria(boolean puedeConsultarAuditoria) { this.puedeConsultarAuditoria = puedeConsultarAuditoria; }
}

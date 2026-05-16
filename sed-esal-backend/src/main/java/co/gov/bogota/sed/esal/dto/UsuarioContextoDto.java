package co.gov.bogota.sed.esal.dto;

public class UsuarioContextoDto {

    private String usuario;
    private String email;
    private String nombre;
    private String rol;

    public UsuarioContextoDto() {}

    public UsuarioContextoDto(String usuario, String email, String nombre, String rol) {
        this.usuario = usuario;
        this.email = email;
        this.nombre = nombre;
        this.rol = rol;
    }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}

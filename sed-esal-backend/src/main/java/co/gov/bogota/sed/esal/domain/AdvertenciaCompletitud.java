package co.gov.bogota.sed.esal.domain;

import co.gov.bogota.sed.esal.domain.enums.TipoAdvertencia;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Advertencia de completitud registrada sobre una ESAL.
 */
@Entity
@Table(schema = "SED_ESAL", name = "ESAL_ADVERTENCIA_COMPLETITUD")
public class AdvertenciaCompletitud {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esal_advertencia_seq")
    @SequenceGenerator(name = "esal_advertencia_seq", sequenceName = "ESAL_ADVERTENCIA_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ESAL_ID", nullable = false)
    private Long esalId;

    @Column(name = "SECCION", length = 255)
    private String seccion;

    @Column(name = "CAMPO", length = 255)
    private String campo;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 50)
    private TipoAdvertencia tipo;

    @Column(name = "BLOQUEANTE", nullable = false)
    private Boolean bloqueante;

    @Column(name = "MENSAJE", length = 1000)
    private String mensaje;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    public AdvertenciaCompletitud() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEsalId() { return esalId; }
    public void setEsalId(Long esalId) { this.esalId = esalId; }

    public String getSeccion() { return seccion; }
    public void setSeccion(String seccion) { this.seccion = seccion; }

    public String getCampo() { return campo; }
    public void setCampo(String campo) { this.campo = campo; }

    public TipoAdvertencia getTipo() { return tipo; }
    public void setTipo(TipoAdvertencia tipo) { this.tipo = tipo; }

    public Boolean getBloqueante() { return bloqueante; }
    public void setBloqueante(Boolean bloqueante) { this.bloqueante = bloqueante; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

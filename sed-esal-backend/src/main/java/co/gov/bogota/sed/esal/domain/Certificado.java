package co.gov.bogota.sed.esal.domain;

import co.gov.bogota.sed.esal.domain.enums.EstadoCertificado;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(schema = "SED_ESAL", name = "ESAL_CERTIFICADO")
public class Certificado {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esal_certificado_seq")
    @SequenceGenerator(name = "esal_certificado_seq", sequenceName = "ESAL_CERTIFICADO_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ESAL_ID", nullable = false)
    private Long esalId;

    @Column(name = "ID_SIPEJ", length = 100)
    private String idSipej;

    @Column(name = "NIT", length = 50)
    private String nit;

    @Column(name = "NUMERO_CERTIFICADO", unique = true, length = 50)
    private String numeroCertificado;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_CERTIFICADO", nullable = false, length = 20)
    private EstadoCertificado estadoCertificado;

    @Column(name = "VERSION_DATOS", length = 50)
    private String versionDatos;

    @Column(name = "FECHA_EXPEDICION")
    private LocalDateTime fechaExpedicion;

    @Column(name = "FIRMANTE_ID")
    private Long firmanteId;

    @Column(name = "FIRMANTE_NOMBRE", length = 255)
    private String firmanteNombre;

    @Column(name = "FIRMANTE_CARGO", length = 255)
    private String firmanteCargo;

    @Column(name = "PLANTILLA_VERSION", length = 50)
    private String plantillaVersion;

    @Column(name = "HASH_SHA256", length = 64)
    private String hashSha256;

    @Column(name = "RUTA_PDF", length = 500)
    private String rutaPdf;

    @Column(name = "NOMBRE_ARCHIVO", length = 255)
    private String nombreArchivo;

    @Column(name = "CONTENT_TYPE", length = 100)
    private String contentType;

    @Column(name = "TAMANO_BYTES")
    private Long tamanoBytes;

    @Column(name = "ERROR_DETALLE", length = 2000)
    private String errorDetalle;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY", length = 255)
    private String createdBy;

    public Certificado() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEsalId() { return esalId; }
    public void setEsalId(Long esalId) { this.esalId = esalId; }

    public String getIdSipej() { return idSipej; }
    public void setIdSipej(String idSipej) { this.idSipej = idSipej; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getNumeroCertificado() { return numeroCertificado; }
    public void setNumeroCertificado(String numeroCertificado) { this.numeroCertificado = numeroCertificado; }

    public EstadoCertificado getEstadoCertificado() { return estadoCertificado; }
    public void setEstadoCertificado(EstadoCertificado estadoCertificado) { this.estadoCertificado = estadoCertificado; }

    public String getVersionDatos() { return versionDatos; }
    public void setVersionDatos(String versionDatos) { this.versionDatos = versionDatos; }

    public LocalDateTime getFechaExpedicion() { return fechaExpedicion; }
    public void setFechaExpedicion(LocalDateTime fechaExpedicion) { this.fechaExpedicion = fechaExpedicion; }

    public Long getFirmanteId() { return firmanteId; }
    public void setFirmanteId(Long firmanteId) { this.firmanteId = firmanteId; }

    public String getFirmanteNombre() { return firmanteNombre; }
    public void setFirmanteNombre(String firmanteNombre) { this.firmanteNombre = firmanteNombre; }

    public String getFirmanteCargo() { return firmanteCargo; }
    public void setFirmanteCargo(String firmanteCargo) { this.firmanteCargo = firmanteCargo; }

    public String getPlantillaVersion() { return plantillaVersion; }
    public void setPlantillaVersion(String plantillaVersion) { this.plantillaVersion = plantillaVersion; }

    public String getHashSha256() { return hashSha256; }
    public void setHashSha256(String hashSha256) { this.hashSha256 = hashSha256; }

    public String getRutaPdf() { return rutaPdf; }
    public void setRutaPdf(String rutaPdf) { this.rutaPdf = rutaPdf; }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Long getTamanoBytes() { return tamanoBytes; }
    public void setTamanoBytes(Long tamanoBytes) { this.tamanoBytes = tamanoBytes; }

    public String getErrorDetalle() { return errorDetalle; }
    public void setErrorDetalle(String errorDetalle) { this.errorDetalle = errorDetalle; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}

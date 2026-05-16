package co.gov.bogota.sed.esal.dto;

import co.gov.bogota.sed.esal.domain.enums.EstadoCertificado;

import java.time.LocalDateTime;

public class CertificadoDto {
    private Long certificadoId;
    private Long esalId;
    private String idSipej;
    private String nit;
    private String numeroCertificado;
    private EstadoCertificado estadoCertificado;
    private LocalDateTime fechaExpedicion;
    private LocalDateTime versionDatos;
    private String firmanteNombre;
    private String firmanteCargo;
    private String plantillaVersion;
    private String hashSha256;
    private String nombreArchivo;
    private Long tamanoBytes;
    private String errorDetalle;
    private LocalDateTime createdAt;
    private String createdBy;

    public CertificadoDto() {}

    public Long getCertificadoId() { return certificadoId; }
    public void setCertificadoId(Long certificadoId) { this.certificadoId = certificadoId; }

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

    public LocalDateTime getFechaExpedicion() { return fechaExpedicion; }
    public void setFechaExpedicion(LocalDateTime fechaExpedicion) { this.fechaExpedicion = fechaExpedicion; }

    public LocalDateTime getVersionDatos() { return versionDatos; }
    public void setVersionDatos(LocalDateTime versionDatos) { this.versionDatos = versionDatos; }

    public String getFirmanteNombre() { return firmanteNombre; }
    public void setFirmanteNombre(String firmanteNombre) { this.firmanteNombre = firmanteNombre; }

    public String getFirmanteCargo() { return firmanteCargo; }
    public void setFirmanteCargo(String firmanteCargo) { this.firmanteCargo = firmanteCargo; }

    public String getPlantillaVersion() { return plantillaVersion; }
    public void setPlantillaVersion(String plantillaVersion) { this.plantillaVersion = plantillaVersion; }

    public String getHashSha256() { return hashSha256; }
    public void setHashSha256(String hashSha256) { this.hashSha256 = hashSha256; }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    public Long getTamanoBytes() { return tamanoBytes; }
    public void setTamanoBytes(Long tamanoBytes) { this.tamanoBytes = tamanoBytes; }

    public String getErrorDetalle() { return errorDetalle; }
    public void setErrorDetalle(String errorDetalle) { this.errorDetalle = errorDetalle; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}

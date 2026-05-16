package co.gov.bogota.sed.esal.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Resumen del resultado de la importación histórica de ESALes desde Excel.
 */
public class EsalImportResultDto {

    private Long importacionId;
    private int totalLeidos;
    private int totalImportados;
    private int totalRechazados;
    private int totalAdvertencias;
    private int totalReformas;
    private List<String> advertencias = new ArrayList<>();
    private LocalDateTime fechaImportacion;
    private String importadoPor;

    public EsalImportResultDto() {
    }

    public Long getImportacionId() { return importacionId; }
    public void setImportacionId(Long importacionId) { this.importacionId = importacionId; }

    public int getTotalLeidos() { return totalLeidos; }
    public void setTotalLeidos(int totalLeidos) { this.totalLeidos = totalLeidos; }

    public int getTotalImportados() { return totalImportados; }
    public void setTotalImportados(int totalImportados) { this.totalImportados = totalImportados; }

    public int getTotalRechazados() { return totalRechazados; }
    public void setTotalRechazados(int totalRechazados) { this.totalRechazados = totalRechazados; }

    public int getTotalAdvertencias() { return totalAdvertencias; }
    public void setTotalAdvertencias(int totalAdvertencias) { this.totalAdvertencias = totalAdvertencias; }

    public int getTotalReformas() { return totalReformas; }
    public void setTotalReformas(int totalReformas) { this.totalReformas = totalReformas; }

    public List<String> getAdvertencias() { return advertencias; }
    public void setAdvertencias(List<String> advertencias) { this.advertencias = advertencias; }

    public LocalDateTime getFechaImportacion() { return fechaImportacion; }
    public void setFechaImportacion(LocalDateTime fechaImportacion) { this.fechaImportacion = fechaImportacion; }

    public String getImportadoPor() { return importadoPor; }
    public void setImportadoPor(String importadoPor) { this.importadoPor = importadoPor; }
}

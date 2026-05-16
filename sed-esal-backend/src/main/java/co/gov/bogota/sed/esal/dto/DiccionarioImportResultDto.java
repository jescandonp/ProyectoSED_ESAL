package co.gov.bogota.sed.esal.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Resultado de la importación del diccionario de campos desde Base excel.xlsx.
 */
public class DiccionarioImportResultDto {

    private int totalLeidos;
    private int totalPersistidos;
    private int totalObligatorios;
    private int totalOpcionales;
    private List<String> advertencias = new ArrayList<>();

    public DiccionarioImportResultDto() {
    }

    public int getTotalLeidos() { return totalLeidos; }
    public void setTotalLeidos(int totalLeidos) { this.totalLeidos = totalLeidos; }

    public int getTotalPersistidos() { return totalPersistidos; }
    public void setTotalPersistidos(int totalPersistidos) { this.totalPersistidos = totalPersistidos; }

    public int getTotalObligatorios() { return totalObligatorios; }
    public void setTotalObligatorios(int totalObligatorios) { this.totalObligatorios = totalObligatorios; }

    public int getTotalOpcionales() { return totalOpcionales; }
    public void setTotalOpcionales(int totalOpcionales) { this.totalOpcionales = totalOpcionales; }

    public List<String> getAdvertencias() { return advertencias; }
    public void setAdvertencias(List<String> advertencias) { this.advertencias = advertencias; }
}
